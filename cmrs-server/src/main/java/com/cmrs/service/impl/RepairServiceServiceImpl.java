package com.cmrs.service.impl;

import com.cmrs.constant.MessageConstant;
import com.cmrs.constant.StatusConstant;
import com.cmrs.dto.RepairOrderDTO;
import com.cmrs.dto.RepairOrderPageQueryDTO;
import com.cmrs.entity.Category;
import com.cmrs.entity.RepairOrder;
import com.cmrs.entity.RepairRecord;
import com.cmrs.entity.User;
import com.cmrs.exception.UserNotFoundException;
import com.cmrs.mapper.CategoryMapper;
import com.cmrs.mapper.RepairOrderMapper;
import com.cmrs.mapper.RepairRecordMapper;
import com.cmrs.mapper.UserMapper;
import com.cmrs.result.PageResult;
import com.cmrs.service.RepairServiceService;
import com.cmrs.vo.RepairOrderVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class RepairServiceServiceImpl implements RepairServiceService {
    @Autowired
    private RepairOrderMapper repairOrderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private RepairRecordMapper repairRecordMapper;

    /**
     * 新增报修单
     * @param repairOrderDTO
     * @return
     */
    public void addRepairOrder(RepairOrderDTO repairOrderDTO) {
        RepairOrder repairOrder = new RepairOrder();
        RepairRecord repairRecord = new RepairRecord();
        //属性复制
        BeanUtils.copyProperties(repairOrderDTO, repairOrder);
        //获取用户id
        User user = userMapper.getNormalUserByNickname(repairOrderDTO.getNickName());
        if (user == null) {
            throw new UserNotFoundException(MessageConstant.USER_NOT_FOUND);
        }else {
            repairOrder.setUserId(user.getId());
        }
        //获取分类id
        Category category = categoryMapper.getByName(repairOrderDTO.getCategoryName());
        repairOrder.setCategoryId(category.getId());

        repairOrderMapper.addRepairOrder(repairOrder);

        // 新增完报修单后，获取自增ID并设置到维修记录中
        Long orderId = repairOrder.getId(); // 这里会自动获取到自增ID
        repairRecord.setOrderId(orderId);
        //设置订单初始状态(待处理)
        repairRecord.setStatus(StatusConstant.PENDING);

        repairRecordMapper.addRepairRecord(repairRecord);
    }



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

    /**
     * 用户进行反馈
     * @param repairOrderDTO
     * @return
     */
    public void feedback(RepairOrderDTO repairOrderDTO) {
        RepairOrder repairOrder = new RepairOrder();
        BeanUtils.copyProperties(repairOrderDTO, repairOrder);
        repairOrderMapper.update(repairOrder);
    }
}
