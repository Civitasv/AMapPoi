package com.civitasv.spider.controller;

import com.civitasv.spider.MainApplication;
import com.civitasv.spider.controller.helper.BaseController;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.Objects;

public class DonateController extends BaseController {
    public ImageView about;

    public void show(String imagePath) throws IOException {
        init(imagePath);
        stage.show();
    }

    private void init(String imagePath) {
        Image image = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream(imagePath)));
        about.setImage(image);
    }
}
