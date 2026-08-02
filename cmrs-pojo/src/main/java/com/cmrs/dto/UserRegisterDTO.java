package com.cmrs.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRegisterDTO implements Serializable {
    //前端注册页面传来的数据
    private String username;

    private String password;

    private String nickname;

    private String phone;
}
