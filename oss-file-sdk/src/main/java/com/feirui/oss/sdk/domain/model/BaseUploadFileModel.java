package com.feirui.oss.sdk.domain.model;

import com.feirui.oss.sdk.constant.FileSdkConstant;
import com.feirui.oss.sdk.utils.FileAesEncryptUtils;
import com.feirui.oss.sdk.utils.FileUtils;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.InputStream;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@Accessors(chain = true)
public abstract class BaseUploadFileModel {

    /**
     * 文件名
     */
    private String fileName;
    /**
     * 文件类型
     */
    private String fileType;
    /**
     * 文件mime类型
     */
    private String mimeType;
    /**
     * 文件目录(业务相关)
     */
    private String filePackage;
    /**
     * 企业编码
     */
    private String companyCode;
    /**
     * 上传者
     */
    private String creator;
    /**
     * 文件配置对象
     */
    private FileSdkSettingModel fileSettingModel;

    /**
     * 根据加密开关，获取文件流
     */
    public InputStream getInputStream(boolean pwdSwitch) {
        return pwdSwitch ? FileAesEncryptUtils.aesEncryptToStream(getInputStream()) : getInputStream();
    }

    /**
     * 获取文件流
     */
    public abstract InputStream getInputStream();

    /**
     * 获取文件字节数组
     */
    public abstract byte[] getByteArray();

    /**
     * 构建文件上传路径，目录层级固定，跟老版本保持一致
     */
    public String buildPath(String basePath, String fileSeparator) {
        String path = basePath + fileSeparator + this.companyCode + fileSeparator + this.filePackage + fileSeparator + new SimpleDateFormat("yyyy/MM/dd").format(new Date()) + fileSeparator +
                FileSdkConstant.FIRE_PART_ARR[new SecureRandom().nextInt(FileSdkConstant.FIRE_PART_ARR.length)];
        FileUtils.createDir(path);
        return path;
    }

}
