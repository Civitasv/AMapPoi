package com.civitasv.spider.controller;

import com.civitasv.spider.MainApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class AboutController {
    public ImageView about;

    public void show(boolean isQQ) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("about.fxml"));
        Parent root = fxmlLoader.load();
        AboutController controller = fxmlLoader.getController();
        controller.init(isQQ);
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle(isQQ ? "加入用户群" : "关注公众号");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(MainApplication.class.getResource("styles.css").toString());
        stage.setScene(scene);
        stage.getIcons().add(new Image(MainApplication.class.getResourceAsStream("icon/icon.png")));
        stage.show();
    }

    private void init(boolean isQQ) {
        Image image = new Image(MainApplication.class.getResourceAsStream(isQQ ? "icon/qq.png" : "icon/wechat.jpg"));
        about.setImage(image);
    }
}
