package com.feirui.oss;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.feirui.oss.domain.dto.UploadFileDto;
import com.feirui.oss.service.CloudStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootTest
class ApplicationTests {

    @Resource
    private CloudStorageService cloudStorageService;

    @Test
    void contextLoads() throws Exception {
        InputStream inputStream = Files.newInputStream(Paths.get("D:\\壁纸\\下载.png"));
        UploadFileDto dto = new UploadFileDto();
        dto.setInputStream(inputStream);
        dto.setFileType("png");
        dto.setFileSize(FileUtil.size(new File("D:\\壁纸\\下载.png")));
        dto.setFilePackage("feirui");
        System.out.println(cloudStorageService.upload(dto, diskFileModel -> {
            System.out.println(JSONUtil.toJsonStr(diskFileModel));
        }));
    }

}
