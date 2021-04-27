package com.civitasv.spider;

import com.civitasv.spider.controller.GeocodingController;
import com.civitasv.spider.controller.LoadingController;
import com.civitasv.spider.controller.POIController;
import com.civitasv.spider.util.FXMLUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.*;

public class MainApplication extends Application {

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void start(Stage stage) throws IOException {
        POIController controller = new POIController();
        controller.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    public static void main(String[] args) {
        launch();
    }
}
