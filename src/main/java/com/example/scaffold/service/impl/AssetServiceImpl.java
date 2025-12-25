package com.example.scaffold.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.scaffold.mapper.AssetMapper;
import com.example.scaffold.mapper.AssetTrendMapper;
import com.example.scaffold.model.Asset;
import com.example.scaffold.model.AssetTrend;
import com.example.scaffold.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetServiceImpl extends ServiceImpl<AssetMapper, Asset> implements AssetService {
    
    @Autowired
    private AssetTrendMapper assetTrendMapper;
    
    @Override
    public Asset getAssetDetailById(Long id) {
        Asset asset = this.getById(id);
        if (asset != null) {
            // 查询该资产的趋势数据
            QueryWrapper<AssetTrend> wrapper = new QueryWrapper<>();
            wrapper.eq("asset_id", id);
            wrapper.orderByAsc("date");
            List<AssetTrend> trends = assetTrendMapper.selectList(wrapper);
            asset.setTend(trends);
        }
        return asset;
    }
}

