package com.cmrs.service;

import com.cmrs.dto.RepairOrderDTO;
import com.cmrs.dto.RepairOrderPageQueryDTO;
import com.cmrs.result.PageResult;
import com.cmrs.vo.RepairOrderVO;
import com.cmrs.vo.RepairStatisticsVO;
import com.cmrs.vo.UserVO;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface FaultListService {
    /**
     * 故障列表分页查询
     */
    PageResult pageQuery(RepairOrderPageQueryDTO repairOrderPageQueryDTO);


    /**
     * 新增故障单
     */
    void addRepairOrder(RepairOrderDTO repairOrderDTO);

    /**
     * 删除故障单
     */
    void delete(Long id);

    /**
     * 编辑故障单信息
     */
    void update(RepairOrderDTO repairOrderDTO);


    /**
     * 分配维修人员
     */
    void assign(Long id, String repairmanName);


    /**
     * 列举维修人员
     */
    List<UserVO> listRepairman();


    /**
     * 获取报修统计信息
     */
    RepairStatisticsVO getRepairStatistics(RepairOrderPageQueryDTO queryDTO);


    /**
     * 导出功能
     */
    void exportRepairOrders(RepairOrderPageQueryDTO queryDTO, HttpServletResponse response);


    /**
     * 转发报修单
     */
    void transfer(Long id, String repairmanName, Long rrId);


    /**
     * 点击详情获取报修单信息（包括转发）
     */
    List<RepairOrderVO> getRepairOrderDetail(Long orderId);
}
