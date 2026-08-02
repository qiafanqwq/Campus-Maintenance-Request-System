package com.cmrs.mapper;

import com.cmrs.annotation.AutoFill;
import com.cmrs.dto.RepairOrderPageQueryDTO;
import com.cmrs.entity.RepairOrder;
import com.cmrs.enumeration.OperationType;
import com.cmrs.vo.RepairOrderVO;
import com.cmrs.vo.RepairStatisticsVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RepairOrderMapper {
    /**
     * 故障列表分页查询
     */
    Page<RepairOrderVO> pageQueryWithJoin(RepairOrderPageQueryDTO repairOrderPageQueryDTO);


    /**
     * 新增故障单
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into repair_order (user_id, nickname, user_phone, address, category_id, category_name, description, expect_time, remark, feedback, create_time, update_time)" +
            "values" +
            "(#{userId},#{nickName},#{userPhone},#{address},#{categoryId},#{categoryName},#{description},#{expectTime},#{remark},#{feedback},#{createTime},#{updateTime})")
    @AutoFill(OperationType.INSERT)
    void addRepairOrder(RepairOrder repairOrder);


    /**
     * 删除故障单
     */
    @Delete("delete from repair_order where id = #{id}")
    void deleteById(Long id);


    /**
     * 编辑故障单
     */
    @AutoFill(OperationType.UPDATE)
    void update(RepairOrder repairOrder);

    /**
     * 通过用户id来查询报修情况
     */
    List<RepairOrderVO> getByUserId(long userId);



    /**
     * 通过故障单ID来获取故障单
     */
    @Select("select * from repair_order where id = #{id}")
    RepairOrder getById(Long id);

    /**
     * 查询基础报修单信息
     */
    RepairOrderVO selectBaseOrderById(Long orderId);


    /**
     * 按地址统计（复用RepairOrderPageQueryDTO）
     */
    List<RepairStatisticsVO.AddressStatVO> statByAddress(@Param("params") RepairOrderPageQueryDTO params);

    /**
     * 按处理时间统计（按天）
     */
    List<RepairStatisticsVO.TimeStatVO> statByTime(@Param("params") RepairOrderPageQueryDTO params);

    /**
     * 按状态统计
     */
    List<RepairStatisticsVO.StatusStatVO> statByStatus(@Param("params") RepairOrderPageQueryDTO params);

    /**
     * 总统计
     */
    RepairStatisticsVO.TotalStatVO statTotal(@Param("params") RepairOrderPageQueryDTO params);

}
