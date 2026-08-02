package com.cmrs.controller.admin;

import com.cmrs.dto.RepairOrderDTO;
import com.cmrs.dto.RepairOrderPageQueryDTO;
import com.cmrs.exception.BaseException;
import com.cmrs.result.PageResult;
import com.cmrs.result.Result;
import com.cmrs.service.FaultListService;
import com.cmrs.vo.RepairOrderVO;
import com.cmrs.vo.RepairStatisticsVO;
import com.cmrs.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin/faultList")
@Slf4j
@Tag(name = "故障列表接口", description = "故障列表相关接口")
public class FaultListController {
    @Autowired
    private FaultListService faultListService;

    /**
     * 故障列表分页查询
     * @param repairOrderPageQueryDTO
     * @return
     */
    @GetMapping("/pageQuery")
    @Operation(summary = "故障列表分页查询", description = "故障列表分页查询接口")
    public Result<PageResult> pageFaultList(@Parameter(description = "故障列表分页查询参数")
                                    @ParameterObject RepairOrderPageQueryDTO repairOrderPageQueryDTO) {
        log.info("故障列表分页查询，参数为:{}", repairOrderPageQueryDTO);
        PageResult pageResult = faultListService.pageQuery(repairOrderPageQueryDTO);
        return Result.success(pageResult);
    }


    /**
     * 新增故障单
     * @param repairOrderDTO
     * @return
     */
    @PostMapping
    @Operation(summary = "新增故障单", description = "新增故障单接口")
    public Result addRepairOrder(@RequestBody RepairOrderDTO repairOrderDTO) {
        log.info("新增报修单：{}",repairOrderDTO);
        try {
            faultListService.addRepairOrder(repairOrderDTO);
            return Result.success();
        }catch (BaseException be){
            return Result.error(be.getMessage());
        }
    }

    /**
     * 删除故障单
     * @param rrId
     * @return
     */
    @DeleteMapping("/{rrId}")
    @Operation(summary = "删除故障单", description = "删除故障单接口")
    public Result deleteRepairOrder(@PathVariable Long rrId) {
        log.info("删除报修单：{}",rrId);
        faultListService.delete(rrId);
        return Result.success();
    }

    /**
     * 编辑故障单信息
     * @param repairOrderDTO
     * @return
     */
    @PutMapping
    @Operation(summary = "编辑故障单信息", description = "编辑故障单信息接口")
    public Result updateRepairOrder(@RequestBody RepairOrderDTO repairOrderDTO) {
        log.info("编辑报修单信息：{}",repairOrderDTO);
        faultListService.update(repairOrderDTO);
        return Result.success();
    }


    /**
     * 分配维修人员
     * @param id
     * @param repairmanName
     * @return
     */
    @PutMapping("/assign")
    @Operation(summary = "分配维修人员", description = "分配维修人员接口")
    public Result assignRepairman(@RequestParam Long id, @RequestParam String repairmanName) {
        log.info("故障单ID和分配维修人员信息：{},{}",id,repairmanName);
        faultListService.assign(id,repairmanName);
        return Result.success();
    }


    /**
     * 转发报修单
     * @param id
     * @param repairmanName
     * @return
     */
    @PutMapping("/transfer")
    @Operation(summary = "转发报修单", description = "转发报修单接口")
    public Result transfer(@RequestParam Long id, @RequestParam String repairmanName, @RequestParam Long rrId) {
        log.info("要转发的报修单ID和维修人员信息和维修记录ID：{},{},{}",id,repairmanName,rrId);
        faultListService.transfer(id,repairmanName,rrId);
        return Result.success();
    }


    /**
     * 列举维修人员
     * @return
     */
    @GetMapping("/listRepairman")
    @Operation(summary = "列举维修人员", description = "列举维修人员接口")
    public Result<List<UserVO>> assignRepairman() {
        log.info("列举维修人员");
        List<UserVO> list = faultListService.listRepairman();
        return Result.success(list);
    }


    /**
     * 获取报修统计数据
     * @param params
     */
    @GetMapping("/statistics")
    public Result<RepairStatisticsVO> getStatistics(RepairOrderPageQueryDTO params) {
        try {
            RepairStatisticsVO statistics = faultListService.getRepairStatistics(params);
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取报修统计失败", e);
            return Result.error("获取统计数据失败");
        }
    }


    /**
     * 导出功能
     * @param queryDTO
     * @param response
     */
    @CrossOrigin
    @PostMapping("/export")
    public void exportRepairOrders(@RequestBody RepairOrderPageQueryDTO queryDTO, HttpServletResponse response) {
        faultListService.exportRepairOrders(queryDTO, response);
    }


    /**
     * 点击详情获取报修单信息（包括转发）
     * @param orderId
     * @return
     */
    @GetMapping("/detail/{orderId}")
    public Result getRepairOrderDetail(@PathVariable Long orderId) {
        List<RepairOrderVO> result = faultListService.getRepairOrderDetail(orderId);
        return Result.success(result);
    }
}
