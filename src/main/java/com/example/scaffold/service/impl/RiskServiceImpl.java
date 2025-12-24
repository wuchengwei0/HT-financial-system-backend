package com.example.scaffold.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.scaffold.mapper.RiskDrawdownMapper;
import com.example.scaffold.mapper.RiskEventMapper;
import com.example.scaffold.model.RiskDrawdown;
import com.example.scaffold.model.RiskEvent;
import com.example.scaffold.service.RiskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RiskServiceImpl implements RiskService {
    
    @Autowired
    private RiskEventMapper riskEventMapper;
    
    @Autowired
    private RiskDrawdownMapper riskDrawdownMapper;
    
    @Override
    public Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("var95", 2.8);
        metrics.put("cvar95", 4.3);
        metrics.put("stressTestLoss", 12.5);
        metrics.put("liquidityRisk", 3.2);
        metrics.put("concentrationRisk", 18.5);
        metrics.put("creditRisk", 2.1);
        return metrics;
    }
    
    @Override
    public List<RiskDrawdown> getDrawdown(Integer days) {
        if (days == null) {
            days = 365;
        }

        // 获取当前精确时间
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_MONTH, -days);
        Date startDate = calendar.getTime();
        QueryWrapper<RiskDrawdown> wrapper = new QueryWrapper<>();
        wrapper.ge("date", startDate);
        wrapper.le("date", now);
        wrapper.orderByAsc("date");
        return riskDrawdownMapper.selectList(wrapper);
    }
    
    @Override
    public List<RiskEvent> getEvents() {
        QueryWrapper<RiskEvent> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("event_date");
        return riskEventMapper.selectList(wrapper);
    }
}

