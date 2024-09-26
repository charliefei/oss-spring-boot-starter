package com.feirui.oss.service.impl;

import com.feirui.oss.config.OssProperties;
import com.feirui.oss.domain.dto.UploadFileDto;
import com.feirui.oss.domain.model.DiskFileModel;
import com.feirui.oss.service.CloudStorageService;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
public class MinioOSSAdapter implements CloudStorageService {
    private static OssProperties.Minio config;

    private static MinioClient minioClient;

    public MinioOSSAdapter(OssProperties.Minio minio) {
        config = minio;
        minioClient = MinioClient.builder()
                .endpoint(minio.getEndpoint())
                .credentials(minio.getAccessKey(), minio.getSecretKey())
                .build();
    }

    @Override
    public String upload(UploadFileDto uploadFileDto, Consumer<DiskFileModel> callback) throws Exception {
        String objectName = uploadFileDto.getObjectName(config.getFolderPrefix());
        createBucket(config.getBucketName());
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(config.getBucketName())
                .object(objectName)
                .stream(uploadFileDto.getFile().getInputStream(), -1, 5242889L)
                .build());
        String filePath = getUrl(objectName);
        log.info("upload minioOOS successful: {}", filePath);
        return filePath;
    }

    public void createBucket(String bucket) throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }

    /**
     * 获取文件路径
     */
    public String getUrl(String objectName) {
        return config.getEndpoint() + "/" + config.getBucketName() + "/" + objectName;
    }
}
