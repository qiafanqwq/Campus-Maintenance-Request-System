package com.cmrs.controller.admin;

import com.cmrs.dto.FAQDTO;
import com.cmrs.dto.FAQPageQueryDTO;
import com.cmrs.result.PageResult;
import com.cmrs.result.Result;
import com.cmrs.service.FAQService;
import com.cmrs.vo.FAQVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/FAQ")
@Slf4j
@Tag(name = "常见问题接口", description = "常见问题相关接口")
public class FAQController {
    @Autowired
    private FAQService FAQService;

    /**
     * 新增常见问题
     * @param FAQDTO
     * @return
     */
    @PostMapping
    @Operation(summary = "新增常见问题", description = "新增常见问题接口")
    public Result addFAQ(@RequestBody FAQDTO FAQDTO){
        log.info("新增常见问题：{}", FAQDTO);
        FAQService.addFAQ(FAQDTO);
        return Result.success();
    }


    /**
     * 编辑常见问题
     * @param FAQDTO
     * @return
     */
    @PutMapping
    @Operation(summary = "编辑常见问题", description = "编辑常见问题接口")
    public Result updateFAQ(@RequestBody FAQDTO FAQDTO){
        log.info("编辑常见问题：{}", FAQDTO);
        FAQService.updateFAQ(FAQDTO);
        return Result.success();
    }


    /**
     * 删除常见问题
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除常见问题", description = "删除常见问题接口")
    public Result deleteFAQ(@PathVariable Long id){
        log.info("删除常见问题ID：{}",id);
        FAQService.deleteFAQ(id);
        return Result.success();
    }

    /**
     * 根据标题查询常见问题
     * @param title
     * @return
     */
    @GetMapping("/list")
    @Operation(summary = "查询常见问题")
    public Result<List<FAQVO>> list(String title){
        log.info("查询常见问题：{}",title);
        List<FAQVO> list = FAQService.list(title);
        return Result.success(list);
    }
}
