package com.cmrs.service;

import com.cmrs.dto.AdminPageQueryDTO;
import com.cmrs.dto.RepairmanPageQueryDTO;
import com.cmrs.dto.UserDTO;
import com.cmrs.dto.UserPageQueryDTO;
import com.cmrs.result.PageResult;

public interface UserCenterService {
    /**
     * 管理员分页查询
     */
    PageResult adminPageQuery(AdminPageQueryDTO adminPageQueryDTO);

    /**
     * 维修人员员分页查询
     */
    PageResult repairmanPageQuery(RepairmanPageQueryDTO repairmanPageQueryDTO);

    /**
     * 注册用户分页查询
     */
    PageResult userPageQuery(UserPageQueryDTO userPageQueryDTO);

    /**
     * 添加普通用户
     */
    void addUser1(UserDTO userDTO);

    /**
     * 添加管理员或维修人员
     */
    void addUser2(UserDTO userDTO);


    /**
     * 编辑普通用户信息
     */
    void updateNormalUser(UserDTO userDTO);

    /**
     * 编辑管理员或维修人员信息
     */
    void updateAdminOrRepairman(UserDTO userDTO);


    /**
     * 启用或禁用普通用户账户
     */
    void starOrStopNormalUser(Integer status, Long id);

    /**
     * 启用禁用普通管理员或维修人员账号
     */
    void starOrStopAdminOrRepairman(Integer status, Long id);


}
