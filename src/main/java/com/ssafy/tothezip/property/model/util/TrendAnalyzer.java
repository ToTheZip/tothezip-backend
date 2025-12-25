package com.ssafy.tothezip.property.model.util;

import com.ssafy.tothezip.property.model.PropertyCompareDto;

import java.util.List;

public class TrendAnalyzer {

    public static String analyze(List<PropertyCompareDto.PricePoint> series) {
        if (series == null || series.size() < 2) {
            return "UNKNOWN";
        }

        Double first = toDouble(series.get(0).getAmount());
        Double last  = toDouble(series.get(series.size() - 1).getAmount());

        if (first == null || last == null || first == 0.0) {
            return "UNKNOWN";
        }

        if (last > first * 1.05) return "UP";
        if (last < first * 0.95) return "DOWN";
        return "FLAT";
    }

    /** 문자열 금액 → double (콤마/공백/원 제거) */
    private static Double toDouble(String s) {
        if (s == null) return null;
        String onlyNum = s.replaceAll("[^0-9]", "");
        if (onlyNum.isBlank()) return null;
        try {
            return Double.parseDouble(onlyNum);
        } catch (Exception e) {
            return null;
        }
    }
}
