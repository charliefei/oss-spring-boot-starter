package com.feirui.oss.service.impl;

import com.aliyun.oss.OSSClient;
import com.feirui.oss.config.OssProperties;
import com.feirui.oss.domain.dto.UploadFileDto;
import com.feirui.oss.service.CloudStorageService;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;

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
    public String upload(UploadFileDto uploadFileDto) throws Exception {
        String objectName = uploadFileDto.getObjectName(ossConfig.getFolderPrefix());
        ossClient.putObject(ossConfig.getBucketName(),
                objectName, new ByteArrayInputStream(uploadFileDto.getFile().getBytes()));
        String filePath = ossConfig.getBaseUrl() + objectName;
        log.info("upload OOS successful: {}", filePath);
        return filePath;
    }

}