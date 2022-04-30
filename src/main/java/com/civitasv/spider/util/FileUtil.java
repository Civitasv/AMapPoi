package com.civitasv.spider.util;

import org.apache.commons.io.FileExistsException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class FileUtil {
    /**
     * 获取文件名后缀
     *
     * @param path 文件完整路径
     * @return 文件名后缀
     */
    public static String getExtension(String path) {
        int i = path.lastIndexOf('.');
        return i > 0 ? path.substring(i + 1) : null;
    }

    /**
     * 获取文件名
     *
     * @param path 文件完整路径
     * @return 文件名
     */
    public static String getFileName(String path) {
        path = path.trim();
        String[] temp = path.split("\\\\");
        path = temp[temp.length - 1];
        String[] arr = path.split("\\.");
        return arr[0];
    }

    /**
     * 读取数据为字符串
     *
     * @param path 文件完整路径
     * @return 文件字符串数据
     */
    public static String readFile(String path) throws IOException {
        File file = new File(path);
        if (file.exists()) {
            StringBuilder res = new StringBuilder();
            Files.lines(file.toPath())
                    .forEach(res::append);
            return res.toString();
        }
        throw new FileExistsException("文件不存在：" + path);
    }

    /**
     * 根据原始文件路径生成一个不重复的文件路径，如果A.shp不存在，则使用原路径
     * 如果A.shp已经存在，则使用新路径A(1).shp，如果A（1）.txt已存在，则使用新路径A（2）.txt，依次类推
     *
     * @param filePath 文件完整路径
     * @return 新路径
     */
    public static File getNewFile(String filePath) {
        File file = new File(filePath);
        File parentFile = file.getParentFile();
        // 如果文件夹不存在则生成文件夹
        if (!parentFile.exists()) {
            if (!parentFile.mkdirs()) return null;
        }
        // 如果文件存在则
        if (!file.exists()) {
            return file;
        } else {
            int i = 1;
            while (true) {
                String tempPath = file.getPath();
                int index = tempPath.lastIndexOf('.');
                String name = tempPath.substring(0, index);
                String suffix = tempPath.substring(index + 1);
                String nameFilePath = name + "(" + i + ")" + "." + suffix;
                File newFile = new File(nameFilePath);
                if (!newFile.exists()) {
                    return newFile;
                }
                i++;
            }
        }
    }

    /**
     * 生成cpg文件
     *
     * @param filePath 文件完整路径
     * @param charset  文件编码
     * @return 是否生成成功
     */
    public static boolean generateCpgFile(String filePath, Charset charset) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return false;
            }
            String tempPath = file.getPath();
            int index = tempPath.lastIndexOf('.');
            String name = tempPath.substring(0, index);
            String cpgFilePath = name + ".cpg";
            File cpgFile = new File(cpgFilePath);
            if (cpgFile.exists()) {
                return true;
            }
            boolean newFile = cpgFile.createNewFile();
            if (newFile) {
                Files.write(cpgFile.toPath(), charset.toString().getBytes(charset));
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
