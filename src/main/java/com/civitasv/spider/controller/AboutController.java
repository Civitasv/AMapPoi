package com.civitasv.spider.controller;

import com.civitasv.spider.MainApplication;
import com.civitasv.spider.controller.helper.BaseController;
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
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle(isQQ ? "加入用户群" : "关注公众号");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(MainApplication.class.getResource("styles.css")).toString());
        stage.setScene(scene);
        stage.getIcons().add(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("icon/icon.png"))));
        stage.show();
    }

    private void init(boolean isQQ) {
        Image image = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream(isQQ ? "icon/qq.png" : "icon/wechat.jpg")));
        about.setImage(image);
    }
}
