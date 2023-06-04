package com.xuyue.octopus.repository.mapper;


import com.xuyue.octopus.entity.UrlMap;
import com.xuyue.octopus.entity.UrlMapExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UrlMapMapper {

    void insertSelective(UrlMap urlMap);

}