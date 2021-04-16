package com.civitasv.spider.controller;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class LoadingController {
    public ProgressIndicator progress;
    public Button cancel;
    public Button message;

    public void setProgress(double progress) {
        this.progress.setProgress(progress);
    }

    public void cancel(EventHandler<MouseEvent> event) {
        cancel.setOnMouseClicked(event1 -> {
            if (event1 != null)
                event.handle(event1);
            // 关闭弹窗
            close();
        });
    }

    public void close() {
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
    }

    public void finish() {
        this.cancel.setVisible(false);
        this.message.setVisible(true);
    }
}
