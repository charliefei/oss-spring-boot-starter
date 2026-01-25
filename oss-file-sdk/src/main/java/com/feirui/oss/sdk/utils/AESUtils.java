package com.feirui.oss.sdk.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class AESUtils {
    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final String AES = "AES";

    /**
     * 文件AES加密密钥
     */
    public static final String AES_KEY = "fr_nb_1234567890";

    /**
     * 加密
     *
     * @param text    明文
     * @param key     密钥哈希
     * @param charset 字符集
     * @return 密文
     */
    public static String encrypt(String text, String key, Charset charset) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key(key));
        byte[] textBytes = text.getBytes(charset);
        byte[] bytes = cipher.doFinal(textBytes);
        return new String(Base64.encodeBase64(bytes), charset);
    }

    /**
     * 加密
     *
     * @param text 明文
     * @return 密文
     */
    public static String encrypt(String text) throws Exception {
        return encrypt(text, AES_KEY, StandardCharsets.UTF_8);
    }

    /**
     * 解密
     *
     * @param text    密文
     * @param key     密钥哈希
     * @param charset 字符集
     * @return 明文
     */
    public static String decrypt(String text, String key, Charset charset) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key(key));
        byte[] textBytes = Base64.decodeBase64(text);
        byte[] bytes = cipher.doFinal(textBytes);
        return new String(bytes, charset);
    }

    /**
     * 解密
     *
     * @param text 密文
     * @return 明文
     */
    public static String decrypt(String text) throws Exception {
        return decrypt(text, AES_KEY, StandardCharsets.UTF_8);
    }

    /**
     * 生成密钥
     *
     * @param seed 密钥种子
     * @return 密钥哈希
     */
    public static SecretKeySpec key(String seed) throws Exception {
        return new SecretKeySpec(seed.getBytes(StandardCharsets.UTF_8), AES);
    }

    /**
     * 获取Cipher
     *
     * @param key 密钥
     * @param mode 模式
     * @return Cipher
     */
    public static Cipher getCipher(String key, int mode) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(mode, key(key));
        return cipher;
    }
}
