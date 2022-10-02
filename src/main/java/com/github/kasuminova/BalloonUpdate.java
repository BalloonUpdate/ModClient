package com.github.kasuminova;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.Name("BalloonUpdatePlugin")
@IFMLLoadingPlugin.TransformerExclusions("github.kasuminova")
public class BalloonUpdate implements IFMLLoadingPlugin {
    // static 代码块, 超前调用外部 JAR 包
    static {
        Logger logger = LogManager.getLogger("BalloonUpdateModLoader");
        File directory = new File(".");
        File[] files = directory.listFiles();
        assert files != null;

        String path = null;

        //根据自定义文件名 BalloonUpdateFileName 内的文件名搜索文件, 如果文件不存在则不做任何修改
        File fileNameTXT = new File("./BalloonUpdateFileName.txt");
        if (fileNameTXT.exists() && fileNameTXT.isFile()) {
            try {
                Reader reader = new InputStreamReader(Files.newInputStream(fileNameTXT.toPath()), StandardCharsets.UTF_8);
                StringBuilder sb = new StringBuilder();
                int ch;
                while ((ch = reader.read()) != -1) {
                    sb.append((char) ch);
                }
                reader.close();

                File target = new File("./" + sb);
                if (target.exists() && target.isFile()) {
                    path = target.getAbsoluteFile().toURI().toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //根据自定义文件名 OldBalloonUpdateFileName 内的文件名搜索文件, 如果存在则删除指定文件
        File oldFileNamesTXT = new File("./OldBalloonUpdateFileName.txt");
        if (oldFileNamesTXT.exists() && oldFileNamesTXT.isFile()) {
            try {
                Reader reader = new InputStreamReader(Files.newInputStream(oldFileNamesTXT.toPath()), StandardCharsets.UTF_8);
                StringBuilder sb = new StringBuilder();
                int ch;
                while ((ch = reader.read()) != -1) {
                    sb.append((char) ch);
                }
                reader.close();

                //读取旧文件名 txt 文件后, 将每行分割成多个文件名然后循环搜索
                String[] oldFileNames = sb.toString().split("\n");
                for (String oldFileName : oldFileNames) {
                    File target = new File("./" + oldFileName);
                    if (target.exists() && target.isFile()) {
                        if (target.delete()) {
                            logger.info("Successfully Delete Old JarClient File: " + target.getName());
                        } else {
                            logger.warn("Failed Delete Old JarClient File: " + target.getName());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //如果自定义 JarClient 文件未找到, 则搜索当前文件夹所有文件, 寻找对应的 JarClient
        if (path == null) {
            for (File target : files) {
                if (target.isFile() && target.getName().contains("JarClient") && target.getName().endsWith(".jar")) {
                    path = target.getAbsoluteFile().toURI().toString();
                    break;
                }
            }
        }

        if (path == null) {
            logger.error("Cannot Find JarClient JAR File!");
        } else {
            //开始载入外部 Jar
            URLClassLoader urlClassLoader = null;
            Class<?> BalloonUpdateMain;
            Class<?> SetupSwing;
            try {
                logger.info("Loading JAR: " + path);
                //通过URLClassLoader加载外部jar
                urlClassLoader = new URLClassLoader(new URL[]{new URL(path)});

                //获取外部jar里面的具体类对象
                logger.info("Loading Class: " + "com.github.balloonupdate.BalloonUpdateMain");

                BalloonUpdateMain = urlClassLoader.loadClass("com.github.balloonupdate.BalloonUpdateMain");

                if (Desktop.isDesktopSupported()) {
                    SetupSwing = urlClassLoader.loadClass("com.github.kasuminova.GUI.SetupSwing");
                    Method init = SetupSwing.getMethod("init");
                    init.invoke(null);
                }

                Method main = BalloonUpdateMain.getMethod("main", String[].class);

                logger.info("Method Load Successfully, invoke Method main()...");

                main.invoke(null, (Object) new String[0]);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //卸载关闭外部 JAR
                try {
                    if (urlClassLoader != null) urlClassLoader.close();
                    System.out.println("Finished!");
                } catch (IOException e) {
                    System.out.println("Close JAR Failed：" + e.getMessage());
                }
            }
        }
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}