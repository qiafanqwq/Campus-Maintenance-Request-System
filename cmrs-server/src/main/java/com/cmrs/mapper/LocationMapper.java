package com.cmrs.mapper;

import com.cmrs.annotation.AutoFill;
import com.cmrs.entity.Location;
import com.cmrs.enumeration.OperationType;
import com.cmrs.vo.LocationVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LocationMapper {
    /**
     * 获取属于某一分类数量
     */
    @Select("select count(*) from repair_location where parent_id = #{parentId}")
    int getTotalOfLevel(Integer parentId);


    /**
     * 新增地址分类
     */
    @AutoFill(OperationType.INSERT)
    @Insert("insert into repair_location (name, parent_id, sort_order ,create_time, update_time)" +
            "values" +
            "(#{name},#{parentId},#{sortOrder},#{createTime},#{updateTime})")
    void insert(Location location);


    /**
     * 获取地址分类以展示给用户
     */
    List<LocationVO> list();


    /**
     * 编辑地址分类
     */
    @AutoFill(OperationType.UPDATE)
    void update(Location location);


    /**
     * 删除报修分类
     */
    @Delete("delete from repair_location where id = #{id}")
    void delete(int id);


    /**
     * 通过ID查询分类
     */
    @Select("select * from repair_location where id = #{id}")
    Location getById(int id);


    /**
     * 通过父级ID获取所有二级分类
     */
    List<Location> getByParentId(int id);
}
