package com.cmrs.controller.user;

import com.cmrs.entity.FAQ;
import com.cmrs.result.Result;
import com.cmrs.service.FAQService;
import com.cmrs.vo.FAQVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userFAQController")
@RequestMapping("/user/FAQ")
@Tag(name = "用户端-常见问题接口")
@Slf4j
public class FAQController {
    @Autowired
    private FAQService FAQService;

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
