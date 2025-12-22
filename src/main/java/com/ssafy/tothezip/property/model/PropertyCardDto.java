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

    private boolean isLiked = false;
}
