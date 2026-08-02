package com.cmrs.service;

import com.cmrs.dto.RepairOrderDTO;
import com.cmrs.dto.RepairOrderPageQueryDTO;
import com.cmrs.entity.RepairOrder;
import com.cmrs.result.PageResult;
import com.cmrs.vo.RepairOrderVO;

import java.util.List;

public interface RepairServiceService {
    /**
     * 新增报修单
     */
    void addRepairOrder(RepairOrderDTO repairOrderDTO);


    /**
     * 报修信息分页查询
     */
    PageResult pageQuery(RepairOrderPageQueryDTO repairOrderPageQueryDTO);


    /**
     * 点击详情获取报修单信息（包括转发）
     */
    List<RepairOrderVO> getRepairOrderDetail(Long orderId);


    /**
     * 用户进行反馈
     */
    void feedback(RepairOrderDTO repairOrderDTO);
}
