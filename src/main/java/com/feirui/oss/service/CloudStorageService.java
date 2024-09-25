package com.feirui.oss.service;

import com.feirui.oss.domain.dto.UploadFileDto;
import com.feirui.oss.domain.model.DiskFileModel;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;

public interface CloudStorageService {
    String upload(UploadFileDto uploadFileDto, Consumer<DiskFileModel> callback) throws Exception;

    default InputStream downloadFileToStream(DiskFileModel diskFile) throws Exception {
        return null;
    }

    default String downloadFileToBase64(DiskFileModel diskFile) throws Exception {
        return null;
    }

    default byte[] downloadFileToByte(DiskFileModel diskFile) throws Exception {
        return null;
    }

    default void downloadFileToOutput(DiskFileModel diskFile, OutputStream output) throws Exception {
    }

    default void downloadFileToPath(DiskFileModel diskFile, String targetPath) throws Exception {
    }
}