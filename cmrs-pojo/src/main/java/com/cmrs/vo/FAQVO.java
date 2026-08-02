package com.cmrs.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FAQVO {
    private Long id;

    //问题的标题
    private String title;

    //问题的原因
    private String reason;

    //解决方法
    private String solution;
}
