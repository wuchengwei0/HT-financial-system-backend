package com.example.scaffold.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.scaffold.mapper.DistributionAllocationMapper;
import com.example.scaffold.mapper.DistributionHistoricalMapper;
import com.example.scaffold.model.DistributionAllocation;
import com.example.scaffold.model.DistributionHistorical;
import com.example.scaffold.service.DistributionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DistributionServiceImpl implements DistributionService {
    
    @Autowired
    private DistributionAllocationMapper distributionAllocationMapper;
    
    @Autowired
    private DistributionHistoricalMapper distributionHistoricalMapper;
    
    @Override
    public List<DistributionAllocation> getAllocation(String category, String range) {
        QueryWrapper<DistributionAllocation> wrapper = new QueryWrapper<>();
        if (category != null && !category.isEmpty() && !"all".equals(category)) {
            wrapper.eq("category", category);
        }
        return distributionAllocationMapper.selectList(wrapper);
    }
    
    @Override
    public List<DistributionHistorical> getHistorical() {
        QueryWrapper<DistributionHistorical> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("date");
        return distributionHistoricalMapper.selectList(wrapper);
    }
    
    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAssets", 125000000);
        stats.put("stockPercentage", 65.5);
        stats.put("bondPercentage", 25.3);
        stats.put("cashPercentage", 9.2);
        stats.put("topIndustry", "科技");
        stats.put("topIndustryPercentage", 32.8);
        stats.put("diversificationScore", 8.5);
        return stats;
    }
}

