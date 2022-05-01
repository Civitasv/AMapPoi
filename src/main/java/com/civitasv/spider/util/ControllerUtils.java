package com.civitasv.spider.util;

import com.civitasv.spider.controller.*;
import com.civitasv.spider.controller.helper.ControllerAttr;
import com.civitasv.spider.controller.helper.ControllerFactory;
import com.civitasv.spider.controller.helper.ControllerFactoryBuilder;

public class ControllerUtils {
    private static ControllerFactory controllerFactory;

    public static ControllerFactory getControllerFactory() {
        if (controllerFactory != null) {
            return controllerFactory;
        }
        controllerFactory = new ControllerFactoryBuilder()
                .addController(DonateController.class,
                        new ControllerAttr.Builder().title("捐赠").fxmlFile("donate.fxml").stylesFile("styles.css").iconFile("icon/icon.png").build())
                .addController(POIController.class,
                        new ControllerAttr.Builder().title("POIKit").fxmlFile("poi.fxml").stylesFile("styles.css").iconFile("icon/icon.png").build())
                .addController(AboutController.class,
                        new ControllerAttr.Builder().title("关于").fxmlFile("about.fxml").stylesFile("styles.css").iconFile("icon/icon.png").build())
                .addController(CityChooseController.class,
                        new ControllerAttr.Builder().title("选择城市").fxmlFile("choose-city.fxml").stylesFile("styles.css").iconFile("icon/icon.png").build())
                .addController(FieldsChooseController.class,
                        new ControllerAttr.Builder().title("选择需要输出的字段").fxmlFile("choose-fields.fxml").stylesFile("styles.css").iconFile("icon/icon.png").build())
                .addController(CoordinateTransformController.class,
                        new ControllerAttr.Builder().title("坐标转换").fxmlFile("transform-coordinate.fxml").stylesFile("styles.css").iconFile("icon/icon.png").build())
                .addController(GeocodingController.class,
                        new ControllerAttr.Builder().title("地理编码").fxmlFile("geocoding.fxml").stylesFile("styles.css").iconFile("icon/icon.png").build())
                .addController(SpatialDataTransformController.class,
                        new ControllerAttr.Builder().title("格式转换").fxmlFile("transform-spatial-data.fxml").stylesFile("styles.css").iconFile("icon/icon.png").build())
                .build();
        return controllerFactory;
    }
}
