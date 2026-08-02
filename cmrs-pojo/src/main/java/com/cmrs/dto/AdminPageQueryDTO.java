package com.cmrs.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

@Data
@Schema(description = "管理员分页查询参数DTO")
public class AdminPageQueryDTO implements Serializable {

    @Schema(description = "查询的姓名（模糊匹配）", example = "超级")
    private String nickname;

    @Schema(description = "当前页码（从1开始）", example = "1")
    private int page;

    @Schema(description = "每页显示条数", example = "10")
    private int pageSize;
}
