package com.cmrs.controller;

import com.cmrs.constant.MessageConstant;
import com.cmrs.dto.UserLoginDTO;
import com.cmrs.dto.UserRegisterDTO;
import com.cmrs.entity.User;
import com.cmrs.exception.BaseException;
import com.cmrs.properties.JwtProperties;
import com.cmrs.result.Result;
import com.cmrs.service.LoginService;
import com.cmrs.utils.JwtUtil;
import com.cmrs.vo.UserLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
@Tag(name = "用户注册与登录接口", description = "用户注册与登录相关接口")
public class LoginController {
    @Autowired
    private LoginService loginService;
    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录接口")
    @ApiResponse(responseCode = "1", description = "登录成功",
            content = @Content(schema = @Schema(implementation = UserLoginVO.class)))
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("用户登录：{}", userLoginDTO);
        try {
            User user = loginService.login(userLoginDTO);
            //如果是普通用户，权限直接设为0
            Integer AuthorityId = user.getAuthorityId() != null ? user.getAuthorityId() : 0;

            //登录成功后，生成jwt令牌
            Map<String, Object> claims = new HashMap<>();
            //存储必要的用户信息
            claims.put("id", user.getId());
            claims.put("username", user.getUsername());
            claims.put("AuthorityId", AuthorityId);
            claims.put("nickname", user.getNickname());
            String token = JwtUtil.createJWT(
                    jwtProperties.getUserSecretKey(),
                    jwtProperties.getUserTtl(),
                    claims);

            UserLoginVO userLoginVO = UserLoginVO.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .nickname(user.getNickname())
                    .authorityId(AuthorityId)
                    .token(token)
                    .build();
            log.info(userLoginVO.toString());
            return Result.success(userLoginVO);
        }catch (BaseException be){
            return Result.error(be.getMessage());
        }
    }


    /**
     * 用户注册
     * @param registerDTO
     * @return
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "用户注册接口")
    public Result<UserRegisterDTO> register(@RequestBody UserRegisterDTO registerDTO) {
        log.info("用户注册：{}", registerDTO);
        try {
            loginService.register(registerDTO);
            return Result.success();
        }catch (BaseException be){
            return Result.error(be.getMessage());
        }
    }
}
