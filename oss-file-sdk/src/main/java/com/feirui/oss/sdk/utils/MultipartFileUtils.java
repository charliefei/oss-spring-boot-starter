package com.feirui.oss.sdk.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Slf4j
public class MultipartFileUtils {
    private static final int BUFFER_SIZE = 8192;

    /**
     * 将输入流转换为MultipartFile（不指定content-type）
     *
     * @param inputStream 文件输入流
     * @param fileName    文件名
     * @param fieldName   表单字段名
     * @return MultipartFile
     */
    public static MultipartFile createMultipartFile(InputStream inputStream,
                                                    String fileName,
                                                    String fieldName) throws IllegalStateException {
        return createMultipartFile(inputStream, fileName, null, fieldName);
    }

    /**
     * 将输入流转换为MultipartFile
     *
     * @param inputStream 文件输入流
     * @param fileName    文件名
     * @param contentType 文件类型
     * @param fieldName   表单字段名
     * @return MultipartFile
     */
    public static MultipartFile createMultipartFile(InputStream inputStream,
                                                    String fileName,
                                                    String contentType,
                                                    String fieldName) throws IllegalStateException {
        String actualContentType = StringUtils.isNotBlank(contentType) ?
                contentType : ContentTypes.determineContentType(fileName, inputStream);

        FileItemFactory factory = new DiskFileItemFactory(1024 * 1024, null);
        FileItem item = factory.createItem(fieldName, actualContentType, true, fileName);

        try (OutputStream outputStream = item.getOutputStream()) {
            int bytesRead;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return new CommonsMultipartFile(item);
        } catch (Exception e) {
            item.delete();
            log.error("create multipart file error", e);
            throw new IllegalStateException("create multipart file error", e);
        } finally {
            FileUtils.closeInputStream(inputStream);
        }
    }
}