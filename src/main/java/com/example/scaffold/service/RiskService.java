package com.example.scaffold.service;

import com.example.scaffold.model.RiskDrawdown;
import com.example.scaffold.model.RiskEvent;

import java.util.List;
import java.util.Map;

public interface RiskService {
    Map<String, Object> getMetrics();
    List<RiskDrawdown> getDrawdown(Integer days);
    List<RiskEvent> getEvents();
}

