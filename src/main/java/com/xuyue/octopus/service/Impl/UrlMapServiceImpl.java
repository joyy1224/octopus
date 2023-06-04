package com.xuyue.octopus.service.Impl;

import com.xuyue.octopus.cache.UrlMapCacheManager;
import com.xuyue.octopus.entity.CompressionCode;
import com.xuyue.octopus.entity.DomainConf;
import com.xuyue.octopus.entity.UrlMap;
import com.xuyue.octopus.infra.common.CommonConstant;
import com.xuyue.octopus.infra.common.CompressionCodeStatus;
import com.xuyue.octopus.infra.common.LockKey;
import com.xuyue.octopus.infra.support.keygen.SequenceGenerator;
import com.xuyue.octopus.infra.support.lock.DistributedLock;
import com.xuyue.octopus.infra.support.lock.DistributedLockFactory;
import com.xuyue.octopus.infra.util.ConversionUtils;
import com.xuyue.octopus.repository.CompressionCodeDao;
import com.xuyue.octopus.repository.DomainConfDao;
import com.xuyue.octopus.repository.UrlMapDao;
import com.xuyue.octopus.service.UrlMapService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.net.URI;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UrlMapServiceImpl implements UrlMapService {


    private final DomainConfDao domainConfDao;
    private final UrlMapDao urlMapDao;
    private final CompressionCodeDao compressionCodeDao;
    private final UrlMapCacheManager urlMapCacheManager;


//    private UrlMapService self;

    @Value("${compress.code.batch:100}")
    private Integer compressCodeBatch;
    private final SequenceGenerator sequenceGenerator;

    private final DistributedLockFactory distributedLockFactory;


//    @Override
//    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
//        self = beanFactory.getBean(UrlMapServiceImpl.class);
//    }

    private final UrlValidator urlValidator = new UrlValidator(new String[]{CommonConstant.HTTP_PROTOCOL,
            CommonConstant.HTTPS_PROTOCOL});
    @Override
    public String createUrlMap(String domain, UrlMap insertEntity) {
        //加锁
        DistributedLock lock = distributedLockFactory.provideDistributedLock(LockKey.CREATE_URL_MAP.getCode());
        try {
            lock.lock(LockKey.CREATE_URL_MAP.getReleaseTime(), TimeUnit.MILLISECONDS);
            //查询短链limit 1
            CompressionCode compressionCode = getAvailableCompressCode();
            Assert.isTrue(Objects.nonNull(compressionCode) &&
                    CompressionCodeStatus.AVAILABLE.getValue().equals(compressionCode.getCodeStatus()), "压缩码不存在或者已经被使用");
            String longUrl = insertEntity.getLongUrl();
            Assert.isTrue(urlValidator.isValid(insertEntity.getLongUrl()), String.format("链接[%s]非法", longUrl));
            DomainConf domainConf = domainConfDao.selectByDomain(domain);
            Assert.notNull(domainConf, String.format("域名不存在[c:%s]", domain));
            UrlMap urlMap = new UrlMap();
            urlMap.setLongUrl(longUrl);
            String code = compressionCode.getCompressionCode();
            String shortUrl = String.format("%s://%s/%s", domainConf.getProtocol(), domainConf.getDomainValue(), code);
            urlMap.setShortUrl(shortUrl);
            urlMap.setCompressionCode(code);
            urlMap.setUrlStatus(insertEntity.getUrlStatus());
            urlMap.setDescription(insertEntity.getDescription());
            // 长短链的摘要
            urlMap.setShortUrlDigest(DigestUtils.sha1Hex(urlMap.getShortUrl()));
            urlMap.setLongUrlDigest(DigestUtils.sha1Hex(urlMap.getLongUrl()));
            CompressionCode updater = new CompressionCode();
            updater.setCodeStatus(CompressionCodeStatus.USED.getValue());
            updater.setId(compressionCode.getId());

            saveUrlMapAndUpdateCompressCode(urlMap, updater);
            urlMapCacheManager.refreshUrlMapCache(urlMap);
            return shortUrl;
        }finally {
            lock.unlock();
        }
    }

    private CompressionCode getAvailableCompressCode() {
        CompressionCode compressionCode = compressionCodeDao.getLatestAvailableCompressionCode();
        if (Objects.nonNull(compressionCode)) {
            return compressionCode;
        } else {
            generateBatchCompressionCodes();
            return Objects.requireNonNull(compressionCodeDao.getLatestAvailableCompressionCode());
        }
    }

    /**
     * 保存短链映射和更新压缩码状态
     *
     * @param urlMap          urlMap
     * @param compressionCode compressionCode
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveUrlMapAndUpdateCompressCode(UrlMap urlMap, CompressionCode compressionCode) {
        compressionCodeDao.updateByPrimaryKeySelective(compressionCode);
        urlMapDao.insertSelective(urlMap);
    }

    private void generateBatchCompressionCodes() {
        for (int i = 0; i < compressCodeBatch; i++) {
            //生成一个雪花序列
            long sequence = sequenceGenerator.generate();
            CompressionCode compressionCode = new CompressionCode();
            compressionCode.setSequenceValue(String.valueOf(sequence));
            //转换成62位
            String code = ConversionUtils.X.encode62(sequence);
            code = code.substring(code.length() - 6);
            compressionCode.setCompressionCode(code);
            compressionCodeDao.insertSelective(compressionCode);
        }
    }

}
