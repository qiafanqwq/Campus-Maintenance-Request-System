package com.cmrs.service;

import com.cmrs.dto.LocationDTO;
import com.cmrs.vo.LocationVO;

import java.util.List;

public interface LocationService {
    /**
     * 新增地址分类
     */
    void addLocation(LocationDTO locationDTO);


    /**
     * 编辑地址分类
     */
    void updateLocation(LocationDTO locationDTO);


    /**
     * 删除地址分类
     */
    void deleteLocation(int id);


    /**
     * 获取地址分类
     */
    List<LocationVO> list();
}
