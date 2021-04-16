package com.civitasv.spider.api;

import java.util.ArrayList;
import java.util.List;

public class AMapKeys {
    private static final List<String> AMAP_KEYS = new ArrayList<>();

    static {
        AMAP_KEYS.add("17b3ad7ccaafe0b0fd1041ce89d20024");
    }

    public static void addKey(String key) {
        AMAP_KEYS.add(key);
    }

    public static List<String> getAmapKeys() {
        return AMAP_KEYS;
    }
}
