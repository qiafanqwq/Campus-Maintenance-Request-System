package com.cmrs.service;

import com.cmrs.dto.UserDTO;
import com.cmrs.dto.UserPwdDTO;
import com.cmrs.vo.UserVO;

public interface PersonalCenterService {
    /**
     * 根据ID查询普通用户的个人信息
     */
    UserVO displayNormalUser(long id);

    /**
     * 根据ID查询管理员或维修人员的个人信息
     */
    UserVO displayAdminOrRepairman(long id);


    /**
     * 编辑普通用户个人信息
     */
    void updateUser(UserDTO userDTO);

    /**
     * 编辑维修人员个人信息
     */
    void updateRepairman(UserDTO userDTO);


    /**
     * 普通用户修改密码
     */
    void editPwd1(UserPwdDTO userPwdDTO);

    /**
     * 管理员或维修人员修改密码
     */
    void editPwd2(UserPwdDTO userPwdDTO);
}
