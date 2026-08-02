package com.cmrs.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVO implements Serializable {
    private Long id;

    private String username;

    private String nickname;

    private String phone;

    private String email;

    private Integer status;

    private Integer AuthorityId;
}
