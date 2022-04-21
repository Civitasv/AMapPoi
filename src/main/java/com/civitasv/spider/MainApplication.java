package com.civitasv.spider;

import com.civitasv.spider.controller.POIController;
import com.civitasv.spider.model.GitHubRelease;
import com.civitasv.spider.util.FileUtil;
import com.civitasv.spider.util.GitHubUtils;
import com.civitasv.spider.util.MessageUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

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
        tryGetLatestRelease();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    public static void main(String[] args) {
        launch();
    }

    public void tryGetLatestRelease(){
        ExecutorService worker = Executors.newSingleThreadExecutor();
        worker.submit(()->{
            try {
                GitHubRelease gitHubReleaseLatest = GitHubUtils.getGitHubReleaseLatest();
                String versionFilePath = Objects.requireNonNull(MainApplication.class.getResource("version")).toString();
                String currentVersion = FileUtil.readFile(versionFilePath, "file:/");
                if(currentVersion.equals(gitHubReleaseLatest.getTag_name())){
                   return;
                }
                final FutureTask<Boolean> query = new FutureTask<>(() ->
                        MessageUtil.alertConfirmationDialog("新版本更新提示",
                                "新版本更新提示",
                                "POIKit已发布新版本，版本相关信息如下：\n" +
                                        "版本号：" + gitHubReleaseLatest.getTag_name() + "\n" +
                                        "标题：" + gitHubReleaseLatest.getName() + "\n" +
                                        "描述：\n" + gitHubReleaseLatest.getBody() + "\n",
                                "前往下载新版本",
                                "关闭"));
                Platform.runLater(query);
                // 阻塞本线程
                if (query.get()) {
                    Desktop.getDesktop().browse(new URI(gitHubReleaseLatest.getHtml_url()));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }
}