package com.cmrs.mapper;

import com.cmrs.annotation.AutoFill;
import com.cmrs.dto.AdminPageQueryDTO;
import com.cmrs.dto.RepairmanPageQueryDTO;
import com.cmrs.dto.UserPageQueryDTO;
import com.cmrs.entity.User;
import com.cmrs.enumeration.OperationType;
import com.cmrs.vo.UserVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {
    /**
     * 根据用户账户查询普通用户
     */
    @Select("select * from user1 where username = #{username}")
    User getByUsername1(String username);

    /**
     * 根据用户账户查询管理员或维修人员
     */
    @Select("select * from user2 where username = #{username}")
    User getByUsername2(String username);


    /**
     * 管理员分页查询
     */
    Page<UserVO> adminPageQuery(AdminPageQueryDTO adminPageQueryDTO);

    /**
     * 维修人员分页查询
     */
    Page<UserVO> repairmanPageQuery(RepairmanPageQueryDTO repairmanPageQueryDTO);

    /**
     * 注册用户分页查询
     */
    Page<UserVO> userPageQuery(UserPageQueryDTO userPageQueryDTO);


    /**
     * 插入用户数据(添加普通用户)
     */
    @Insert("insert into user1 (username, password, nickname, email, phone, status, create_time, update_time)" +
            "values" +
            "(#{username},#{password},#{nickname},#{email},#{phone},#{status},#{createTime},#{updateTime})")
    @AutoFill(value = OperationType.INSERT)
    void insertNormalUser(User user);


    /**
     * 插入用户数据(添加管理员或维修人员)
     */
    @Insert("insert into user2 (username, password, nickname, email, phone, status, authority_id, create_time, update_time)" +
            "values" +
            "(#{username},#{password},#{nickname},#{email},#{phone},#{status},#{AuthorityId},#{createTime},#{updateTime})")
    @AutoFill(value = OperationType.INSERT)
    void insertAdminOrRepairman(User user);


    /**
     * 根据主键动态修改属性
     */
    @AutoFill(value = OperationType.UPDATE)
    void updateNormalUser(User user);

    /**
     * 根据主键动态修改属性
     */
    @AutoFill(value = OperationType.UPDATE)
    void updateAdminOrRepairman(User user);


    /**
     * 根据用户姓名查询普通用户
     */
    @Select("select * from user1 where nickname = #{nickname}")
    User getNormalUserByNickname(String nickname);

    /**
     * 根据用户姓名查询管理员或维修人员
     */
    @Select("select * from user2 where nickname = #{nickname}")
    User getAdminOrRepairmanByNickname(String nickname);


    /**
     * 根据ID查询普通用户的个人信息(返回前端)
     */
    UserVO getNormalUserById2(long id);

    /**
     * 根据ID查询管理员或维修人员的个人信息(返回前端)
     */
    UserVO getAdminOrRepairmanById2(long id);


    /**
     * 根据ID查询普通用户的个人信息(后端查询用)
     */
    @Select("select * from user1 where id = #{id}")
    User getNormalUserById(long id);

    /**
     * 根据ID查询管理员或维修人员的个人信息(后端查询用)
     */
    @Select("select * from user2 where id = #{id}")
    User getAdminOrRepairmanById(long id);

    /**
     * 列举维修人员
     */
    List<UserVO> listRepairman();
}
