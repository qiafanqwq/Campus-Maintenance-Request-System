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
public class Notice {
    private Long id;

    //通知标题
    private String title;

    //通知内容
    private String content;

    //发布者
    private String publisher;

    //创建时间
    private LocalDateTime createTime;

    //更新时间
    private LocalDateTime updateTime;
}
