package com.civitasv.spider.controller;

import com.civitasv.spider.MainApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class DonateController {
    public ImageView about;

    public void show() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("donate.fxml"));
        Parent root = fxmlLoader.load();
        DonateController controller = fxmlLoader.getController();
        controller.init();
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("捐赠");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(MainApplication.class.getResource("styles.css").toString());
        stage.setScene(scene);
        stage.getIcons().add(new Image(MainApplication.class.getResourceAsStream("icon/icon.png")));
        stage.show();
    }

    private void init() {
        Image image = new Image(MainApplication.class.getResourceAsStream("icon/zhifubao.jpg"));
        about.setImage(image);
    }
}
