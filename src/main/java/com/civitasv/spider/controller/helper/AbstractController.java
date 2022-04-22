package com.civitasv.spider.controller.helper;

import javafx.scene.Parent;

public abstract class AbstractController {
    protected Parent root = null;

    public Parent getRoot() {
        return root;
    }

    public void setRoot(Parent root) {
        this.root = root;
    }
}
