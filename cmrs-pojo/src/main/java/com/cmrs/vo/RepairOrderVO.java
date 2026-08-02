package com.cmrs.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepairOrderVO {
    private Long id;

    private String nickName;    //报修人姓名

    private String userPhone;   //报修人手机号码

    private String address;     //报修地址

    private String categoryName;   //报修分类名称

    private String description;     //故障描述

    private LocalDateTime expectTime;   //期望解决时间

    private String remark;      //备注

    private String feedback;    //反馈

    private Long rrId;          //维修记录ID

    private String repairmanName;   //维修人员的姓名

    private LocalDateTime repairTime;   //处理时间

    private String repairProcess;       //处理过程

    private Integer status;     //报修状态：0-待处理，1-处理中，2-待审核，3-已完成，4-已取消

    private String createTime;  //报障时间

    private Integer totalCount;  // 维修记录总数，如果 >1 表示有历史记录

}
