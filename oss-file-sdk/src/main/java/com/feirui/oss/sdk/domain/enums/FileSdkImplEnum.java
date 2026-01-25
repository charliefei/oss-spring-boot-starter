package com.feirui.oss.sdk.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileSdkImplEnum {
    LOCAL_SDK_NAME("localImpl", "本地存储"),
    OSS_SDK_NAME("ossImpl", "阿里云OSS存储"),
    OBS_SDK_NAME("obsImpl", "华为云OBS存储"),
    COS_SDK_NAME("cosImpl", "腾讯云COS存储"),
    MINIO_SDK_NAME("minioImpl", "Minio存储"),
    ;

    private final String implName;
    private final String desc;

}
