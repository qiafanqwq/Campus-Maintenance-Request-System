package com.cmrs.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryVO {
    private Integer id;

    private String name;

    private Integer parentId;

    private String description;

    private Integer sortOrder;
}
