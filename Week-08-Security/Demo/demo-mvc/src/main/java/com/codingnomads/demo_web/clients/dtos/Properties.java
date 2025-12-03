package com.codingnomads.demo_web.clients.dtos;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Properties {
    private String units;
    private Map<String, Object> elevation;
    private List<Period> periods;

}
