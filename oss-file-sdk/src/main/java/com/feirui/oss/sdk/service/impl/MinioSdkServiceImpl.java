package com.feirui.oss.sdk.service.impl;

import com.feirui.oss.sdk.config.CommonFileProperties;
import com.feirui.oss.sdk.constant.SymbolBaseConstant;
import com.feirui.oss.sdk.domain.entity.DiskFile;
import com.feirui.oss.sdk.domain.enums.FileSdkImplEnum;
import com.feirui.oss.sdk.domain.enums.FileSdkRespEnum;
import com.feirui.oss.sdk.domain.model.BaseUploadFileModel;
import com.feirui.oss.sdk.exception.FileSdkException;
import com.feirui.oss.sdk.service.FileSdkService;
import com.feirui.oss.sdk.utils.FileAesEncryptUtils;
import com.feirui.oss.sdk.utils.FileUtils;
import com.feirui.oss.sdk.utils.MinioUtils;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

@Slf4j
public class MinioSdkServiceImpl implements FileSdkService, InitializingBean {
    private final CommonFileProperties.Minio minio;
    private MinioUtils minioUtils;

    public MinioSdkServiceImpl(CommonFileProperties commonFileProperties) {
        minio = commonFileProperties.getMinio();
    }

    @Override
    public void afterPropertiesSet() {
        if (StringUtils.isAnyBlank(minio.getEndpoint(), minio.getAccessKey(), minio.getSecretKey())) {
            log.info("未配置MinIO存储配置, 跳过MinIO初始化");
            return;
        }
        MinioClient minioClient = MinioClient.builder()
                .endpoint(minio.getEndpoint())
                .credentials(minio.getAccessKey(), minio.getSecretKey())
                .build();
        minioUtils = new MinioUtils(minioClient);
        // 提前创建好minio的桶
        minioUtils.makeBucket(minio.getBucketName());
    }

    @Override
    public DiskFile uploadFile(BaseUploadFileModel model) throws FileSdkException {
        FileSdkRespEnum.FILE_SDK_PARAM_NULL.assertObjectNotNull(model);

        InputStream byteInput = null;
        DiskFile diskFile = null;
        try (InputStream in = model.getInputStream()) {
            // 加载基本数据
            diskFile = new DiskFile().loadUploadData(model, FileSdkImplEnum.MINIO_SDK_NAME.getImplName());

            byte[] fileBytes;
            if (diskFile.pwdSwitchFlag()) {
                fileBytes = FileAesEncryptUtils.aesEncryptToByte(in);
            } else {
                fileBytes = FileUtils.inputStream2byte(in);
            }

            long fileSize = fileBytes.length;
            byteInput = new ByteArrayInputStream(fileBytes);
            Boolean success = minioUtils.putObject(minio.getBucketName(), diskFile.getPath(), byteInput, fileSize, minio.getMaxPartSize());
            FileSdkRespEnum.FILE_UPLOAD_ERROR.assertTrue(success);

            // 这边存储数据库的path，需要加上桶名作为前缀
            diskFile.addPathPrefix(minio.getBucketName()).setSize((long) fileBytes.length);
        } catch (Exception e) {
            FileSdkRespEnum.FILE_UPLOAD_ERROR.assertToException(model, e);
        } finally {
            FileUtils.closeInputStream(byteInput);
        }
        return diskFile;
    }

    @Override
    public InputStream downloadFileToStream(DiskFile diskFile) throws FileSdkException {
        FileSdkRespEnum.FILE_SDK_PARAM_NULL.assertObjectNotNull(diskFile);

        String path = diskFile.getPath();
        InputStream inputStream = null;
        try {
            // 根据存储路径，解析出桶名和OSS存储路径
            int index = path.indexOf(SymbolBaseConstant.LEFT_SLASH);
            String bucketName = path.substring(0, index);
            String ossFilePath = path.substring(index + 1);

            inputStream = minioUtils.getObject(bucketName, ossFilePath);

            // 文件解密
            if (diskFile.pwdSwitchFlag()) {
                inputStream = FileAesEncryptUtils.aesDecryptToStream(inputStream);
            }
        } catch (Exception e) {
            FileSdkRespEnum.FILE_DOWNLOAD_ERROR.assertToException(diskFile, e);
        }
        return inputStream;
    }

    @Override
    public String downloadFileToBase64(DiskFile diskFile) throws FileSdkException {
        return FileUtils.byteToBase64(downloadFileToByte(diskFile));
    }

    @Override
    public byte[] downloadFileToByte(DiskFile diskFile) throws FileSdkException {
        return FileUtils.inputStream2byte(downloadFileToStream(diskFile));
    }

    @Override
    public void downloadFileToOutput(DiskFile diskFile, OutputStream output) throws FileSdkException {
        FileUtils.copyFile(downloadFileToStream(diskFile), output);
    }

    @Override
    public void downloadFileToPath(DiskFile diskFile, String targetPath) throws FileSdkException {
        FileUtils.copyFile(downloadFileToStream(diskFile), targetPath);
    }

    @Override
    public boolean deleteFile(DiskFile diskFile) throws FileSdkException {
        FileSdkRespEnum.FILE_SDK_PARAM_NULL.assertObjectNotNull(diskFile);

        String path = diskFile.getPath();
        try {
            // 根据存储路径，解析出桶名和OSS存储路径
            int index = path.indexOf(SymbolBaseConstant.LEFT_SLASH);
            String bucketName = path.substring(0, index);
            String ossFilePath = path.substring(index + 1);

            minioUtils.removeObject(bucketName, ossFilePath);
            return true;
        } catch (Exception ex) {
            FileSdkRespEnum.FILE_DELETE_ERROR.assertToException(diskFile, ex);
            return false;
        }
    }

}
