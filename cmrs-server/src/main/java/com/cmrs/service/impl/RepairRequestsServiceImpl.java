package com.cmrs.service.impl;

import com.cmrs.dto.RepairOrderDTO;
import com.cmrs.dto.RepairOrderPageQueryDTO;
import com.cmrs.entity.RepairOrder;
import com.cmrs.entity.RepairRecord;
import com.cmrs.mapper.RepairOrderMapper;
import com.cmrs.mapper.RepairRecordMapper;
import com.cmrs.result.PageResult;
import com.cmrs.service.RepairRequestsService;
import com.cmrs.vo.RepairOrderVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class RepairRequestsServiceImpl implements RepairRequestsService {
    @Autowired
    private RepairOrderMapper repairOrderMapper;
    @Autowired
    private RepairRecordMapper repairRecordMapper;

    /**
     * 报修信息分页查询
     * @param repairOrderPageQueryDTO
     * @return
     */
    public PageResult pageQuery(RepairOrderPageQueryDTO repairOrderPageQueryDTO) {
        PageHelper.startPage(repairOrderPageQueryDTO.getPage(), repairOrderPageQueryDTO.getPageSize());
        Page<RepairOrderVO> page = repairOrderMapper.pageQueryWithJoin(repairOrderPageQueryDTO);

        long total = page.getTotal();
        List<RepairOrderVO> lists = page.getResult();
        return new PageResult(total,lists);
    }


    /**
     * 维修反馈(改变维修状态、填写维修过程、维修时间)
     * @param repairOrderDTO
     * @return
     */
    public void updateRepairOrder(RepairOrderDTO repairOrderDTO) {
        RepairRecord repairRecord = new RepairRecord();
        BeanUtils.copyProperties(repairOrderDTO, repairRecord);
        repairRecord.setRepairTime(LocalDateTime.now());
        repairRecordMapper.update(repairRecord);

    }


    /**
     * 点击详情后返回报修单的信息
     * @param orderId
     * @return
     */
    public List<RepairOrderVO> getRepairOrderDetail(Long orderId) {
        // 先查询基础报修单信息
        RepairOrderVO baseOrder = repairOrderMapper.selectBaseOrderById(orderId);

        // 然后查询该报修单的所有维修记录（包括转发的）
        List<RepairRecord> records = repairRecordMapper.selectAllRecordsByOrderId(orderId);

        // 构造返回列表
        List<RepairOrderVO> resultList = new ArrayList<>();

        // 首条记录（当前/最新的维修记录）
        if (!records.isEmpty()) {
            // 找到最新的记录（按时间倒序）
            RepairRecord latestRecord = records.stream()
                    .max(Comparator.comparing(RepairRecord::getCreateTime))
                    .orElse(records.get(0));

            baseOrder.setRrId(latestRecord.getRrId());
            baseOrder.setRepairmanName(latestRecord.getRepairmanName());
            baseOrder.setRepairTime(latestRecord.getRepairTime());
            baseOrder.setRepairProcess(latestRecord.getRepairProcess());
            baseOrder.setStatus(latestRecord.getStatus());
            baseOrder.setTotalCount(records.size());

            resultList.add(baseOrder);

            // 如果有历史记录，添加历史版本
            if (records.size() > 1) {
                for (int i = records.size() - 2; i >= 0; i--) {
                    RepairRecord historyRecord = records.get(i);
                    RepairOrderVO historyVO = createHistoryVO(baseOrder, historyRecord);
                    resultList.add(historyVO);
                }
            }
        }

        return resultList;
    }

    private RepairOrderVO createHistoryVO(RepairOrderVO base, RepairRecord record) {
        RepairOrderVO vo = new RepairOrderVO();
        // 复制报修单基本信息
        vo.setId(base.getId());
        vo.setNickName(base.getNickName());
        vo.setUserPhone(base.getUserPhone());
        vo.setAddress(base.getAddress());
        vo.setCategoryName(base.getCategoryName());
        vo.setDescription(base.getDescription());
        vo.setExpectTime(base.getExpectTime());
        vo.setRemark(base.getRemark());
        vo.setCreateTime(base.getCreateTime());
        vo.setTotalCount(base.getTotalCount());  // 历史记录也保留总数

        // 设置历史维修记录信息
        vo.setRrId(record.getRrId());
        vo.setRepairmanName(record.getRepairmanName());
        vo.setRepairTime(record.getRepairTime());
        vo.setRepairProcess(record.getRepairProcess());
        vo.setStatus(record.getStatus());
        return vo;
    }
}
