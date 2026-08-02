package com.cmrs.controller.user;

import com.cmrs.dto.RepairOrderDTO;
import com.cmrs.dto.RepairOrderPageQueryDTO;
import com.cmrs.exception.BaseException;
import com.cmrs.result.PageResult;
import com.cmrs.result.Result;
import com.cmrs.service.RepairServiceService;
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
@RequestMapping("/user/repairService")
@Slf4j
@Tag(name = "用户端-报修服务接口")
public class RepairServiceController {
    @Autowired
    private RepairServiceService repairServiceService;

    /**
     * 新增报修单
     * @param repairOrderDTO
     * @return
     */
    @PostMapping
    @Operation(summary = "用户申请报修")
    public Result addRepairOrder(@RequestBody RepairOrderDTO repairOrderDTO) {
        log.info("新增报修单：{}",repairOrderDTO);
        try {
            repairServiceService.addRepairOrder(repairOrderDTO);
            return Result.success();
        }catch (BaseException be){
            return Result.error(be.getMessage());
        }
    }


    /**
     * 报修信息分页查询
     * @param repairOrderPageQueryDTO
     * @return
     */
    @GetMapping("/pageQuery")
    @Operation(summary = "我的报修情况分页查询")
    public Result<PageResult> pageRepairOrder(@Parameter(description = "我的报修情况分页查询参数")
                                         @ParameterObject RepairOrderPageQueryDTO repairOrderPageQueryDTO) {
        log.info("我的报修情况分页查询，参数为:{}", repairOrderPageQueryDTO);
        PageResult pageResult = repairServiceService.pageQuery(repairOrderPageQueryDTO);
        return Result.success(pageResult);
    }


    /**
     * 点击详情获取报修单信息（包括转发）
     * @param orderId
     * @return
     */
    @GetMapping("/detail/{orderId}")
    public Result getRepairOrderDetail(@PathVariable Long orderId) {
        List<RepairOrderVO> result = repairServiceService.getRepairOrderDetail(orderId);
        return Result.success(result);
    }


    /**
     * 用户进行反馈
     * @param repairOrderDTO
     * @return
     */
    @PutMapping("/feedback")
    public Result feedback(@RequestBody RepairOrderDTO repairOrderDTO){
        log.info("用户报修单反馈：{}",repairOrderDTO);
        repairServiceService.feedback(repairOrderDTO);
        return Result.success();
    }
}
