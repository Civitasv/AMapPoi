package com.civitasv.spider.util;

import com.civitasv.spider.MainApplication;
import com.civitasv.spider.controller.*;
import com.civitasv.spider.controller.helper.ControllerAttr;
import com.civitasv.spider.controller.helper.ControllerFactory;
import com.civitasv.spider.controller.helper.ControllerFactoryBuilder;

import java.util.Objects;

public class ControllerUtils {
    private static ControllerFactory controllerFactory;

    public static ControllerFactory getControllerFactory() {
        if (controllerFactory != null) {
            return controllerFactory;
        }
        controllerFactory = new ControllerFactoryBuilder()
                .addController(DonateController.class,
                        new ControllerAttr.Builder().title("捐赠").fxmlFile("donate.fxml").stylesFile("styles.css").build())
                .addController(POIController.class,
                        new ControllerAttr.Builder().title("POIKit").fxmlFile("poi.fxml").stylesFile("styles.css").build())
                .addController(AboutController.class,
                        new ControllerAttr.Builder().title("关于").fxmlFile("about.fxml").stylesFile("styles.css").build())
                .addController(CityChooseController.class,
                        new ControllerAttr.Builder().title("选择城市").fxmlFile("choose-city.fxml").stylesFile("styles.css").build())
                .addController(CoordinateTransformController.class,
                        new ControllerAttr.Builder().title("坐标转换").fxmlFile("transform-coordinate.fxml").stylesFile("styles.css").build())
                .addController(GeocodingController.class,
                        new ControllerAttr.Builder().title("地理编码").fxmlFile("geocoding.fxml").stylesFile("styles.css").build())
                .addController(SpatialDataTransformController.class,
                        new ControllerAttr.Builder().title("格式转换").fxmlFile("transform-spatial-data.fxml").stylesFile("styles.css").build())
                .build();
        return controllerFactory;
    }
}
