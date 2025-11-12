package com.codingnomads.demo_converter.converters;

import com.codingnomads.demo_converter.models.ImperialDTO;
import com.codingnomads.demo_converter.models.MetricDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class MetricToImperialConverter implements Converter<MetricDTO, ImperialDTO> {

    @Override
    public ImperialDTO convert(MetricDTO source) {
        return new ImperialDTO((source.getCelsius() * 9 / 5) + 32);
    }

}
