package com.civitasv.spider.controller;

import com.civitasv.spider.controller.helper.BaseController;
import com.civitasv.spider.model.po.City;
import com.civitasv.spider.service.CityCodeService;
import com.civitasv.spider.service.serviceImpl.CityCodeServiceImpl;
import com.civitasv.spider.util.MessageUtil;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Modality;

import java.io.IOException;
import java.util.List;

/**
 * Created by leon
 * 2022-03-19
 */
public class CityChooseController extends BaseController {
    // 该页面依赖于 POI 查询页面存在
    public TreeView<City> cityTree;
    public Button btnConfirm, btnCancel;
    private POIController parent;
    private City selectedCity;
    private final CityCodeService cityCodeService = new CityCodeServiceImpl();

    public void show(POIController parent) throws IOException {
        this.parent = parent;
        initTreeView();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(parent.stage());
        stage.show();
    }

    private void initTreeView() {
        // 根节点
        City rootCity = new City("100000", "中国");
        TreeItem<City> rootItem = new TreeItem<>(rootCity);
        rootItem.setExpanded(true);

        // 省节点
        List<City> provinces = cityCodeService.listByCityId("100000");
        for (City province : provinces) {
            TreeItem<City> provinceItem = new TreeItem<>(province);
            rootItem.getChildren().add(provinceItem);
            String provinceId = province.cityId();
            // 市节点
            List<City> cities = cityCodeService.listByCityId(provinceId);
            for (City city : cities) {
                TreeItem<City> cityItem = new TreeItem<>(city);
                provinceItem.getChildren().add(cityItem);
                String cityId = city.cityId();
                // 区县节点
                List<City> districts = cityCodeService.listByCityId(cityId);
                for (City district : districts) {
                    TreeItem<City> districtItem = new TreeItem<>(district);
                    cityItem.getChildren().add(districtItem);
                }
            }
        }

        // 设置
        cityTree.setRoot(rootItem);

        cityTree.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldItem, newItem) -> selectedCity
                        = newItem.getValue()
        );

    }

    public void confirm() {
        if (selectedCity == null) {
            MessageUtil.alert(Alert.AlertType.ERROR, "未选择", null, "请先选择行政区划！");
            return;
        }
        if (selectedCity.cityId().equals("100000")) {
            MessageUtil.alert(Alert.AlertType.ERROR, "范围过大", null, "选择的查询范围过大！");
            return;
        }
        parent.adCode.setText(selectedCity.cityId());
        // 关闭前通知一下事件处理
        stage.close();
    }

    public void cancel() {
        stage.close();
    }
}
