package com.ssafy.tothezip.region.controller;

import com.ssafy.tothezip.region.model.RegionDto;
import com.ssafy.tothezip.region.model.mapper.RegionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/regions")
@RequiredArgsConstructor
public class RegionController {

    private final RegionMapper regionMapper;

    @GetMapping("/sidos")
    public List<String> sidos() {
        return regionMapper.selectSidos();
    }

    @GetMapping("/guguns")
    public List<String> guguns(@RequestParam String sido) {
        return regionMapper.selectGuguns(sido);
    }

    @GetMapping("/dongs")
    public List<RegionDto.Dong> dongs(@RequestParam String sido,
                                      @RequestParam String gugun) {
        return regionMapper.selectDongs(sido, gugun);
    }
}
