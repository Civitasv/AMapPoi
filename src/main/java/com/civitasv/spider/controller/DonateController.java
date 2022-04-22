package com.civitasv.spider.controller;

import com.civitasv.spider.MainApplication;
import com.civitasv.spider.controller.helper.AbstractController;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class DonateController extends AbstractController {
    public ImageView about;

    private String imagePath;

    public DonateController() {
    }

    public void show(String imagePath) throws IOException {
        this.imagePath = imagePath;
        init();
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("捐赠");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(MainApplication.class.getResource("styles.css")).toString());
        stage.setScene(scene);
        stage.getIcons().add(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("icon/icon.png"))));
        stage.show();
    }

    private void init() {
        Image image = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream(imagePath)));
        about.setImage(image);
    }
}
