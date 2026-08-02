package com.cmrs.mapper;

import com.cmrs.annotation.AutoFill;
import com.cmrs.dto.NoticePageQueryDTO;
import com.cmrs.entity.Notice;
import com.cmrs.enumeration.OperationType;
import com.cmrs.vo.NoticeVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NoticeMapper {
    /**
     * 新增通知公告
     */
    @Insert("insert into notices (title, publisher, content, create_time, update_time)" +
            "values" +
            "(#{title},#{publisher},#{content},#{createTime},#{updateTime})")
    @AutoFill(OperationType.INSERT)
    void insert(Notice notice);


    /**
     * 编辑通知公告
     */
    @AutoFill(OperationType.UPDATE)
    void update(Notice notice);


    /**
     * 删除通知公告
     */
    @Delete("delete from notices where id = #{id}")
    void deleteByID(int id);

    /**
     * 根据标题查询通知公告
     */
    List<NoticeVO> list(String title);
}
