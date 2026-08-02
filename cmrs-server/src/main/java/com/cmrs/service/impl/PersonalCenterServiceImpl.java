package com.cmrs.service.impl;

import com.cmrs.constant.MessageConstant;
import com.cmrs.dto.UserDTO;
import com.cmrs.dto.UserPwdDTO;
import com.cmrs.entity.RepairOrder;
import com.cmrs.entity.RepairRecord;
import com.cmrs.entity.User;
import com.cmrs.exception.PasswordErrorException;
import com.cmrs.mapper.RepairOrderMapper;
import com.cmrs.mapper.RepairRecordMapper;
import com.cmrs.mapper.UserMapper;
import com.cmrs.service.PersonalCenterService;
import com.cmrs.vo.RepairOrderVO;
import com.cmrs.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;

@Service
public class PersonalCenterServiceImpl implements PersonalCenterService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RepairOrderMapper repairOrderMapper;
    @Autowired
    private RepairRecordMapper repairRecordMapper;

    /**
     * 根据ID查询普通用户的个人信息
     * @return
     */
    public UserVO displayNormalUser(long id) {
        return userMapper.getNormalUserById2(id);
    }

    /**
     * 根据ID查询管理员或维修人员的个人信息
     * @return
     */
    public UserVO displayAdminOrRepairman(long id) {
        return userMapper.getAdminOrRepairmanById2(id);
    }



    /**
     * 编辑个人信息后，订单表所记录的用户个人信息也得变（普通用户）
     * @param userDTO
     * @return
     */
    public void updateUser(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        userMapper.updateNormalUser(user);
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
     * 编辑个人信息后，维修记录表所记录的用户个人信息也得变（维修人员）
     * @param userDTO
     * @return
     */
    public void updateRepairman(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        userMapper.updateAdminOrRepairman(user);
        List<RepairRecord> list = repairRecordMapper.getByRepairmanId(user.getId());
        for (RepairRecord repairRecord : list) {
            repairRecord.setRepairmanName(user.getNickname());
            repairRecordMapper.update(repairRecord);
        }

    }


    /**
     * 普通用户修改密码
     * @param userPwdDTO
     * @return
     */
    public void editPwd1(UserPwdDTO userPwdDTO) {
        User user = userMapper.getNormalUserById(userPwdDTO.getId());
        String oldPwd = userPwdDTO.getOldPassword();
        String newPwd = userPwdDTO.getNewPassword();
        oldPwd = DigestUtils.md5DigestAsHex(oldPwd.getBytes());
        newPwd = DigestUtils.md5DigestAsHex(newPwd.getBytes());
        //匹配旧密码是否和数据库表中的密码一致
        if( !user.getPassword().equals(oldPwd)) {
            throw new PasswordErrorException(MessageConstant.OLDPASSWORD_MATCH_FAILED);
        }
        //匹配新密码是否和数据库表中的密码一致
        if( user.getPassword().equals(newPwd)) {
            throw new PasswordErrorException(MessageConstant.NEWPASSWORD_FAILED);
        }
        user.setPassword(newPwd);
        userMapper.updateNormalUser(user);
    }

    /**
     * 管理员或维修人员修改密码
     * @param userPwdDTO
     * @return
     */
    public void editPwd2(UserPwdDTO userPwdDTO) {
        User user = userMapper.getAdminOrRepairmanById(userPwdDTO.getId());
        String oldPwd = userPwdDTO.getOldPassword();
        String newPwd = userPwdDTO.getNewPassword();
        oldPwd = DigestUtils.md5DigestAsHex(oldPwd.getBytes());
        newPwd = DigestUtils.md5DigestAsHex(newPwd.getBytes());
        //匹配旧密码是否和数据库表中的密码一致
        if( !user.getPassword().equals(oldPwd)) {
            throw new PasswordErrorException(MessageConstant.OLDPASSWORD_MATCH_FAILED);
        }
        //匹配新密码是否和数据库表中的密码一致
        if( user.getPassword().equals(newPwd)) {
            throw new PasswordErrorException(MessageConstant.NEWPASSWORD_FAILED);
        }
        user.setPassword(newPwd);
        userMapper.updateAdminOrRepairman(user);
    }
}
