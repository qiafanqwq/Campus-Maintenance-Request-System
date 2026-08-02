package com.cmrs.dto;

import lombok.Data;

@Data
public class CategoryDTO {
    private Integer id;

    private String name;

    private Integer parentId;

    private String description;

    private Integer sortOrder;
}
