package com.feirui.oss.service;

import cn.hutool.core.date.DateUtil;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.UUID;

public interface CloudStorageService {
    String upload(MultipartFile file) throws Exception;

    default String getObjectName(String originalFilename, String folderPrefix) {
        String date = DateUtil.formatDate(new Date());
        String name = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
        String folderName = folderPrefix + "/" + date + "/";
        return folderName + name;
    }
}