# OSS Spring Boot Starter

[![Java Version](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.7-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

一个强大的Spring Boot Starter，支持多云文件存储（OSS）服务，包括阿里云OSS、Minio以及本地存储。提供统一的上传、下载、删除等文件操作接口，支持MySQL数据库持久化文件元数据。

## ✨ 特性

- 🚀 **多云存储支持**：阿里云OSS、Minio、本地文件系统
- 🔄 **统一接口**：提供统一的文件操作API，支持多种存储后端
- 💾 **数据库持久化**：集成MySQL存储文件元数据信息
- 🔐 **文件加密**：支持文件AES加密存储
- 📦 **大文件分片**：支持大文件分片上传（阿里云OSS）
- 🏗️ **开箱即用**：Spring Boot Starter设计，快速集成
- 🎯 **多租户支持**：支持企业级多租户场景
- 🛡️ **类型安全**：强类型配置，编译时验证
- 📊 **完整生命周期**：支持上传、下载、删除、批量操作

## 📦 模块结构

```
oss-spring-boot-starter/
├── oss-file-sdk/              # 核心SDK模块
│   ├── service/               # 文件服务接口及实现
│   ├── factory/               # 工厂模式实现
│   ├── domain/                # 领域模型
│   ├── config/                # 配置类
│   ├── utils/                 # 工具类
│   └── exception/             # 异常定义
└── oss-file-manage-mysql/     # MySQL文件管理模块
    ├── manager/               # 文件管理接口及实现
    ├── mapper/                # MyBatis Mapper
    └── resources/sql/         # 数据库SQL脚本
```

## 🛠️ 技术栈

- **Java 17+**
- **Spring Boot 3.4.7**
- **Maven 3.6+**
- **MySQL 8.0+**
- **MyBatis Spring Boot Starter 3.0.4**
- **Druid 1.2.16** (数据库连接池)
- **Fastjson2 2.0.56**
- **Hutool 5.8.16**
- **Lombok**

## 🚀 快速开始

### 1. 引入依赖

**方式一：下载源码构建（推荐）**

```bash
git clone <repository-url>
cd oss-spring-boot-starter
mvn clean install
```

然后在你的项目中添加依赖：

```xml
<dependency>
    <groupId>com.feirui</groupId>
    <artifactId>oss-file-sdk</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- 如果需要MySQL文件管理功能 -->
<dependency>
    <groupId>com.feirui</groupId>
    <artifactId>oss-file-manage-mysql</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 基础配置

#### 配置存储类型（必需）

**application.yml**

```yaml
common:
  file:
    # 指定存储实现 (localImpl, ossImpl, minioImpl)
    implName: localImpl

    # 最大文件大小 (字节，默认500MB)
    maxFileSize: 524288000

    # 允许的文件后缀 (逗号分隔)
    suffix: jpg,jpeg,png,gif,doc,docx,pdf,xls,xlsx,ppt,pptx,zip,mp4,txt

    # 基础存储路径
    basePath: /path/to/storage

    # 阿里云OSS配置 (选择OSS时需要)
    oss:
      enabled: false
      endpoint: https://oss-cn-hangzhou.aliyuncs.com
      accessKeyId: YOUR_ACCESS_KEY_ID
      accessKeySecret: YOUR_ACCESS_KEY_SECRET
      bucketName: your-bucket-name
      partSize: 500  # 分片大小(MB)
      largeFileSize: 200  # 大文件阈值(MB)

    # Minio配置 (选择Minio时需要)
    minio:
      enabled: false
      endpoint: http://localhost:9000
      accessKey: minioadmin
      secretKey: minioadmin
      bucketName: your-bucket
      maxPartSize: 5242880  # 最大分片大小(字节)
```

#### MySQL配置（使用文件管理功能时必需）

```yaml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/your_database?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
    druid:
      initial-size: 5
      min-idle: 5
      max-active: 20
      max-wait: 60000
      time-between-eviction-runs-millis: 60000
      min-evictable-idle-time-millis: 300000
      validation-query: SELECT 1
      test-while-idle: true
      test-on-borrow: false
      test-on-return: false
      pool-prepared-statements: true
      max-pool-prepared-statement-per-connection-size: 20

mybatis:
  mapper-locations: classpath*:mapper/**/*.xml
  type-aliases-package: com.feirui.oss.sdk.domain.entity
```

### 3. 数据库初始化

执行SQL脚本创建数据表：

```sql
-- 位于 oss-file-manage-mysql/src/main/resources/sql/diskfile.sql
CREATE TABLE `disk_file` (
    `pid` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    `id` varchar(60) NOT NULL DEFAULT '' COMMENT '唯一约束',
    `path` varchar(255) NULL DEFAULT NULL COMMENT '路径',
    `file_name` varchar(200) NOT NULL COMMENT '文件名',
    `file_package` varchar(50) NOT NULL COMMENT '文件夹',
    `file_type` varchar(20) NOT NULL COMMENT '文件类型',
    `mime_type` varchar(255) NOT NULL COMMENT '文件mime类型',
    `company_code` varchar(50) NOT NULL COMMENT '公司编号',
    `create_date` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
    `pwd_switch` int(1) NULL DEFAULT NULL COMMENT '密码开关',
    `size` double(15, 0) NULL DEFAULT NULL COMMENT '文件大小',
    `impl_name` varchar(50) NOT NULL DEFAULT '' COMMENT '文件上传接口名称',
    PRIMARY KEY (`pid`) USING BTREE,
    UNIQUE INDEX `id` (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
```

### 4. 启用自动配置

在Spring Boot启动类上添加注解（如果需要MySQL文件管理）：

```java
@SpringBootApplication
@MapperScan("com.feirui.oss.manage.mysql.mapper")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

## 📖 使用示例

### 1. 使用FileBaseManager（推荐，带数据库持久化）

```java
@Service
public class FileService {

    @Autowired
    private FileBaseManager fileBaseManager;

    /**
     * 上传文件（从字节数组）
     */
    public String uploadFromBytes() throws FileSdkException {
        byte[] fileBytes = Files.readAllBytes(Paths.get("/path/to/file.jpg"));

        BaseUploadFileModel model = new UploadFileFromByteArrModel()
                .setFileBytes(fileBytes)
                .setFileName("test.jpg")
                .setFileType("jpg")
                .setMimeType("image/jpeg");

        return fileBaseManager.uploadReturnId(model);
    }

    /**
     * 上传文件（从文件路径）
     */
    public String uploadFromPath() throws FileSdkException {
        BaseUploadFileModel model = new UploadFileFromPathModel()
                .setFilePath("/path/to/document.pdf")
                .setFileType("pdf")
                .setFileName("document.pdf");

        DiskFile diskFile = fileBaseManager.uploadFile(model);
        return diskFile.getId();
    }

    /**
     * 上传Base64图片
     */
    public String uploadBase64(String base64Data) throws FileSdkException {
        DiskFile diskFile = new DiskFile()
                .setFileName("image.jpg")
                .setFileType("jpg")
                .setMimeType("image/jpeg");

        return fileBaseManager.uploadReturnId(
            fileBaseManager.uploadBase64File(base64Data, diskFile)
        );
    }

    /**
     * 下载文件
     */
    public byte[] downloadFile(String fileId) throws FileSdkException {
        DiskFile diskFile = fileBaseManager.getDiskFileById(fileId);
        return fileBaseManager.downloadFileToByte(diskFile);
    }

    /**
     * 下载文件到本地
     */
    public String downloadToPath(String fileId, String targetPath) throws FileSdkException {
        DiskFile diskFile = fileBaseManager.getDiskFileById(fileId);
        return fileBaseManager.downloadFileToPath(diskFile, targetPath);
    }

    /**
     * 删除文件
     */
    public boolean deleteFile(String fileId) throws FileSdkException {
        return fileBaseManager.deleteFileById(fileId);
    }

    /**
     * 批量删除文件
     */
    public boolean batchDelete(List<String> fileIds) throws FileSdkException {
        List<DiskFile> diskFiles = fileIds.stream()
            .map(id -> fileBaseManager.getDiskFileById(id))
            .collect(Collectors.toList());

        return fileBaseManager.batchDropFiles(diskFiles);
    }
}
```

### 2. 直接使用FileSdkService（无数据库）

```java
@Service
public class FileSdkServiceExample {

    @Autowired
    private FileSdkImplFactory fileSdkImplFactory;

    /**
     * 使用默认存储配置上传
     */
    public void uploadWithDefaultStorage() throws FileSdkException {
        FileSdkService fileSdkService = fileSdkImplFactory.createDefaultFileSdk();

        BaseUploadFileModel model = new UploadFileFromPathModel()
                .setFilePath("/path/to/file.jpg")
                .setFileType("jpg");

        DiskFile diskFile = fileSdkService.uploadFile(model);
        System.out.println("File uploaded: " + diskFile.getId());
    }

    /**
     * 强制使用指定存储
     */
    public void uploadToSpecificStorage() throws FileSdkException {
        FileSdkService fileSdkService = fileSdkImplFactory.createAssignedFileSdk("ossImpl");

        BaseUploadFileModel model = new UploadFileFromStreamModel()
                .setInputStream(new FileInputStream("/path/to/file.mp4"))
                .setFileType("mp4")
                .setFileName("video.mp4");

        DiskFile diskFile = fileSdkService.uploadFile(model);
    }
}
```

### 3. 完整示例：文件上传下载REST API

```java
@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileBaseManager fileBaseManager;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        try {
            BaseUploadFileModel model = new UploadFileFromPathModel()
                    .setFilePath(file.getOriginalFilename())
                    .setFileType(FilenameUtils.getExtension(file.getOriginalFilename()));

            String fileId = fileBaseManager.uploadReturnId(model);
            return ResponseEntity.ok(fileId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> download(@PathVariable String id) {
        try {
            DiskFile diskFile = fileBaseManager.getDiskFileById(id);
            byte[] fileBytes = fileBaseManager.downloadFileToByte(diskFile);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(diskFile.getMimeType()));
            headers.setContentDispositionFormData("attachment", diskFile.getFileName());

            return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        try {
            fileBaseManager.deleteFileById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
```

## ⚙️ 高级配置

### 1. 切换存储后端

```yaml
# 切换到阿里云OSS
common:
  file:
    implName: ossImpl  # 或 minioImpl

# 动态切换存储（代码中）
FileSdkService ossService = fileSdkImplFactory.createAssignedFileSdk("ossImpl");
FileSdkService minioService = fileSdkImplFactory.createAssignedFileSdk("minioImpl");
```

### 2. 文件加密配置

```yaml
common:
  file:
    # 全局加密开关（在FileSdkSettingModel中配置）
    # 开启后所有上传文件都将加密存储
```

```java
// 上传时开启加密
FileSdkSettingModel setting = new FileSdkSettingModel()
    .setBasePath("encrypted")
    .setPasswordSwitch(true);  // 开启加密

BaseUploadFileModel model = new UploadFileFromPathModel()
    .setFilePath("/path/to/file.jpg")
    .setFileType("jpg")
    .setFileSettingModel(setting);
```

### 3. 多租户配置

```java
// 在DiskFile中设置企业码
DiskFile diskFile = DiskFile.builder()
    .companyCode("COMPANY_001")  // 设置企业代码
    .fileName("file.jpg")
    .build();

// 查询企业下所有文件大小
long totalSize = fileBaseManager.countDiskFileSize("COMPANY_001");
```

## 📊 支持的存储后端

| 存储类型 | Bean名称 | 配置前缀 | 特性 |
|---------|---------|----------|------|
| 本地文件系统 | localImpl | 无需配置 | 默认支持，无需额外配置 |
| 阿里云OSS | ossImpl | common.file.oss | 支持分片上传、大文件 |
| Minio | minioImpl | common.file.minio | 开源对象存储 |

## 🔧 开发指南

### 构建项目

```bash
# 完整构建
mvn clean install

# 跳过测试
mvn clean install -DskipTests

# 构建特定模块
mvn clean install -pl oss-file-sdk
mvn clean install -pl oss-file-manage-mysql
```

### 运行测试

```bash
# 运行所有测试
mvn test

# 运行特定模块测试
mvn test -pl oss-file-sdk
```

### 添加新的存储实现

1. 在`oss-file-sdk/service/impl/`目录下创建新的实现类
2. 实现`FileSdkService`接口
3. 在`FileSdkAutoConfiguration`中注册Bean
4. 在`FileSdkImplEnum`中添加新的存储类型

## 📝 注意事项

1. **Java版本**：项目使用Java 17，请确保JDK版本≥17
2. **Maven版本**：建议使用Maven 3.6+
3. **数据库**：MySQL 8.0+（使用文件管理功能时）
4. **存储路径**：确保配置的存储路径有读写权限
5. **网络权限**：使用云存储时确保网络连通性

## 🤝 贡献

欢迎提交Issue和Pull Request！

## 📄 许可证

本项目采用MIT许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 📞 支持

如有问题，请提交Issue或联系维护者。

---

⭐ 如果这个项目对你有帮助，请给我们一个Star！
