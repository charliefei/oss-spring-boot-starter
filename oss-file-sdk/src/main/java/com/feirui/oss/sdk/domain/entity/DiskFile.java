package com.feirui.oss.sdk.domain.entity;

import com.feirui.oss.sdk.constant.DefaultValueConstant;
import com.feirui.oss.sdk.constant.FileSdkConstant;
import com.feirui.oss.sdk.constant.SymbolBaseConstant;
import com.feirui.oss.sdk.domain.enums.FileSdkImplEnum;
import com.feirui.oss.sdk.domain.model.BaseUploadFileModel;
import com.feirui.oss.sdk.domain.model.FileSdkSettingModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class DiskFile {

    /**
     * 唯一id
     */
    private String id;
    /**
     * 文件真实路径
     */
    private String path;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 文件目录
     */
    private String filePackage = DefaultValueConstant.DEFAULT_FILE_PACKAGE;
    /**
     * 文件类型
     */
    private String fileType;
    /**
     * 文件mime类型
     */
    private String mimeType = DefaultValueConstant.DEFAULT_MIME_TYPE;
    /**
     * 文件大小，单位：byte
     */
    private Long size;
    /**
     * 是否加密：0 加密；1 不加密
     */
    private Integer pwdSwitch;
    /**
     * 文件存储实现
     * @see com.feirui.oss.sdk.domain.enums.FileSdkImplEnum
     */
    private String implName;
    /**
     * 企业码（saas使用）
     */
    private String companyCode = DefaultValueConstant.DEFAULT_COMPANY_CODE;
    /**
     * 上传者
     */
    private String creator = DefaultValueConstant.DEFAULT_SYS_USER;
    /**
     * 创建时间
     */
    private Date createDate;

    public boolean pwdSwitchFlag() {
        return FileSdkConstant.USED_PASSWORD.equals(pwdSwitch);
    }

    public DiskFile loadUploadData(BaseUploadFileModel model, String implName) {
        BeanUtils.copyProperties(model, this);

        FileSdkSettingModel settingModel = FileSdkSettingModel.checkNullAndInit(model.getFileSettingModel());

        String basePath = settingModel.getBasePath();
        if (!FileSdkImplEnum.LOCAL_SDK_NAME.getImplName().equals(implName)) {
            basePath = basePath.substring(1);
        }
        this.id = UUID.randomUUID().toString().replace("-", "");
        this.path = model.buildPath(basePath, SymbolBaseConstant.LEFT_SLASH) + SymbolBaseConstant.LEFT_SLASH + FileSdkConstant.FILE_HEADER + this.id + "." + model.getFileType();
        if (this.fileName == null || this.fileName.isEmpty()) {
            this.fileName = this.id + "." + model.getFileType();
        }
        this.createDate = new Date();
        this.pwdSwitch = settingModel.convertPwdSwitch();
        this.implName = implName;
        return this;
    }

    public DiskFile addPathPrefix(String prefix) {
        if (prefix != null) {
            this.path = prefix + SymbolBaseConstant.LEFT_SLASH + this.path;
        }
        return this;
    }

}
