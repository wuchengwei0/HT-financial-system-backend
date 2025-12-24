package com.example.scaffold.service.impl;

import com.example.scaffold.mapper.AssetMapper;
import com.example.scaffold.mapper.DistributionAllocationMapper;
import com.example.scaffold.model.Asset;
import com.example.scaffold.model.DistributionAllocation;
import com.example.scaffold.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private AssetMapper assetMapper;

    @Autowired
    private DistributionAllocationMapper distributionAllocationMapper;

    @Override
    public Map<String, Object> getMetrics() {
        List<Asset> assets = assetMapper.selectList(null);

        double totalAssets = assets.stream()
                .mapToDouble(a -> Optional.ofNullable(a.getCurrentPrice()).orElse(0.0) *
                        Optional.ofNullable(a.getPosition()).orElse(0))
                .sum();

        double dailyPnL = assets.stream()
                .mapToDouble(a -> Optional.ofNullable(a.getDailyGain()).orElse(0.0))
                .sum();

        double totalPnL = assets.stream()
                .mapToDouble(a -> Optional.ofNullable(a.getTotalGain()).orElse(0.0))
                .sum();

        double avgSharpeRatio = assets.stream()
                .mapToDouble(a -> Optional.ofNullable(a.getSharpeRatio()).orElse(0.0))
                .average()
                .orElse(0.0);

        double avgMaxDrawdown = assets.stream()
                .mapToDouble(a -> Optional.ofNullable(a.getMaxDrawdown()).orElse(0.0))
                .average()
                .orElse(0.0);

        double avgVolatility = assets.stream()
                .mapToDouble(a -> Optional.ofNullable(a.getVolatility()).orElse(0.0))
                .average()
                .orElse(0.0);

        double avgBeta = assets.stream()
                .mapToDouble(a -> Optional.ofNullable(a.getBeta()).orElse(0.0))
                .average()
                .orElse(0.0);

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalAssets", totalAssets);
        metrics.put("dailyPnL", dailyPnL);
        metrics.put("totalPnL", totalPnL);
        metrics.put("sharpeRatio", Math.round(avgSharpeRatio * 100.0) / 100.0);
        metrics.put("maxDrawdown", Math.round(avgMaxDrawdown * 100.0) / 100.0);
        metrics.put("volatility", Math.round(avgVolatility * 100.0) / 100.0);
        metrics.put("beta", Math.round(avgBeta * 100.0) / 100.0);
        metrics.put("alpha", 0.05);
        metrics.put("winRate", 65.5);
        metrics.put("avgReturn", 1.25);

        return metrics;
    }

    @Override
    public List<Map<String, Object>> getAllocation() {
        // 从 distribution_allocation 表读取数据
        List<DistributionAllocation> list = distributionAllocationMapper.selectList(null);

        return list.stream()
                .map(da -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("category", da.getCategory());
                    item.put("value", da.getValue());
                    item.put("color", da.getColor());
                    return item;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getPerformance(String range) {
        List<Map<String, Object>> data = new ArrayList<>();

        // 模拟数据
        for (int i = 1; i <= 12; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("date", String.format("2024-%02d", i));
            item.put("portfolio", 100 + i * 2.5);
            item.put("benchmark", 100 + i * 2.0);
            data.add(item);
        }

        return data;
    }
}

