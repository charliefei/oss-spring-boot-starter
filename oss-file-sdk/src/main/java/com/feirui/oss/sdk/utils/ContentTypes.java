package com.feirui.oss.sdk.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ContentTypes {
    public static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";
    public static final Map<String, String> DATA = new HashMap<>();
    private static final Tika tika = new Tika();

    static {
        // ------ office ------
        DATA.put("pdf", "application/pdf");
        DATA.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        DATA.put("doc", "application/msword");
        DATA.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        DATA.put("xls", "application/vnd.ms-excel");
        DATA.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        DATA.put("ppt", "application/vnd.ms-powerpoint");
        // ------ archive ------
        DATA.put("zip", "application/zip");
        DATA.put("rar", "application/x-rar-compressed");
        DATA.put("7z", "application/x-7z-compressed");
        DATA.put("tar", "application/x-tar");
        DATA.put("gz", "application/gzip");
        // ------ software ------
        DATA.put("jar", "application/java-archive");
        DATA.put("exe", "application/vnd.microsoft.portable-executable");
        DATA.put("dmg", "application/vnd.apple.diskimage");
        DATA.put("deb", "application/vnd.debian.binary-package");
        DATA.put("rpm", "application/x-rpm");
        // ------ media ------
        DATA.put("msi", "application/x-ms-installer");
        DATA.put("mp3", "audio/mpeg");
        DATA.put("mp4", "video/mp4");
        DATA.put("wav", "audio/wav");
        DATA.put("mpeg", "video/mpeg");
        DATA.put("avi", "video/x-msvideo");
        // ------ image ------
        DATA.put("gif", "image/gif");
        DATA.put("jpg", "image/jpeg");
        DATA.put("jpeg", "image/jpeg");
        DATA.put("png", "image/png");
        // ------ text ------
        DATA.put("txt", "text/plain");
        DATA.put("md", "text/markdown");
        DATA.put("html", "text/html");
    }

    public static String getContentType(String suffix) {
        return DATA.get(suffix);
    }

    private static String determineContentTypeByName(String fileName) {
        try {
            Path path = Paths.get(fileName);
            String probeType = Files.probeContentType(path);
            if (probeType != null && !probeType.isEmpty()) {
                return probeType;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private static String determineContentTypeByTika(String fileName, InputStream inputStream) {
        try {
            if (inputStream != null) {
                if (!inputStream.markSupported()) {
                    inputStream = new BufferedInputStream(inputStream);
                }
                inputStream.mark(Integer.MAX_VALUE);

                try {
                    return tika.detect(inputStream, fileName);
                } finally {
                    inputStream.reset();
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public static String determineContentType(String fileName, InputStream inputStream) {
        String step1 = determineContentTypeByName(fileName);
        if (StringUtils.isNotBlank(step1)) return step1;
        String step2 = determineContentTypeByTika(fileName, inputStream);
        if (StringUtils.isNotBlank(step2)) return step2;
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        return DATA.getOrDefault(extension, DEFAULT_CONTENT_TYPE);
    }

    public static String determineContentType(InputStream inputStream) {
        String detect = determineContentTypeByTika(null, inputStream);
        if (StringUtils.isNotBlank(detect)) return detect;
        return DEFAULT_CONTENT_TYPE;
    }

    public static String determineContentType(String fileName) {
        String detect = determineContentTypeByName(fileName);
        if (StringUtils.isNotBlank(detect)) return detect;
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        return DATA.getOrDefault(extension, DEFAULT_CONTENT_TYPE);
    }
}
