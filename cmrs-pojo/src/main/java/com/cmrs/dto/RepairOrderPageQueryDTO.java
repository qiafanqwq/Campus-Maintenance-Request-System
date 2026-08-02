package com.cmrs.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "故障列表分页查询参数DTO")
public class RepairOrderPageQueryDTO {
    @Schema(description = "查询的单号（模糊匹配）", example = "")
    private Long id;

    @Schema(description = "查询故障地址（模糊匹配）", example = "")
    private String address;

    @Schema(description = "查询维修人员（模糊匹配）", example = "")
    private String repairmanName;

    @Schema(description = "查询报修人（模糊匹配）", example = "")
    private String nickName;

    @Schema(description = "查询处理情况", example = "")
    private String status;

    @Schema(description = "查询处理时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String repairTime;

    @Schema(description = "当前页码（从1开始）", example = "1")
    private int page;

    @Schema(description = "每页显示条数", example = "10")
    private int pageSize;
}
