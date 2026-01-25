package com.feirui.oss.sdk.service.impl;

import com.feirui.oss.sdk.constant.SymbolBaseConstant;
import com.feirui.oss.sdk.domain.entity.DiskFile;
import com.feirui.oss.sdk.domain.enums.FileSdkImplEnum;
import com.feirui.oss.sdk.domain.enums.FileSdkRespEnum;
import com.feirui.oss.sdk.domain.model.BaseUploadFileModel;
import com.feirui.oss.sdk.exception.FileSdkException;
import com.feirui.oss.sdk.service.FileSdkService;
import com.feirui.oss.sdk.utils.FileAesEncryptUtils;
import com.feirui.oss.sdk.utils.FileUtils;

import java.io.*;

public class LocalSdkServiceImpl implements FileSdkService {

    @Override
    public DiskFile uploadFile(BaseUploadFileModel model) throws FileSdkException {
        FileSdkRespEnum.FILE_SDK_PARAM_NULL.assertObjectNotNull(model);

        DiskFile diskFile = null;
        InputStream in = null;
        try {
            diskFile = new DiskFile().loadUploadData(model, FileSdkImplEnum.LOCAL_SDK_NAME.getImplName());

            String path = diskFile.getPath();
            FileUtils.createDir(path.substring(0, path.lastIndexOf(SymbolBaseConstant.LEFT_SLASH)));

            in = model.getInputStream(diskFile.pwdSwitchFlag());
            FileUtils.copyFile(in, path);
            diskFile.setSize(new File(path).length());
        } catch (Exception e) {
            FileSdkRespEnum.FILE_UPLOAD_ERROR.assertToException(model, e);
        } finally {
            FileUtils.closeInputStream(in);
        }
        return diskFile;
    }

    @Override
    public InputStream downloadFileToStream(DiskFile diskFile) throws FileSdkException {
        FileSdkRespEnum.FILE_SDK_PARAM_NULL.assertObjectNotNull(diskFile);

        InputStream input = null;
        try {
            input = new FileInputStream(diskFile.getPath());
            if (diskFile.pwdSwitchFlag()) {
                input = FileAesEncryptUtils.aesDecryptToStream(input);
            }
        } catch (Exception e) {
            FileSdkRespEnum.FILE_DOWNLOAD_ERROR.assertToException(diskFile, e);
        }
        return input;
    }

    @Override
    public String downloadFileToBase64(DiskFile diskFile) throws FileSdkException {
        return FileUtils.byteToBase64(downloadFileToByte(diskFile));
    }

    @Override
    public byte[] downloadFileToByte(DiskFile diskFile) throws FileSdkException {
        FileSdkRespEnum.FILE_SDK_PARAM_NULL.assertObjectNotNull(diskFile);

        byte[] result = null;
        InputStream in = null;
        try {
            in = new FileInputStream(diskFile.getPath());
            if (diskFile.pwdSwitchFlag()) {
                result = FileAesEncryptUtils.aesDecryptToByte(in);
            } else {
                result = FileUtils.inputStream2byte(in);
            }
        } catch (Exception e) {
            FileSdkRespEnum.FILE_DOWNLOAD_ERROR.assertToException(diskFile, e);
        } finally {
            FileUtils.closeInputStream(in);
        }
        return result;
    }

    @Override
    public void downloadFileToOutput(DiskFile diskFile, OutputStream output) throws FileSdkException {
        FileSdkRespEnum.FILE_SDK_PARAM_NULL.assertObjectNotNull(diskFile);

        InputStream in = null;
        try {
            in = new FileInputStream(diskFile.getPath());
            if (diskFile.pwdSwitchFlag()) {
                FileUtils.copyFile(FileAesEncryptUtils.aesDecryptToStream(in), output);
            } else {
                FileUtils.copyFile(in, output);
            }
        } catch (Exception e) {
            FileSdkRespEnum.FILE_DOWNLOAD_ERROR.assertToException(diskFile, e);
        } finally {
            FileUtils.closeInputStream(in);
        }
    }

    @Override
    public void downloadFileToPath(DiskFile diskFile, String targetPath) throws FileSdkException {
        try {
            downloadFileToOutput(diskFile, new FileOutputStream(targetPath));
        } catch (FileNotFoundException e) {
            FileSdkRespEnum.FILE_DOWNLOAD_ERROR.assertToException(diskFile, e);
        }
    }

    @Override
    public boolean deleteFile(DiskFile diskFile) throws FileSdkException {
        FileSdkRespEnum.FILE_SDK_PARAM_NULL.assertObjectNotNull(diskFile);
        return FileUtils.forceDeleteFile(new File(diskFile.getPath()));
    }

}
