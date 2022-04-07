package com.civitasv.spider.dao;

import com.civitasv.spider.model.Geocodes;
import com.civitasv.spider.model.bo.POI;

public interface AMapDao {
    Geocodes.Response geocoding(String key, String address, String city);

    POI getPoi(String key, String polygon, String keywords, String types, String extensions, int page, int size);
}
