package com.cmrs.service.impl;

import com.cmrs.constant.MessageConstant;
import com.cmrs.constant.StatusConstant;
import com.cmrs.dto.UserLoginDTO;
import com.cmrs.dto.UserRegisterDTO;
import com.cmrs.entity.User;
import com.cmrs.exception.AccountLockedException;
import com.cmrs.exception.AccountNotFoundException;
import com.cmrs.exception.PasswordErrorException;
import com.cmrs.mapper.UserMapper;
import com.cmrs.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;



@Service
@Slf4j
public class LoginServiceImpl implements LoginService {
    @Autowired
    private UserMapper userMapper;

    /**
     * 用户登录
     * @param userLoginDTO
     * @return
     */
    public User login(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();
        String authority = userLoginDTO.getAuthority();

        User user = new User();
        //1、根据用户名查询数据库中的数据
        if(authority.equals("adminOrRepman")){
            //user2表，管理员和维修人员
            user = userMapper.getByUsername2(username);
        }else if(authority.equals("user")){
            //user1表，普通用户
            user = userMapper.getByUsername1(username);
        }


        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (user == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        //对前端传过来的明文密码进行md5加密处理
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(user.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (user.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }
        //3、返回实体对象
        return user;
    }


    /**
     * 用户注册
     * @param registerDTO
     * @return
     */
    public void register(UserRegisterDTO registerDTO) {
        String username = registerDTO.getUsername();
        String password = registerDTO.getPassword();

        //注册的都为普通用户
        //根据用户名查询数据库中的数据
        User user = userMapper.getByUsername1(username);

        //处理异常情况
        if (user == null) {
            //md5加密
            registerDTO.setPassword( DigestUtils.md5DigestAsHex(password.getBytes()) );

            User temp = new User();
            BeanUtils.copyProperties(registerDTO,temp);
            temp.setStatus(StatusConstant.ENABLE);
            userMapper.insertNormalUser(temp);
        }else{
            //账号已存在
            throw new AccountNotFoundException(MessageConstant.ALREADY_EXISTS);
        }
    }
}
