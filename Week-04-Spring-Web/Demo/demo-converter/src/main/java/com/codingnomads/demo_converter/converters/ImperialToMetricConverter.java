package com.codingnomads.demo_converter.converters;

import com.codingnomads.demo_converter.models.ImperialDTO;
import com.codingnomads.demo_converter.models.MetricDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ImperialToMetricConverter implements Converter<ImperialDTO, MetricDTO> {
    @Override
    public MetricDTO convert(ImperialDTO source) {
        return new MetricDTO((source.getFahrenheit() - 32) * 5 / 9);
    }
}
