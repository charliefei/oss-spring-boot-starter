package com.feirui.oss.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CloseIO {

    private static final Logger logger = LoggerFactory.getLogger(CloseIO.class);

    /**
     * 功能描述:
     */
    public static void closeIO(InputStream in, OutputStream out) {
        closeInputStream(in);
        closeOutputStream(out);
    }

    /**
     * 功能描述:
     */
    public static void closeOutputStream(OutputStream out) {
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            logger.error("输出流关闭异常");
        }
    }

    /**
     * 功能描述:
     */
    public static void closeInputStream(InputStream in) {
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            logger.error("输入流关闭异常");
        }
    }
}
