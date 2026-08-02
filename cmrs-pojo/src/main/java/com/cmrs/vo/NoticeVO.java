package com.cmrs.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoticeVO {
    private Long id;

    //通知标题
    private String title;

    //通知内容
    private String content;

    //发布者
    private String publisher;

    //创建时间
    private LocalDateTime createTime;
}
