package com.cmrs.service.impl;

import com.alibaba.excel.EasyExcel;
import com.cmrs.constant.MessageConstant;
import com.cmrs.constant.StatusConstant;
import com.cmrs.dto.RepairOrderDTO;
import com.cmrs.dto.RepairOrderExportDTO;
import com.cmrs.dto.RepairOrderPageQueryDTO;
import com.cmrs.dto.RepairmanPageQueryDTO;
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
import com.cmrs.service.FaultListService;
import com.cmrs.vo.RepairOrderVO;
import com.cmrs.vo.RepairStatisticsVO;
import com.cmrs.vo.UserVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FaultListServiceImpl implements FaultListService {
    @Autowired
    private RepairOrderMapper repairOrderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private RepairRecordMapper repairRecordMapper;

    /**
     * 故障列表分页查询
     */
    public PageResult pageQuery(RepairOrderPageQueryDTO queryDTO) {
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getPageSize());
        Page<RepairOrderVO> page = repairOrderMapper.pageQueryWithJoin(queryDTO);

        long total = page.getTotal();
        List<RepairOrderVO> lists = page.getResult();
        return new PageResult(total,lists);
    }


    /**
     * 新增故障单
     */
    public void addRepairOrder(RepairOrderDTO repairOrderDTO) {
        RepairOrder repairOrder = new RepairOrder();
        RepairRecord repairRecord = new RepairRecord();
        BeanUtils.copyProperties(repairOrderDTO, repairOrder);

        //将订单初始状态设为待处理
        repairRecord.setStatus(StatusConstant.PENDING);
        //查询分类id
        Category category = categoryMapper.getByName(repairOrderDTO.getCategoryName());
        repairOrder.setCategoryId(category.getId());

        //查询报修人的id并赋值
        User user = userMapper.getNormalUserByNickname(repairOrderDTO.getNickName());
        if (user == null) {
            throw new UserNotFoundException(MessageConstant.USER_NOT_FOUND);
        } else {
            repairOrder.setUserId(user.getId());
        }

        repairOrderMapper.addRepairOrder(repairOrder);

        // 新增完报修单后，获取自增ID并设置到维修记录中
        Long orderId = repairOrder.getId(); // 这里会自动获取到自增ID
        repairRecord.setOrderId(orderId);

        repairRecordMapper.addRepairRecord(repairRecord);
    }


    /**
     * 删除故障单
     */
    public void delete(Long rrId) {
        log.info("要删除的报修的维修记录rrId为{}",rrId);
        //删除先前查询该故障单是否只有一个维修记录
        long orderId = repairRecordMapper.getOrderIdById(rrId);
        int total = repairRecordMapper.getTotalByOrderId(orderId);

        if (total > 1) {
            //如果不止1条维修记录，就删除rrId(当前维修记录ID)所代表的那条维修记录，但其他维修记录保留
            repairRecordMapper.deleteById(rrId);
        }else {
            //如果只有1条维修记录，那直接连同报修单表也一起删除
            repairRecordMapper.deleteById(rrId);
            repairOrderMapper.deleteById(orderId);
        }
    }


    /**
     * 编辑故障单信息
     */
    public void update(RepairOrderDTO repairOrderDTO) {
        log.info(repairOrderDTO.toString());
        RepairOrder repairOrder = new RepairOrder();
        RepairRecord repairRecord = new RepairRecord();

        BeanUtils.copyProperties(repairOrderDTO, repairOrder);
        BeanUtils.copyProperties(repairOrderDTO, repairRecord);

        //查询分类id
        Category category = categoryMapper.getByName(repairOrderDTO.getCategoryName());
        repairOrder.setCategoryId(category.getId());

        repairOrderMapper.update(repairOrder);
        repairRecordMapper.update(repairRecord);

    }


    /**
     * 分配维修人员(首次)
     */
    public void assign(Long id, String repairmanName) {
        // 根据报修单id查询维修记录
        RepairRecord repairRecord = repairRecordMapper.getByOrderId(id);
        if (repairRecord == null) {
            throw new RuntimeException(MessageConstant.ORDER_NOT_FOUND);
        }

        // 更新维修记录中的维修人员信息
        User user = userMapper.getAdminOrRepairmanByNickname(repairmanName);
        repairRecord.setRepairmanId(user.getId());
        repairRecord.setRepairmanName(repairmanName);
        repairRecord.setStatus(StatusConstant.IN_PROGRESS); // 更新状态为处理中
        repairRecord.setUpdateTime(LocalDateTime.now());

        repairRecordMapper.update(repairRecord);
    }


    /**
     * 转发报修单
     */
    public void transfer(Long id, String repairmanName, Long rrId) {
        // 将旧维修记录的报修状态改为已完成
        RepairRecord r1 = new RepairRecord();
        r1.setRrId(rrId);
        r1.setStatus(StatusConstant.COMPLETED);
        repairRecordMapper.update(r1);

        // 创建新的维修记录，并将要转发的报修单id录入新的记录中
        RepairRecord r2 = new RepairRecord();
        r2.setOrderId(id);

        // 插入维修人员信息
        User user = userMapper.getAdminOrRepairmanByNickname(repairmanName);
        r2.setRepairmanId(user.getId());
        r2.setRepairmanName(repairmanName);
        r2.setStatus(StatusConstant.IN_PROGRESS); // 更新状态为处理中

        repairRecordMapper.addRepairRecord(r2);
    }


    /**
     * 列举维修人员
     */
    public List<UserVO> listRepairman() {
        return userMapper.listRepairman();
    }


    /**
     * 获取报修统计数据
     */
    public RepairStatisticsVO getRepairStatistics(RepairOrderPageQueryDTO params) {
        RepairStatisticsVO statisticsVO = new RepairStatisticsVO();

        // 按地址统计
        List<RepairStatisticsVO.AddressStatVO> addressStats = repairOrderMapper.statByAddress(params);

        // 计算总数
        int total = addressStats.stream().mapToInt(RepairStatisticsVO.AddressStatVO::getCount).sum();

        // 计算占比
        addressStats.forEach(stat -> {
            stat.setPercentage(total > 0 ? Math.round((stat.getCount() * 100.0) / total * 100.0) / 100.0 : 0.0);
        });
        statisticsVO.setAddressStats(addressStats);

        // 按时间统计
        List<RepairStatisticsVO.TimeStatVO> timeStats = repairOrderMapper.statByTime(params);
        statisticsVO.setTimeStats(timeStats);

        // 按状态统计
        List<RepairStatisticsVO.StatusStatVO> statusStats = repairOrderMapper.statByStatus(params);
        statusStats.forEach(stat -> {
            stat.setPercentage(total > 0 ? Math.round((stat.getCount() * 100.0) / total * 100.0) / 100.0 : 0.0);
        });
        statisticsVO.setStatusStats(statusStats);

        // 总统计
        RepairStatisticsVO.TotalStatVO totalStat = repairOrderMapper.statTotal(params);
        statisticsVO.setTotalStat(totalStat);

        return statisticsVO;
    }


    /**
     * 导出功能
     */
    public void exportRepairOrders(RepairOrderPageQueryDTO queryDTO, HttpServletResponse response) {
        try {
            // 设置响应头
            setupResponse(response);
            // 获取导出数据
            List<RepairOrderExportDTO> exportData = getExportData(queryDTO);
            // 写入Excel
            writeExcel(response, exportData);
        } catch (IOException e) {
            throw new RuntimeException("导出Excel失败", e);
        }
    }

    private void setupResponse(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("故障报修数据", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
    }

    private List<RepairOrderExportDTO> getExportData(RepairOrderPageQueryDTO queryDTO) {
        // 查询报修订单数据
        List<RepairOrderVO> repairOrders = repairOrderMapper.pageQueryWithJoin(queryDTO);

        return repairOrders.stream().map(this::convertToExportDTO).collect(Collectors.toList());
    }

    private RepairOrderExportDTO convertToExportDTO(RepairOrderVO order) {
        RepairOrderExportDTO exportDTO = new RepairOrderExportDTO();
        exportDTO.setUserName(order.getNickName());
        exportDTO.setAddress(order.getAddress());
        exportDTO.setDescription(order.getDescription());
        exportDTO.setUserPhone(order.getUserPhone());
        exportDTO.setReportTime(formatDate(order.getCreateTime()));
        exportDTO.setRepairProcess(order.getRepairProcess());
        exportDTO.setRepairTime(formatDate(order.getRepairTime()));
        exportDTO.setRepairmanName(order.getRepairmanName());

        // 查询用户账号
        String userAccount = getNormalUserAccount(order.getNickName());
        exportDTO.setUserAccount(userAccount);

        // 查询维修人员账号
        if (order.getRepairmanName() != null) {
            String repairmanAccount = getRepairmanAccount(order.getRepairmanName());
            exportDTO.setRepairmanAccount(repairmanAccount);
        }
        // 状态转换
        exportDTO.setStatus(convertStatus(order.getStatus()));

        return exportDTO;
    }

    private String getNormalUserAccount(String nickname) {
        if (nickname == null) return "";
        User user = userMapper.getNormalUserByNickname(nickname);
        return user != null ? user.getUsername() : "";
    }
    private String getRepairmanAccount(String nickname) {
        if (nickname == null) return "";
        User user = userMapper.getAdminOrRepairmanByNickname(nickname);
        return user != null ? user.getUsername() : "";
    }

    private void writeExcel(HttpServletResponse response, List<RepairOrderExportDTO> data) throws IOException {
        EasyExcel.write(response.getOutputStream(), RepairOrderExportDTO.class)
                .sheet("故障报修数据")
                .doWrite(data);
    }

    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    private String formatDate(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) return "";

        try {
            // 尝试解析带T的ISO格式
            if (dateTimeStr.contains("T")) {
                LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr,
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            // 尝试解析空格分隔的格式
            else if (dateTimeStr.contains(" ")) {
                LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                return dateTimeStr; // 已经是正确格式，直接返回
            }
            // 其他格式直接返回
            else {
                return dateTimeStr;
            }
        } catch (DateTimeParseException e) {
            return dateTimeStr; // 解析失败返回原值
        }
    }

    private String convertStatus(Integer status) {
        if (status == null) return "";
        switch (status) {
            case 0: return "待处理";
            case 1: return "处理中";
            case 2: return "已完成";
            case 3: return "已取消";
            case 4: return "待转发";
            default: return "未知";
        }
    }


    /**
     * 点击详情后返回报修单的信息
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
