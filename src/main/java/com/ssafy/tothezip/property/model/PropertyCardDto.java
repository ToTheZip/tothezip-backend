package com.ssafy.tothezip.property.model;

import lombok.Data;
import java.util.List;

@Data
public class PropertyCardDto {
    private String aptSeq;
    private String aptName;
    private String roadAddress;
    private Double propertyRating;
    private List<String> tags;
    private String imageUrl;

    private Double latitude;
    private Double longitude;
    private Integer buildYear;

    private String minDealType;
    private Long minPrice;
    private Long minDeposit;

    private boolean isLiked = false;
}
