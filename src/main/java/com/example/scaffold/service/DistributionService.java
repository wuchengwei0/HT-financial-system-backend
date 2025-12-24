package com.example.scaffold.service;

import com.example.scaffold.model.DistributionAllocation;
import com.example.scaffold.model.DistributionHistorical;

import java.util.List;
import java.util.Map;

public interface DistributionService {
    List<DistributionAllocation> getAllocation(String category, String range);
    List<DistributionHistorical> getHistorical();
    Map<String, Object> getStats();
}

