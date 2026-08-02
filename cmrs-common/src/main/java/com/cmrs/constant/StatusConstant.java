package com.cmrs.constant;

/**
 * 状态常量
 */
public class StatusConstant {
    /**
     * 账户状态 0-禁用  1-启用
     */
    public static final Integer ENABLE = 1;

    public static final Integer DISABLE = 0;

    /**
     * 报修状态：0-待处理，1-处理中，2-已完成，3-已取消，4-待转发
     */
    public static final Integer PENDING = 0;

    public static final Integer IN_PROGRESS = 1;

    public static final Integer COMPLETED = 2;

    public static final Integer CANCELLED = 3;

    public static final Integer FORWARD = 4;

}
