package com.cmrs.service;

import com.cmrs.dto.UserLoginDTO;
import com.cmrs.dto.UserRegisterDTO;
import com.cmrs.entity.User;

public interface LoginService {
    /**
     * 用户登录
     */
    User login(UserLoginDTO userLoginDTO);

    /**
     * 用户注册
     */
    void register(UserRegisterDTO userLoginDTO);
}
