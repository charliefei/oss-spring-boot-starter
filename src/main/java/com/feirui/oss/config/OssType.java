package com.feirui.oss.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OssType {
    aliyun(1, "aliyun"),
    tencent(2, "tencent"),
    minio(3, "minio"),
    local(4, "local");

    private final int code;
    private final String desc;
}