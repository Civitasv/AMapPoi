package com.civitasv.spider.util;

import com.civitasv.spider.MainApplication;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.*;

/**
 * 消息提示
 */
public class MessageUtil {
    public static void alert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(MainApplication.class.getResourceAsStream("icon/icon.png")));
        alert.showAndWait();
    }
}
