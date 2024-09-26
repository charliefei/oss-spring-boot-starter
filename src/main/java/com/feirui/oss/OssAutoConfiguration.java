package com.feirui.oss;

import com.feirui.oss.config.OssProperties;
import com.feirui.oss.service.CloudStorageService;
import com.feirui.oss.service.impl.AliyunOSSAdapter;
import com.feirui.oss.service.impl.LocalOSSAdapter;
import com.feirui.oss.service.impl.TencentCOSAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author charliefei
 * @version V1.0
 * @description 描述信息
 * @date 2024/07/19 17:21 周五
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(OssProperties.class)
@ConditionalOnClass(CloudStorageService.class)
public class OssAutoConfiguration {
    @Bean
    @ConditionalOnProperty(prefix = "spring.oss", name = "type", havingValue = "aliyun")
    public CloudStorageService aliyunOSS(OssProperties ossProperties) {
        return new AliyunOSSAdapter(ossProperties.getAliyun());
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.oss", name = "type", havingValue = "tencent")
    public CloudStorageService tencentCOS(OssProperties ossProperties) {
        return new TencentCOSAdapter(ossProperties.getTencent());
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.oss", name = "type", havingValue = "local")
    public CloudStorageService localOSS(OssProperties ossProperties) {
        return new LocalOSSAdapter(ossProperties.getLocal());
    }
}
