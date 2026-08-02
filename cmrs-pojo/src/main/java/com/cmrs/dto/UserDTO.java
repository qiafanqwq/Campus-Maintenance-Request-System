package com.cmrs.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String email;

    private String phone;

    private Integer AuthorityId;
}
