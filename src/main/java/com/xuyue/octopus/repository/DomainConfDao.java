package com.xuyue.octopus.repository;

import com.xuyue.octopus.entity.DomainConf;
import com.xuyue.octopus.repository.mapper.DomainConfMapper;

public interface DomainConfDao extends DomainConfMapper {
    DomainConf selectByDomain(String domain);
}
