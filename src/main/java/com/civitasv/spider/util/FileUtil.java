package com.civitasv.spider.util;

import java.io.File;
import java.nio.charset.Charset;

public class FileUtil {
    public static String getExtension(String path) {
        int i = path.lastIndexOf('.');
        return i > 0 ? path.substring(i + 1) : null;
    }

    public static String getFileName(String path) {
        path = path.trim();
        String[] temp = path.split("\\\\");
        path = temp[temp.length - 1];
        String[] arr = path.split("\\.");
        return arr[0];
    }
}
