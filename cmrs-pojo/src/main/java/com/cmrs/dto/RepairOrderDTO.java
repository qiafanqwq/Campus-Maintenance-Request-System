package com.cmrs.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

@Data
public class RepairOrderDTO {
    private Long id;

    private String nickName;

    private String userPhone;

    private String address;

    private String categoryName;

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expectTime;

    private String remark;

    private String feedback;

    private Long rrId;          //维修记录ID

    private String repairmanName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime repairTime;

    private String repairProcess;

    private Integer status;     //报修状态：0-待处理，1-处理中，2-待审核，3-已完成，4-已取消，5-转发
}
