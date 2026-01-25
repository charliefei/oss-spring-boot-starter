package com.feirui.oss.sdk.domain.model;

import com.feirui.oss.sdk.utils.FileUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.InputStream;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@Slf4j
public class UploadFileFromPathModel extends BaseUploadFileModel {

    /**
     * 文件路径
     */
    private String filePath;

    @Override
    public InputStream getInputStream() {
        try {
            return new FileInputStream(filePath);
        } catch (Exception e) {
            log.error("获取输入流失败", e);
        }
        return null;
    }

    @Override
    public byte[] getByteArray() {
        return FileUtils.inputStream2byte(getInputStream());
    }

}
