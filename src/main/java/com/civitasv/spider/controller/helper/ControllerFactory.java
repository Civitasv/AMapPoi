package com.civitasv.spider.controller.helper;

import com.civitasv.spider.MainApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ControllerFactory {

    private final Map<Class<? extends BaseController>, ControllerAttr> controllerClassFxmlMap;

    public ControllerFactory(Map<Class<? extends BaseController>, ControllerAttr> controllerClassFxmlMap) {
        this.controllerClassFxmlMap = new HashMap<>(controllerClassFxmlMap);
    }

    public <T extends BaseController> T createController(Class<T> clazz, String title) {
        try {
            if (!controllerClassFxmlMap.containsKey(clazz)) {
                throw new IllegalArgumentException(clazz.toString() + "尚未注册");
            }
            FXMLLoader fxmlLoader = new FXMLLoader(controllerClassFxmlMap.get(clazz));
            Parent root = fxmlLoader.load();
            T controller = fxmlLoader.getController();
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle(title);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(MainApplication.class.getResource("styles.css")).toString());
            stage.setScene(scene);
            stage.getIcons().add(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("icon/icon.png"))));

            new BaseController.Builder().root(root).scene()
            controller.setRoot(root);
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
