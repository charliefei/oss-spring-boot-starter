# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a **Spring Boot Starter** for multi-cloud file storage (OSS) with support for:
- **Local storage** (default)
- **Aliyun OSS**
- **Minio**
- **MySQL-based file metadata management**

The project is a Maven multi-module project with two main modules:
1. `oss-file-sdk` - Core SDK for file operations
2. `oss-file-manage-mysql` - MySQL-based file management with database persistence

## Architecture

### Module Structure

**oss-file-sdk** - Core file storage SDK
- `service/impl/` - Three storage implementations:
  - `LocalSdkServiceImpl` - Local filesystem storage
  - `OssSdkServiceImpl` - Aliyun OSS storage
  - `MinioSdkServiceImpl` - Minio storage
- `factory/` - Factory pattern for creating SDK instances:
  - `FileSdkImplFactory` - Creates storage SDK instances based on configuration
  - `UploadFileFactory` - Creates upload file models from different sources
- `domain/` - Data models:
  - `DiskFile` - Core file entity with metadata
  - `model/` - Upload models (BaseUploadFileModel, UploadFileFromByteArrModel, etc.)
  - `enums/` - FileSdkImplEnum, FileSdkRespEnum
- `config/` - `CommonFileProperties` - Configuration properties (prefix: `common.file`)
- `utils/` - File utilities, encryption, multipart handling
- `FileSdkAutoConfiguration` - Spring Boot auto-configuration

**oss-file-manage-mysql** - Database-backed file management
- `manager/impl/FileBaseManagerImpl` - Implements file operations with database persistence
- `mapper/DiskFileMapper` - MyBatis mapper for file metadata
- `MysqlFileManageAutoConfiguration` - Auto-configuration
- Database schema: `src/main/resources/sql/diskfile.sql`

### Design Patterns

1. **Factory Pattern**: `FileSdkImplFactory` dynamically selects storage implementation based on `common.file.implName` configuration
2. **Strategy Pattern**: Different storage backends implement `FileSdkService` interface
3. **Template Method**: `DiskFile.loadUploadData()` provides common file metadata setup
4. **Builder Pattern**: `DiskFile` uses Lombok's `@Builder` for construction

### Storage Backends

- **localImpl** - Local filesystem storage (default, always enabled)
- **ossImpl** - Aliyun OSS (enabled when `common.file.oss.enabled=true`)
- **minioImpl** - Minio storage (enabled when `common.file.minio.enabled=true`)

## Build & Development Commands

### Prerequisites
- **Java 17** (configured in pom.xml)
- **Maven 3.6+**

### Common Commands

```bash
# Build all modules
mvn clean install

# Build without running tests
mvn clean install -DskipTests

# Build single module
mvn clean install -pl oss-file-sdk
mvn clean install -pl oss-file-manage-mysql

# Run tests
mvn test

# Run tests for specific module
mvn test -pl oss-file-sdk

# Clean build
mvn clean

# Package without installing to local repository
mvn package

# Skip Maven flattening plugin (used for version management)
mvn clean install -Dflatten.skip=true
```

### Maven Modules
- Parent module: `oss-spring-boot-starter`
- Module 1: `oss-file-sdk` (id: oss-file-sdk)
- Module 2: `oss-file-manage-mysql` (id: oss-file-manage-mysql)

### Java Version
- Source/target: **Java 17**
- Encoding: **UTF-8**

## Configuration

### File Storage Configuration (`common.file.*`)

```yaml
common:
  file:
    # Default storage implementation (localImpl, ossImpl, minioImpl)
    implName: localImpl

    # Max file size (bytes, default: 500MB)
    maxFileSize: 524288000

    # Allowed file suffixes (comma-separated)
    suffix: jpg,jpeg,png,gif,doc,docx,pdf,xls,xlsx,ppt,pptx,zip,mp4,h264,txt,zip,apk,tar,wps

    # Base path for file storage
    basePath: /path/to/storage

    # Aliyun OSS Configuration
    oss:
      enabled: false
      endpoint: https://oss-cn-hangzhou.aliyuncs.com
      accessKeyId: YOUR_ACCESS_KEY
      accessKeySecret: YOUR_SECRET_KEY
      bucketName: your-bucket
      partSize: 500  # MB
      largeFileSize: 200  # MB

    # Minio Configuration
    minio:
      enabled: false
      endpoint: http://localhost:9000
      accessKey: minioadmin
      secretKey: minioadmin
      bucketName: your-bucket
      maxPartSize: 5242880  # 5MB in bytes
```

### Database Configuration (for oss-file-manage-mysql)

Requires a configured DataSource bean. Add to your application:
```yaml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/your_db
    username: your_username
    password: your_password
    druid:
      # Druid configuration...
```

## Key Classes & Interfaces

### Core Service Interface
- **FileSdkService** (`oss-sdk/service/FileSdkService.java`)
  - `uploadFile(BaseUploadFileModel)` - Upload file
  - `downloadFileToStream(DiskFile)` - Download to InputStream
  - `downloadFileToBase64(DiskFile)` - Download to base64
  - `downloadFileToByte(DiskFile)` - Download to byte array
  - `deleteFile(DiskFile)` - Delete file

