package com.codingnomads.demo_converter.controllers;

import com.codingnomads.demo_converter.models.ImperialDTO;
import com.codingnomads.demo_converter.models.MetricDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ConverterController {

    private final ConversionService conversionService;

    @PostMapping("/metric-to-imperial")
    public ImperialDTO convert(@RequestBody MetricDTO metricDTO) {
        return conversionService.convert(metricDTO, ImperialDTO.class);
    }

    @PostMapping("/imperial-to-metric")
    public MetricDTO convertImperial(@RequestBody ImperialDTO imperialDTO) {
        return conversionService.convert(imperialDTO, MetricDTO.class);
    }

}
