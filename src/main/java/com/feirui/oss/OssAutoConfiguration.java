package com.feirui.oss;

import com.feirui.oss.config.OssProperties;
import com.feirui.oss.service.CloudStorageService;
import com.feirui.oss.service.impl.AliyunOSSAdapter;
import com.feirui.oss.service.impl.TencentCOSAdapter;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Copyright (C), 群杰物联
 *
 * @author charliefei
 * @version V1.0
 * @description 描述信息
 * @date 2024/07/19 17:21 周五
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(OssProperties.class)
@ConditionalOnClass(CloudStorageService.class)
public class OssAutoConfiguration {
    @Resource
    private OssProperties ossProperties;

    @Bean
    @ConditionalOnProperty(prefix = "oss", name = "type", havingValue = "aliyun")
    public CloudStorageService aliyunOSS() {
        return new AliyunOSSAdapter(ossProperties.getAliyun());
    }

    @Bean
    @ConditionalOnProperty(prefix = "oss", name = "type", havingValue = "tencent")
    public CloudStorageService tencentCOS() {
        return new TencentCOSAdapter(ossProperties.getTencent());
    }
}
