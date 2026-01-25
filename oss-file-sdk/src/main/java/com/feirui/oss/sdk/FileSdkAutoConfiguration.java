package com.feirui.oss.sdk;

import com.feirui.oss.sdk.config.CommonFileProperties;
import com.feirui.oss.sdk.constant.FileSdkConstant;
import com.feirui.oss.sdk.factory.FileSdkImplFactory;
import com.feirui.oss.sdk.service.FileSdkService;
import com.feirui.oss.sdk.service.impl.LocalSdkServiceImpl;
import com.feirui.oss.sdk.service.impl.MinioSdkServiceImpl;
import com.feirui.oss.sdk.service.impl.OssSdkServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.Map;

@ConditionalOnBean(CommonFileProperties.class)
@EnableConfigurationProperties({CommonFileProperties.class})
public class FileSdkAutoConfiguration {

    @Bean
    public FileSdkConstant fileSdkConstant(CommonFileProperties commonFileProperties) {
        return new FileSdkConstant(commonFileProperties);
    }

    @Bean("localImpl")
    public FileSdkService localSdkService() {
        return new LocalSdkServiceImpl();
    }

    @Bean("ossImpl")
    @ConditionalOnProperty(value = "common.file.oss.enabled", havingValue = "true")
    public FileSdkService ossSdkService(CommonFileProperties commonFileProperties) {
        return new OssSdkServiceImpl(commonFileProperties);
    }

    @Bean("minioImpl")
    @ConditionalOnProperty(value = "common.file.minio.enabled", havingValue = "true")
    public FileSdkService minioSdkService(CommonFileProperties commonFileProperties) {
        return new MinioSdkServiceImpl(commonFileProperties);
    }

    @Bean
    @ConditionalOnBean(FileSdkService.class)
    public FileSdkImplFactory fileSdkImplFactory(CommonFileProperties commonFileProperties,
                                                 Map<String, FileSdkService> sdkImplMap) {
        return new FileSdkImplFactory(commonFileProperties, sdkImplMap);
    }

}
