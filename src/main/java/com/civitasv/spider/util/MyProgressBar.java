package com.civitasv.spider.util;

import javafx.scene.control.TextArea;

import java.text.DecimalFormat;

public class MyProgressBar {
    /**
     * 进度条固定长度为50，即total -> 50
     */
    private final int len;
    private final int total;

    /**
     * 用于进度条显示的字符
     */
    private final String showChar;
    // 显示区域
    private final TextArea textArea;

    private final DecimalFormat formatter = new DecimalFormat("#.##%");

    /**
     * 使用系统标准输出，显示字符进度条及其百分比
     */
    public MyProgressBar(TextArea textArea, int total, int len, String showChar) {
        this.textArea = textArea;
        this.total = total;
        this.len = len;
        this.showChar = showChar;
    }

    /**
     * 显示进度条
     */
    public void show(int value) {
        if (value < 0 || value > total) {
            return;
        }
        // 比例
        float rate = (float) (value * 1.0 / total);
        // 比例*进度条总长度=当前长度
        draw(rate);
        this.textArea.appendText("\r\n");
    }

    /**
     * 画指定长度个showChar
     */
    private void draw(float rate) {
        int len = (int) (rate * this.len);
        this.textArea.appendText("Progress: ");
        for (int i = 0; i < len; i++) {
            this.textArea.appendText(showChar);
        }
        for (int i = 0; i < this.len - len; i++) {
            this.textArea.appendText(" ");
        }
        this.textArea.appendText(" |" + format(rate));
    }


    private String format(float num) {
        return formatter.format(num);
    }
}
