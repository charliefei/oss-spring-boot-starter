package com.feirui.oss.utils;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.nio.file.Files;

public class MultipartFileUtils {

    private static final Logger logger = LoggerFactory.getLogger(MultipartFileUtils.class);

    public static MultipartFile createMultipartFile(InputStream inputStream, String fileName, String filedName) {
        FileItemFactory factory = new DiskFileItemFactory(16, null);
        FileItem item = factory.createItem(filedName, "text/plain", true, fileName);
        int bytesRead;
        byte[] buffer = new byte[8192];
        OutputStream outputStream = null;
        MultipartFile mfile = null;
        try {
            outputStream = item.getOutputStream();
            while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            mfile = new CommonsMultipartFile(item);
        } catch (Exception e) {
            logger.error("create multipart file error");
        } finally {
            CloseIO.closeIO(inputStream, outputStream);
        }
        return mfile;
    }

    public static MultipartFile createMultipartFile(File file, String filedName) {
        try {
            return createMultipartFile(new FileInputStream(file), file.getName(), filedName);
        } catch (FileNotFoundException e) {
            logger.error("create multipart file error");
        }
        return null;
    }

    public static MultipartFile createMultipartFile(byte[] file, String fileName, String filedName) {
        return createMultipartFile(new ByteArrayInputStream(file), fileName, filedName);
    }

    public static MultipartFile createMultipartFile(String inPath, String filedName) {
        try {
            File file = new File(inPath);
            return createMultipartFile(Files.newInputStream(file.toPath()), file.getName(), filedName);
        } catch (Exception e) {
            logger.error("create multipart file error");
        }
        return null;
    }

}
