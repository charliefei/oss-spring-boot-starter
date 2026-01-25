package com.feirui.oss.sdk.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
public class FileAesEncryptUtils {
    /**
     * 对称加密模式
     */
    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";

    /**
     * 文件流AES加密，输出文件流
     */
    public static InputStream aesEncryptToStream(InputStream input) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(
                    AESUtils.AES_KEY.getBytes(StandardCharsets.UTF_8), "AES"));
        } catch (Exception e) {
            log.error("aesEncryptToStream.err: {}", e.getMessage(), e);
        }
        return new CipherInputStream(input, cipher);
    }

    /**
     * 文件流AES加密，输出目标路径
     */
    public static void aesEncryptToPath(InputStream input, String targetPath) {
        FileUtils.copyFile(aesEncryptToStream(input), targetPath);
    }

    /**
     * 文件流AES加密，输出字节数组
     */
    public static byte[] aesEncryptToByte(InputStream input) {
        return FileUtils.inputStream2byte(aesEncryptToStream(input));
    }

    /**
     * 文件流AES解密，输出文件流
     */
    public static InputStream aesDecryptToStream(InputStream input) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(
                    AESUtils.AES_KEY.getBytes(StandardCharsets.UTF_8), "AES"));
        } catch (Exception e) {
            log.error("aesDecryptToStream.err: {}", e.getMessage(), e);
        }
        return new CipherInputStream(input, cipher);
    }

    /**
     * 文件流AES解密，输出字节数组
     */
    public static byte[] aesDecryptToByte(InputStream input) {
        return FileUtils.inputStream2byte(aesDecryptToStream(input));
    }
}
