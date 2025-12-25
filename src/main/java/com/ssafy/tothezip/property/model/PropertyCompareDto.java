package com.ssafy.tothezip.property.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

public class PropertyCompareDto {

    @Data
    public static class PricePoint {
        private String date;   // "YYYY-MM-DD"
        private String amount; // 원본 그대로(예: "43000" / "4억3천" 등) - DB 타입에 맞게
    }

    @Data
    public static class BaseListing {
        private Integer propertyId;
        private String aptSeq;
        private String type;        // 월세/전세/매매
        private String price;       // 월세면 월세 값, 전세/매매면 가격
        private String deposit;     // 월세 보증금
        private Double area;
        private Integer floor;
        private Double latitude;
        private Double longitude;

        private String aptName;

        // ✅ 추가
        private Double rating;                 // 평균 평점
        private List<PricePoint> recentPriceSeries; // 최근 거래 추세
        private String trend;                  // UP/DOWN/FLAT/UNKNOWN
    }

    @Data
    public static class CandidateListing {
        private Integer propertyId;
        private String aptSeq;
        private String aptName;
        private String type;
        private String price;
        private String deposit;
        private Integer floor;
        private Double area;
        private Double distM;
        private Double score;

        private Double latitude;
        private Double longitude;

        // ✅ 추가(백엔드에서 DB조회로 채움)
        private Double rating;
        private List<PricePoint> recentPriceSeries;
        private String trend;

        // ✅ AI 결과
        private Double aiScore;        // 0~100
        private String aiJudgeCode;    // STRONG_RECO / RECO / CAUTION / WEAK_RECO
        private String aiSummary;
        private List<String> aiReasons;
        private Map<String, Double> aiBreakdown; // dist/price/area/rating/trend 등
    }

    @Data
    public static class CompareResponse {
        private BaseListing base;
        private List<CandidateListing> candidates;
        private Integer usedRadiusM;

        // ✅ AI 상태 메타
        private Boolean aiEnabled;
        private String aiModel;
        private String aiError;
    }
}
