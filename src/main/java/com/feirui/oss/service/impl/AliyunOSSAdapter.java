package com.feirui.oss.service.impl;

import com.aliyun.oss.OSSClient;
import com.feirui.oss.config.OssProperties;
import com.feirui.oss.service.CloudStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.Objects;

@Slf4j
public class AliyunOSSAdapter implements CloudStorageService {

    private static OssProperties.Aliyun ossConfig = null;
    private static OSSClient ossClient = null;

    public AliyunOSSAdapter(OssProperties.Aliyun aliyun) {
        ossConfig = aliyun;
        ossClient = new OSSClient(ossConfig.getEndpoint(),
                ossConfig.getAccessKeyId(),
                ossConfig.getAccessKeySecret());
    }

    @Override
    public String upload(MultipartFile file) throws Exception {
        String objectName = getObjectName(Objects.requireNonNull(file.getOriginalFilename()),
                ossConfig.getFolderPrefix());
        ossClient.putObject(ossConfig.getBucketName(),
                objectName, new ByteArrayInputStream(file.getBytes()));
        String filePath = ossConfig.getBaseUrl() + objectName;
        log.info("upload OOS successful: {}", filePath);
        return filePath;
    }

}