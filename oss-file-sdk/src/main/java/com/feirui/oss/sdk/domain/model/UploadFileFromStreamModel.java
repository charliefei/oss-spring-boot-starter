package com.feirui.oss.sdk.domain.model;

import com.feirui.oss.sdk.utils.FileUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@Slf4j
public class UploadFileFromStreamModel extends BaseUploadFileModel {

    /**
     * 文件输入流
     */
    private InputStream fileInput;

    @Override
    public InputStream getInputStream() {
        return fileInput;
    }

    @Override
    public byte[] getByteArray() {
        return FileUtils.inputStream2byte(fileInput);
    }

}
