package com.feirui.oss.domain.dto;

import com.feirui.oss.constant.FileSdkConstant;
import com.feirui.oss.utils.QunjeEncryptUtils;
import com.feirui.oss.utils.QunjeFileUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UploadFileDto {
    private MultipartFile file;

    private InputStream inputStream;

    private String fileId;

    private String fileName;

    private String fileType;

    private Long fileSize;

    private String filePackage;

    private Boolean pwdSwitch = false;

    {
        fileId = UUID.randomUUID().toString();
    }

    /**
     * 根据加密开关，获取文件流
     */
    public InputStream getInputStream() {
        return pwdSwitch ? QunjeEncryptUtils.aesEncryptToStream(inputStream)
                : inputStream;
    }

    public String getObjectName(String folderPrefix) {
        fileName = fileId + "." + fileType;
        String path = buildPath(folderPrefix, FileSdkConstant.LEFT_SLASH);
        return path + FileSdkConstant.LEFT_SLASH + fileName;
    }

    public String buildPath(String folderPrefix, String fileSeparator) {
        String path = folderPrefix
                + fileSeparator + filePackage
                + fileSeparator + new SimpleDateFormat("yyyy/MM/dd").format(new Date())
                + fileSeparator + FileSdkConstant.FIRE_PART_ARR[new SecureRandom().nextInt(FileSdkConstant.FIRE_PART_ARR.length)];
        QunjeFileUtils.createDir(path);
        return path;
    }
}
