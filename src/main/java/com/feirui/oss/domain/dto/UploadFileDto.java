package com.feirui.oss.domain.dto;

import com.feirui.oss.constant.FileSdkConstant;
import com.feirui.oss.utils.QunjeEncryptUtils;
import com.feirui.oss.utils.QunjeFileUtils;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class UploadFileDto {
    private MultipartFile file;

    private String filePackage;

    private String fileSuffix;

    private Boolean pwdSwitch = false;

    /**
     * 根据加密开关，获取文件流
     */
    public InputStream getInputStream() throws IOException {
        return pwdSwitch ? QunjeEncryptUtils.aesEncryptToStream(file.getInputStream())
                : file.getInputStream();
    }

    public String getObjectName(String folderPrefix) {
        String folderName = buildPath(folderPrefix, FileSdkConstant.LEFT_SLASH);
        return folderName + getFileName();
    }

    public String getFileName() {
        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null;
        return UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
    }

    public String buildPath(String folderPrefix, String fileSeparator) {
        String path = fileSeparator + folderPrefix
                + fileSeparator + filePackage
                + fileSeparator + new SimpleDateFormat("yyyy/MM/dd").format(new Date())
                + fileSeparator + FileSdkConstant.FIRE_PART_ARR[new SecureRandom().nextInt(FileSdkConstant.FIRE_PART_ARR.length)];
        QunjeFileUtils.createDir(path);
        return path;
    }
}
