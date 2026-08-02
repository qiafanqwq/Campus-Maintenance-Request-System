package com.cmrs.mapper;

import com.cmrs.annotation.AutoFill;
import com.cmrs.entity.Category;
import com.cmrs.enumeration.OperationType;
import com.cmrs.vo.CategoryVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {
    /**
     * 获取属于某一分类数量
     */
    @Select("select count(*) from fault_category where parent_id = #{id}")
    int getTotalOfLevel(Integer id);

    /**
     * 新增报修分类
     */
    @AutoFill(OperationType.INSERT)
    @Insert("insert into fault_category (name, parent_id, description, sort_order ,create_time,update_time)" +
            "values" +
            "(#{name},#{parentId},#{description},#{sortOrder},#{createTime},#{updateTime})")
    void insert(Category category);

    /**
     * 查询父类ID
     */
    @Select("select parent_id from fault_category where id = #{id}")
    int getParentIdById(Integer id);


    /**
     * 编辑报修分类
     */
    @AutoFill(OperationType.UPDATE)
    void update(Category category);

    /**
     * 删除报修分类
     */
    @Delete("delete from fault_category where id = #{id}")
    void deleteById(int id);

    /**
     * 通过名字获取分类
     */
    @Select("select * from fault_category where name = #{categoryName}")
    Category getByName(String categoryName);


    /**
     * 获取报修分类以展示给用户
     */
    List<CategoryVO> list();

    /**
     * 通过ID查询分类
     */
    @Select("select * from fault_category where id = #{id}")
    Category getById(int id);


    /**
     * 通过父级ID获取所有二级分类
     */
    List<Category> getByParentId(int id);
}
