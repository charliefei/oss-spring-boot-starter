package com.feirui.oss.sdk.factory;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson2.JSON;
import com.feirui.oss.sdk.domain.entity.DiskFile;
import com.feirui.oss.sdk.domain.enums.FileSdkRespEnum;
import com.feirui.oss.sdk.domain.model.BaseUploadFileModel;
import com.feirui.oss.sdk.domain.model.UploadFileFromByteArrModel;
import com.feirui.oss.sdk.domain.model.UploadFileFromPathModel;
import com.feirui.oss.sdk.domain.model.UploadFileFromStreamModel;
import com.feirui.oss.sdk.exception.FileSdkException;
import com.feirui.oss.sdk.utils.ContentTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Objects;

public class UploadFileFactory {

    /**
     * 创建输入流上传参数对象
     *
     * @param input    输入流
     * @param diskFile 必须属性: fileType
     */
    public static BaseUploadFileModel createUploadFileModel(InputStream input, DiskFile diskFile) {
        UploadFileFromStreamModel result = new UploadFileFromStreamModel();
        BeanUtils.copyProperties(diskFile, result);
        return result.setFileInput(input);
    }

    /**
     * 创建路径上传参数对象
     *
     * @param path     路径
     * @param diskFile 必须属性: 无
     */
    public static BaseUploadFileModel createUploadFileModel(String path, DiskFile diskFile) {
        UploadFileFromPathModel result = new UploadFileFromPathModel();
        BeanUtils.copyProperties(diskFile, result);
        String fileName = new File(path).getName();
        String fileType = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        String mimeType = ContentTypes.determineContentType(fileName, FileUtil.getInputStream(path));
        return result.setFilePath(path).setFileType(fileType).setMimeType(mimeType).setFileName(fileName);
    }

    /**
     * 创建字节数组上传参数对象
     *
     * @param byteArr  字节数组
     * @param diskFile 必须属性: fileType
     */
    public static BaseUploadFileModel createUploadFileModel(byte[] byteArr, DiskFile diskFile) {
        UploadFileFromByteArrModel result = JSON.toJavaObject(diskFile, UploadFileFromByteArrModel.class);
        assert result != null;
        return result.setFileByte(byteArr);
    }

    /**
     * 创建MultipartFile上传参数对象
     *
     * @param file     multipartFile
     * @param diskFile 必须属性: 无
     */
    public static BaseUploadFileModel createUploadFileModel(MultipartFile file, DiskFile diskFile) throws IOException, FileSdkException {
        String fileName = file.getOriginalFilename();
        FileSdkRespEnum.FILE_SDK_PARAM_NULL.assertStringNotBlank(fileName);
        String fileType = Objects.requireNonNull(fileName).substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        String mimeType = ContentTypes.determineContentType(fileName, file.getInputStream());
        return createUploadFileModel(file.getInputStream(), diskFile.setFileType(fileType)).setMimeType(mimeType).setFileName(fileName);
    }

}
