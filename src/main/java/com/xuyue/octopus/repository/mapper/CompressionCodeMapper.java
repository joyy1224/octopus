package com.xuyue.octopus.repository.mapper;

import com.xuyue.octopus.entity.CompressionCode;

public interface CompressionCodeMapper {
    void insertSelective(CompressionCode compressionCode);

    void updateByPrimaryKeySelective(CompressionCode compressionCode);
}
