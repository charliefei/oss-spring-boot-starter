package com.feirui.oss.manage.mysql.manager.impl;

import com.feirui.oss.manage.mysql.manager.FileBaseManager;
import com.feirui.oss.manage.mysql.mapper.DiskFileMapper;
import com.feirui.oss.sdk.config.CommonFileProperties;
import com.feirui.oss.sdk.constant.FileSdkConstant;
import com.feirui.oss.sdk.constant.FileSuffixConstant;
import com.feirui.oss.sdk.domain.entity.DiskFile;
import com.feirui.oss.sdk.domain.enums.FileSdkRespEnum;
import com.feirui.oss.sdk.domain.model.BaseUploadFileModel;
import com.feirui.oss.sdk.domain.model.FileSdkSettingModel;
import com.feirui.oss.sdk.exception.FileSdkException;
import com.feirui.oss.sdk.factory.FileSdkImplFactory;
import com.feirui.oss.sdk.factory.UploadFileFactory;
import com.feirui.oss.sdk.utils.FileUtils;
import com.feirui.oss.sdk.utils.MultipartFileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class FileBaseManagerImpl implements FileBaseManager {

    private final DiskFileMapper diskFileMapper;
    private final CommonFileProperties commonFileProperties;
    private final FileSdkImplFactory fileSdkImplFactory;

    @Override
    public boolean saveDiskFile(DiskFile diskFile) {
        return diskFileMapper.insert(diskFile) > 0;
    }

    @Override
    public boolean saveDiskFileBatch(List<DiskFile> diskFileList) {
        return diskFileMapper.insertBatch(diskFileList) > 0;
    }

    @Override
    public boolean removeDiskFile(String id) {
        return diskFileMapper.deleteById(id) > 0;
    }

    @Override
    public boolean removeDiskFileBatch(List<String> idList) {
        return diskFileMapper.deleteBatchByIds(idList) > 0;
    }

    @Override
    public DiskFile getDiskFileById(String id) {
        return diskFileMapper.selectById(id);
    }

    @Override
    public List<DiskFile> listDiskFileByIds(List<String> idList) {
        return diskFileMapper.listByIds(idList);
    }

    @Override
    public DiskFile uploadBase64File(String file, DiskFile diskFile) throws FileSdkException {
        BaseUploadFileModel model = UploadFileFactory.createUploadFileModel(FileUtils.base64ToByte(
                file.replace("data:image/png;base64,", "")),
                diskFile.setFileType(FileSuffixConstant.TYPE_JPG).setMimeType("image/jpeg"));
        return uploadFile(model);
    }

    @Override
    public String uploadReturnId(BaseUploadFileModel model) throws FileSdkException {
        return uploadFile(model).getId();
    }

    @Override
    public DiskFile uploadFile(BaseUploadFileModel model) throws FileSdkException {
        log.info("文件格式：{}", model.getFileType());
        // 格式校验
        boolean contains = commonFileProperties.getSuffix().contains(model.getFileType().toLowerCase());
        FileSdkRespEnum.FILE_TYPE_NOT_SUPPORT.assertTrue(contains);
        // 文件配置
        FileSdkSettingModel settingModel = new FileSdkSettingModel().setBasePath(FileSdkConstant.UPLOAD_PACKAGE).setPasswordSwitch(false);
        // 上传文件
        DiskFile diskFile = fileSdkImplFactory.createDefaultFileSdk().uploadFile(model.setFileSettingModel(settingModel));
        // 数据库记录
        diskFileMapper.insert(diskFile);
        return diskFile;
    }

    @Override
    public InputStream downloadFileToStream(DiskFile diskFile) throws FileSdkException {
        return fileSdkImplFactory.createAssignedFileSdk(diskFile.getImplName()).downloadFileToStream(diskFile);
    }

    @Override
    public MultipartFile downloadFileToMultipart(DiskFile diskFile, String fieldName) throws FileSdkException {
        InputStream inputStream = downloadFileToStream(diskFile);
        return MultipartFileUtils.createMultipartFile(inputStream, diskFile.getFileName(), fieldName);
    }

    @Override
    public String downloadFileToBase64(DiskFile diskFile) throws FileSdkException {
        return fileSdkImplFactory.createAssignedFileSdk(diskFile.getImplName()).downloadFileToBase64(diskFile);
    }

    @Override
    public byte[] downloadFileToByte(DiskFile diskFile) throws FileSdkException {
        return fileSdkImplFactory.createAssignedFileSdk(diskFile.getImplName()).downloadFileToByte(diskFile);
    }

    @Override
    public void downloadFileToOutput(DiskFile diskFile, OutputStream output) throws FileSdkException {
        fileSdkImplFactory.createAssignedFileSdk(diskFile.getImplName()).downloadFileToOutput(diskFile, output);
    }

    @Override
    public String downloadFileToPath(DiskFile diskFile, String targetPath) throws FileSdkException {
        fileSdkImplFactory.createAssignedFileSdk(diskFile.getImplName()).downloadFileToPath(diskFile, targetPath);
        return targetPath;
    }

    @Override
    public boolean deleteFile(DiskFile diskFile) throws FileSdkException {
        diskFileMapper.deleteById(diskFile.getId());
        return fileSdkImplFactory.createAssignedFileSdk(diskFile.getImplName()).deleteFile(diskFile);
    }

    @Override
    public boolean deleteFileById(String id) throws FileSdkException {
        DiskFile diskFile = diskFileMapper.selectById(id);
        if (diskFile == null) {
            return false;
        }
        return deleteFile(diskFile);
    }

    @Override
    public boolean batchDropFiles(List<DiskFile> diskFiles) throws FileSdkException {
        List<String> idList = new ArrayList<>();
        for (DiskFile diskFile : diskFiles) {
            idList.add(diskFile.getId());
            fileSdkImplFactory.createAssignedFileSdk(diskFile.getImplName()).deleteFile(diskFile);
        }
        return removeDiskFileBatch(idList);
    }

    @Override
    public boolean deleteLocalFile(String path) {
        return FileUtils.forceDeleteFile(new File(path));
    }

    @Override
    public long countDiskFileSize(String companyCode) {
        return diskFileMapper.countTotalSize(companyCode);
    }

}
