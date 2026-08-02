package com.cmrs.service.impl;

import com.cmrs.dto.LocationDTO;
import com.cmrs.entity.Location;
import com.cmrs.mapper.LocationMapper;
import com.cmrs.service.LocationService;
import com.cmrs.vo.LocationVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationServiceImpl implements LocationService {
    @Autowired
    private LocationMapper locationMapper;

    /**
     * 新增地址分类
     */
    public void addLocation(LocationDTO locationDTO) {
        Location location = new Location();
        BeanUtils.copyProperties(locationDTO,location);
        // 获取属于某一分类数量
        int total = locationMapper.getTotalOfLevel(locationDTO.getParentId()) + 1;
        location.setSortOrder(total);
        locationMapper.insert(location);
    }

    /**
     * 编辑地址分类
     */
    public void updateLocation(LocationDTO locationDTO) {
        Location location = new Location();
        BeanUtils.copyProperties(locationDTO,location);
        locationMapper.update(location);
    }

    /**
     * 删除地址分类
     */
    public void deleteLocation(int id) {
        // 删除前先查询是否是根分类
        Location location = locationMapper.getById(id);

        if(location.getParentId() == 0){
            // 如果该分类为根分类,就要查询它的子分类，然后逐个删除
            List<Location> list = locationMapper.getByParentId(id);
            for(Location loc : list){
                locationMapper.delete(loc.getId());
            }
            // 最后删除根分类
            locationMapper.delete(id);
        }else {
            // 如果不是根分类，那就直接删除
            locationMapper.delete(id);
        }
    }


    /**
     * 获取地址分类
     */
    public List<LocationVO> list() {
        return locationMapper.list();
    }
}
