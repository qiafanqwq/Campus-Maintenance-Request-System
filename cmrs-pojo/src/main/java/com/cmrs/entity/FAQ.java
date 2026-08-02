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
public class FAQ {

    private Long id;

    //问题的标题
    private String title;

    //问题的原因
    private String reason;

    //解决方法
    private String solution;

    //创建时间
    private LocalDateTime createTime;

    //更新时间
    private LocalDateTime updateTime;
}
