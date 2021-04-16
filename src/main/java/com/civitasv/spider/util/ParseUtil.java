package com.civitasv.spider.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Consumer;

public class ParseUtil {
    public static List<Map<String, String>> parseTxtOrCsv(String path) {
        File file = new File(path);
        List<Map<String, String>> res = new ArrayList<>();
        if (file.exists()) {
            try {
                Files.lines(file.toPath())
                        .forEach(new Consumer<String>() {
                            private int index = 0;
                            private String[] keys;

                            @Override
                            public void accept(String line) {
                                if (index == 0) {
                                    keys = line.split(",");
                                } else {
                                    String[] values = line.split(",");
                                    Map<String, String> item = new LinkedHashMap<>();
                                    for (int i = 0; i < values.length; i++)
                                        item.put(keys[i], values[i]);
                                    res.add(item);
                                }
                                index++;
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return res;
    }


}
