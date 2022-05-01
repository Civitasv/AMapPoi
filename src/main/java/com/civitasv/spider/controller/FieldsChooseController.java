package com.civitasv.spider.controller;

import com.civitasv.spider.controller.helper.BaseController;
import com.civitasv.spider.model.bo.POI;
import com.civitasv.spider.util.MessageUtil;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.stage.Modality;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于填入所有输出的字段
 */
public class FieldsChooseController extends BaseController {
    // 该页面依赖于 POI 查询页面存在
    public TreeView<POI.OutputFields> fieldsView;
    public Button btnConfirm, btnCancel;
    private POIController parent;

    public void show(POIController parent) throws IOException {
        this.parent = parent;
        initFieldsListView();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(parent.stage());
        stage.show();
    }

    private void initFieldsListView() {
        CheckBoxTreeItem<POI.OutputFields> root = new CheckBoxTreeItem<>();
        for (POI.OutputFields value : POI.OutputFields.values()) {
            CheckBoxTreeItem<POI.OutputFields> item = new CheckBoxTreeItem<>(value);
            item.selectedProperty().setValue(value.checked());
            root.getChildren().add(item);
        }
        fieldsView.setCellFactory(CheckBoxTreeCell.forTreeView());
        fieldsView.setShowRoot(false);
        fieldsView.setRoot(root);
    }

    public void confirm() {
        List<POI.OutputFields> selectedFields = new ArrayList<>();
        // get all selected properties
        for (TreeItem<POI.OutputFields> item : fieldsView.getRoot().getChildren()) {
            boolean checked = ((CheckBoxTreeItem<POI.OutputFields>) item).selectedProperty().getValue();
            item.getValue().checked(checked);
            if (checked) {
                selectedFields.add(item.getValue());
            }
        }

        if (selectedFields.size() == 0) {
            MessageUtil.alert(Alert.AlertType.ERROR, "未选择任何字段", null, "至少选择一个输出字段！");
            return;
        }
        // give it to POIController
        parent.outputFields(selectedFields);
        stage.close();
    }

    public void cancel() {
        stage.close();
    }
}
