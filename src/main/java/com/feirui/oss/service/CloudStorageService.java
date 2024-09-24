package com.feirui.oss.service;

import com.feirui.oss.domain.dto.UploadFileDto;
import com.feirui.oss.domain.model.DiskFileModel;

import java.util.function.Consumer;

public interface CloudStorageService {
    String upload(UploadFileDto uploadFileDto, Consumer<DiskFileModel> callback) throws Exception;
}