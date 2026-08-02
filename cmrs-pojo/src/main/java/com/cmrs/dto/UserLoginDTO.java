package com.cmrs.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginDTO implements Serializable {
    //前端登录页面传来的数据
    private String username;

    private String password;

    private String authority;
}
