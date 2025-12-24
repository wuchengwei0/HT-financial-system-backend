package com.example.scaffold.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.scaffold.model.User;
import com.example.scaffold.model.dto.LoginRequest;
import com.example.scaffold.model.dto.LoginResponse;
import com.example.scaffold.model.dto.UserInfoResponse;
import com.example.scaffold.service.AuthService;
import com.example.scaffold.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {
    
    @Autowired
    private UserService userService;
    
    @Override
    public LoginResponse login(LoginRequest request) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", request.getUsername());
        wrapper.eq("password", request.getPassword());
        User user = userService.getOne(wrapper);
        
        if (user == null) {
            LoginResponse response = new LoginResponse();
            response.setSuccess(false);
            response.setMessage("用户名或密码错误");
            return response;
        }
        
        // 更新最后登录时间
        user.setLastLogin(new Date());
        userService.updateById(user);
        
        LoginResponse response = new LoginResponse();
        response.setSuccess(true);
        response.setMessage("登录成功");
        
        
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setName(user.getName());
        userInfo.setRole(user.getRole());
        response.setUser(userInfo);

        // 生成简单的token（实际项目中应使用JWT）
        String token = "Bearer_" + UUID.randomUUID().toString().replace("-", "") + ":" + user.getUsername();
        response.setToken(token);
        
        return response;
    }
    
    @Override
    public UserInfoResponse getUserInfo(String token) {
        String username = null;
        if (token.startsWith("Bearer_")) {
            // 去掉Bearer_前缀
            String tokenWithoutPrefix = token.substring(7);

            // 分割token获取username部分
            String[] parts = tokenWithoutPrefix.split(":");
            if (parts.length >= 2) {
                // username是最后一部分
                username = parts[parts.length - 1];
            }
        }else{
            return null;
        }
        
        
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User user = userService.getOne(wrapper);
        
        if (user == null) {
            return null;
        }
        
        UserInfoResponse response = new UserInfoResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setName(user.getName());
        response.setRole(user.getRole());
        response.setLastLogin(user.getLastLogin());
        
        return response;
    }
}

