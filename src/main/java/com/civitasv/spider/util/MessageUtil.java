package com.civitasv.spider.util;

import com.civitasv.spider.MainApplication;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.*;

/**
 * 消息提示
 */
public class MessageUtil {
    /**
     * 信息提示框
     *
     * @param type    {@link Alert.AlertType}
     * @param title   标题
     * @param header  头信息
     * @param content 具体内容
     */
    public static void alert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                MainApplication.class.getResource("myDialogs.css").toExternalForm());
        dialogPane.getStyleClass().add("myDialog");
        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.getIcons().add(new Image(MainApplication.class.getResourceAsStream("icon/icon.png")));
        alert.showAndWait();
    }
}
