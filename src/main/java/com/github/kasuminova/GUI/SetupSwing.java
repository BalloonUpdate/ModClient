package com.github.kasuminova.GUI;

import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneDarkContrastIJTheme;

import javax.swing.*;
import java.awt.*;

/**
 * @author Kasumi_Nova
 * 一个工具类，用于初始化 Swing（即美化）
 */
public class SetupSwing {
    public static void init() {
        //抗锯齿字体
        System.setProperty("awt.useSystemAAFontSettings", "lcd");
        System.setProperty("swing.aatext", "true");

        //UI 配置线程
        Thread uiThread = new Thread(() -> {
            //设置圆角弧度
            UIManager.put("Button.arc", 7);
            UIManager.put("Component.arc", 7);
            UIManager.put("ProgressBar.arc", 7);
            UIManager.put("TextComponent.arc", 5);
            UIManager.put("CheckBox.arc", 3);
            //设置滚动条
            UIManager.put("ScrollBar.showButtons", false);
//        UIManager.put("ScrollBar.trackArc", 3);
            UIManager.put("ScrollBar.thumbArc", 7);
            UIManager.put("ScrollBar.width", 12);
//        UIManager.put("ScrollBar.trackInsets", new Insets(5,5,5,5));
            UIManager.put("ScrollBar.thumbInsets", new Insets(2,2,2,2));
            UIManager.put("ScrollBar.track", new Color(0,0,0,0));
            //选项卡分隔线
            UIManager.put("TabbedPane.showTabSeparators", true);
            UIManager.put("TabbedPane.tabSeparatorsFullHeight", true);
            //菜单
            UIManager.put("MenuItem.selectionType", "underline");
            UIManager.put("MenuItem.underlineSelectionHeight", 3);
            UIManager.put("MenuItem.margin", new Insets(5,8,3,5));
            //窗口标题居中
            UIManager.put("TitlePane.centerTitle", true);
            //进度条
            UIManager.put("ProgressBar.repaintInterval", 15);
            UIManager.put("ProgressBar.cycleTime", 7500);
        });
        uiThread.start();

        Thread themeThread = new Thread(() -> {
            //更新 UI
            try {
                UIManager.setLookAndFeel(new FlatAtomOneDarkContrastIJTheme());
            } catch (Exception e) {
                System.err.println("Failed to initialize LaF");
                e.printStackTrace();
            }
        });
        themeThread.start();

        try {
            uiThread.join();
            themeThread.join();
        } catch (InterruptedException ignored) {}
    }
}
