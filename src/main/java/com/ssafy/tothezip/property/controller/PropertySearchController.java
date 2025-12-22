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

    @PostMapping("/search")
    public List<PropertySearchDto.BuildingCard> search(
            @RequestBody PropertySearchDto.SearchRequest req
    ) {
        return propertySearchService.searchBuildings(req);
    }

    @GetMapping("/autocomplete")
    public List<PropertySearchDto.AptItem> searchAutoComplete(
            @RequestParam String query,
            @RequestParam(required = false) String sido,
            @RequestParam(required = false) String gugun,
            @RequestParam(required = false) String dong
    ) {
        return propertySearchService.search(query, sido, gugun, dong);
    }
}
