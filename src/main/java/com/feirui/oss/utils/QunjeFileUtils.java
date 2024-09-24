package com.feirui.oss.utils;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;
import java.util.Objects;

@Slf4j
public class QunjeFileUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(QunjeFileUtils.class);

    /**
     * 字节数组写到本地路径
     *
     * @param bytes    字节数组
     * @param filePath 输出文件路径
     * @return boolean 转换结果
     */
    public static boolean byteToPath(byte[] bytes, String filePath) {
        try (OutputStream out = new FileOutputStream(filePath)) {
            return byte2OutputStream(bytes, out);
        } catch (Exception e) {
            LOGGER.error("字节数组写到本地路径失败 >>>>> ", e);
        }
        return false;
    }

    /**
     * 文件写到本地路径
     *
     * @param file     文件
     * @param filePath 输出文件路径
     * @return boolean 转换结果
     */
    public static boolean fileToPath(MultipartFile file, String filePath) {
        try {
            InputStream inputStream = file.getInputStream();
            byte[] bytes = inputStream2byte(inputStream);
            byteToPath(bytes, filePath);
        } catch (Exception e) {
            LOGGER.error("文件写到本地路径,文件转字节数组失败 >>>>> ", e);
        }
        return true;
    }


    /**
     * 字节数组转成输出流
     *
     * @param bytes 字节数组
     * @param fos   输出流
     * @return boolean 转换结果
     */
    public static boolean byte2OutputStream(byte[] bytes, OutputStream fos) {
        try (BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            bos.write(bytes);
            return true;
        } catch (Exception e) {
            LOGGER.error("字节数组转成输出流失败 >>>>> ");
        }
        return false;
    }

    /**
     * 输入流转换字节数组
     *
     * @param in 输入流
     * @return byte[] 字节数组
     */
    public static byte[] inputStream2byte(InputStream in) {
        byte[] b = new byte[1024];
        int n;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            while ((n = in.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            return bos.toByteArray();
        } catch (Exception e) {
            LOGGER.error("输入流转换字节数组失败 >>>>> ");
        } finally {
            QunjeFileUtils.closeInputStream(in);
        }
        return new byte[]{};
    }

    /**
     * base64图片转字节数组
     *
     * @param base64 base64字符串
     * @return byte[] 字节数组
     */
    public static byte[] base64ToByte(String base64) {
        byte[] bytes = null;
        try {
            bytes = Base64.getDecoder().decode(base64);
        } catch (Exception e) {
            LOGGER.error("base64图片转字节数组失败 >>>>>>> ", e);
        }
        return bytes;
    }

    /**
     * 图片字节数组转base64图片
     *
     * @param bytes 文件字节数组
     * @return java.lang.String base图片
     */
    public static String byteToBase64(byte[] bytes) {
        try {
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            LOGGER.error("图片字节数组转base64图片失败 >>>>>> ", e);
        }
        return "";
    }

    /**
     * 复制文件
     *
     * @param in  输入流
     * @param out 输出流
     * @return boolean 复制结果
     */
    public static boolean copyFile(InputStream in, OutputStream out) {
        int len;
        try (BufferedInputStream bis = new BufferedInputStream(in);
             BufferedOutputStream bos = new BufferedOutputStream(out);) {
            while ((len = bis.read()) != -1) {
                bos.write(len);
            }
            return true;
        } catch (Exception ex) {
            LOGGER.error("复制文件失败 >>>>> ", ex);
        }
        return false;
    }

    /**
     * 复制文件
     *
     * @param srcInput 输入流
     * @param outPath  输出路径
     * @return boolean 复制结果
     */
    public static boolean copyFile(InputStream srcInput, String outPath) {
        try (InputStream in = srcInput;
             OutputStream out = new FileOutputStream(outPath)) {
            return copyFile(in, out);
        } catch (Exception ex) {
            LOGGER.error("复制文件失败 >>>>> ", ex);
        }
        return false;
    }

    /**
     * 根据路径判断, 如果目录不存在, 就创建目录
     *
     * @param filePathList 待创建文件路径
     */
    public static void createDir(String... filePathList) {
        File file;
        for (String filePath : filePathList) {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
    }

    /**
     * 强制删除文件或目录
     * 1、如果是文件，直接删除
     * 2、如果是目录，删除目录下所有文件和子目录
     *
     * @param file 待删除文件或目录
     * @return boolean 删除结果
     */
    public static boolean delFileForce(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        boolean result = false;
        try {
            FileUtils.forceDelete(file);
            result = true;
        } catch (IOException e) {
            LOGGER.error("强制删除文件或目录失败 >>>>>>>> ", e);
        }
        return result;
    }

    /**
     * 关闭输入流和输出流
     *
     * @param in  待关闭输入流
     * @param out 待关闭输出流
     */
    public static void closeStream(InputStream in, OutputStream out) {
        closeInputStream(in);
        closeOutputStream(out);
    }

    /**
     * 关闭输入流
     *
     * @param in 待关闭输入流
     */
    public static void closeInputStream(InputStream in) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                LOGGER.error("关闭输入流失败 >>>>>> ");
            }
        }
    }

    /**
     * 关闭输出流
     *
     * @param out 待关闭输出流
     */
    public static void closeOutputStream(OutputStream out) {
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                LOGGER.error("关闭输出流失败 >>>>>> ");
            }
        }
    }

    public static InputStream thumbnails(InputStream inputStream, double scale) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(inputStream2byte(inputStream));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            Thumbnails.of(byteArrayInputStream).scale(scale).toOutputStream(byteArrayOutputStream);
            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            LOGGER.error("thumbnail image to stream error : ", e);
        } finally {
            closeStream(inputStream, byteArrayOutputStream);
        }
        return null;
    }


    public static byte[] thumbnails(byte[] image, double scale) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        InputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(image);
            Thumbnails.of(inputStream).scale(scale).toOutputStream(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            LOGGER.error("thumbnail image to bytes error : ", e);
        } finally {
            closeStream(inputStream, byteArrayOutputStream);
        }
        return null;
    }

    public static byte[] imageRotate(InputStream inputStream, int rotate) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            Thumbnails.of(inputStream).scale(1).rotate(rotate).toOutputStream(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            LOGGER.error("image rotate error ");
        } finally {
            closeStream(inputStream, byteArrayOutputStream);
        }
        return null;
    }

    public static void imageRotate(InputStream inputStream, int rotate, String outPath) {
        try {
            Thumbnails.of(inputStream).scale(1).rotate(rotate).toFile(outPath);
        } catch (IOException e) {
            LOGGER.error("image rotate error ");
        } finally {
            closeInputStream(inputStream);
        }
    }

    public static byte[] imageRotate(byte[] image, int rotate) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        InputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(image);
            Thumbnails.of(inputStream).scale(1).rotate(rotate).toOutputStream(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            LOGGER.error("imageRotate to bytes error : ", e);
        } finally {
            closeStream(inputStream, byteArrayOutputStream);
        }
        return new byte[]{};
    }

    public static boolean showByFile(OutputStream fos, InputStream inputStream) {
        return bufferOutByPass(inputStream, fos, false, 0);
    }

    public static boolean bufferOutByPass(InputStream in, OutputStream out, boolean usedPassword, int password) {
        try (BufferedOutputStream bos = new BufferedOutputStream(out);
             BufferedInputStream bis = new BufferedInputStream(in)) {
            copyFile(usedPassword, bos, bis, password);
            return true;
        } catch (IOException e) {
            LOGGER.error("save file error ：", e);
        }
        return false;
    }

    public static void copyFile(boolean usedPassword, BufferedOutputStream bos, BufferedInputStream bis, int password) throws IOException {
        int len = -1;
        if (usedPassword) {
            while ((len = bis.read()) != -1) {
                bos.write(len ^ password);
            }
        } else {
            while ((len = bis.read()) != -1) {
                bos.write(len);
            }
        }
    }

    public static byte[] inputStream2bytePwd(InputStream in, String password) {
        try {
            Cipher cipher = AESUtils.getCipher(password, Cipher.DECRYPT_MODE);
            CipherInputStream cipherIn = new CipherInputStream(in, cipher);
            return inputStream2byte(cipherIn);
        } catch (Exception e) {
            log.error("aes decrypt inputStream error : ", e);
        }
        return null;
    }

    /**
     * 获取文件格式
     */
    public static String getFileType(String filename) {
        int begin = Objects.requireNonNull(filename).indexOf(".");
        int last = filename.length();
        // 截取文件格式
        return filename.substring(begin + 1, last).toLowerCase(Locale.US);
    }

    /**
     * 字符串写到本地文件
     *
     * @param str
     * @param path
     */
    public static void strToPath(String str, String path) {
        File file = new File(path);
        // 创建目录
        createDir(file.getParent());
        // 判断文件是否存在，存在删除
        delFileForce(file);
        try (
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);) {
            file.createNewFile();
            // 实例化FileOutputStream
            bufferedWriter.write(str);
            bufferedWriter.flush();
        } catch (Exception e) {
            LOGGER.error("strToPath error", e);
        }
    }
}
