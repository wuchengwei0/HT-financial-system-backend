package com.example.scaffold.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.scaffold.mapper.UserMapper;
import com.example.scaffold.model.User;
import com.example.scaffold.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}

