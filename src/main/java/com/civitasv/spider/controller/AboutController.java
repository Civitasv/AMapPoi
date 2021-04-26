package com.civitasv.spider.controller;

import com.civitasv.spider.MainApplication;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class AboutController {
    public ImageView about;

    public void init(boolean isQQ) {
        Image image = new Image(MainApplication.class.getResourceAsStream(isQQ ? "icon/qq.png" : "icon/wechat.jpg"));
        about.setImage(image);
    }
}
