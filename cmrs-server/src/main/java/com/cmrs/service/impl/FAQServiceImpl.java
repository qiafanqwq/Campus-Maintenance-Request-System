package com.cmrs.service.impl;

import com.cmrs.dto.FAQDTO;
import com.cmrs.dto.FAQPageQueryDTO;
import com.cmrs.entity.FAQ;
import com.cmrs.mapper.FAQMapper;
import com.cmrs.result.PageResult;
import com.cmrs.service.FAQService;
import com.cmrs.vo.FAQVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class FAQServiceImpl implements FAQService {
    @Autowired
    private FAQMapper FAQMapper;

    /**
     * 新增常见问题
     * @param FAQDTO
     * @return
     */
    public void addFAQ(FAQDTO FAQDTO) {
        FAQ FAQ = new FAQ();
        BeanUtils.copyProperties(FAQDTO, FAQ);
        FAQMapper.insert(FAQ);
    }


    /**
     * 编辑常见问题
     * @param FAQDTO
     * @return
     */
    public void updateFAQ(FAQDTO FAQDTO) {
        FAQ FAQ = new FAQ();
        BeanUtils.copyProperties(FAQDTO, FAQ);
        FAQMapper.update(FAQ);
    }


    /**
     * 删除常见问题
     * @param id
     * @return
     */
    public void deleteFAQ(Long id) {
        FAQMapper.deleteByID(id);
    }


    /**
     * 根据标题查询常见问题
     * @param title
     * @return
     */
    public List<FAQVO> list(String title) {
        return FAQMapper.list(title);
    }
}
