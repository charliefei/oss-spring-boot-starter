package com.feirui.oss.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.feirui.oss.domain.dto.UploadFileDto;
import com.feirui.oss.domain.model.DiskFileModel;
import com.feirui.oss.service.CloudStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/oss")
@Slf4j
public class FileController {
    @Resource
    private CloudStorageService cloudStorageService;

    @PostMapping("/upload")
    public Map<String, Object> upload(MultipartFile file) {
        Map<String, Object> map = new HashMap<>();
        try {
            UploadFileDto dto = UploadFileDto.builder().file(file)
                    .filePackage("feirui")
                    .fileSize(file.getSize())
                    .fileType(FileUtil.getSuffix(file.getOriginalFilename()))
                    .inputStream(file.getInputStream())
                    .pwdSwitch(true)
                    .build();

            String res = cloudStorageService.upload(dto, diskFileModel -> {
                // diskFileModel保存到数据库 ...
                String jsonStr = JSONUtil.toJsonStr(diskFileModel);
                System.out.println(jsonStr);
            });
            map.put("success", 1);
            map.put("code", 200);
            map.put("msg", "上传文件成功");
            map.put("data", res);
            return map;
        } catch (Exception e) {
            log.error("上传文件失败", e);
            map.put("success", 0);
            map.put("code", 400);
            map.put("msg", "上传文件失败");
            return map;
        }
    }

    @GetMapping("/{fileId}")
    public void download(@PathVariable String fileId, HttpServletResponse response) {
        try {
            // 根据fileId查数据库 ...
            DiskFileModel model = DiskFileModel.builder()
                    .pwdSwitch(false)
                    .path("/files/feirui/2024/09/26/J/fa0ec115-95a7-47f8-9830-bb228787358a.png")
                    .build();
            ServletOutputStream outputStream = response.getOutputStream();
            cloudStorageService.downloadFileToOutput(model, outputStream);
        } catch (Exception e) {
            log.error("下载文件失败 fileId = {}", fileId, e);
        }
    }
}