### File Management (Database-backed)
- **FileBaseManager** - Interface for file management with database persistence
- **FileBaseManagerImpl** - Implementation using MyBatis mapper

### Entity
- **DiskFile** - File metadata entity (id, path, fileName, fileType, mimeType, size, implName, etc.)

### Factory
- **FileSdkImplFactory** - Creates storage SDK instances
  - `createDefaultFileSdk()` - Creates default SDK based on `implName`
  - `createAssignedFileSdk(String)` - Creates specific SDK by name

### Configuration
- **CommonFileProperties** - Configuration properties class with nested OSS and Minio configs
- **FileSdkAutoConfiguration** - Spring Boot auto-configuration for SDK
- **MysqlFileManageAutoConfiguration** - Auto-configuration for MySQL management

## Usage Examples

### Using FileBaseManager (with MySQL)

```java
@Autowired
private FileBaseManager fileBaseManager;

// Upload from bytes
byte[] fileBytes = Files.readAllBytes(path.toPath());
BaseUploadFileModel model = new UploadFileFromByteArrModel()
    .setFileBytes(fileBytes)
    .setFileName("test.jpg")
    .setFileType("jpg");
String fileId = fileBaseManager.uploadReturnId(model);

// Download
DiskFile diskFile = fileBaseManager.getDiskFileById(fileId);
InputStream is = fileBaseManager.downloadFileToStream(diskFile);

// Delete
fileBaseManager.deleteFileById(fileId);
```

### Using FileSdkService Directly

```java
@Autowired
private FileSdkImplFactory fileSdkImplFactory;

// Get default SDK based on configuration
FileSdkService fileSdkService = fileSdkImplFactory.createDefaultFileSdk();

// Upload
BaseUploadFileModel model = new UploadFileFromPathModel()
    .setFilePath("/path/to/file.jpg")
    .setFileType("jpg");
DiskFile diskFile = fileSdkService.uploadFile(model);

// Download
byte[] bytes = fileSdkService.downloadFileToByte(diskFile);
```

## Database Schema

Table: `disk_file`
- `pid` - Primary key (auto-increment)
- `id` - Unique file identifier (varchar 60)
- `path` - File path (varchar 255)
- `file_name` - File name (varchar 200)
- `file_package` - File directory/package (varchar 50)
- `file_type` - File extension (varchar 20)
- `mime_type` - MIME type (varchar 255)
- `company_code` - Company code for multi-tenancy (varchar 50)
- `create_date` - Creation timestamp (datetime)
- `pwd_switch` - Encryption flag (int 1)
- `size` - File size in bytes (double 15,0)
- `impl_name` - Storage implementation name (varchar 50)

SQL file location: `oss-file-manage-mysql/src/main/resources/sql/diskfile.sql`

## Dependencies

### oss-file-sdk
- Spring Boot 3.4.7
- Fastjson2 2.0.56
- Hutool 5.8.16
- Apache Tika 3.2.1
- Aliyun OSS SDK 3.17.4
- Minio 8.2.0
- Lombok

### oss-file-manage-mysql
- MyBatis Spring Boot Starter 3.0.4
- MySQL Connector 8.0.32
- Druid Spring Boot Starter 1.2.16
- Depends on oss-file-sdk

## Maven Modules

This is a multi-module Maven project with the following modules:

1. **oss-file-sdk** - Core file storage SDK
2. **oss-file-manage-mysql** - MySQL-based file management

Parent POM manages:
- Spring Boot dependencies
- Java 17 configuration
- Compiler plugin (3.8.1)
- Flatten plugin for version management

## Important Implementation Details

### File Encryption Support
- Files can be encrypted with password switch (`pwdSwitch` field in DiskFile)
- `FileAesEncryptUtils` provides AES encryption utilities
- `pwdSwitchFlag()` method checks encryption status

### Upload Model Factory
- `UploadFileFactory.createUploadFileModel()` - Creates appropriate upload model from different sources:
  - Byte array
  - File path
  - Input stream
  - Base64 string

### File Type Validation
- File types validated against `common.file.suffix` configuration
- Uses `FileSuffixConstant` for predefined file types
- `ContentTypes` utility for MIME type handling

### Multi-tenant Support
- `companyCode` field in DiskFile for enterprise/multi-tenant usage
- Default value: "default"

## Spring Boot Auto-Configuration

### FileSdkAutoConfiguration
Automatically configures:
- `FileSdkConstant` bean
- `localImpl` bean (always)
- `ossImpl` bean (if `common.file.oss.enabled=true`)
- `minioImpl` bean (if `common.file.minio.enabled=true`)
- `FileSdkImplFactory` bean (if any FileSdkService beans present)

### MysqlFileManageAutoConfiguration
Automatically configures:
- `FileBaseManager` bean (if DataSource and DiskFileMapper beans present)

## Git Configuration

- `.gitignore` excludes: target/, .idea/, .flattened-pom.xml
- Standard Java/Maven/IDE ignore patterns
