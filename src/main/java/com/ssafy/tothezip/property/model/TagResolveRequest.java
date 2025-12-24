package com.ssafy.tothezip.property.model;

import lombok.Data;

import java.util.List;

@Data
public class TagResolveRequest {
    private List<Integer> tagIds;
}
