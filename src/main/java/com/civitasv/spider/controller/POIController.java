package com.civitasv.spider.controller;

import com.civitasv.spider.controller.helper.BaseController;
import com.civitasv.spider.controller.helper.ControllerFactory;
import com.civitasv.spider.helper.Enum.*;
import com.civitasv.spider.helper.exception.NoTryAgainException;
import com.civitasv.spider.helper.exception.TryAgainException;
import com.civitasv.spider.model.bo.POI;
import com.civitasv.spider.model.bo.Task;
import com.civitasv.spider.service.JobService;
import com.civitasv.spider.service.PoiCategoryService;
import com.civitasv.spider.service.PoiService;
import com.civitasv.spider.service.TaskService;
import com.civitasv.spider.service.serviceImpl.JobServiceImpl;
import com.civitasv.spider.service.serviceImpl.PoiCategoryServiceImpl;
import com.civitasv.spider.service.serviceImpl.PoiServiceImpl;
import com.civitasv.spider.service.serviceImpl.TaskServiceImpl;
import com.civitasv.spider.util.ControllerUtils;
import com.civitasv.spider.util.GitHubUtils;
import com.civitasv.spider.util.MessageUtil;
import com.civitasv.spider.viewmodel.POIViewModel;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class POIController extends BaseController {
    public TextField threadNum; // 线程数目
    public TextField keywords; // 关键字
    public TextArea keys; // 高德 API Key
    public TextField types; // 类型
    public TextField adCode; // 行政区六位代码
    public TextField rectangle; // 矩形左上角#矩形右下角
    public TextField threshold; // 阈值
    public Button openQPSBtn; // 查看 QPS
    public ChoiceBox<OutputType> format; // 输出格式
    public TextField outputDirectory; // 输出文件夹
    public Button chooseOutputFieldsBtn; // 选择输出字段
    public TextArea messageDetail; // 输出信息
    public TextField userFile; // 用户自定义文件
    public TabPane tabs; // tab 栏
    public Button directoryBtn; // 点击选择文件夹
    public Button execute; // 执行
    public Button poiType; // 点击查看 poi 类型
    public ChoiceBox<UserType> userType; // 用户类型
    public ChoiceBox<CoordinateType> rectangleCoordinateType; // 矩形坐标格式
    public ChoiceBox<CoordinateType> userFileCoordinateType; // 用户自定义文件坐标格式
    public MenuItem wechat; // 微信
    public MenuItem joinQQ; // QQ群

    public ChoiceBox<String> poiCateBig; // POI大类
    public ChoiceBox<String> poiCateMid; // POI中类
    public ChoiceBox<String> poiCateSub; // POI小类
    public Button poiCateAddBtn; // poi添加

    // 打开其它页面
    private final ControllerFactory controllerFactory = ControllerUtils.getControllerFactory();

    // 大中小类
    private String cateBigText, cateMidText, cateSubText;
    private String curCategoryId;

    // 输出字段
    public void outputFields(List<POI.OutputFields> val) {
        this.poiViewModel.outputFields(val);
    }

    private final TaskService taskService = new TaskServiceImpl();
    private final JobService jobService = new JobServiceImpl();
    private final PoiService poiService = new PoiServiceImpl();
    private final PoiCategoryService poiCategoryService = new PoiCategoryServiceImpl();

    public Stage stage() {
        return stage;
    }

    private POIViewModel poiViewModel;

    private boolean skipHint = false;

    public void show() throws IOException {
        init();
        initStageHandler();
        stage.show();
    }

    private void initStageHandler() {
        stage.setOnShown(event -> {
            try {
                // TODO: 2022/5/1 handleLastTask 执行了两次 
                if (handleLastTask(false) != null) {
                    skipHint = true;
                    execute();
                    skipHint = false;
                }
            } catch (TryAgainException | NoTryAgainException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void init() {
        this.poiViewModel = new POIViewModel(this);
        // 设置允许输入的格式
        this.threadNum.setTextFormatter(getFormatterOnlyNumber());
        this.threshold.setTextFormatter(getFormatterOnlyNumber());
        this.adCode.setTextFormatter(getFormatterOnlyNumber());
        this.types.setTextFormatter(getFormatterNumberPlusComma());
        this.keys.setTextFormatter(getFormatterNumberPlusCommaPlusEnglish());

        this.userType.setItems(new ObservableListWrapper<>(
                Arrays.asList(
                        UserType.IndividualDevelopers,
                        UserType.IndividualCertifiedDeveloper,
                        UserType.EnterpriseDeveloper)
        ));
        this.format.setItems(new ObservableListWrapper<>(
                Arrays.asList(
                        OutputType.CSV,
                        OutputType.GEOJSON,
                        OutputType.SHAPEFILE)
        ));

        // 设置坐标类型
        List<CoordinateType> list = Arrays.asList(CoordinateType.WGS84, CoordinateType.BD09, CoordinateType.GCJ02);
        this.rectangleCoordinateType.setItems(new ObservableListWrapper<>(list));
        this.userFileCoordinateType.setItems(new ObservableListWrapper<>(list));
        this.rectangleCoordinateType.getSelectionModel().selectFirst();
        this.userFileCoordinateType.getSelectionModel().selectFirst();

        wechat.setOnAction(event -> {
            try {
                openAbout(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        joinQQ.setOnAction(event -> {
            try {
                openAbout(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // 设置key
        this.keys.setText("");

        // 设置cate1下拉
        refreshChoiceBoxCateBig();

        this.poiCateBig.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> refreshChoiceBoxCateMid(newValue));
        this.poiCateMid.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> refreshChoiceBoxCateSub(newValue));
        this.poiCateSub.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> getPoiCategory(newValue));

        // messageDetail 设置为不可编辑
        this.messageDetail.setEditable(false);
    }

    private boolean continueLastTaskByAlert(Task task, int allJobSize, int unFinishJobSize) {
        return MessageUtil.alertConfirmationDialog(
                "未完成任务提示",
                "上一次任务未完成",
                "您有未完成的任务，请确认是否继续爬取\n" +
                        "任务状态：" + task.taskStatus().description() + "\n" +
                        "完成度：" +
                        (TaskStatus.Pause.equals(task.taskStatus()) || TaskStatus.Some_Failed.equals(task.taskStatus())
                                ? (allJobSize - unFinishJobSize) + "/" + allJobSize
                                : "任务正在预处理...")
                        + " \n" + "点击是则继续爬取上一个任务，否则放弃任务",
                "是",
                "否");
    }

    private boolean startNewTaskByAlert() {
        return MessageUtil.alertConfirmationDialog(
                "开启新任务",
                null,
                "是否使用当前参数开启新任务？",
                "是",
                "否");
    }

    public Task handleLastTask(boolean skipAlert) throws TryAgainException, NoTryAgainException, IOException {
        // 判断是否有未完成的task
        Task task = taskService.getUnFinishedTask();
        // 如果全部已完成，那么使 index 归零
        if (task == null) {
            jobService.clearTable();
            poiService.clearTable();
            return null;
        }

        if (!skipAlert && !continueLastTaskByAlert(task, jobService.count(), jobService.countUnFinished())) {
            // 如果用户选择不爬取未完成任务
            jobService.clearTable();
            poiService.clearTable();
            task.taskStatus(TaskStatus.Give_Up);
            taskService.updateById(task.toTaskPo());
            if (!StringUtils.isEmpty(outputDirectory.getText()) && !startNewTaskByAlert()) {
                throw new NoTryAgainException(NoTryAgainErrorCode.STOP_TASK);
            }
            return null;
        }

        // 初始化界面
        // 若当前用户填入的 key 与之前任务不同
        if (!keys.getText().equals(String.join(",", task.aMapKeys()))) {
            // 则选择新加入的 key
            if (!StringUtils.isEmpty(keys.getText())) {
                task.aMapKeys(Arrays.stream(keys.getText().split(","))
                        .collect(Collectors.toCollection(ArrayDeque::new)));
            }
            // 如果还未填入key，则直接使上次执行任务时的key
            keys.setText(String.join(",", task.aMapKeys()));
        }
        int selectedUserTypeIndex = userType.getSelectionModel().getSelectedIndex();
        if (!task.userType().code().equals(selectedUserTypeIndex)) {
            // 如果用户填入了不同的用户类型，选择新的
            if (selectedUserTypeIndex != -1) {
                task.userType(UserType.getUserType(selectedUserTypeIndex));
            }
            userType.getSelectionModel().select(task.userType().code());
        }
        types.setText(task.types());
        keywords.setText(task.keywords());
        tabs.getSelectionModel().select(task.boundaryType().code());
        threshold.setText(task.threshold().toString());
        if (!threadNum.getText().equals(task.threadNum().toString())) {
            // 如果用户填入了不同的线程数，选择新的
            if (!StringUtils.isEmpty(threadNum.getText())) {
                task.threadNum(Integer.parseInt(threadNum.getText()));
            }
            threadNum.setText(task.threadNum().toString());
        }
        int selectedOutputTypeIndex = format.getSelectionModel().getSelectedIndex();
        if (!task.outputType().code().equals(selectedOutputTypeIndex)) {
            // 如果用户填入了不同的输出格式，选择新的
            if (selectedOutputTypeIndex != -1) {
                task.outputType(OutputType.getOutputType(selectedOutputTypeIndex));
            }
            format.getSelectionModel().select(task.outputType().code());
        }
        outputDirectory.setText(task.outputDirectory());
        String configContent = task.boundaryConfig().split(":")[1];
        switch (task.boundaryType()) {
            case ADCODE:
                adCode.setText(configContent.split(",")[0]);
                break;
            case RECTANGLE:
                rectangle.setText(configContent.split(",")[0]);
                rectangleCoordinateType.getSelectionModel()
                        .select(CoordinateType.getCoordinateType(configContent.split(",")[1]));
                break;
            case CUSTOM:
                userFile.setText(configContent.split(",")[0]);
                userFileCoordinateType.getSelectionModel()
                        .select(CoordinateType.getCoordinateType(configContent.split(",")[1]));
                break;
        }
        taskService.updateById(task.toTaskPo());
        return task;
    }

    private void refreshChoiceBoxCateBig() {
        List<String> cateBig = poiCategoryService.getPoiCategoryBig();
        this.poiCateBig.setItems(new ObservableListWrapper<>(cateBig));
    }

    private void refreshChoiceBoxCateMid(String big) {
        this.cateBigText = big;
        List<String> cateMid = poiCategoryService.getPoiCategoryMid(this.cateBigText.substring(0, this.cateBigText.indexOf("(")));
        this.poiCateMid.setItems(new ObservableListWrapper<>(cateMid));
        this.poiCateMid.setValue("");
    }

    private void refreshChoiceBoxCateSub(String mid) {
        this.cateMidText = mid;
        List<String> cateSub = poiCategoryService.getPoiCategorySub(
                this.cateBigText.substring(0, this.cateBigText.indexOf("(")),
                this.cateMidText.substring(0, this.cateMidText.indexOf("("))
        );
        this.poiCateSub.setItems(new ObservableListWrapper<>(cateSub));
        this.poiCateSub.setValue("");
    }

    private void getPoiCategory(String sub) {
        if (StringUtils.isEmpty(sub)) {
            return;
        }
        this.cateSubText = sub;
        this.curCategoryId = poiCategoryService.getPoiCategoryId(
                this.cateBigText.substring(0, this.cateBigText.indexOf("(")),
                this.cateMidText.substring(0, this.cateMidText.indexOf("(")),
                this.cateSubText.substring(0, this.cateSubText.indexOf("("))
        );
    }

    public void addPoiCategory() {
        if ((this.poiCateBig.getValue().equals("")) || (this.poiCateMid.getValue().equals("")) || (this.poiCateSub.getValue().equals(""))) {
            MessageUtil.alert(Alert.AlertType.ERROR, "类型错误", null, "请完整选择POI类型！");
            return;
        }
        String text = this.types.getText();
        text = text.replace(" ", "");
        Set<String> types = Arrays.stream(text.split(",")).collect(Collectors.toSet());
        if (!types.contains(curCategoryId)) {
            if (!StringUtils.isEmpty(text)) {
                this.types.setText(text + "," + curCategoryId);
            } else {
                this.types.setText(curCategoryId);
            }
        }
    }

    private TextFormatter<Integer> getFormatterOnlyNumber() {
        return getFormatter("\\d*", "\\s");
    }

    private TextFormatter<Integer> getFormatterNumberPlusComma() {
        return getFormatter("[0-9,]*", "\\s");
    }

    private TextFormatter<Integer> getFormatterNumberPlusCommaPlusEnglish() {
        return getFormatter("[0-9,a-zA-Z]*", "\\s");
    }

    private TextFormatter<Integer> getFormatter(String passRegex) {
        return new TextFormatter<>(
                c -> Pattern.matches(passRegex, c.getText()) ? c : null);
    }

    private TextFormatter<Integer> getFormatter(String passRegex, String rejectRegex) {
        return new TextFormatter<>(
                c -> Pattern.matches(passRegex, c.getText()) && !Pattern.matches(rejectRegex, c.getText()) ? c : null);
    }

    public void execute() {
        try {
            poiViewModel.execute(handleLastTask(skipHint));
        } catch (TryAgainException | NoTryAgainException | IOException e) {
            e.printStackTrace();
        }
    }

    public void cancel() {
        poiViewModel.cancel();
    }

    public void chooseAdCode() throws IOException {
        CityChooseController controller = controllerFactory.createController(CityChooseController.class);
        controller.show(this);
    }

    public void chooseOutputFields() throws IOException {
        FieldsChooseController controller = controllerFactory.createController(FieldsChooseController.class);
        controller.show(this);
    }

    public void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择输入文件");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("geojson", "*.json")
        );
        File file = fileChooser.showOpenDialog(scene.getWindow());
        if (file != null)
            userFile.setText(file.getAbsolutePath());
    }

    public void chooseDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择输出文件夹");
        File file = directoryChooser.showDialog(scene.getWindow());
        if (file != null)
            outputDirectory.setText(file.getAbsolutePath());
    }

    public void openQPSPage() throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("https://console.amap.com/dev/flow/manage"));
    }

    public void openPOITypes() {
        this.types.setText("");
    }

    public void openGeocoding() throws IOException {
        GeocodingController controller = controllerFactory.createController(GeocodingController.class);
        controller.show();
    }

    public void openSpatialTransform() throws IOException {
        SpatialDataTransformController controller = controllerFactory.createController(SpatialDataTransformController.class);
        controller.show();
    }

    public void openCoordinateTransform() throws IOException {
        CoordinateTransformController controller = controllerFactory.createController(CoordinateTransformController.class);
        controller.show();
    }

    public void openAbout(boolean isQQ) throws IOException {
        AboutController controller = controllerFactory.createController(AboutController.class);
        controller.show(isQQ);
    }

    public void openDonate() throws IOException {
        DonateController controller = controllerFactory.createController(DonateController.class);
        controller.show("icon/zhifubao.jpg");
        DonateController controller2 = controllerFactory.createController(DonateController.class);
        controller2.show("icon/zhifubao2.jpg");
    }

    public void starsMe() throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("https://github.com/Civitasv/AMapPoi"));
    }

    public void updateVersion() {
        GitHubUtils.tryGetLatestRelease(true);
    }
}