package com.example.scaffold.service;

import java.util.List;
import java.util.Map;

public interface DashboardService {
    Map<String, Object> getMetrics(String dateFrom, String dateTo);
    List<Map<String, Object>> getAllocation(String dateFrom, String dateTo);
    List<Map<String, Object>> getPerformance(String dateFrom, String dateTo);
}

