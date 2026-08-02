package com.cmrs.mapper;

import com.cmrs.annotation.AutoFill;
import com.cmrs.entity.RepairRecord;
import com.cmrs.enumeration.OperationType;
import com.cmrs.vo.RepairOrderVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RepairRecordMapper {
    /**
     * 新增维修记录
     */
    @Insert("insert into repair_record (rr_id, order_id, repairman_id, repairman_name, repair_time, repair_process, status, create_time, update_time)" +
            "values" +
            "(#{rrId},#{orderId},#{repairmanId},#{repairmanName},#{repairTime},#{repairProcess},#{status},#{createTime},#{updateTime})")
    @AutoFill(OperationType.INSERT)
    void addRepairRecord(RepairRecord repairRecord);

    /**
     * 通过维修记录ID获取其对应的报修单ID
     */
    @Select("select order_id from repair_record where rr_id = #{rrId}")
    int getOrderIdById(Long rrId);

    /**
     * 获取当前报修单总共有几条维修记录
     */
    @Select("select count(*) from repair_record where order_id = #{orderId}")
    int getTotalByOrderId(Long orderId);

    /**
     * 删除当前维修记录
     */
    @Delete("delete from repair_record where rr_id = #{rrId}")
    void deleteById(Long rrId);


    /**
     * 编辑故障单
     */
    @AutoFill(OperationType.UPDATE)
    void update(RepairRecord repairRecord);

    /**
     * 通过报修单ID获取维修记录
     */
    @Select("select * from repair_record where order_id = #{id}")
    RepairRecord getByOrderId(Long id);

    /**
     * 通过维修人员id来查询报修情况
     */
    List<RepairRecord> getByRepairmanId(Long id);

    /**
     * 查询该报修单的所有维修记录（包括转发的）
     */
    List<RepairRecord> selectAllRecordsByOrderId(Long orderId);
}
