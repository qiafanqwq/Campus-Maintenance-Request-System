package com.cmrs.service;

import com.cmrs.dto.NoticeDTO;
import com.cmrs.dto.NoticePageQueryDTO;
import com.cmrs.result.PageResult;
import com.cmrs.vo.NoticeVO;

import java.util.List;

public interface NoticeService {
    /**
     * 新增通知公告
     */
    void addNotice(NoticeDTO noticeDTO);

    /**
     * 编辑通知公告
     */
    void updateNotice(NoticeDTO noticeDTO);

    /**
     * 删除通知公告
     */
    void deleteNotice(int id);

    /**
     * 根据标题查询通知公告
     */
    List<NoticeVO> list(String title);
}
