package com.xuyue.octopus.cache;

import com.xuyue.octopus.cache.dto.UrlMapCacheDto;
import com.xuyue.octopus.entity.UrlMap;
import com.xuyue.octopus.infra.common.CacheKey;
import com.xuyue.octopus.infra.common.UrlMapStatus;
import com.xuyue.octopus.infra.util.BeanCopierUtils;
import com.xuyue.octopus.infra.util.JacksonUtils;
import com.xuyue.octopus.repository.UrlMapDao;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class UrlMapCacheManager {

    private final StringRedisTemplate stringRedisTemplate;
//    private final UrlMapDao urlMapDao;

    private final Function<UrlMap, UrlMapCacheDto> function = urlMap -> {
        UrlMapCacheDto urlMapCacheDto = new UrlMapCacheDto();
        //Bean拷贝
        BeanCopierUtils.X.copy(urlMap, urlMapCacheDto);
        urlMapCacheDto.setEnable(UrlMapStatus.AVAILABLE.getValue().equals(urlMap.getUrlStatus()));
        return urlMapCacheDto;
    };

    public void refreshUrlMapCache(UrlMap urlMap) {
        if (null != urlMap) {
            refreshUrlMapCache(function.apply(urlMap));
        }
    }

    private void refreshUrlMapCache(UrlMapCacheDto dto) {
        HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
        hashOperations.put(CacheKey.ACCESS_CODE_HASH.getKey(), dto.getCompressionCode(), JacksonUtils.X.format(dto));
    }
}
