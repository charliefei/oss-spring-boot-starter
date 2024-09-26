package com.feirui.oss.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author charliefei
 * @version V1.0
 * @description 描述信息
 * @date 2024/07/19 17:22 周五
 */
@Data
@ConfigurationProperties(prefix = "spring.oss")
public class OssProperties {
    /**
     * @see OssType
     */
    private OssType type;

    private Aliyun aliyun = new Aliyun();

    private Tencent tencent = new Tencent();

    private Local local = new Local();

    @Data
    public static class Aliyun {
        private String endpoint;
        private String accessKeyId;
        private String accessKeySecret;
        private String bucketName;
        private String folderPrefix;
    }

    @Data
    public static class Tencent {
        private String endpoint;
        private String accessKey;
        private String secretKey;
        private String regionName;
        private String bucketName;
        private String folderPrefix;
    }

    @Data
    public static class Local {
        private String folderPrefix;
    }
}
