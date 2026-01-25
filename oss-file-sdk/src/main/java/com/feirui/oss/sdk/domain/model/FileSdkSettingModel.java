package com.feirui.oss.sdk.domain.model;

import com.feirui.oss.sdk.constant.FileSdkConstant;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FileSdkSettingModel {
    /**
     * 文件是否加密
     */
    private Boolean passwordSwitch;
    /**
     * 文件存储根目录（配置 + 固定:"/fileUpload"）
     */
    private String basePath;

    /**
     * 文件配置对象判空，配置获取失败时给默认值，继续文件处理
     */
    public static FileSdkSettingModel checkNullAndInit(FileSdkSettingModel fileSetting) {
        return fileSetting == null ? new FileSdkSettingModel().setBasePath(FileSdkConstant.FILE_PATH) : fileSetting;
    }

    /**
     * 功能描述: 根据文件配置表的加密状态，转换成文件表的加密状态
     */
    public int convertPwdSwitch() {
        return this.passwordSwitch ? FileSdkConstant.USED_PASSWORD : FileSdkConstant.NOT_USED_PASSWORD;
    }
}