package com.cmrs.vo;

import lombok.Data;
import java.util.List;

/**
 * 统计结果VO
 */
@Data
public class RepairStatisticsVO {
    // 按故障地址统计
    private List<AddressStatVO> addressStats;
    // 按处理时间统计（按天）
    private List<TimeStatVO> timeStats;
    // 按报修状态统计
    private List<StatusStatVO> statusStats;
    // 总统计
    private TotalStatVO totalStat;

    /**
     * 按地址统计子VO
     */
    @Data
    public static class AddressStatVO {
        private String address; // 故障地址
        private Integer count; // 数量
        private Double percentage; // 占比（%）
    }

    /**
     * 按时间统计子VO
     */
    @Data
    public static class TimeStatVO {
        private String date; // 日期（yyyy-MM-dd）
        private Integer count; // 数量
    }

    /**
     * 按状态统计子VO
     */
    @Data
    public static class StatusStatVO {
        private Integer status; // 状态值
        private String statusName; // 状态名称
        private Integer count; // 数量
        private Double percentage; // 占比（%）
    }

    /**
     * 总统计子VO
     */
    @Data
    public static class TotalStatVO {
        private Integer totalCount; // 总数量
        private Integer pendingCount; // 待处理数量
        private Integer processingCount; // 处理中数量
        private Integer completedCount; // 已完成数量
        private Integer cancelledCount; // 已取消数量
        private Integer pendingTransferCount;
    }
}