package com.cmrs.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    private Long id;

    //账户：学号/工号
    private String username;

    //用户密码
    private String password;

    //姓名
    private String nickname;

    //邮箱
    private String email;

    //电话号码
    private String phone;

    //账户状态：1-正常、0-禁用
    private Integer status;

    //账户权限：1-超级管理员、2-维修人员、0-普通用户、3-普通管理员
    private Integer AuthorityId;

    //创建时间
    private LocalDateTime createTime;

    //更新时间
    private LocalDateTime updateTime;
}
