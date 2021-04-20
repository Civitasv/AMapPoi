package com.civitasv.spider.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

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

    public static String readFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            try {
                StringBuilder res = new StringBuilder();
                Files.lines(file.toPath())
                        .forEach(res::append);
                return res.toString();
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }
}
