package com.ssafy.tothezip.property.controller;

import com.ssafy.tothezip.property.model.PropertyDto;
import com.ssafy.tothezip.property.model.RegionDto;
import com.ssafy.tothezip.property.model.TagDto;
import com.ssafy.tothezip.property.model.service.PropertyService;
import com.ssafy.tothezip.security.CustomUserDetails;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/property")
@AllArgsConstructor
@Slf4j
public class PropertyController {

    private final PropertyService propertyService;

    @GetMapping("/recommendations")
    public ResponseEntity<PropertyDto.RecommendationsProperty> recommendations(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer userId = (userDetails != null)
                ? userDetails.getUser().getUserId()
                : null;
        return ResponseEntity.ok(propertyService.getHomeRecommendations(userId));
    }

    // 시/도 목록 (광역시, 특별시, 도 전부)
    @GetMapping("/regions/sido")
    public ResponseEntity<List<String>> getSidoList() {
        return ResponseEntity.ok(propertyService.getSidoList());
    }

    // 구/군 목록
    @GetMapping("/regions/gugun")
    public ResponseEntity<List<String>> getGugunList(
            @RequestParam String sido
    ) {
        return ResponseEntity.ok(propertyService.getGugunList(sido));
    }

    // 태그 조회 (주변시설 등)
    @GetMapping("/tags")
    public ResponseEntity<List<TagDto>> getTags(
            @RequestParam(required = false) String type
    ) {
        return ResponseEntity.ok(propertyService.getTagList(type));
    }
}
