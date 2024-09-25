package com.feirui.oss.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;

@Slf4j
public class QunjeEncryptUtils {
    /**
     * 对称加密模式
     */
    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";

    /**
     * 文件流AES加密，输出文件流
     */
    public static InputStream aesEncryptToStream(InputStream input) {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(AESUtils.AES_KEY.getBytes(StandardCharsets.UTF_8), "AES"));
        } catch (Exception e) {
            throw new RuntimeException("文件加密失败");
        }
        return new CipherInputStream(input, cipher);
    }

    /**
     * 文件流AES加密，输出目标路径
     */
    public static void aesEncryptToPath(InputStream input, String targetPath) {
        QunjeFileUtils.copyFile(aesEncryptToStream(input), targetPath);
    }

    /**
     * 文件流AES加密，输出字节数组
     */
    public static byte[] aesEncryptToByte(InputStream input) {
        return QunjeFileUtils.inputStream2byte(aesEncryptToStream(input));
    }

    /**
     * 文件流AES解密，输出文件流
     */
    public static InputStream aesDecryptToStream(InputStream input) {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(AESUtils.AES_KEY.getBytes(StandardCharsets.UTF_8), "AES"));
        } catch (Exception e) {
            throw new RuntimeException("文件解密失败");
        }
        return new CipherInputStream(input, cipher);
    }

    /**
     * 文件流AES解密，输出字节数组
     */
    public static byte[] aesDecryptToByte(InputStream input) {
        return QunjeFileUtils.inputStream2byte(aesDecryptToStream(input));
    }

    /**
     * 功能描述: 字节数组转换为MultipartFile文件
     **/
    public static MultipartFile byteConvertToFile(byte[] file, String fileName, String var) {
        InputStream inputStream = new ByteArrayInputStream(file);
        FileItemFactory factory = new DiskFileItemFactory(16, null);
        FileItem item = factory.createItem(var, "text/plain", true, fileName);
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
            log.error("create multipart file error");
        } finally {
            QunjeFileUtils.closeStream(inputStream, outputStream);
        }
        return mfile;

    }

    /**
     * 功能描述: 流转换到另一个目录流
     **/
    public static void inputToOutputStream(InputStream in, OutputStream out) {
        try (BufferedOutputStream bos = new BufferedOutputStream(out);
             BufferedInputStream bis = new BufferedInputStream(in)) {
            int len;
            while ((len = bis.read()) != -1) {
                bos.write(len);
            }
        } catch (IOException e) {
            log.error("save file error ：", e);
        }
    }
}
