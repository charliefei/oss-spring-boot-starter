package com.feirui.oss.service.impl;

import com.feirui.oss.config.OssProperties;
import com.feirui.oss.domain.dto.UploadFileDto;
import com.feirui.oss.domain.model.DiskFileModel;
import com.feirui.oss.service.CloudStorageService;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.UploadResult;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.transfer.TransferManager;
import com.qcloud.cos.transfer.Upload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Slf4j
public class TencentCOSAdapter implements CloudStorageService {

    private static OssProperties.Tencent cosConfig;
    private static TransferManager transferManager;

    public TencentCOSAdapter(OssProperties.Tencent tencent) {
        cosConfig = tencent;
        COSCredentials cred = new BasicCOSCredentials(cosConfig.getAccessKey(), cosConfig.getSecretKey());
        ClientConfig clientConfig = new ClientConfig(new Region(cosConfig.getRegionName()));
        COSClient cosClient = new COSClient(cred, clientConfig);
        transferManager = new TransferManager(cosClient, Executors.newFixedThreadPool(8));
    }

    @Override
    public String upload(UploadFileDto uploadFileDto, Consumer<DiskFileModel> callback) throws Exception {
        String objectName = uploadFileDto.getObjectName(cosConfig.getFolderPrefix());
        File localFile = null;
        try {
            localFile = transferToFile(uploadFileDto.getFile());
            PutObjectRequest putObjectRequest = new PutObjectRequest(cosConfig.getBucketName(), objectName, localFile);
            // 返回一个异步结果Upload, 可同步的调用waitForUploadResult等待upload结束, 成功返回UploadResult, 失败抛出异常
            Upload upload = transferManager.upload(putObjectRequest);
            UploadResult uploadResult = upload.waitForUploadResult();
            String filePath = cosConfig.getBaseUrl() + uploadResult.getKey();
            log.info("upload COS successful: {}", filePath);
            return filePath;
        } finally {
            transferManager.shutdownNow();
            localFile.delete();
        }
    }

    /**
     * 用缓冲区来实现这个转换, 即创建临时文件
     * 使用 MultipartFile.transferTo()
     */
    private static File transferToFile(MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        String prefix = originalFilename.split("\\.")[0];
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        File file = File.createTempFile(prefix, suffix);
        multipartFile.transferTo(file);
        return file;
    }

}