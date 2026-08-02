package com.cmrs.service;

import com.cmrs.dto.FAQDTO;
import com.cmrs.dto.FAQPageQueryDTO;
import com.cmrs.entity.FAQ;
import com.cmrs.result.PageResult;
import com.cmrs.vo.FAQVO;

import java.util.List;

public interface FAQService {
    /**
     * 新增常见问题
     */
    void addFAQ(FAQDTO FAQDTO);

    /**
     * 编辑常见问题
     */
    void updateFAQ(FAQDTO FAQDTO);

    /**
     * 删除常见问题
     */
    void deleteFAQ(Long id);


    /**
     * 根据标题查询常见问题
     */
    List<FAQVO> list(String title);
}
