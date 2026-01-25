DROP TABLE IF EXISTS `disk_file`;
CREATE TABLE `disk_file`
(
    `pid`            int(10) UNSIGNED                                              NOT NULL AUTO_INCREMENT,
    `id`             varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '唯一约束',
    `path`           varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '路径',
    `file_name`      varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件名',
    `file_package`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '文件夹',
    `file_type`      varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '文件类型',
    `mime_type`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '文件mime类型',
    `company_code`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '公司编号',
    `create_date`    datetime                                                      NULL     DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
    `pwd_switch`     int(1)                                                        NULL     DEFAULT NULL COMMENT '密码开关',
    `size`           double(15, 0)                                                 NULL     DEFAULT NULL COMMENT '文件大小',
    `impl_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '文件上传接口名称',
    PRIMARY KEY (`pid`) USING BTREE,
    UNIQUE INDEX `id` (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC;