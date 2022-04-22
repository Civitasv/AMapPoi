package com.civitasv.spider.controller.helper;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ControllerFactoryBuilder {

    private final Map<Class<? extends AbstractController>, URL> controllerClassFxmlMap = new HashMap<>();

    public ControllerFactoryBuilder addController(Class<? extends AbstractController> clazz, URL fxmlPath){
        controllerClassFxmlMap.put(clazz, fxmlPath);
        return this;
    }

    public ControllerFactory build(){
        return new ControllerFactory(controllerClassFxmlMap);
    }
}
