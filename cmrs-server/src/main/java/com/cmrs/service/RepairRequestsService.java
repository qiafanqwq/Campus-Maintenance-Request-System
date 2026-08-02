package com.cmrs.service;

import com.cmrs.dto.RepairOrderDTO;
import com.cmrs.dto.RepairOrderPageQueryDTO;
import com.cmrs.result.PageResult;
import com.cmrs.vo.RepairOrderVO;

import java.util.List;

public interface RepairRequestsService {
    /**
     * 故障列表分页查询
     */
    PageResult pageQuery(RepairOrderPageQueryDTO repairOrderPageQueryDTO);


    /**
     * 维修反馈(改变维修状态、填写维修过程、维修时间)
     */
    void updateRepairOrder(RepairOrderDTO repairOrderDTO);


    /**
     * 点击详情获取报修单信息（包括转发）
     */
    List<RepairOrderVO> getRepairOrderDetail(Long orderId);
}
