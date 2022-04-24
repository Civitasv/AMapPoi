package com.civitasv.spider.controller.helper;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class BaseController {
    protected Parent root = null;
    protected Stage stage = null;
    protected Scene scene = null;
    protected ControllerAttr attrs;

    public void show() throws IOException {
        stage.show();
    }
}
