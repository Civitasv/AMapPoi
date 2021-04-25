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

    /**
     *  根据原始文件路径生成一个不重复的文件路径，如果A.shp不存在，则使用原路径
     *  如果A.shp已经存在，则使用新路径A(1).shp，如果A（1）.txt已存在，则使用新路径A（2）.txt，依次类推
     * @param filePath 原路径
     * @return 新路径
     * @throws IOException
     */
    public static File getNewFile(String filePath) throws IOException {
        File file = new File(filePath);
        File parentFile = file.getParentFile();
        // 如果文件夹不存在则生成文件夹
        if(!parentFile.exists()){
            parentFile.mkdirs();
        }
        // 如果文件存在则
        if (!file.exists()) {
            return file;
        } else {
            int i = 1;
            while (true) {
                String[] split = file.getPath().split("\\.");
                String nameFilePath = split[0] + "(" + i + ")" + "." + split[1];
                File newFile = new File(nameFilePath);
                if (!newFile.exists()) {
                   return newFile;
                }
                i++;
            }
        }
    }

    public static void saveCpgFile(String filePath, Charset charset) throws IOException {
        File file = new File(filePath);
        if(!file.exists()){
            return;
        }
        String[] split = file.getPath().split("\\.");
        String cpgFilePath = split[0] + ".cpg";
        File cpgFile = new File(cpgFilePath);
        if(cpgFile.exists()){
            return;
        }
        boolean newFile = cpgFile.createNewFile();
        if(newFile){
            Files.write(cpgFile.toPath(),charset.toString().getBytes(charset));
        }
    }
}
