package com.civitasv.spider.util;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.text.DecimalFormat;

/**
 * 进度条
 */
public class MyProgressBar {
    // 最大值，当达到最大值时，进度条达到总长度
    private final int maxVal;
    // 进度条总长度
    private final int total;


    // 用于进度条显示的字符
    private final String finishChar;
    // 未完成的字符
    private final String unFinishChar;
    // 显示区域
    private final TextArea textArea;
    // 当前
    private int current;

    /**
     * 使用系统标准输出，显示字符进度条及其百分比
     */
    public MyProgressBar(TextArea textArea, int total, int maxVal, String finishChar, String unFinishChar) {
        this.textArea = textArea;
        this.total = total;
        this.maxVal = maxVal;
        this.finishChar = finishChar;
        this.unFinishChar = unFinishChar;
        Platform.runLater(this::init);
    }

    private void init() {
        this.textArea.appendText("\r\n");
        this.textArea.appendText("Progress:");
        this.textArea.appendText(String.format("%3d%%", 0));
        /*this.textArea.appendText("[");
        for (int i = 0; i < total; i++) {
            this.textArea.appendText(unFinishChar);
        }
        this.textArea.appendText("]");*/
    }

    /**
     * 显示进度条
     */
    public void show(int value) {
        if (value < 0 || value > maxVal) {
            return;
        }
        this.current = value;
        // 比例
        float rate = (float) (value * 1.0 / maxVal);
        // 比例*进度条总长度=当前长度
        draw(rate);
    }

    /**
     * 画指定长度个showChar
     */
    private void draw(float rate) {
        int finish = (int) (rate * this.total);
        int unFinish = total - finish;
        this.textArea.appendText("\r\n");
        /*for (int i = 0; i < total + 6; i++)
            this.textArea.deletePreviousChar();*/
        this.textArea.appendText("Progress:");
        this.textArea.appendText(String.format("%3d%%", (int) (rate * 100)));
        /*this.textArea.appendText("[");
        for (int i = 0; i < finish; i++) {
            this.textArea.appendText(finishChar);
        }
        for (int i = 0; i < unFinish; i++) {
            this.textArea.appendText(unFinishChar);
        }
        this.textArea.appendText("]");*/
        if ((int) (rate * 100) == 100)
            this.textArea.appendText("\r\n");
    }

    public int getCurrent() {
        return current;
    }
}
