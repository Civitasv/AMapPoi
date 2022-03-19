package com.civitasv.spider.controller;

import com.civitasv.spider.MainApplication;
import com.civitasv.spider.model.City;
import com.civitasv.spider.util.MessageUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * Created by leon
 * 2022-03-19
 */
public class CityChooseController {
    // 该页面依赖于 POI 查询页面存在
    private POIController parent;
    public TreeView<City> cityTree;
    public Button btnConfirm,btnCancel;
    private String selectCityCode;

    public void show(POIController parent) throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("choose-city.fxml"));
        Parent root = fxmlLoader.load();
        CityChooseController controller = fxmlLoader.getController();
        controller.parent = parent;
        controller.initTreeView();

        Stage stage = new Stage();
        // 设为模态
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(parent.getMainStage());

        stage.setResizable(false);
        stage.setTitle("中国行政区划");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(MainApplication.class.getResource("styles.css").toString());
        stage.setScene(scene);
        stage.getIcons().add(new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream("icon/icon.png"))));
        stage.show();
    }

    private void initTreeView() {
        // 根节点
        City rootCity = new City("100000","中国");
        TreeItem<City> rootItem = new TreeItem<> (rootCity);
        rootItem.setExpanded(true);

        try {
            // 省节点
            List<City> provinces = parent.getDatabase().getCitesBelong("100000");
            for (City province : provinces) {
                TreeItem<City> provinceItem = new TreeItem<> (province);
                rootItem.getChildren().add(provinceItem);
                String provinceId = province.getCityId();
                // 市节点
                List<City> cities = parent.getDatabase().getCitesBelong(provinceId);
                for (City city : cities) {
                    TreeItem<City> cityItem = new TreeItem<> (city);
                    provinceItem.getChildren().add(cityItem);
                    String cityId = city.getCityId();
                    // 区县节点
                    List<City> districts = parent.getDatabase().getCitesBelong(cityId);
                    for(City district : districts) {
                        TreeItem<City> districtItem = new TreeItem<>(district);
                        cityItem.getChildren().add(districtItem);
                    }
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        // 设置
        cityTree.setRoot(rootItem);

        cityTree.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldItem, newItem) -> selectCityCode = newItem.getValue().getCityId()
        );

    }

    public void confirm() {
        if(StringUtils.isEmpty(selectCityCode)){
            MessageUtil.alert(Alert.AlertType.ERROR, "未选择", null, "请先选择行政区划！");
            return;
        }
        if(selectCityCode.equals("100000")){
            MessageUtil.alert(Alert.AlertType.ERROR, "范围过大", null, "选择的查询范围过大！");
            return;
        }
        parent.adCode.setText(selectCityCode);
        // 像这样关闭舞台会绕过 onCloseRequest 事件处理程序（如果有的话）
        Stage stage = (Stage) btnConfirm.getScene().getWindow();
        // 关闭前通知一下事件处理
        stage.close();
    }

    public void cancel() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}
