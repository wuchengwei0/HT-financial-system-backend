package com.example.scaffold.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.scaffold.model.Asset;

public interface AssetService extends IService<Asset> {
    /**
     * 根据ID获取资产详情（包含趋势数据）
     * @param id 资产ID
     * @return 资产对象（包含趋势数据）
     */
    Asset getAssetDetailById(Long id);
}

