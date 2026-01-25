package com.feirui.oss.sdk.service.impl;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.*;
import com.feirui.oss.sdk.config.CommonFileProperties;
import com.feirui.oss.sdk.constant.SymbolBaseConstant;
import com.feirui.oss.sdk.domain.entity.DiskFile;
import com.feirui.oss.sdk.domain.enums.FileSdkImplEnum;
import com.feirui.oss.sdk.domain.enums.FileSdkRespEnum;
import com.feirui.oss.sdk.domain.model.BaseUploadFileModel;
import com.feirui.oss.sdk.domain.model.UploadFileFromPathModel;
import com.feirui.oss.sdk.exception.FileSdkException;
import com.feirui.oss.sdk.service.FileSdkService;
import com.feirui.oss.sdk.utils.FileAesEncryptUtils;
import com.feirui.oss.sdk.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class OssSdkServiceImpl implements FileSdkService, InitializingBean {
    public static final long BINARY_BASIC_UNIT = 1024L;

    private final CommonFileProperties.AliyunOSS oss;
    private OSS ossClient;

    public OssSdkServiceImpl(CommonFileProperties commonFileProperties) {
        oss = commonFileProperties.getOss();
    }

    @Override
    public void afterPropertiesSet() {
        if (StringUtils.isAnyBlank(oss.getAccessKeyId(), oss.getAccessKeySecret(), oss.getEndpoint())) {
            log.info("未配置OSS存储配置, 跳过OSS初始化");
            return;
        }
        ClientBuilderConfiguration conf = new ClientBuilderConfiguration();
        conf.setCrcCheckEnabled(false);

        ossClient = new OSSClientBuilder().build(
                oss.getEndpoint(),
                oss.getAccessKeyId(),
                oss.getAccessKeySecret(),
                conf);
        // 创建存储空间, 先判斷是否存在
        if (!ossClient.doesBucketExist(oss.getBucketName())) {
            ossClient.createBucket(oss.getBucketName());
        }
    }

    @Override
    public DiskFile uploadFile(BaseUploadFileModel model) throws FileSdkException {
        FileSdkRespEnum.FILE_SDK_PARAM_NULL.assertObjectNotNull(model);

        InputStream byteInput = null;
        DiskFile diskFile = null;
        try (InputStream in = model.getInputStream()) {
            // 加载基本数据
            diskFile = new DiskFile().loadUploadData(model, FileSdkImplEnum.OSS_SDK_NAME.getImplName());

            if (model instanceof UploadFileFromPathModel) {
                File file = new File(((UploadFileFromPathModel) model).getFilePath());
                if (file.length() > oss.getLargeFileSize() * BINARY_BASIC_UNIT * BINARY_BASIC_UNIT) {
                    // 分片上传
                    fragmentUpload(file, diskFile.getPath());
                    return diskFile.addPathPrefix(oss.getBucketName());
                }
            }

            byte[] fileBytes;
            if (diskFile.pwdSwitchFlag()) {
                fileBytes = FileAesEncryptUtils.aesEncryptToByte(in);
            } else {
                fileBytes = FileUtils.inputStream2byte(in);
            }
            // 设置MD5校验, 保证数据上传完整性
            ObjectMetadata meta = new ObjectMetadata();
            String md5 = BinaryUtil.toBase64String(BinaryUtil.calculateMd5(fileBytes));
            meta.setContentMD5(md5);

            byteInput = new ByteArrayInputStream(fileBytes);
            PutObjectResult putObjectResult = ossClient.putObject(oss.getBucketName(), diskFile.getPath(), byteInput, meta);

            FileSdkRespEnum.FILE_UPLOAD_ERROR.assertObjectNotNull(putObjectResult);
            FileSdkRespEnum.FILE_UPLOAD_ERROR.assertStringNotBlank(putObjectResult.getETag());

            // 这边存储数据库的path，需要加上桶名作为前缀
            diskFile.addPathPrefix(oss.getBucketName()).setSize((long) fileBytes.length);
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
            int index = path.indexOf(SymbolBaseConstant.LEFT_SLASH);
            // ossObject包含文件所在的存储空间名称、文件名称、文件元信息以及一个输入流。
            OSSObject ossObject = ossClient.getObject(path.substring(0, index), path.substring(index + 1));
            inputStream = ossObject.getObjectContent();
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
            ossClient.deleteObject(path.substring(0, index), path.substring(index + 1));
        } catch (Exception ex) {
            FileSdkRespEnum.FILE_DELETE_ERROR.assertToException(diskFile, ex);
        }
        return true;
    }

    private void fragmentUpload(File srcFile, String targetName) throws IOException, FileSdkException {
        // 创建InitiateMultipartUploadRequest对象。
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(oss.getBucketName(), targetName);
        // 初始化分片。
        InitiateMultipartUploadResult upresult = ossClient.initiateMultipartUpload(request);
        // 返回uploadId，它是分片上传事件的唯一标识。您可以根据该uploadId发起相关的操作，例如取消分片上传、查询分片上传等。
        String uploadId = upresult.getUploadId();
        // partETags是PartETag的集合。PartETag由分片的ETag和分片号组成。
        List<PartETag> partTags = new ArrayList<>();
        // 每个分片的大小，用于计算文件有多少个分片。单位为字节。
        final long partSize = oss.getPartSize() * 1024 * 1024L;
        // 填写本地文件的完整路径。如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件。
        long fileLength = srcFile.length();
        int partCount = (int) (fileLength / partSize);
        if (fileLength % partSize != 0) {
            partCount++;
        }
        // 遍历分片上传。
        for (int i = 0; i < partCount; i++) {
            long startPos = i * partSize;
            long curPartSize = (i + 1 == partCount) ? (fileLength - startPos) : partSize;
            InputStream instream = new FileInputStream(srcFile);
            // 跳过已经上传的分片。
            long skip = instream.skip(startPos);
            log.debug("跳过大小：{}", skip);
            UploadPartRequest uploadPartRequest = new UploadPartRequest();
            uploadPartRequest.setBucketName(oss.getBucketName());
            uploadPartRequest.setKey(targetName);
            uploadPartRequest.setUploadId(uploadId);
            uploadPartRequest.setInputStream(instream);
            // 设置分片大小。除了最后一个分片没有大小限制，其他的分片最小为100 KB。
            uploadPartRequest.setPartSize(curPartSize);
            // 设置分片号。每一个上传的分片都有一个分片号，取值范围是1~10000，如果超出此范围，OSS将返回InvalidArgument错误码。
            uploadPartRequest.setPartNumber(i + 1);
            // 每个分片不需要按顺序上传，甚至可以在不同客户端上传，OSS会按照分片号排序组成完整的文件。
            UploadPartResult uploadPartResult = ossClient.uploadPart(uploadPartRequest);
            // 每次上传分片之后，OSS的返回结果包含PartETag。PartETag将被保存在partETags中。
            partTags.add(uploadPartResult.getPartETag());
            if (i % 10 == 0) {
                log.info(">>>>>>>>>> 分片上传中: {} : {}", targetName, i + " / " + partCount);
            }
        }
        // 在执行完成分片上传操作时，需要提供所有有效的partETags。OSS收到提交的partETags后，会逐一验证每个分片的有效性。当所有的数据分片验证通过后，OSS将把这些分片组合成一个完整的文件。
        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                new CompleteMultipartUploadRequest(oss.getBucketName(), targetName, uploadId, partTags);
        // 完成分片上传。
        CompleteMultipartUploadResult uploadResult = ossClient.completeMultipartUpload(completeMultipartUploadRequest);

        FileSdkRespEnum.FILE_UPLOAD_ERROR.assertObjectNotNull(uploadResult);
        FileSdkRespEnum.FILE_UPLOAD_ERROR.assertStringNotBlank(uploadResult.getETag());
    }
}
