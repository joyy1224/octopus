package com.xuyue.octopus.repository;

import com.xuyue.octopus.entity.CompressionCode;
import com.xuyue.octopus.repository.mapper.CompressionCodeMapper;

public interface CompressionCodeDao extends CompressionCodeMapper {
    CompressionCode getLatestAvailableCompressionCode();


}
