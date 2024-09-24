package com.feirui.oss.service;

import com.feirui.oss.domain.dto.UploadFileDto;

public interface CloudStorageService {
    String upload(UploadFileDto uploadFileDto) throws Exception;
}