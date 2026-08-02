package com.cmrs.controller;

import com.cmrs.dto.UserDTO;
import com.cmrs.dto.UserPwdDTO;
import com.cmrs.exception.BaseException;
import com.cmrs.result.Result;
import com.cmrs.service.PersonalCenterService;
import com.cmrs.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/personalCenter")
@Slf4j
@Tag(name = "用户/维修人员端-个人中心接口")
public class PersonalCenterController {
    @Autowired
    private PersonalCenterService personalCenterService;

    /**
     * 根据ID查询普通用户的个人信息
     * @return
     */
    @GetMapping("/display1/{id}")
    @Operation(summary = "普通用户的个人信息展示")
    public Result<UserVO> displayNormalUser(@PathVariable long id) {
        log.info("根据ID查询普通用户的信息：{}",id);
        UserVO userVO = personalCenterService.displayNormalUser(id);
        return Result.success(userVO);
    }

    /**
     * 根据ID查询管理员或维修人员的个人信息
     * @return
     */
    @GetMapping("/display2/{id}")
    @Operation(summary = "管理员或维修人员的个人信息展示")
    public Result<UserVO> displayAdminOrRepairman(@PathVariable long id) {
        log.info("根据ID查询管理员或维修人员信息：{}",id);
        UserVO userVO = personalCenterService.displayAdminOrRepairman(id);
        return Result.success(userVO);
    }



    /**
     * 编辑个人信息
     * @param userDTO
     * @return
     */
    @PutMapping("/user")
    @Operation(summary = "编辑普通用户个人信息")
    public Result<UserVO> updateUser(@RequestBody UserDTO userDTO) {
        log.info("编辑普通用户个人信息：{}",userDTO);
        personalCenterService.updateUser(userDTO);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userDTO,userVO);
        return Result.success(userVO);
    }


    /**
     * 编辑个人信息
     * @param userDTO
     * @return
     */
    @PutMapping("/repairman")
    @Operation(summary = "编辑维修人员个人信息")
    public Result<UserVO> updateRepairman(@RequestBody UserDTO userDTO) {
        log.info("编辑维修人员个人信息：{}",userDTO);
        personalCenterService.updateRepairman(userDTO);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userDTO,userVO);
        return Result.success(userVO);
    }


    /**
     * 普通用户修改密码
     * @param userPwdDTO
     * @return
     */
    @PutMapping("/editPwd1")
    @Operation(summary = "修改密码")
    public Result editPwd(@RequestBody UserPwdDTO userPwdDTO) {
        try {
            log.info("用户ID和新密码信息：{}", userPwdDTO);
            personalCenterService.editPwd1(userPwdDTO);
            return Result.success();
        }catch (BaseException be){
            return Result.error(be.getMessage());
        }
    }

    /**
     * 管理员或维修人员修改密码
     * @param userPwdDTO
     * @return
     */
    @PutMapping("/editPwd2")
    @Operation(summary = "修改密码")
    public Result editPwd2(@RequestBody UserPwdDTO userPwdDTO) {
        try {
            log.info("用户ID和新密码信息：{}", userPwdDTO);
            personalCenterService.editPwd2(userPwdDTO);
            return Result.success();
        }catch (BaseException be){
            return Result.error(be.getMessage());
        }
    }
}
