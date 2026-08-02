package com.cmrs.controller.admin;

import com.cmrs.dto.NoticeDTO;
import com.cmrs.dto.NoticePageQueryDTO;
import com.cmrs.result.PageResult;
import com.cmrs.result.Result;
import com.cmrs.service.NoticeService;
import com.cmrs.vo.NoticeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/Notice")
@Slf4j
@Tag(name = "通知公告接口", description = "通知公告相关接口")
public class NoticeController {
    @Autowired
    private NoticeService noticeService;

    /**
     * 根据标题查询通知公告
     * @param title
     * @return
     */
    @GetMapping("/list1")
    @Operation(summary = "查询通知公告")
    public Result<List<NoticeVO>> list1(String title){
        log.info("查询通知公告：{}",title);
        List<NoticeVO> list = noticeService.list(title);
        return Result.success(list);
    }


    /**
     * 新增通知公告
     * @param noticeDTO
     * @return
     */
    @PostMapping
    @Operation(summary = "新增通知公告", description = "新增通知公告接口")
    public Result addNotice(@RequestBody NoticeDTO noticeDTO){
        log.info("新增通知公告：{}",noticeDTO);
        noticeService.addNotice(noticeDTO);
        return Result.success();
    }


    /**
     * 编辑通知公告
     * @param noticeDTO
     * @return
     */
    @PutMapping
    @Operation(summary = "编辑通知公告", description = "编辑通知公告接口")
    public Result updateNotice(@RequestBody NoticeDTO noticeDTO){
        log.info("编辑通知公告：{}",noticeDTO);
        noticeService.updateNotice(noticeDTO);
        return Result.success();
    }


    /**
     * 删除通知公告
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除通知公告", description = "删除通知公告接口")
    public Result deleteNotice(@PathVariable int id){
        log.info("删除通知公告ID：{}",id);
        noticeService.deleteNotice(id);
        return Result.success();
    }

}
