package com.example.scaffold.service;

import com.example.scaffold.model.MonthlyReturn;
import com.example.scaffold.model.PortfolioTrend;

import java.util.List;
import java.util.Map;

public interface TrendService {
    List<PortfolioTrend> getPortfolioTrend(String range);
    List<MonthlyReturn> getMonthlyReturns(String range);
    Map<String, Object> getStats();
}

