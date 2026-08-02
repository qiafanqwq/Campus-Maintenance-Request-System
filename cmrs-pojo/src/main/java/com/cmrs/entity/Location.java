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
public class Location {
    private Integer id;

    //地址名称
    private String name;

    //父级 ID：0 表示一级分类，>0 表示对应一级分类的 ID
    private Integer parentId;

    //排序序号（控制同一层级内的展示顺序，如一级分类的显示顺序、二级分类在所属一级分类下的顺序）
    private Integer sortOrder;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
