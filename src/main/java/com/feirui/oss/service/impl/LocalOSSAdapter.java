package com.feirui.oss.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.feirui.oss.config.OssProperties;
import com.feirui.oss.config.OssType;
import com.feirui.oss.domain.dto.UploadFileDto;
import com.feirui.oss.domain.model.DiskFileModel;
import com.feirui.oss.service.CloudStorageService;
import com.feirui.oss.utils.QunjeEncryptUtils;
import com.feirui.oss.utils.QunjeFileUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Consumer;

@Slf4j
public class LocalOSSAdapter implements CloudStorageService {

    private static OssProperties.Local config = null;

    public LocalOSSAdapter(OssProperties.Local local) {
        config = local;
    }

    @Override
    public String upload(UploadFileDto uploadFileDto, Consumer<DiskFileModel> callback) throws Exception {
        String objectName = uploadFileDto.getObjectName(config.getFolderPrefix());
        log.info("localOSS upload file path: {}", objectName);
        InputStream in = uploadFileDto.getInputStream();
        OutputStream out = Files.newOutputStream(Paths.get(objectName));
        copyFile(in, out);
        DiskFileModel diskFileModel = new DiskFileModel();
        BeanUtil.copyProperties(uploadFileDto, diskFileModel);
        diskFileModel.setId(uploadFileDto.getFileId());
        diskFileModel.setPath(objectName);
        diskFileModel.setSize(uploadFileDto.getFileSize());
        diskFileModel.setOssType(OssType.local.getDesc());
        callback.accept(diskFileModel);
        log.info("localOSS upload file successfully: {}", objectName);
        return "disk-" + uploadFileDto.getFileName();
    }

    @Override
    public InputStream downloadFileToStream(DiskFileModel diskFile) throws Exception {
        InputStream input;
        input = Files.newInputStream(Paths.get(diskFile.getPath()));
        if (diskFile.getPwdSwitch()) {
            input = QunjeEncryptUtils.aesDecryptToStream(input);
        }
        return input;
    }

    @Override
    public String downloadFileToBase64(DiskFileModel diskFile) throws Exception {
        return QunjeFileUtils.byteToBase64(downloadFileToByte(diskFile));
    }

    @Override
    public byte[] downloadFileToByte(DiskFileModel diskFile) throws Exception {
        byte[] result;
        InputStream in;
        in = Files.newInputStream(Paths.get(diskFile.getPath()));
        if (diskFile.getPwdSwitch()) {
            result = QunjeEncryptUtils.aesDecryptToByte(in);
        } else {
            result = QunjeFileUtils.inputStream2byte(in);
        }
        QunjeFileUtils.closeInputStream(in);
        return result;
    }

    @Override
    public void downloadFileToOutput(DiskFileModel diskFile, OutputStream output) throws Exception {
        InputStream in;
        in = Files.newInputStream(Paths.get(diskFile.getPath()));
        if (diskFile.getPwdSwitch()) {
            QunjeFileUtils.copyFile(QunjeEncryptUtils.aesDecryptToStream(in), output);
        } else {
            QunjeFileUtils.copyFile(in, output);
        }
        QunjeFileUtils.closeInputStream(in);
    }

    @Override
    public void downloadFileToPath(DiskFileModel diskFile, String targetPath) throws Exception {
        downloadFileToOutput(diskFile, Files.newOutputStream(Paths.get(targetPath)));
    }

    private static void copyFile(InputStream in, OutputStream out) {
        int len;
        try (BufferedInputStream bis = new BufferedInputStream(in);
             BufferedOutputStream bos = new BufferedOutputStream(out)) {
            while ((len = bis.read()) != -1) {
                bos.write(len);
            }
        } catch (Exception ex) {
            log.error("复制文件失败 >>>>> ", ex);
        }
    }
}
