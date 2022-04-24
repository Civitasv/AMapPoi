package com.civitasv.spider.controller.helper;

import com.civitasv.spider.MainApplication;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.Objects;

public class ControllerAttr {
    public String title;
    public URL fxmlURL;
    public URL stylesURL;
    public Image icon;

    private ControllerAttr(String title, URL fxmlURL, URL stylesURL, Image icon) {
        this.title = title;
        this.fxmlURL = fxmlURL;
        this.stylesURL = stylesURL;
        this.icon = icon;
    }

    public static class Builder {
        private String title;
        private URL fxmlURL;
        private URL stylesURL;
        private Image icon;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder fxmlFile(String fxmlFile) {
            this.fxmlURL = MainApplication.class.getResource(fxmlFile);
            return this;
        }

        public Builder stylesFile(String stylesFile) {
            this.stylesURL = MainApplication.class.getResource(stylesFile);
            return this;
        }

        public Builder iconFile(String iconFile){
            this.icon = new Image(Objects.requireNonNull(MainApplication.class.getResourceAsStream(iconFile)));
            return this;
        }

        public ControllerAttr build() {
            return new ControllerAttr(title, fxmlURL, stylesURL, icon);
        }
    }
}
