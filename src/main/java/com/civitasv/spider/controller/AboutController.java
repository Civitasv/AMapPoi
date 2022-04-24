package com.civitasv.spider.controller;

import com.civitasv.spider.MainApplication;
import com.civitasv.spider.controller.helper.BaseController;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class AboutController extends BaseController {
    public ImageView about;

    public void show(boolean isQQ) throws IOException {
        init(isQQ);
        stage.show();
    }

    private void init(boolean isQQ) {
        Image image = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream(isQQ ? "icon/qq.png" : "icon/wechat.jpg")));
        about.setImage(image);
    }
}
