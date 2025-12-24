package com.example.scaffold.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.scaffold.mapper.MonthlyReturnMapper;
import com.example.scaffold.mapper.PortfolioTrendMapper;
import com.example.scaffold.model.MonthlyReturn;
import com.example.scaffold.model.PortfolioTrend;
import com.example.scaffold.service.TrendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TrendServiceImpl implements TrendService {
    
    @Autowired
    private PortfolioTrendMapper portfolioTrendMapper;
    
    @Autowired
    private MonthlyReturnMapper monthlyReturnMapper;
    
    @Override
    public List<PortfolioTrend> getPortfolioTrend(String range) {
        QueryWrapper<PortfolioTrend> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("date");
        return portfolioTrendMapper.selectList(wrapper);
    }
    
    @Override
    public List<MonthlyReturn> getMonthlyReturns(String range) {
        QueryWrapper<MonthlyReturn> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("`month`");
        return monthlyReturnMapper.selectList(wrapper);
    }
    
    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalReturn", 35.0);
        stats.put("benchmarkReturn", 28.0);
        stats.put("alpha", 7.0);
        stats.put("beta", 0.95);
        stats.put("sharpeRatio", 1.8);
        stats.put("informationRatio", 0.9);
        return stats;
    }
}

