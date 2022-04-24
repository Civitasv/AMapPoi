package com.civitasv.spider.controller.helper;

import com.civitasv.spider.MainApplication;

import java.net.URL;

public class ControllerAttr {
    public String title;
    public URL fxmlURL;
    public URL stylesURL;

    private ControllerAttr(String title, URL fxmlURL, URL stylesURL) {
        this.title = title;
        this.fxmlURL = fxmlURL;
        this.stylesURL = stylesURL;
    }

    public static class Builder {
        private String title;
        private URL fxmlURL;
        private URL stylesURL;

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

        public ControllerAttr build() {
            return new ControllerAttr(title, fxmlURL, stylesURL);
        }
    }
}
