package com.ssafy.tothezip.property.model.service;

import com.ssafy.tothezip.property.model.AiRecoRankDto;
import com.ssafy.tothezip.property.model.PropertyCompareDto;
import com.ssafy.tothezip.property.model.mapper.PropertyCompareMapper;
import com.ssafy.tothezip.property.model.util.TrendAnalyzer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecoRankService {

    private final AiRecoClient aiRecoClient;
    private final PropertyCompareMapper propertyCompareMapper;

    public void enrich(PropertyCompareDto.BaseListing base,
                       List<PropertyCompareDto.CandidateListing> candidates,
                       PropertyCompareDto.CompareResponse res,
                       int topK) {

        try {
            // 1) base 평점/거래추세 채우기
            fillRatingAndSeriesForBase(base);

            // 2) 후보 평점/거래추세 채우기
            fillRatingAndSeriesForCandidates(candidates);

            // 3) AI 요청 DTO 만들기
            AiRecoRankDto.RankRequest req = new AiRecoRankDto.RankRequest();
            req.setTopK(topK);
            req.setMaxReasons(3);
            req.setMode("compare");

            req.setBase(mapBase(base));
            req.setCandidates(mapCandidates(candidates));

            // 4) AI 서버 호출
            AiRecoRankDto.RankResponse ai = aiRecoClient.rankExplain(req);

            res.setAiEnabled(true);
            res.setAiModel(ai != null ? ai.getModel() : null);

            if (ai == null) {
                res.setAiError("AI server returned null response");
                return;
            }
            if (ai.getError() != null && !ai.getError().isBlank()) {
                res.setAiError(ai.getError());
                return;
            }
            if (ai.getResults() == null) return;

            // 5) 결과를 후보에 부착
            Map<Integer, AiRecoRankDto.CandidateResult> map = new HashMap<>();
            for (var r : ai.getResults()) map.put(r.getPropertyId(), r);

            for (var c : candidates) {
                var rr = map.get(c.getPropertyId());
                if (rr == null) continue;
                c.setAiScore(rr.getScore());
                c.setAiJudgeCode(rr.getJudgeCode());
                c.setAiSummary(rr.getSummary());
                c.setAiReasons(rr.getReasons());
                c.setAiBreakdown(rr.getBreakdown());
            }

        } catch (Exception e) {
            res.setAiEnabled(true);
            res.setAiError("AI enrich failed: " + e.getMessage());
        }
    }

    private void fillRatingAndSeriesForBase(PropertyCompareDto.BaseListing base) {
        Double rating = propertyCompareMapper.findRatingByAptSeq(base.getAptSeq());
        base.setRating(rating);

        base.setRecentPriceSeries(
                propertyCompareMapper.findRecentPriceSeriesByAptSeq(
                        base.getAptSeq(), 10
                )
        );
        base.setTrend(TrendAnalyzer.analyze(base.getRecentPriceSeries()));
    }

    private void fillRatingAndSeriesForCandidates(List<PropertyCompareDto.CandidateListing> candidates) {
        for (var c : candidates) {
            Double rating = propertyCompareMapper.findRatingByAptSeq(c.getAptSeq());
            c.setRating(rating);

            c.setRecentPriceSeries(
                    propertyCompareMapper.findRecentPriceSeriesByAptSeq(
                            c.getAptSeq(), 10
                    )
            );
            c.setTrend(TrendAnalyzer.analyze(c.getRecentPriceSeries()));
        }
    }

    private String calcTrend(List<PropertyCompareDto.PricePoint> series) {
        if (series == null || series.size() < 2) return "UNKNOWN";
        Double first = toDouble(series.get(0).getAmount());
        Double last = toDouble(series.get(series.size()-1).getAmount());
        if (first == null || last == null || first == 0) return "UNKNOWN";

        double change = (last - first) / Math.abs(first);
        if (change >= 0.05) return "UP";
        if (change <= -0.05) return "DOWN";
        return "FLAT";
    }

    private Double toDouble(String s) {
        if (s == null) return null;
        String onlyNum = s.replaceAll("[^0-9]", "");
        if (onlyNum.isBlank()) return null;
        try { return Double.parseDouble(onlyNum); } catch (Exception e) { return null; }
    }

    private AiRecoRankDto.Base mapBase(PropertyCompareDto.BaseListing base) {
        AiRecoRankDto.Base b = new AiRecoRankDto.Base();
        b.setPropertyId(base.getPropertyId());
        b.setType(base.getType());
        b.setPrice(base.getPrice());
        b.setDeposit(base.getDeposit());
        b.setArea(base.getArea());
        b.setLatitude(base.getLatitude());
        b.setLongitude(base.getLongitude());
        b.setAptName(base.getAptName());
        b.setRating(base.getRating());
        b.setTrend(base.getTrend());
        b.setRecentPriceSeries(mapSeries(base.getRecentPriceSeries()));
        return b;
    }

    private List<AiRecoRankDto.Candidate> mapCandidates(List<PropertyCompareDto.CandidateListing> candidates) {
        List<AiRecoRankDto.Candidate> out = new ArrayList<>();
        for (var c : candidates) {
            AiRecoRankDto.Candidate cc = new AiRecoRankDto.Candidate();
            cc.setPropertyId(c.getPropertyId());
            cc.setAptName(c.getAptName());
            cc.setPrice(c.getPrice());
            cc.setDeposit(c.getDeposit());
            cc.setArea(c.getArea());
            cc.setDistM(c.getDistM());
            cc.setRating(c.getRating());
            cc.setTrend(c.getTrend());
            cc.setRecentPriceSeries(mapSeries(c.getRecentPriceSeries()));
            out.add(cc);
        }
        return out;
    }

    private List<AiRecoRankDto.PricePoint> mapSeries(List<PropertyCompareDto.PricePoint> series) {
        if (series == null) return null;
        List<AiRecoRankDto.PricePoint> out = new ArrayList<>();
        for (var p : series) {
            AiRecoRankDto.PricePoint pp = new AiRecoRankDto.PricePoint();
            pp.setDate(p.getDate());
            pp.setAmount(p.getAmount());
            out.add(pp);
        }
        return out;
    }
}
