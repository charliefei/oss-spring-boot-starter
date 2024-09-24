package com.feirui.oss.service.impl;

import com.feirui.oss.config.OssProperties;
import com.feirui.oss.domain.dto.UploadFileDto;
import com.feirui.oss.domain.model.DiskFileModel;
import com.feirui.oss.service.CloudStorageService;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
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
        OutputStream out = new FileOutputStream(objectName);
        copyFile(in, out);
        callback.accept(new DiskFileModel());
        log.info("localOSS upload file successfully: {}", objectName);
        return "disk-" + uploadFileDto.getFileName();
    }

    private static void copyFile(InputStream in, OutputStream out) {
        int len;
        try (BufferedInputStream bis = new BufferedInputStream(in);
             BufferedOutputStream bos = new BufferedOutputStream(out);) {
            while ((len = bis.read()) != -1) {
                bos.write(len);
            }
        } catch (Exception ex) {
            log.error("复制文件失败 >>>>> ", ex);
        }
    }
}
