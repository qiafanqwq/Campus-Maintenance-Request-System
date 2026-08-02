package com.cmrs.service.impl;

import com.cmrs.dto.NoticeDTO;
import com.cmrs.dto.NoticePageQueryDTO;
import com.cmrs.entity.Notice;
import com.cmrs.mapper.NoticeMapper;
import com.cmrs.result.PageResult;
import com.cmrs.service.NoticeService;
import com.cmrs.vo.NoticeVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class NoticeServiceImpl implements NoticeService {
    @Autowired
    private NoticeMapper noticeMapper;

    /**
     * 新增通知公告
     * @param noticeDTO
     * @return
     */
    public void addNotice(NoticeDTO noticeDTO) {
        Notice notice = new Notice();
        BeanUtils.copyProperties(noticeDTO,notice);
        noticeMapper.insert(notice);
    }


    /**
     * 编辑通知公告
     * @param noticeDTO
     * @return
     */
    public void updateNotice(NoticeDTO noticeDTO) {
        Notice notice = new Notice();
        BeanUtils.copyProperties(noticeDTO,notice);
        noticeMapper.update(notice);
    }


    /**
     * 删除通知公告
     * @param id
     * @return
     */
    public void deleteNotice(int id) {
        noticeMapper.deleteByID(id);
    }


    /**
     * 根据标题查询通知公告
     *
     * @param title
     * @return
     */
    public List<NoticeVO> list(String title) {
        return noticeMapper.list(title);
    }
}
