package com.cmrs.dto;

import lombok.Data;

@Data
public class NoticeDTO {
    private Long id;

    private String title;

    private String publisher;

    private String content;
}
