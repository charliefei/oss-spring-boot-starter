package com.feirui.oss.manage.mysql.manager;

import com.feirui.oss.sdk.domain.entity.DiskFile;
import com.feirui.oss.sdk.domain.model.BaseUploadFileModel;
import com.feirui.oss.sdk.exception.FileSdkException;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface FileBaseManager {
    /**
     * 保存文件落盘信息
     */
    boolean saveDiskFile(DiskFile diskFile);

    /**
     * 批量保存文件落盘信息
     */
    boolean saveDiskFileBatch(List<DiskFile> diskFileList);

    /**
     * 移除文件落盘信息
     */
    boolean removeDiskFile(String id);

    /**
     * 批量移除文件落盘信息
     */
    boolean removeDiskFileBatch(List<String> idList);

    /**
     * 根据id查询文件落盘信息
     */
    DiskFile getDiskFileById(String id);

    /**
     * 根据id集合查询文件落盘信息
     */
    List<DiskFile> listDiskFileByIds(List<String> idList);

    /**
     * 上传base64图片，提供默认实现
     */
    DiskFile uploadBase64File(String file, DiskFile diskFile) throws FileSdkException;

    /**
     * 通用上传文件, 返回id
     */
    String uploadReturnId(BaseUploadFileModel model) throws FileSdkException;

    /**
     * 通用上传文件
     */
    DiskFile uploadFile(BaseUploadFileModel model) throws FileSdkException;

    /**
     * 下载文件，返回文件流
     */
    InputStream downloadFileToStream(DiskFile diskFile) throws FileSdkException;

    /**
     * 下载文件，返回MultipartFile
     */
    MultipartFile downloadFileToMultipart(DiskFile diskFile, String fieldName) throws FileSdkException;

    /**
     * 下载文件，返回base64字符串
     */
    String downloadFileToBase64(DiskFile diskFile) throws FileSdkException;

    /**
     * 下载文件，返回字节数组
     */
    byte[] downloadFileToByte(DiskFile diskFile) throws FileSdkException;

    /**
     * 下载文件到输出流
     */
    void downloadFileToOutput(DiskFile diskFile, OutputStream output) throws FileSdkException;

    /**
     * 下载文件到输入路径
     */
    String downloadFileToPath(DiskFile diskFile, String targetPath) throws FileSdkException;

    /**
     * 删除文件
     */
    boolean deleteFile(DiskFile diskFile) throws FileSdkException;

    /**
     * 通过id删除文件
     */
    boolean deleteFileById(String id) throws FileSdkException;

    /**
     * 删除文件
     * 先删除文件存储 然后删除数据库记录
     */
    boolean batchDropFiles(List<DiskFile> diskFiles) throws FileSdkException;

    /**
     * 功能描述: 删除本地临时文件
     **/
    boolean deleteLocalFile(String path);

    /**
     * 统计文件数量及大小
     */
    long countDiskFileSize(String companyCode);

}
