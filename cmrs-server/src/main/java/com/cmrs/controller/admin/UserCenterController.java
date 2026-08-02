package com.cmrs.controller.admin;

import com.cmrs.dto.AdminPageQueryDTO;
import com.cmrs.dto.RepairmanPageQueryDTO;
import com.cmrs.dto.UserDTO;
import com.cmrs.dto.UserPageQueryDTO;
import com.cmrs.result.PageResult;
import com.cmrs.result.Result;
import com.cmrs.service.UserCenterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/userCenter")
@Slf4j
@Tag(name = "用户中心接口", description = "用户中心相关接口")
public class UserCenterController {

    @Autowired
    private UserCenterService userCenterService;

    /**
     * 管理员分页查询
     * @param adminPageQueryDTO
     * @return
     */
    @GetMapping("/pageAdmin")
    @Operation(summary = "管理员分页查询", description = "管理员分页查询接口")
    public Result<PageResult> getAdminPage(@Parameter(description = "管理员分页查询参数") @ParameterObject AdminPageQueryDTO adminPageQueryDTO) {
        log.info("管理员分页查询，参数为:{}", adminPageQueryDTO);
        PageResult pageResult = userCenterService.adminPageQuery(adminPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 维修人员分页查询
     * @param repairmanPageQueryDTO
     * @return
     */
    @GetMapping("/pageRepairman")
    @Operation(summary = "维修人员分页查询", description = "维修人员分页查询接口")
    public Result<PageResult> getRepairmanPage(@Parameter(description = "管理员分页查询参数") @ParameterObject RepairmanPageQueryDTO repairmanPageQueryDTO) {
        log.info("维修人员分页查询，参数为:{}", repairmanPageQueryDTO);
        PageResult pageResult = userCenterService.repairmanPageQuery(repairmanPageQueryDTO);
        return Result.success(pageResult);
    }


    /**
     * 注册用户分页查询
     * @param userPageQueryDTO
     * @return
     */
    @GetMapping("/pageUser")
    @Operation(summary = "注册用户分页查询", description = "注册用户分页查询接口")
    public Result<PageResult> getUserPage(@Parameter(description = "注册用户分页查询参数") @ParameterObject UserPageQueryDTO userPageQueryDTO) {
        log.info("注册用户分页查询，参数为:{}", userPageQueryDTO);
        PageResult pageResult = userCenterService.userPageQuery(userPageQueryDTO);
        return Result.success(pageResult);
    }


    /**
     * 添加普通用户
     * @param userDTO
     * @return
     */
    @PostMapping("/addNormalUser")
    @Operation(summary = "添加普通用户", description = "添加普通用户接口")
    public Result addUser1(@RequestBody UserDTO userDTO){
        log.info("新增普通用户：{}",userDTO);
        userCenterService.addUser1(userDTO);
        return Result.success();
    }

    /**
     * 添加管理员或维修人员
     * @param userDTO
     * @return
     */
    @PostMapping("/addAdminOrRepairman")
    @Operation(summary = "添加管理员或维修人员", description = "添加管理员或维修人员接口")
    public Result addUser2(@RequestBody UserDTO userDTO){
        log.info("新增管理员或维修人员：{}",userDTO);
        userCenterService.addUser2(userDTO);
        return Result.success();
    }


    /**
     * 编辑普通用户信息
     * @param userDTO
     * @return
     */
    @PutMapping("/editNormalUser")
    @Operation(summary = "编辑普通用户信息", description = "修改普通用户信息接口")
    public Result updateUser1(@RequestBody UserDTO userDTO){
        log.info("编辑普通用户信息：{}",userDTO);
        userCenterService.updateNormalUser(userDTO);
        return Result.success();
    }

    /**
     * 编辑管理员或维修人员信息
     * @param userDTO
     * @return
     */
    @PutMapping("/editAdminOrRepairman")
    @Operation(summary = "编辑管理员或维修人员信息", description = "修改管理员或维修人员信息接口")
    public Result updateUser2(@RequestBody UserDTO userDTO){
        log.info("编辑管理员或维修人员信息：{}",userDTO);
        userCenterService.updateAdminOrRepairman(userDTO);
        return Result.success();
    }

    /**
     * 启用禁用普通用户账号
     * @param status
     * @param id
     * @return
     */
    @PutMapping("/status1/{status}")
    @Operation(summary = "启用禁用普通用户账号", description = "启用或禁用普通用户账号接口")
    public Result starOrStopNormalUser(
            @Parameter(description = "账户状态：0禁用, 1启用") @PathVariable Integer status,
            @Parameter(description = "用户id") @RequestParam Long id){
        log.info("启用禁用普通用户ID：{},{}",status,id);
        userCenterService.starOrStopNormalUser(status,id);
        return Result.success();
    }


    /**
     * 启用禁用普通管理员或维修人员账号
     * @param status
     * @param id
     * @return
     */
    @PutMapping("/status2/{status}")
    @Operation(summary = "启用禁用普通管理员或维修人员账号", description = "启用禁用普通管理员或维修人员账号接口")
    public Result starOrStopAdminOrRepairman(
            @Parameter(description = "账户状态：0禁用, 1启用") @PathVariable Integer status,
            @Parameter(description = "用户id") @RequestParam Long id){
        log.info("启用禁用普通管理员或维修人员ID：{},{}",status,id);
        userCenterService.starOrStopAdminOrRepairman(status,id);
        return Result.success();
    }

}
