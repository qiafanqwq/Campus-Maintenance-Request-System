package com.cmrs.controller.admin;

import com.cmrs.dto.CategoryDTO;
import com.cmrs.result.Result;
import com.cmrs.service.CategoryService;
import com.cmrs.vo.CategoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/Category")
@Slf4j
@Tag(name = "报修分类接口", description = "报修分类相关接口")
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    /**
     * 新增报修分类
     * @param categoryDTO
     * @return
     */
    @PostMapping
    @Operation(summary = "新增报修分类", description = "新增报修分类接口")
    public Result addCategory(@RequestBody CategoryDTO categoryDTO){
        log.info("新增报修分类：{}",categoryDTO);
        categoryService.addCategory(categoryDTO);
        return Result.success();
    }

    /**
     * 编辑报修分类
     * @param categoryDTO
     * @return
     */
    @PutMapping
    @Operation(summary = "编辑报修分类", description = "编辑报修分类接口")
    public Result updateCategory(@RequestBody CategoryDTO categoryDTO){
        log.info("编辑报修分类：{}",categoryDTO);
        categoryService.updateCategory(categoryDTO);
        return Result.success();
    }


    /**
     * 删除报修分类
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除报修分类", description = "删除报修分类接口")
    public Result deleteCategory(@PathVariable int id){
        log.info("删除报修分类ID：{}",id);
        categoryService.deleteCategory(id);
        return Result.success();
    }


    /**
     * 获取报修分类
     * @return
     */
    @GetMapping
    @Operation(summary = "获取报修分类接口", description = "获取报修分类以展示给用户")
    public Result<List<CategoryVO>> listCategory(){
        log.info("获取报修分类接口");
        List<CategoryVO> list = categoryService.list();
        return Result.success(list);
    }
}
