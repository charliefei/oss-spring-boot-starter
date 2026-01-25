package com.feirui.oss.sdk.utils;

import io.minio.*;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.util.Objects;

public class MinioUtils {

    private final MinioClient minioClient;

    public MinioUtils(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    /**
     * 桶是否存在
     *
     * @param bucketName 桶名
     * @return 是否存在
     */
    @SneakyThrows
    public Boolean bucketExists(String bucketName) {
        return minioClient.bucketExists(
                BucketExistsArgs.builder()
                        .bucket(bucketName)
                        .build());
    }

    /**
     * 创建存储桶
     *
     * @param bucketName 桶名
     */
    @SneakyThrows
    public void makeBucket(String bucketName) {
        if (!bucketExists(bucketName)) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucketName)
                            .build());
        }
    }

    /**
     * GetObject接口用于获取某个文件（Object）。此操作需要对此Object具有读权限。
     *
     * @param bucketName  桶名
     * @param ossFilePath Oss文件路径
     */
    @SneakyThrows
    public InputStream getObject(String bucketName, String ossFilePath) {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(ossFilePath)
                        .build());
    }

    /**
     * 上传文件
     *
     * @param bucketName  桶名
     * @param ossFilePath Oss文件路径
     * @param is          文件输入流
     * @param fileSize     文件大小
     * @param partSize     分片大小
     */
    @SneakyThrows
    public Boolean putObject(String bucketName, String ossFilePath, InputStream is, Long fileSize, Long partSize) {
        ObjectWriteResponse response = minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(ossFilePath)
                        .stream(is, fileSize, partSize)
                        .build());
        return Objects.nonNull(response) && response.etag() != null;
    }

    /**
     * 删除文件
     *
     * @param bucketName  桶名
     * @param ossFilePath Oss文件路径
     */
    @SneakyThrows
    public void removeObject(String bucketName, String ossFilePath) {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(ossFilePath)
                        .build());
    }

}
