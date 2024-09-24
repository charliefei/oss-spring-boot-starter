package com.feirui.oss.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESUtils {

    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final String AES = "AES";

    /**
     * 文件AES加密密钥
     */
    public static final String AES_KEY = "js_qj_1234567890";

    /**
     * 解密
     *
     * @param text    密文
     * @param key     密钥哈希
     * @param charset 字符集
     * @return 明文
     * @throws Exception
     */
    public static String decrypt(String text, String key, String charset) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key(key));
        byte[] textBytes = Base64.decodeBase64(text);
        byte[] bytes = cipher.doFinal(textBytes);

        return new String(bytes, charset);
    }

    /**
     * 生成密钥
     *
     * @param seed 密钥种子
     *             //     * @param size 密钥长度
     * @return 密钥哈希
     * @throws Exception
     */
    public static SecretKeySpec key(String seed) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(seed.getBytes("utf-8"), AES);
        return secretKey;
    }

    public static Cipher getCipher(String key, int mode) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(mode, key(key));
        return cipher;
    }
}
