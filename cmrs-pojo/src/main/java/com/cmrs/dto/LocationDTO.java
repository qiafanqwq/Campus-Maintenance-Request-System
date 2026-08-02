package com.cmrs.dto;

import lombok.Data;

@Data
public class LocationDTO {
    private Integer id;

    private String name;

    private Integer parentId;

    private Integer sortOrder;
}
