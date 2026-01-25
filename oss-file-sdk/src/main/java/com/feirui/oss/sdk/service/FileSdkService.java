package com.feirui.oss.sdk.service;

import com.feirui.oss.sdk.constant.FileSuffixConstant;
import com.feirui.oss.sdk.domain.entity.DiskFile;
import com.feirui.oss.sdk.domain.model.BaseUploadFileModel;
import com.feirui.oss.sdk.exception.FileSdkException;
import com.feirui.oss.sdk.factory.UploadFileFactory;
import com.feirui.oss.sdk.utils.FileUtils;

import java.io.InputStream;
import java.io.OutputStream;

public interface FileSdkService {

    /**
     * 上传base64图片，提供默认实现
     *
     * @param file
     * @param DiskFile
     * @return
     * @throws com.feirui.oss.sdk.exception.FileSdkException
     */
    default DiskFile uploadBase64File(String file, DiskFile DiskFile) throws FileSdkException {
        BaseUploadFileModel model = UploadFileFactory.createUploadFileModel(FileUtils.base64ToByte(file), DiskFile.setFileType(FileSuffixConstant.TYPE_JPG));
        return uploadFile(model);
    }

    /**
     * 通用上传文件
     *
     * @param model
     * @return
     * @throws FileSdkException
     */
    DiskFile uploadFile(BaseUploadFileModel model) throws FileSdkException;

    /**
     * 下载文件，返回文件流
     *
     * @param diskFile
     * @return
     * @throws FileSdkException
     */
    InputStream downloadFileToStream(DiskFile diskFile) throws FileSdkException;

    /**
     * 下载文件，返回base64字符串
     *
     * @param diskFile
     * @return
     * @throws FileSdkException
     */
    String downloadFileToBase64(DiskFile diskFile) throws FileSdkException;

    /**
     * 下载文件，返回字节数组
     *
     * @param diskFile
     * @return
     * @throws FileSdkException
     */
    byte[] downloadFileToByte(DiskFile diskFile) throws FileSdkException;

    /**
     * 下载文件到输出流
     *
     * @param diskFile
     * @param output
     * @throws FileSdkException
     */
    void downloadFileToOutput(DiskFile diskFile, OutputStream output) throws FileSdkException;

    /**
     * 下载文件到输入路径
     *
     * @param diskFile
     * @param targetPath
     * @throws FileSdkException
     */
    void downloadFileToPath(DiskFile diskFile, String targetPath) throws FileSdkException;

    /**
     * 删除文件
     *
     * @param diskFile
     * @return
     * @throws FileSdkException
     */
    boolean deleteFile(DiskFile diskFile) throws FileSdkException;

}
