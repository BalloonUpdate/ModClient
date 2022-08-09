package com.github.kasuminova.Downloader;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MultiThreadedDownloader {
    static {
        SetupSwing.init();
    }

    /**
     * 外部程序调用的方法
     * 调用后直接弹出一个新的窗口显示下载进度
     * @param programPath 下载路径/程序路径，传入绝对路径（记得最后带斜杠），如 "C:/Downloads/"
     * @param url 下载链接，传入 http/https 链接（记得最后带斜杠），如 "https://kasuminova.cn:90/"
     * @param filePaths 传入文件列表，可以传入的格式如下 <p>
     *                  file1.jar
     *                  dir1/file1.jar
     *                  dir1/file2.zip
     *                  dir1/dir2/file3.txt
     *                  dir3/file4.rar
     * </p>
     */
    public static void startDownload(String programPath, String url, List<String> filePaths) {
        MultiThreadedDownloader.programPath = programPath;
        MultiThreadedDownloader.url = url;
        MultiThreadedDownloader.filePaths = filePaths;

        new MultiThreadedDownloader().init();
    }

    public static String programPath = "C:/Users/Kasumi_Nova/IdeaProjects/BalloonUpdateCoreModClient/Downloads/";
    List<File> files = new ArrayList<>();
    long start = System.currentTimeMillis();
    static JFrame frame = new JFrame("下载中");
    static List<String> filePaths = new ArrayList<>();
    static String url = "https://kasuminova.cn:90/";
    static List<String> urls;
    static AtomicLong completedBytes = new AtomicLong(0);
    static JPanel panel = new JPanel(new VFlowLayout());
    static AtomicInteger runningThreads = new AtomicInteger(0);
    static AtomicInteger completedThreads = new AtomicInteger(0);
    JProgressBar statusBarProgress;

    public static void main(String[] args) {
        filePaths.add("witchery (1).jar");
        filePaths.add("witchery (2).jar");
        filePaths.add("witchery (3).jar");
        filePaths.add("witchery (4).jar");
        filePaths.add("witchery (5).jar");
        filePaths.add("witchery (6).jar");
        filePaths.add("witchery (7).jar");
        filePaths.add("witchery (8).jar");
        filePaths.add("files/witchery.jar");
        filePaths.add("123");

        new MultiThreadedDownloader().init();
    }

    /**
     * 主窗口，用于容纳进度条等元素
     */
    private void init() {
        //设置要下载的文件列表
        for (String filePath : filePaths) {
            //添加文件
            files.add(new File(programPath + filePath));
        }

        //文件数量
        int fileCount = filePaths.size();

        //线程数量
        int threadCount = Runtime.getRuntime().availableProcessors() * 2;

        //如果文件数量少于线程数量，则线程数量设置为文件数量
        if (fileCount < threadCount) {
            threadCount = fileCount;
        }

        //向面板添加滚动条
        JScrollPane panelScrollPane = new JScrollPane(panel);
        //调整滚动条每次滚动的数量
        panelScrollPane.getVerticalScrollBar().setUnitIncrement(22);

        //状态栏
        JPanel statusBarLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel statusBarProgressPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        //内容
        JLabel statusBarLabel = new JLabel("速度：");
        statusBarProgress = new JProgressBar(0,fileCount);
        //添加至面板
        statusBarLabelPanel.add(statusBarLabel);
        statusBarProgressPanel.add(statusBarProgress);
        statusBarProgress.setStringPainted(true);
        //组装
        JPanel statusBarPanel = new JPanel(new BorderLayout());
        statusBarPanel.add(statusBarLabelPanel, BorderLayout.WEST);
        statusBarPanel.add(statusBarProgressPanel, BorderLayout.EAST);

        Downloader downloader = new Downloader();
        downloader.setFilePaths(filePaths);
        downloader.setMaxThreadCount(threadCount);

        new Thread(downloader).start();

        //轮询线程
        QueryThread queryThread = new QueryThread();
        queryThread.setSpeedLabel(statusBarLabel);
        new Thread(queryThread).start();

        //组装窗口
        Box box = Box.createVerticalBox();
        box.add(panelScrollPane);
        box.add(statusBarPanel);
        frame.add(box);
        frame.setSize(450, 550);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        System.out.println("Program Started with " + (System.currentTimeMillis() - start) + "ms");
    }

    private class QueryThread implements Runnable {
        JLabel speedLabel;

        public void setSpeedLabel(JLabel speedLabel) {
            this.speedLabel = speedLabel;
        }

        @Override
        public void run() {
            long bytes = 0;
            while (statusBarProgress.getValue() < statusBarProgress.getMaximum()) {
                statusBarProgress.setValue(completedThreads.get());

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                long speed = completedBytes.get() - bytes;

                if (speed >= 1048576) {
                    speedLabel.setText("速度: " + String.format("%.2f", (double) (speed) / 1048576 * 2) + " MB/s");
                } else if (speed >= 1024) {
                    speedLabel.setText("速度: " + String.format("%.2f", (double) (speed) / 1024 * 2) + " KB/s");
                } else {
                    speedLabel.setText("速度: " + String.format("%.2f", (double) (speed) * 2) + " Byte/s");
                }

                bytes = completedBytes.get();
            }
        }
    }

    /**
     * 用于创建下载线程的线程
     */
    private class Downloader implements Runnable {
        List<String> filePaths;
        int maxThreadCount;

        public void setMaxThreadCount(int maxThreadCount) {
            this.maxThreadCount = maxThreadCount;
        }
        public void setFilePaths(List<String> files) {
            this.filePaths = files;
        }

        @Override
        public void run() {
            for (int i = 0; i < filePaths.size(); i++) {
                //单个线程的面板
                JPanel threadPanel = new JPanel(new VFlowLayout());
                //单个线程的 Box
                Box box = new Box(BoxLayout.LINE_AXIS);
                //线程名
                threadPanel.setBorder(BorderFactory.createTitledBorder(filePaths.get(i)));
                //状态
                box.add(new JLabel("Progress: "), BorderLayout.WEST);

                //进度条
                JProgressBar progressBar = new JProgressBar();
                progressBar.setStringPainted(true);
                progressBar.setString("正在连接...");

                //向 Box 添加进度条
                box.add(progressBar, BorderLayout.EAST);
                //向主面板添加 Box
                threadPanel.add(box);
                //向主面板添加单个线程的面板
                panel.add(threadPanel);

                /*
                   配置并启动线程，如果创建成功，则 runningThreads + 1
                   线程控制逻辑：
                   将线程锁定至指定数量，如果运行的线程大于指定数量，则等待 0.25 后再次检测，直到所有线程都创建完毕。
                 */
                while (true) {
                    if (runningThreads.get() < maxThreadCount) {
                        ProgressBarThread progressBarThread = new ProgressBarThread();
                        progressBarThread.setProgressBar(progressBar);
                        progressBarThread.setFilePath(filePaths.get(i));
                        progressBarThread.setTarget(files.get(i));
                        Thread singleProgressBarThread = new Thread(progressBarThread);
                        singleProgressBarThread.setName(files.get(i).getName());
                        singleProgressBarThread.start();
                        runningThreads.getAndIncrement();
                        break;
                    } else {
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * 实际下载文件的线程
     * 配置：
     * <p>
     * 变量：file 要下载的文件
     * 变量：path 保存路径
     * 变量：progressBar 线程对应的进度条
     * </p>
     */
    private static class ProgressBarThread implements Runnable {
        //路径，用于下载文件的方法
        String filePath;
        //文件，用于保存文件时的参数
        File target;

        public void setTarget(File target) {
            this.target = target;
        }

        JProgressBar progressBar;

        public void setProgressBar(JProgressBar progressBar) {
            this.progressBar = progressBar;
        }
        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
        @Override
        public void run() {
            DownloadUtil.get().download(url + filePath, target.getParent(), target.getName(), new DownloadUtil.OnDownloadListener() {
                String size;
                long singleCompletedBytes = 0;
                @Override
                public void onDownloadSuccess(File file) {
                    //下载完成进行相关逻辑操作
                    panel.remove(progressBar.getParent().getParent());
                    panel.updateUI();

                    System.out.println(file + " download has complete!");
                    runningThreads.getAndDecrement();
                    completedThreads.getAndIncrement();
                }

                @Override
                public void onDownloading(int progress, long bytes) {
                    progressBar.setValue(progress);
                    completedBytes.addAndGet(bytes);

                    singleCompletedBytes += bytes;
                    if (singleCompletedBytes < 1024) {
                        progressBar.setString(size + " Byte / " + singleCompletedBytes + " Byte");
                    } else if (singleCompletedBytes < 1024 * 1024) {
                        progressBar.setString(String.format("%.2f", (double) (singleCompletedBytes) / (1024)) + " KB / " + size);
                    } else if (singleCompletedBytes < 1024 * 1024 * 1024) {
                        progressBar.setString(String.format("%.2f", (double) (singleCompletedBytes) / (1024 * 1024)) + " MB / " + size);
                    } else {
                        progressBar.setString(String.format("%.2f", (double) (singleCompletedBytes) / (1024 * 1024 * 1024)) + " GB / " + size);
                    }
                }

                @Override
                public void onDownloadFailed(Exception e) {
                    progressBar.setString("下载失败：" + e);
                    //下载异常进行相关提示操作
                    e.printStackTrace();
                    runningThreads.getAndDecrement();
                    completedThreads.getAndIncrement();
                }

                @Override
                public void onDownloadFailed(String e) {
                    progressBar.setString("下载失败：" + e);
                    runningThreads.getAndDecrement();
                    completedThreads.getAndIncrement();
                }

                @Override
                public void onDownloadStarted(long fileSize) {
                    if (fileSize < 1024) {
                        size = fileSize + " Byte";
                        progressBar.setString("0 Byte / " + size);
                    } else if (fileSize < 1024 * 1024) {
                        size = String.format("%.2f", (double) (fileSize) / (1024)) + " KB";
                        progressBar.setString("0.00 KB / " + size);
                    } else if (fileSize < 1024 * 1024 * 1024) {
                        size = String.format("%.2f", (double) (fileSize) / (1024 * 1024)) + " MB";
                        progressBar.setString("0.00 MB / " + size);
                    } else {
                        size = String.format("%.2f", (double) (fileSize) / (1024 * 1024 * 1024)) + " GB";
                        progressBar.setString("0.00 GB / " + size);
                    }
                }
            });
        }
    }
}
