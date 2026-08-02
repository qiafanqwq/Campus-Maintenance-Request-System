package com.cmrs.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class RepairOrderExportDTO {
    @ExcelProperty("姓名")
    private String userName;

    @ExcelProperty("学号")
    private String userAccount;

    @ExcelProperty("故障地址")
    private String address;

    @ExcelProperty("故障描述")
    private String description;

    @ExcelProperty("手机号码")
    private String userPhone;

    @ExcelProperty("报障时间")
    private String reportTime;

    @ExcelProperty("维修人员姓名")
    private String repairmanName;

    @ExcelProperty("维修人员账号")
    private String repairmanAccount;

    @ExcelProperty("处理时间")
    private String repairTime;

    @ExcelProperty("处理过程")
    private String repairProcess;

    @ExcelProperty("报修状态")
    private String status;
}
