package com.civitasv.spider.controller.helper;

import java.util.HashMap;
import java.util.Map;

public class ControllerFactoryBuilder {

    private final Map<Class<? extends BaseController>, ControllerAttr> controllerClassFxmlMap = new HashMap<>();

    public ControllerFactoryBuilder addController(Class<? extends BaseController> clazz, ControllerAttr controllerAttr) {
        controllerClassFxmlMap.put(clazz, controllerAttr);
        return this;
    }

    public ControllerFactory build() {
        return new ControllerFactory(controllerClassFxmlMap);
    }
}
