package com.ssafy.tothezip.property.model;

import lombok.Data;

@Data
public class PropertyDto {
    // 건물 정보 (property_infos)
    private String aptSeq;
    private String aptNm;
    private String sggCd;
    private String umdCd;
    private String jibun;
    private String roadNm;
    private String roadBun;
    private double latitude;
    private double longitude;
    // private Year buildYear; 우얄까요
    private int buildYear;
}
