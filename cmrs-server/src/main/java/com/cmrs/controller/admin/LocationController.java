package com.cmrs.controller.admin;

import com.cmrs.dto.LocationDTO;
import com.cmrs.result.Result;
import com.cmrs.service.LocationService;
import com.cmrs.vo.LocationVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin/Location")
@Slf4j
@Tag(name = "地址分类接口", description = "地址分类相关接口")
public class LocationController {
    @Autowired
    private LocationService locationService;

    /**
     * 新增地址分类
     * @param locationDTO
     * @return
     */
    @PostMapping
    @Operation(summary = "新增地址分类", description = "新增地址分类接口")
    public Result addLocation(@RequestBody LocationDTO locationDTO){
        log.info("新增地址分类：{}",locationDTO);
        locationService.addLocation(locationDTO);
        return Result.success();
    }

    /**
     * 编辑地址分类
     * @param locationDTO
     * @return
     */
    @PutMapping
    @Operation(summary = "编辑地址分类", description = "编辑地址分类接口")
    public Result updateLocation(@RequestBody LocationDTO locationDTO){
        log.info("编辑地址分类：{}",locationDTO);
        locationService.updateLocation(locationDTO);
        return Result.success();
    }


    /**
     * 删除地址分类
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除地址分类", description = "删除地址分类接口")
    public Result deleteLocation(@PathVariable int id){
        log.info("删除地址分类ID：{}",id);
        locationService.deleteLocation(id);
        return Result.success();
    }


    /**
     * 获取地址分类
     * @return
     */
    @GetMapping
    @Operation(summary = "获取地址分类接口", description = "获取地址分类以展示给用户")
    public Result<List<LocationVO>> listCategory(){
        log.info("获取地址分类接口");
        List<LocationVO> list = locationService.list();
        return Result.success(list);
    }
}
