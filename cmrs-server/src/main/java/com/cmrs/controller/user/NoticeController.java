package com.cmrs.controller.user;

import com.cmrs.entity.Notice;
import com.cmrs.result.Result;
import com.cmrs.service.NoticeService;
import com.cmrs.vo.NoticeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userNoticeController")
@RequestMapping("/user/notice")
@Tag(name = "用户端-通知公告接口")
@Slf4j
public class NoticeController {
    @Autowired
    private NoticeService noticeService;

    /**
     * 根据标题查询通知公告
     * @param title
     * @return
     */
    @GetMapping("/list2")
    @Operation(summary = "查询通知公告")
    public Result<List<NoticeVO>> list2(String title){
        log.info("查询通知公告：{}",title);
        List<NoticeVO> list = noticeService.list(title);
        return Result.success(list);
    }
}
