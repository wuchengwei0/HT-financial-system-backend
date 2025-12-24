package com.example.scaffold.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.scaffold.mapper.AssetMapper;
import com.example.scaffold.model.Asset;
import com.example.scaffold.service.AssetService;
import org.springframework.stereotype.Service;

@Service
public class AssetServiceImpl extends ServiceImpl<AssetMapper, Asset> implements AssetService {
}

