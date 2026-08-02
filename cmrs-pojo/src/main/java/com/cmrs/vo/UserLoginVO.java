package com.cmrs.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLoginVO implements Serializable {
    private Long id;        //主键值

    private String username;    //用户账户

    private String nickname;    //用户姓名

    private Integer authorityId;  //用户权限

    private String token;
}
