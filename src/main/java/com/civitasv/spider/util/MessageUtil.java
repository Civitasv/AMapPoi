package com.civitasv.spider.util;

import com.civitasv.spider.MainApplication;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.Optional;

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
                Objects.requireNonNull(MainApplication.class.getResource("myDialogs.css")).toExternalForm());
        dialogPane.getStyleClass().add("myDialog");
        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("icon/icon.png"))));
        alert.showAndWait();
    }

    public static boolean alertConfirmationDialog(String title, String header, String content, String fooText, String barText) {
        ButtonType foo = new ButtonType(fooText, ButtonBar.ButtonData.OK_DONE);
        ButtonType bar = new ButtonType(barText, ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, content, foo, bar);
        alert.setTitle(title);
        alert.setHeaderText(header);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                Objects.requireNonNull(MainApplication.class.getResource("myDialogs.css")).toExternalForm());
        dialogPane.getStyleClass().add("myDialog");
        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("icon/icon.png"))));
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && foo.equals(result.get());
    }
}