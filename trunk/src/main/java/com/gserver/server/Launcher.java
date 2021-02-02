package com.gserver.server;/** * Copyright (c) 2015-2016, James Xiong 熊杰 (xiongjie.cn@gmail.com). * <p> * Licensed under the Apache License, Version 2.0 (the "License"); * you may not use this file except in compliance with the License. * You may obtain a copy of the License at * <p> * http://www.apache.org/licenses/LICENSE-2.0 * <p> * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. * <p> * Created by xiongjie on 2016/12/22. */import com.gserver.components.IComponent;import com.gserver.config.ServerConfig;import com.gserver.core.AnnotationScanner;import com.gserver.core.ServerInfo;import com.gserver.core.SpringContext;import com.gserver.utils.HostUtil;import com.gserver.utils.AppStatus;import com.gserver.utils.Loggers;import org.springframework.beans.BeansException;import org.springframework.context.ApplicationContext;import org.springframework.context.ApplicationContextAware;import org.springframework.context.ApplicationListener;import org.springframework.context.annotation.Bean;import org.springframework.context.event.ContextRefreshedEvent;import javax.swing.*;import java.util.ArrayList;import java.util.List;/******************************* * 起初，神创造天地。[创世记 1:1] ****************************** * @author xiongjie*/public abstract class Launcher implements ApplicationListener<ContextRefreshedEvent>, ApplicationContextAware {    private List<IComponent> components = new ArrayList<>();    private String id = null;    private void startServer() {        AppStatus.Status = AppStatus.Starting;        Loggers.ServerStatusLogger.info("Starting server.......");        beforeStart();        buildPlugin();        addShutdownHook();        afterStart();        Loggers.ServerStatusLogger.info(ServerInfo.memoryInfo());        AppStatus.Status = AppStatus.Running;    }    @Override    public void onApplicationEvent(ContextRefreshedEvent event) {        if (AppStatus.Status == AppStatus.Stoped) {            startServer();        }    }    public String getId() {        return id;    }    protected void afterStart() {        if (id == null) {            try {                id = HostUtil.getLocalHostLANAddress().getHostAddress() + ":" + ServerConfig.getInstance().getPort();            } catch (Exception e) {                Loggers.ErrorLogger.error("afterStartServer", e);            }        }        merryChristmasTree();    }    protected void beforeStart() {    }    protected void beforeStop() {    }    protected void afterStop() {        Loggers.ServerStatusLogger.info("App:" + id + " stopServer..........");    }    private void merryChristmasTree() {        String[] stars = {                "              *                 ",                "      *      ***          *     ",                "          *********             ",                "   *         ***     *          ",                "           *******              ",                "       ***************      *   ",                "           *******    *         ",                " *        **********   *        ",                "      *****************         ",                "   ***********************      ",                "    *       |||||    *          ",                "    *       |||||    *          ",                "    *       |||||               ",                "*****************************   ",                " 2016.12.25 Merry Christmas!    ",                "",                "GServer Version:" + ServerConfig.getInstance().getVersion(),                "*****************************\n",                "App:" + id + " start..........\n"        };        for (String s : stars) {            System.out.println(s);        }        showGirl();    }    private void showGirl() {        SwingUtilities.invokeLater(new Runnable() {            @Override            public void run() {                try {                    if (!getPicPath().trim().isEmpty()) {                        new WateWave(getPicPath());                    }                } catch (Exception e) {                    Loggers.ErrorLogger.error(e.getMessage());                }            }        });    }    @Bean    public AnnotationScanner createAnnotationScanner() {        return new AnnotationScanner();    }    private void buildPlugin() {        initComponents(components);        for (IComponent component : components) {            boolean result = component.start();            if (!result) {                Loggers.ServerStatusLogger.error("component:" + component.getClass().getSimpleName() + " start faild");            }        }    }    protected String getPicPath() {        return "";    }    protected abstract void initComponents(List<IComponent> plugins);    @Override    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {        SpringContext.setApplicationContext(applicationContext);    }    private void addShutdownHook() {        // 注册停服监听器，用于执行资源的销毁等停服时的处理工作        Runtime.getRuntime().addShutdownHook(new Thread(() -> {            AppStatus.Status = AppStatus.Shutdowning;            Loggers.ServerStatusLogger.info("Begin to shutdown App ");            beforeStop();            for (IComponent plugin : components) {                plugin.stop();            }            components.clear();            afterStop();            // 注销性能收集            Loggers.ServerStatusLogger.info("App shutdowned");            AppStatus.Status = AppStatus.Stoped;        }));    }}