package com.civitasv.spider.util;

import com.civitasv.spider.MainApplication;
import com.civitasv.spider.controller.*;
import com.civitasv.spider.controller.helper.ControllerFactory;
import com.civitasv.spider.controller.helper.ControllerFactoryBuilder;

import java.util.Objects;

public class ControllerUtils {

    private static ControllerFactory controllerFactory;

    public static ControllerFactory getControllerFactory(){
        if(controllerFactory != null){
            return controllerFactory;
        }
        ControllerFactoryBuilder builder = new ControllerFactoryBuilder();
        builder.addController(DonateController.class,
                Objects.requireNonNull(MainApplication.class.getResource("donate.fxml")));
        builder.addController(POIController.class,
                Objects.requireNonNull(MainApplication.class.getResource("poi.fxml")));
        builder.addController(AboutController.class,
                Objects.requireNonNull(MainApplication.class.getResource("about.fxml")));
        builder.addController(CityChooseController.class,
                Objects.requireNonNull(MainApplication.class.getResource("choose-city.fxml")));
        builder.addController(CoordinateTransformController.class,
                Objects.requireNonNull(MainApplication.class.getResource("transform-coordinate.fxml")));
        builder.addController(GeocodingController.class,
                Objects.requireNonNull(MainApplication.class.getResource("geocoding.fxml")));
        builder.addController(SpatialDataTransformController.class,
                Objects.requireNonNull(MainApplication.class.getResource("transform-spatial-data.fxml")));

        controllerFactory = builder.build();
        return controllerFactory;
    }
}
