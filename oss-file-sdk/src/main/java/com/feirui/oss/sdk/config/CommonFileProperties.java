package com.feirui.oss.sdk.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "common.file")
@Data
public class CommonFileProperties {
    /**
     * 允许最大文件
     */
    private Long maxFileSize;
    /**
     * 文件存储实现别名
     */
    private String implName;
    /**
     * 可支持的文件格式
     */
    private String suffix;
    /**
     * 父级存储目录
     */
    private String basePath;
    /**
     * 阿里云OSS
     */
    private AliyunOSS oss;
    /**
     * minio
     */
    private Minio minio;

    {
        this.maxFileSize = 500 * 1024 * 1024L;
        this.implName = "localImpl";
        this.suffix = "jpg,jpeg,png,gif,doc,docx,pdf,xls,xlsx,ppt,pptx,zip,mp4,h264,txt,zip,apk,tar,wps";
        this.basePath = "";
        this.oss = new AliyunOSS();
        this.minio = new Minio();
    }

    @Data
    public static class AliyunOSS {
        private Boolean enabled = false;
        /**
         * OSS 访问端点，集群时需提供统一入口
         */
        private String endpoint;
        /**
         * 用户名
         */
        private String accessKeyId;
        /**
         * 密码
         */
        private String accessKeySecret;
        /**
         * 存储桶
         */
        private String bucketName;
        /**
         * 大文件上传切片大小（单位MB）
         */
        private Long partSize = 500L;
        /**
         * 大文件上传切片阈值（单位MB）
         */
        private Long largeFileSize = 200L;
    }

    @Data
    public static class Minio {
        private Boolean enabled = false;
        /**
         * minio端点
         */
        private String endpoint;
        /**
         * minio用户名
         */
        private String accessKey;
        /**
         * minio密码
         */
        private String secretKey;
        /**
         * minio存储桶
         */
        private String bucketName;
        /**
         * 大文件上传切片大小（单位Byte）
         */
        private Long maxPartSize = 5 * 1024 * 1024L;
    }
}