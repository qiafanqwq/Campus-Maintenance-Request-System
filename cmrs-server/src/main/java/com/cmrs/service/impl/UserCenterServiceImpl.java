package com.cmrs.service.impl;

import com.cmrs.constant.StatusConstant;
import com.cmrs.dto.AdminPageQueryDTO;
import com.cmrs.dto.RepairmanPageQueryDTO;
import com.cmrs.dto.UserDTO;
import com.cmrs.dto.UserPageQueryDTO;
import com.cmrs.entity.RepairOrder;
import com.cmrs.entity.RepairRecord;
import com.cmrs.entity.User;
import com.cmrs.mapper.RepairOrderMapper;
import com.cmrs.mapper.RepairRecordMapper;
import com.cmrs.mapper.UserMapper;
import com.cmrs.result.PageResult;
import com.cmrs.service.UserCenterService;
import com.cmrs.vo.RepairOrderVO;
import com.cmrs.vo.UserVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;

@Service
@Slf4j
public class UserCenterServiceImpl implements UserCenterService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RepairOrderMapper repairOrderMapper;
    @Autowired
    private RepairRecordMapper repairRecordMapper;


    /**
     * 管理员分页查询
     * @param adminPageQueryDTO
     * @return
     */
    public PageResult adminPageQuery(AdminPageQueryDTO adminPageQueryDTO) {
        // select * from user where ... limit 0,10
        //开始分页查询
        PageHelper.startPage(adminPageQueryDTO.getPage(),adminPageQueryDTO.getPageSize());
        Page<UserVO> page = userMapper.adminPageQuery(adminPageQueryDTO);

        long total = page.getTotal();
        List<UserVO> users = page.getResult();

        return new PageResult(total,users);
    }

    /**
     * 维修人员分页查询，方法同管理员分页查询大致相同
     * @param repairmanPageQueryDTO
     * @return
     */
    public PageResult repairmanPageQuery(RepairmanPageQueryDTO repairmanPageQueryDTO) {
        PageHelper.startPage(repairmanPageQueryDTO.getPage(),repairmanPageQueryDTO.getPageSize());
        Page<UserVO> page = userMapper.repairmanPageQuery(repairmanPageQueryDTO);
        long total = page.getTotal();
        List<UserVO> users = page.getResult();
        return new PageResult(total,users);
    }

    /**
     * 注册用户分页查询，方法同管理员分页查询大致相同
     * @param userPageQueryDTO
     * @return
     */
    public PageResult userPageQuery(UserPageQueryDTO userPageQueryDTO) {
        PageHelper.startPage(userPageQueryDTO.getPage(),userPageQueryDTO.getPageSize());
        Page<UserVO> page = userMapper.userPageQuery(userPageQueryDTO);
        long total = page.getTotal();
        List<UserVO> users = page.getResult();
        return new PageResult(total,users);
    }

    /**
     * 添加普通用户
     * @param userDTO
     */
    public void addUser1(UserDTO userDTO) {
        User user = new User();
        //对象属性拷贝
        BeanUtils.copyProperties(userDTO,user);

        //设置账号的状态，默认正常状态 1表示正常 0表示锁定
        user.setStatus(StatusConstant.ENABLE);

        //设置密码并用md5加密
        user.setPassword(DigestUtils.md5DigestAsHex( userDTO.getPassword().getBytes() ) );

        userMapper.insertNormalUser(user);
    }

    /**
     * 添加管理员或维修人员
     * @param userDTO
     */
    public void addUser2(UserDTO userDTO) {
        User user = new User();
        //对象属性拷贝
        BeanUtils.copyProperties(userDTO,user);

        //设置账号的状态，默认正常状态 1表示正常 0表示锁定
        user.setStatus(StatusConstant.ENABLE);

        //设置密码并用md5加密
        user.setPassword(DigestUtils.md5DigestAsHex( userDTO.getPassword().getBytes() ) );

        userMapper.insertAdminOrRepairman(user);
    }


    /**
     * 编辑普通用户信息
     * @param userDTO
     */
    public void updateNormalUser(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO,user);
        userMapper.updateNormalUser(user);
        //重新查询普通用户的最新信息
        user = userMapper.getNormalUserById(userDTO.getId());

        //修改订单表中有他信息的订单
        List<RepairOrderVO> list = repairOrderMapper.getByUserId(user.getId());
        RepairOrder repairOrder = new RepairOrder();
        for (RepairOrderVO repairOrderVO : list) {
            repairOrderVO.setNickName(user.getNickname());
            repairOrderVO.setUserPhone(user.getPhone());
            BeanUtils.copyProperties(repairOrderVO, repairOrder);
            repairOrderMapper.update(repairOrder);
        }

    }


    /**
     * 编辑管理员或维修人员信息
     * @param userDTO
     */
    public void updateAdminOrRepairman(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO,user);
        userMapper.updateAdminOrRepairman(user);
        //重新查询用户的最新信息
        user = userMapper.getAdminOrRepairmanById(userDTO.getId());

        if(user.getAuthorityId() == 2){
            //维修人员，就要修改订单表中有他信息的订单
            List<RepairRecord> list = repairRecordMapper.getByRepairmanId(user.getId());
            for (RepairRecord repairRecord : list) {
                repairRecord.setRepairmanName(user.getNickname());
                repairRecordMapper.update(repairRecord);
            }
        }
    }



    /**
     * 启用或禁用普通用户账户
     * @param status
     * @param id
     */
    public void starOrStopNormalUser(Integer status, Long id) {
        User user = User.builder()
                .id(id)
                .status(status)
                .build();
        userMapper.updateNormalUser(user);
    }

    /**
     * 启用禁用普通管理员或维修人员账号
     * @param status
     * @param id
     */
    public void starOrStopAdminOrRepairman(Integer status, Long id) {
        User user = User.builder()
                .id(id)
                .status(status)
                .build();
        userMapper.updateAdminOrRepairman(user);
    }

}
