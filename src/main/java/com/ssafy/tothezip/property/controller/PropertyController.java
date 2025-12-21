package com.ssafy.tothezip.property.controller;

import com.ssafy.tothezip.property.model.PropertyDto;
import com.ssafy.tothezip.property.model.service.PropertyService;
import com.ssafy.tothezip.security.CustomUserDetails;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
