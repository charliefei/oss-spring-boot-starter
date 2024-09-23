package com.feirui.oss.service.impl;

import com.feirui.oss.config.OssProperties;
import com.feirui.oss.service.CloudStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Objects;

@Slf4j
public class LocalOSSAdapter implements CloudStorageService {

    private static OssProperties.Local config = null;

    public LocalOSSAdapter(OssProperties.Local local) {
        config = local;
    }

    @Override
    public String upload(MultipartFile file) throws Exception {
        String objectName = getObjectName(Objects.requireNonNull(file.getOriginalFilename()),
                config.getFolderPrefix());
        InputStream in = file.getInputStream();
        OutputStream out = new FileOutputStream(objectName);
        copyFile(in, out);
        String filePath = "";
        log.info("upload OOS successful: {}", filePath);
        return filePath;
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
