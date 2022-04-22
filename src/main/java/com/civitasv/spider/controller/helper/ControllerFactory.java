package com.civitasv.spider.controller.helper;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ControllerFactory {

    private final Map<Class<? extends AbstractController>, URL> controllerClassFxmlMap;

    public ControllerFactory(Map<Class<? extends AbstractController>, URL> controllerClassFxmlMap) {
        this.controllerClassFxmlMap = new HashMap<>(controllerClassFxmlMap);
    }

    public <T extends AbstractController> T createController(Class<T> clazz){
        try {
            if(!controllerClassFxmlMap.containsKey(clazz)){
                throw new IllegalArgumentException(clazz.toString() + "尚未注册");
            }
            FXMLLoader fxmlLoader = new FXMLLoader(controllerClassFxmlMap.get(clazz));
            Parent root = fxmlLoader.load();
            T controller = fxmlLoader.getController();
            controller.setRoot(root);
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
