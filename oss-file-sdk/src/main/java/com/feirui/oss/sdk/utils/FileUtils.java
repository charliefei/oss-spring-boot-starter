package com.feirui.oss.sdk.utils;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

@Slf4j
public class FileUtils {

    /**
     * 复制文件到本地路径
     *
     * @param srcInput 输入流
     * @param outPath  输出路径
     * @return boolean 复制结果
     */
    public static boolean copyFile(InputStream srcInput, String outPath) {
        try (InputStream in = srcInput; OutputStream out = Files.newOutputStream(Paths.get(outPath))) {
            return copyFile(in, out);
        } catch (Exception ex) {
            log.error("复制文件失败 >>>>> ", ex);
        }
        return false;
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
        try (BufferedInputStream bis = new BufferedInputStream(in); BufferedOutputStream bos = new BufferedOutputStream(out)) {
            while ((len = bis.read()) != -1) {
                bos.write(len);
            }
            return true;
        } catch (Exception ex) {
            log.error("复制文件失败 >>>>> ", ex);
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
            log.error("输入流转换字节数组失败 >>>>> ", e);
        } finally {
            closeInputStream(in);
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
            log.error("base64图片转字节数组失败 >>>>>>> ", e);
        }
        return bytes;
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
                log.error("关闭输入流失败 >>>>>> ");
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
                log.error("关闭输出流失败 >>>>>> ");
            }
        }
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
                mkdir(filePath);
            }
        }
    }

    /**
     * 创建目录
     *
     * @param dirPath 目录路径
     * @return File 目录
     */
    public static File mkdir(String dirPath) {
        if (dirPath == null) {
            return null;
        } else {
            File dir = FileUtil.file(dirPath);
            return FileUtil.mkdir(dir);
        }
    }

}
