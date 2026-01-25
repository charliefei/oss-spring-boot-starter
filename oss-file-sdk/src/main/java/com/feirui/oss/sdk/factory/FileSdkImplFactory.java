package com.feirui.oss.sdk.factory;

import com.feirui.oss.sdk.config.CommonFileProperties;
import com.feirui.oss.sdk.domain.enums.FileSdkImplEnum;
import com.feirui.oss.sdk.service.FileSdkService;

import java.util.Map;

public class FileSdkImplFactory {
    private final CommonFileProperties commonFileProperties;
    /**
     * 本地缓存各文件系统的sdk实例
     */
    private final Map<String, FileSdkService> sdkImplMap;

    public FileSdkImplFactory(CommonFileProperties commonFileProperties,
                              Map<String, FileSdkService> sdkImplMap) {
        this.commonFileProperties = commonFileProperties;
        this.sdkImplMap = sdkImplMap;

    }

    /**
     * 根据配置文件的配置，获取对应的sdk对象实例
     * 1、先通过配置的sdk名称，获取sdk实例
     * 2、获取不到，返回默认的本地存储sdk实例
     */
    public FileSdkService createDefaultFileSdk() {
        return checkFileServiceNotNull(sdkImplMap.get(commonFileProperties.getImplName()));
    }

    /**
     * 功能描述: 根据指定的sdk名称，获取sdk对象实例
     * 1、先通过传入的sdk名称，获取sdk实例
     * 2、获取不到，返回默认的本地存储sdk实例
     */
    public FileSdkService createAssignedFileSdk(String sdkName) {
        return checkFileServiceNotNull(sdkImplMap.get(sdkName));
    }

    /**
     * 校验一下对象实例非空，如果是null返回默认的sdk实例，即文件存在本地
     */
    private FileSdkService checkFileServiceNotNull(FileSdkService fileSdkManager) {
        return fileSdkManager == null ? sdkImplMap.get(FileSdkImplEnum.LOCAL_SDK_NAME.getImplName()) : fileSdkManager;
    }
}
