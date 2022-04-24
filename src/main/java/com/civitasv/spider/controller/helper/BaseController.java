package com.civitasv.spider.controller.helper;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BaseController {
    protected Parent root = null;
    protected Stage stage = null;
    protected Scene scene = null;

    public BaseController(){}
    private BaseController(Parent root, Stage stage, Scene scene) {
        this.root = root;
        this.stage = stage;
        this.scene = scene;
    }

    public static class Builder{
        private Parent root = null;
        private Stage stage = null;
        private Scene scene = null;

        public Builder root(Parent root){
            this.root = root;
            return this;
        }

        public Builder stage(Stage stage){
            this.stage = stage;
            return this;
        }

        public Builder scene(Scene scene){
            this.scene = scene;
            return this;
        }

        public BaseController build(){
            return new BaseController(root, stage, scene);
        }
    }
}
