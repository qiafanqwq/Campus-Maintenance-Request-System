package com.cmrs.controller.repairman;

import com.cmrs.dto.RepairOrderDTO;
import com.cmrs.dto.RepairOrderPageQueryDTO;
import com.cmrs.result.PageResult;
import com.cmrs.result.Result;
import com.cmrs.service.RepairRequestsService;
import com.cmrs.vo.RepairOrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/repairman/repairRequests")
@Tag(name = "报修信息接口")
@Slf4j
public class RepairRequestsController {
    @Autowired
    private RepairRequestsService repairRequestsService;

    /**
     * 报修信息分页查询
     * @param repairOrderPageQueryDTO
     * @return
     */
    @GetMapping("/pageQuery")
    @Operation(summary = "报修信息分页查询")
    public Result<PageResult> pageRepair(@Parameter(description = "报修信息分页查询参数")
                                            @ParameterObject RepairOrderPageQueryDTO repairOrderPageQueryDTO) {
        log.info("报修信息分页查询，参数为:{}", repairOrderPageQueryDTO);
        PageResult pageResult = repairRequestsService.pageQuery(repairOrderPageQueryDTO);
        return Result.success(pageResult);
    }


    /**
     * 维修反馈(改变维修状态、填写维修过程、维修时间)
     * @param repairOrderDTO
     * @return
     */
    @PutMapping
    @Operation(summary = "维修反馈(改变维修状态、填写维修过程、维修时间)")
    public Result updateRepairOrder(@RequestBody RepairOrderDTO repairOrderDTO) {
        log.info("维修反馈：{}",repairOrderDTO);
        repairRequestsService.updateRepairOrder(repairOrderDTO);
        return Result.success();
    }

    /**
     * 点击详情获取报修单信息（包括转发）
     * @param orderId
     * @return
     */
    @GetMapping("/detail/{orderId}")
    public Result getRepairOrderDetail(@PathVariable Long orderId) {
        List<RepairOrderVO> result = repairRequestsService.getRepairOrderDetail(orderId);
        return Result.success(result);
    }
}
