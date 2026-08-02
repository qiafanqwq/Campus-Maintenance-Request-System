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
public class RepairOrder {
    private Long id;

    private Long userId;        //报修人的id

    private String nickName;    //报修人姓名

    private String userPhone;   //报修人手机号码

    private String address;     //报修地址

    private Integer categoryId;     //报修分类ID

    private String categoryName;   //报修分类名称

    private String description;     //故障描述

    private LocalDateTime expectTime;   //期望解决时间

    private String remark;      //备注

    private String feedback;      //反馈

    private LocalDateTime createTime;       //创建时间

    private LocalDateTime updateTime;       //更新时间

}
