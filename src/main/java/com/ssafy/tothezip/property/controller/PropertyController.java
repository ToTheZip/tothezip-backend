package com.ssafy.tothezip.property.controller;

import com.ssafy.tothezip.property.model.service.PropertyService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/property")
@AllArgsConstructor
@Slf4j
public class PropertyController {

    private PropertyService propertyService;


}
