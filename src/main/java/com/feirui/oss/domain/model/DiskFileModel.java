package com.feirui.oss.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiskFileModel {
    private String id;
    private String path;
    private String fileName;
    private String filePackage;
    private String fileType;
    private Long size;
    private Boolean pwdSwitch;
    private String interfaceName;
}
