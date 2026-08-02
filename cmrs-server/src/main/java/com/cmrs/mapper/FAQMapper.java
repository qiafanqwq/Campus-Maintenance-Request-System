package com.cmrs.mapper;

import com.cmrs.annotation.AutoFill;
import com.cmrs.dto.FAQPageQueryDTO;
import com.cmrs.entity.FAQ;
import com.cmrs.enumeration.OperationType;
import com.cmrs.vo.FAQVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FAQMapper {
    /**
     * 新增常见问题
     */
    @Insert("insert into faq (title, reason, solution, create_time, update_time)" +
            "values" +
            "(#{title},#{reason},#{solution},#{createTime},#{updateTime})")
    @AutoFill(OperationType.INSERT)
    void insert(FAQ FAQ);

    /**
     * 编辑常见问题
     */
    @AutoFill(OperationType.UPDATE)
    void update(FAQ FAQ);

    /**
     * 删除常见问题
     */
    @Delete("delete from faq where id = #{id}")
    void deleteByID(Long id);

    /**
     * 根据标题查询常见问题
     */
    List<FAQVO> list(String title);


    /**
     * 根据标题获取常见问题
     */
    @Select("select * from faq where title = #{title}")
    FAQ getByTitle(String title);
}
