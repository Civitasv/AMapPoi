package com.civitasv.spider.util;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseUtil {
    /**
     * 解析csv或txt
     *
     * @param path 解析文件路径
     * @return 解析结果
     */
    public static List<Map<String, String>> parseTxtOrCsv(String path) {
        List<Map<String, String>> res = new ArrayList<>();
        try {
            CSVReader reader = new CSVReader(new FileReader(path));
            List<String[]> data = reader.readAll();
            // header
            String[] header = data.get(0);
            for (int i = 1; i < data.size(); i++) {
                Map<String, String> map = new HashMap<>();
                for (int j = 0; j < header.length; j++)
                    map.put(header[j], data.get(i)[j]);
                res.add(map);
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 解析字符串至数值
     *
     * @param text 字符串
     * @return int整型
     */
    public static Integer parseStr2Int(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }
}
