package com.cmrs.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RepairRecord {
    private Long rrId;              //维修记录单ID

    private Long orderId;           //报修单ID

    private Long repairmanId;       //维修人员的id

    private String repairmanName;   //维修人员的姓名

    private LocalDateTime repairTime;   //处理时间

    private String repairProcess;       //处理过程

    private Integer status;     //报修状态：0-待处理，1-处理中，2-待审核，3-已完成，4-已取消

    private LocalDateTime createTime;       //创建时间

    private LocalDateTime updateTime;       //更新时间
}
