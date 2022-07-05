package com.civitasv.spider.webdao.impl;

import com.civitasv.spider.MainApplication;
import com.civitasv.spider.api.RetrofitDataVClient;
import com.civitasv.spider.webdao.DataVDao;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import retrofit2.Call;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

public class DataVDaoImpl implements DataVDao {
    @Override
    public JsonObject getBoundary(String areaCode) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            Path path = Paths.get("vendor", "geojson", areaCode + ".json");
            try (Stream<String> stream = Files.lines(path, StandardCharsets.UTF_8)) {
                stream.forEach(s -> stringBuilder.append(s).append("\n"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new JsonParser().parse(stringBuilder.toString()).getAsJsonObject();
    }
}
