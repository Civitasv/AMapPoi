package com.civitasv.spider.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class HomeController {

    @FXML
    public void click() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("Look, an Information Dialog");
        alert.setContentText("I have a great message for you!");

        alert.showAndWait();
    }
}
