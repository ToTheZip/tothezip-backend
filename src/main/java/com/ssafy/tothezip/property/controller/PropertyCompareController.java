package com.ssafy.tothezip.property.controller;

import com.ssafy.tothezip.property.model.PropertyCompareDto;
import com.ssafy.tothezip.property.model.service.PropertyCompareService;
import com.ssafy.tothezip.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/property/listings")
@RequiredArgsConstructor
public class PropertyCompareController {

    private final PropertyCompareService propertyCompareService;

    @GetMapping("/{propertyId}/comparisons")
    public ResponseEntity<PropertyCompareDto.CompareResponse> getComparisons(
            @PathVariable Integer propertyId,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int explain, // ✅ 추가
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer userId = (userDetails != null) ? userDetails.getUser().getUserId() : null;
        boolean doExplain = (explain == 1);
        return ResponseEntity.ok(propertyCompareService.getComparisons(propertyId, userId, limit));
    }
}
