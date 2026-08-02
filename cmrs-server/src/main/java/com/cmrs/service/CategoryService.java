package com.cmrs.service;

import com.cmrs.dto.CategoryDTO;
import com.cmrs.vo.CategoryVO;

import java.util.List;

public interface CategoryService {
    /**
     * 新增报修分类
     */
    void addCategory(CategoryDTO categoryDTO);

    /**
     * 编辑报修分类
     */
    void updateCategory(CategoryDTO categoryDTO);

    /**
     * 删除报修分类
     */
    void deleteCategory(int id);

    /**
     * 获取报修分类以展示给用户
     */
    List<CategoryVO> list();

}
