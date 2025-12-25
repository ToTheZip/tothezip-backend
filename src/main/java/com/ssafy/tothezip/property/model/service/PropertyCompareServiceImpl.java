package com.ssafy.tothezip.property.model.service;

import com.ssafy.tothezip.property.model.PropertyCompareDto;
import com.ssafy.tothezip.property.model.mapper.PropertyCompareMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PropertyCompareServiceImpl implements PropertyCompareService {

    private final PropertyCompareMapper propertyCompareMapper;
    private final RecoRankService recoRankService;

    /**
     * 비교 매물 후보 조회 (MVP: DB 필터 + 휴리스틱 점수)
     * + AI 서버로 (설명/점수/판정코드) enrich
     */
    @Override
    public PropertyCompareDto.CompareResponse getComparisons(Integer propertyId, Integer userId, int limit) {

        int safeLimit = (limit <= 0) ? 10 : Math.min(limit, 30);

        // 1) 기준 매물 조회
        PropertyCompareDto.BaseListing base = propertyCompareMapper.findBaseWithCoord(propertyId);
        if (base == null) throw new IllegalArgumentException("property not found: " + propertyId);

        // 2) 기준 매물 기반 검색 범위 계산(가격/면적)
        RangeParams params = makeRangeParams(base);

        // 3) 1차: 반경 2km 후보 수집(넉넉히)
        int radius = 2000;
        List<PropertyCompareDto.CandidateListing> cands =
                findCandidatesByType(base, params, radius, safeLimit * 5);

        // 4) 후보가 너무 적으면 3km로 확장
        if (cands.size() < safeLimit * 2) {
            radius = 3000;
            cands = findCandidatesByType(base, params, radius, safeLimit * 5);
        }

        // 5) 점수 계산 + topN 선택
        List<PropertyCompareDto.CandidateListing> top = scoreAndPickTop(base, cands, safeLimit);

        // 6) 응답 구성
        PropertyCompareDto.CompareResponse res = new PropertyCompareDto.CompareResponse();
        res.setBase(base);
        res.setCandidates(top);
        res.setUsedRadiusM(radius);

        // 7) AI enrich (평점/거래추세 포함 → AI 서버로 보내서 score/judge/summary/reasons 받기)
        //    실패해도 비교 기능은 동작해야 하므로 try/catch는 RecoRankService 내부에서 처리
        recoRankService.enrich(base, top, res, safeLimit);

        return res;
    }

    /**
     * 타입별로 다른 후보 쿼리 호출
     */
    private List<PropertyCompareDto.CandidateListing> findCandidatesByType(
            PropertyCompareDto.BaseListing base,
            RangeParams params,
            int radiusM,
            int limit
    ) {
        if ("월세".equals(base.getType())) {
            return propertyCompareMapper.findCandidatesMonthly(
                    base.getPropertyId(),
                    base.getLatitude(),
                    base.getLongitude(),
                    radiusM,
                    params.depMin, params.depMax,
                    params.rentMin, params.rentMax,
                    params.areaMin, params.areaMax,
                    limit
            );
        }

        // 전세/매매
        return propertyCompareMapper.findCandidatesSaleJeonse(
                base.getPropertyId(),
                base.getType(),
                base.getLatitude(),
                base.getLongitude(),
                radiusM,
                params.priceMin, params.priceMax,
                params.areaMin, params.areaMax,
                limit
        );
    }

    /**
     * 후보 score 계산 후 topN 추리기
     * - 중복 제거
     * - score(desc) → distM(asc) 정렬
     */
    private List<PropertyCompareDto.CandidateListing> scoreAndPickTop(
            PropertyCompareDto.BaseListing base,
            List<PropertyCompareDto.CandidateListing> cands,
            int topN
    ) {
        if (cands == null || cands.isEmpty()) return Collections.emptyList();

        // 1) 중복 제거 (propertyId 기준)
        Map<Integer, PropertyCompareDto.CandidateListing> uniq = new LinkedHashMap<>();
        for (var c : cands) {
            if (c == null || c.getPropertyId() == null) continue;
            uniq.putIfAbsent(c.getPropertyId(), c);
        }

        // 2) score 계산(거리/가격/면적)
        for (var c : uniq.values()) {
            c.setAiBreakdown(null); // 혹시 기존 값 있으면 초기화(선택)
            // CandidateListing에 score 필드가 따로 없다면 아래처럼 로컬 변수로만 정렬해도 되는데,
            // 현재는 CandidateListing에 setScore(...)가 없을 가능성이 큼.
            // 그래서 score를 Map으로 따로 관리해서 정렬한다.
        }

        // 3) scoreMap 생성
        final Map<Integer, Double> scoreMap = new HashMap<>();
        for (var c : uniq.values()) {
            scoreMap.put(c.getPropertyId(), calcScore(base, c));
        }

        // 4) 정렬 + topN
        return uniq.values().stream()
                .sorted(Comparator
                        .comparing(
                                (PropertyCompareDto.CandidateListing x) -> scoreMap.getOrDefault(x.getPropertyId(), 0.0),
                                Comparator.reverseOrder()
                        )
                        .thenComparing(PropertyCompareDto.CandidateListing::getDistM,
                                Comparator.nullsLast(Double::compareTo))
                )
                .limit(topN)
                .collect(Collectors.toList());
    }

    /**
     * 휴리스틱 점수(0~1 근처)
     * - 거리: 가까울수록 좋음
     * - 가격: 비슷할수록 좋음 (월세면 월세+보증금 혼합)
     * - 면적: 비슷할수록 좋음
     */
    private double calcScore(PropertyCompareDto.BaseListing base, PropertyCompareDto.CandidateListing c) {
        double dist = (c.getDistM() == null) ? 99999.0 : c.getDistM();
        double distScore = 1.0 / (1.0 + dist / 500.0); // 500m 스케일

        double areaScore = similarity(base.getArea(), c.getArea());
        double priceScore;

        if ("월세".equals(base.getType())) {
            double rentScore = similarity(toDouble(base.getPrice()), toDouble(c.getPrice()));
            double depScore = similarity(toDouble(base.getDeposit()), toDouble(c.getDeposit()));
            priceScore = 0.6 * rentScore + 0.4 * depScore;
        } else {
            priceScore = similarity(toDouble(base.getPrice()), toDouble(c.getPrice()));
        }

        return 0.45 * distScore + 0.35 * priceScore + 0.20 * areaScore;
    }

    /**
     * 문자열 가격 -> double
     * - 콤마/공백/원/만원 등 섞여도 숫자만 최대한 추출
     */
    private Double toDouble(String s) {
        if (s == null) return null;
        String onlyNum = s.replaceAll("[^0-9]", "");
        if (onlyNum.isBlank()) return null;
        try {
            return Double.parseDouble(onlyNum);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 두 값 유사도(0~1)
     */
    private double similarity(Double a, Double b) {
        if (a == null || b == null) return 0.0;
        double denom = Math.max(Math.abs(a), 1.0);
        double diff = Math.abs(a - b) / denom;
        return Math.max(0.0, 1.0 - diff);
    }

    /**
     * base 기준으로 가격/면적 검색 범위 생성
     */
    private RangeParams makeRangeParams(PropertyCompareDto.BaseListing base) {
        RangeParams p = new RangeParams();

        // 면적 범위 +-15%
        if (base.getArea() != null) {
            p.areaMin = base.getArea() * 0.85;
            p.areaMax = base.getArea() * 1.15;
        } else {
            p.areaMin = 0.0;
            p.areaMax = 999999.0;
        }

        if ("월세".equals(base.getType())) {
            int dep = toInt(base.getDeposit());
            int rent = toInt(base.getPrice());

            // 보증금 +-20%
            p.depMin = (int) Math.floor(dep * 0.80);
            p.depMax = (int) Math.ceil(dep * 1.20);

            // 월세 +-20%
            p.rentMin = (int) Math.floor(rent * 0.80);
            p.rentMax = (int) Math.ceil(rent * 1.20);
        } else {
            int price = toInt(base.getPrice());

            // 전세/매매 가격 +-15%
            p.priceMin = (int) Math.floor(price * 0.85);
            p.priceMax = (int) Math.ceil(price * 1.15);
        }

        return p;
    }

    private int toInt(String s) {
        Double d = toDouble(s);
        return (d == null) ? 0 : d.intValue();
    }

    /**
     * 검색 범위 파라미터
     */
    private static class RangeParams {
        Integer priceMin;
        Integer priceMax;

        Integer depMin;
        Integer depMax;

        Integer rentMin;
        Integer rentMax;

        Double areaMin;
        Double areaMax;
    }
}
