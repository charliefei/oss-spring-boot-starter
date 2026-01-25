package com.feirui.oss.sdk.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class UploadFileFromByteArrModel extends BaseUploadFileModel {

    /**
     * 文件字节数组
     */
    private byte[] fileByte;

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(fileByte);
    }

    @Override
    public byte[] getByteArray() {
        return fileByte;
    }

}
