package com.civitasv.spider;

import com.civitasv.spider.controller.POIController;
import com.civitasv.spider.util.GitHubUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("poi.fxml"));
        Parent root = fxmlLoader.load();
        POIController controller = fxmlLoader.getController();
        controller.show(root);
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