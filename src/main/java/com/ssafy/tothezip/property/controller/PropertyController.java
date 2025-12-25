package com.ssafy.tothezip.property.controller;

import com.ssafy.tothezip.property.model.*;
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
            @AuthenticationPrincipal CustomUserDetails userDetails) {
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
            @RequestParam String sido) {
        return ResponseEntity.ok(propertyService.getGugunList(sido));
    }

    // 태그 조회 (주변시설 등)
    @GetMapping("/tags")
    public ResponseEntity<List<TagDto>> getTags(
            @RequestParam(required = false) String type) {
        return ResponseEntity.ok(propertyService.getTagList(type));
    }

    @PostMapping("/tags/resolve")
    public ResponseEntity<List<TagDto>> resolveTags(@RequestBody TagResolveRequest req) {
        return ResponseEntity.ok(propertyService.resolveTags(req.getTagIds()));
    }

    @GetMapping("/{aptSeq}/price-series")
    public ResponseEntity<PriceSeriesDto> getPriceSeries(
            @PathVariable String aptSeq,
            @RequestParam(required = false) String dealType,
            @RequestParam(required = false, defaultValue = "month") String period) {
        return ResponseEntity.ok(propertyService.getPriceSeries(aptSeq, dealType, period));
    }

    // 지도 viewport 기반 매물 조회
    @GetMapping("/map-viewport")
    public ResponseEntity<List<PropertyCardDto>> getMapViewportProperties(
            @RequestParam Double minLat,
            @RequestParam Double maxLat,
            @RequestParam Double minLng,
            @RequestParam Double maxLng) {
        List<PropertyCardDto> properties = propertyService.getPropertiesByMapBounds(minLat, maxLat, minLng,
                maxLng);
        return ResponseEntity.ok(properties);
    }

}
