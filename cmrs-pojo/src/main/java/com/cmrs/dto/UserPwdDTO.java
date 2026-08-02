package com.cmrs.dto;

import lombok.Data;

@Data
public class UserPwdDTO {
    private Long id;

    private String oldPassword;

    private String newPassword;
}
