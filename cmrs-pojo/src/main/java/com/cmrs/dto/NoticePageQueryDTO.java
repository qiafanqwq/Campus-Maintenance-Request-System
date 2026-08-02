package com.cmrs.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "通知公告分页查询参数DTO")
public class NoticePageQueryDTO {
    @Schema(description = "通知标题（模糊查询）")
    private String title;

    @Schema(description = "通知内容（模糊查询）")
    private String content;

    @Schema(description = "发布者（模糊查询）")
    private String publisher;

    @Schema(description = "当前页码（从1开始）", example = "1")
    private int page;

    @Schema(description = "每页显示条数", example = "10")
    private int pageSize;
}
