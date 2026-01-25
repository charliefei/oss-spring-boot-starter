package com.feirui.oss.sdk.domain.enums;

import com.feirui.oss.sdk.exception.CustomAssert;
import com.feirui.oss.sdk.exception.FileSdkException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum FileSdkRespEnum implements CustomAssert<FileSdkException> {

    FILE_SDK_PARAM_NULL(10001, "文件sdk参数不能为空"),
    FILE_UPLOAD_ERROR(10002, "文件上传失败"),
    FILE_DOWNLOAD_ERROR(10003, "文件下载失败"),
    FILE_ENCRYPT_ERROR(10004, "文件加密失败"),
    FILE_DECRYPT_ERROR(10005, "文件解密失败"),
    FILE_DELETE_ERROR(10006, "文件删除失败"),
    FILE_SHOW_ERROR(10007, "文件预览失败"),
    FILE_TYPE_NOT_SUPPORT(10008, "不支持该文件格式"),
    FILE_DISK_NULL_ERROR(10009, "文件不存在或已删除"),
    UPLOAD_CHUNK_FILE_NOT_FOUND(100010, "分片文件不存在，请重新上传"),
    UPLOAD_CHUNK_FILE_INSUFFICIENT(100011, "缺少分片%d，无法合并文件");

    private int code;
    private String msg;

}
