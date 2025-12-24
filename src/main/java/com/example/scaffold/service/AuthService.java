package com.example.scaffold.service;

import com.example.scaffold.model.dto.LoginRequest;
import com.example.scaffold.model.dto.LoginResponse;
import com.example.scaffold.model.dto.UserInfoResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
//    UserInfoResponse getUserInfo(String username);
    UserInfoResponse getUserInfo(String token);
}

