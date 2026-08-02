package com.cmrs.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationVO {
    private Integer id;

    private String name;

    private Integer parentId;

    private Integer sortOrder;
}
