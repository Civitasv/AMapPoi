package com.civitasv.spider;

import com.civitasv.spider.controller.POIController;
import com.civitasv.spider.controller.helper.ControllerFactory;
import com.civitasv.spider.util.ControllerUtils;
import com.civitasv.spider.util.GitHubUtils;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void start(Stage stage) throws IOException {
        ControllerFactory controllerFactory = ControllerUtils.getControllerFactory();
        POIController controller = controllerFactory.createController(POIController.class);
        controller.show();
        GitHubUtils.tryGetLatestRelease(false);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    public static void main(String[] args) {
        launch();
    }
}