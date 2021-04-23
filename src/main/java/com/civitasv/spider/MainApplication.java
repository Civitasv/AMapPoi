package com.civitasv.spider;

import com.civitasv.spider.controller.GeocodingController;
import com.civitasv.spider.controller.LoadingController;
import com.civitasv.spider.controller.POIController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.*;

public class MainApplication extends Application {

    private static Scene scene;

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("poi"));
        scene.getStylesheets().add(MainApplication.class.getResource("styles.css").toString());
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setTitle("POIKit");
        stage.getIcons().add(new Image(MainApplication.class.getResourceAsStream("icon/icon.png")));
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(fxml + ".fxml"));
        Parent root = fxmlLoader.load();
        if (fxmlLoader.getController() instanceof GeocodingController) {
            GeocodingController controller = fxmlLoader.getController();
            controller.init();
        }
        if (fxmlLoader.getController() instanceof POIController) {
            POIController controller = fxmlLoader.getController();
            controller.init();
        }
        return root;
    }

    public static void main(String[] args) {
        launch();
    }

    public static Scene getScene() {
        return scene;
    }
}
