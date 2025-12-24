package com.example.scaffold.service;

import java.util.List;
import java.util.Map;

public interface DashboardService {
    Map<String, Object> getMetrics();
    List<Map<String, Object>> getAllocation();
    List<Map<String, Object>> getPerformance(String range);
}

