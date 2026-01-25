package com.feirui.oss.sdk.constant;

import com.feirui.oss.sdk.config.CommonFileProperties;
import org.springframework.beans.factory.InitializingBean;

public class FileSdkConstant implements InitializingBean {
    private final CommonFileProperties commonFileProperties;

    public FileSdkConstant(CommonFileProperties commonFileProperties) {
        this.commonFileProperties = commonFileProperties;
    }

    /**
     * 动态配置的最顶层父级存储目录
     */
    public static String UPLOAD_PACKAGE;

    /**
     * 固定的文件父级存储目录
     */
    public static final String FILE_PATH = "/fileupload";

    /**
     * 固定的文件名前缀
     */
    public static final String FILE_HEADER = "FILE_";

    /**
     * 文件分区目录
     */
    public static final String[] FIRE_PART_ARR = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    /**
     * 文件是否加密：0加密；1不加密
     */
    public static final Integer USED_PASSWORD = 0;

    /**
     * 文件是否加密：0加密；1不加密
     */
    public static final Integer NOT_USED_PASSWORD = 1;

    @Override
    public void afterPropertiesSet() {
        UPLOAD_PACKAGE = commonFileProperties.getBasePath() + FILE_PATH;
    }
}
