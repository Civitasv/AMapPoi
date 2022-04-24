package com.civitasv.spider.controller.helper;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ControllerFactory {

    private final Map<Class<? extends BaseController>, ControllerAttr> controllerClassAttrsMap;

    public ControllerFactory(Map<Class<? extends BaseController>, ControllerAttr> controllerClassAttrsMap) {
        this.controllerClassAttrsMap= new HashMap<>(controllerClassAttrsMap);
    }

    public <T extends BaseController> T createController(Class<T> clazz) {
        try {
            if (!controllerClassAttrsMap.containsKey(clazz)) {
                throw new IllegalArgumentException(clazz.toString() + "尚未注册");
            }
            ControllerAttr attrs = controllerClassAttrsMap.get(clazz);
            FXMLLoader fxmlLoader = new FXMLLoader(attrs.fxmlURL);
            Parent root = fxmlLoader.load();
            T controller = fxmlLoader.getController();
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle(attrs.title);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(attrs.stylesURL.toString());
            stage.setScene(scene);
            stage.getIcons().add(attrs.icon);
            controller.root = root;
            controller.stage = stage;
            controller.scene = scene;
            controller.attrs = attrs;
            return controller;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
