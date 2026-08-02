package com.cmrs.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "常见问题分页查询参数DTO")
public class FAQPageQueryDTO {
    @Schema(description = "常见问题标题（模糊查询）")
    private String title;

    @Schema(description = "常见问题原因（模糊查询）")
    private String reason;

    @Schema(description = "解决方法（模糊查询）")
    private String solution;

    @Schema(description = "当前页码（从1开始）", example = "1")
    private int page;

    @Schema(description = "每页显示条数", example = "10")
    private int pageSize;
}
