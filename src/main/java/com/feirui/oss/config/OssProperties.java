package com.feirui.oss.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Copyright (C), 群杰物联
 *
 * @author charliefei
 * @version V1.0
 * @description 描述信息
 * @date 2024/07/19 17:22 周五
 */
@Data
@ConfigurationProperties(prefix = "oss")
public class OssProperties {
    /**
     * @see OssType
     */
    private OssType type;

    private Aliyun aliyun = new Aliyun();

    private Tencent tencent = new Tencent();

    @Data
    public static class Aliyun {
        private String baseUrl;
        private String endpoint;
        private String accessKeyId;
        private String accessKeySecret;
        private String bucketName;
        private String folderPrefix;
    }

    @Data
    public static class Tencent {
        private String baseUrl;
        private String accessKey;
        private String secretKey;
        private String regionName;
        private String bucketName;
        private String folderPrefix;
    }
}
