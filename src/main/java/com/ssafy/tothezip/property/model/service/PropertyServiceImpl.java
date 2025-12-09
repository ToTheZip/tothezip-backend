package com.ssafy.tothezip.property.model.service;

import com.ssafy.tothezip.property.model.mapper.PropertyMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PropertyServiceImpl implements PropertyService {

    private PropertyMapper propertyMapper;


}
