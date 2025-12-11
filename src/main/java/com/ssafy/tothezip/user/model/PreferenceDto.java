package com.ssafy.tothezip.user.model;

import lombok.Data;

import java.util.List;

@Data
public class PreferenceDto {
    private List<Integer> tagIds;
}
