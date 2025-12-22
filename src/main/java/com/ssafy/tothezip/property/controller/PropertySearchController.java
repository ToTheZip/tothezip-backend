package com.ssafy.tothezip.property.controller;

import com.ssafy.tothezip.property.model.PropertySearchDto;
import com.ssafy.tothezip.property.model.service.PropertySearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/property")
@RequiredArgsConstructor
public class PropertySearchController {

    private final PropertySearchService propertySearchService;

    @GetMapping("/search")
    public List<PropertySearchDto.AptItem> search(
            @RequestParam String query,
            @RequestParam(required = false) String sido,
            @RequestParam(required = false) String gugun,
            @RequestParam(required = false) String dong
    ) {
        return propertySearchService.search(query, sido, gugun, dong);
    }
}
