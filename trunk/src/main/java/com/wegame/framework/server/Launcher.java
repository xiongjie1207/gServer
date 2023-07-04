package com.wegame.framework.server;/** * Copyright (c) 2015-2016, James Xiong 熊杰 (xiongjie.cn@gmail.com). * <p> * Licensed under the Apache License, Version 2.0 (the "License"); * you may not use this file except in compliance with the License. * You may obtain a copy of the License at * <p> * http://www.apache.org/licenses/LICENSE-2.0 * <p> * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. * <p> * Created by xiongjie on 2016/12/22. */import com.wegame.framework.component.IComponent;import com.wegame.framework.config.EventLoopConfig;import com.wegame.framework.config.ServerConfig;import com.wegame.framework.core.*;import com.wegame.util.AppStatus;import com.wegame.util.HostUtils;import lombok.Getter;import lombok.Setter;import lombok.extern.slf4j.Slf4j;import org.springframework.beans.BeansException;import org.springframework.boot.builder.SpringApplicationBuilder;import org.springframework.boot.context.properties.ConfigurationProperties;import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;import org.springframework.context.ApplicationContext;import org.springframework.context.ApplicationContextAware;import org.springframework.context.ApplicationEventPublisher;import org.springframework.context.ApplicationEventPublisherAware;import org.springframework.context.annotation.Bean;import org.springframework.util.StopWatch;import javax.swing.*;import java.util.ArrayList;import java.util.List;import static com.wegame.util.AppStatus.Running;import static com.wegame.util.AppStatus.Stoped;/******************************* * 起初，神创造天地。[创世记 1:1] ****************************** * @author xiongjie*/@Slf4jpublic abstract class Launcher extends SpringBootServletInitializer    implements ApplicationContextAware, ApplicationEventPublisherAware {    private final List<IComponent> plugins = new ArrayList<>();    @Getter    @Setter    private String id = null;    private final String ver = "1.1";    public void startServer() {        StopWatch stopWatch = new StopWatch();        stopWatch.start();        AppStatus.Status = AppStatus.Starting;        log.info("Starting server.......");        Runtime.getRuntime().addShutdownHook(new Thread(() -> stopServer()));        beforeStart();        buildPlugin();        if(enableEventLoop()){            EventLoopConfig eventLoopConfig = SpringContext.getBean(EventLoopConfig.class);            GameEventLoop.getInstance()                    .start(eventLoopConfig.getInitialDelay(), eventLoopConfig.getDelay());        }        log.info(ServerInfo.memoryInfo());        AppStatus.Status = Running;        afterStart();        stopWatch.stop();        log.info("服务启动，耗时[{}]秒", stopWatch.getTotalTimeSeconds());    }    protected boolean enableEventLoop(){        return true;    }    @Bean    @ConfigurationProperties(prefix = "game.server.eventloop.config")    public EventLoopConfig createEventLoopConfig() {        return new EventLoopConfig();    }    @Bean    @ConfigurationProperties(prefix = "game.server.config")    public ServerConfig createServerConfig() {        return new ServerConfig();    }    @Bean    public GameComponentScanner createAnnotationScanner() {        return new GameComponentScanner();    }    @Override    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {        return builder.sources(this.getClass());    }    protected void afterStart() {        if (id == null) {            try {                ServerConfig config = SpringContext.getBean(ServerConfig.class);                id = HostUtils.getLocalHostLANAddress().getHostAddress() + ":" +                    config.getPort();            } catch (Exception e) {                log.error("afterStartServer", e);            }        }        merryChristmasTree();    }    protected void beforeStart() {    }    protected void beforeStop() {    }    protected void afterStop() {        log.info("App:" + id + " stopServer..........");    }    private void merryChristmasTree() {        String[] stars = {            "              *                 ",            "      *      ***          *     ",            "          *********             ",            "   *         ***     *          ",            "           *******              ",            "       ***************      *   ",            "           *******    *         ",            " *        **********   *        ",            "      *****************         ",            "   ***********************      ",            "    *       |||||    *          ",            "    *       |||||    *          ",            "    *       |||||               ",            "*****************************   ",            " 2016.12.25 Merry Christmas!    ",            "",            "GServer Version:" + ver,            "*****************************\n",            "App:" + id + " start..........\n"        };        for (String s : stars) {            System.out.println(s);        }        showGirl();    }    private void showGirl() {        SwingUtilities.invokeLater(() -> {            try {                if (!getPicPath().trim().isEmpty()) {                    new WateWave(getPicPath());                }            } catch (Exception e) {                log.error(e.getMessage());            }        });    }    private void buildPlugin() {        for (IComponent component : PluginManager.getInstance().getPlugins()) {            boolean result = component.start();            if (!result) {                log.error("component:" + component.getClass().getSimpleName() + " start faild");            }        }    }    protected String getPicPath() {        return "";    }    @Override    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {        SpringContext.setApplicationContext(applicationContext);    }    @Override    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {        AppEventPublisher.setPublisher(applicationEventPublisher);    }    public void stopServer() {        AppStatus.Status = AppStatus.Shutdowning;        beforeStop();        for (IComponent plugin : plugins) {            plugin.stop();        }        plugins.clear();        GameEventLoop.getInstance().stop();        afterStop();        AppStatus.Status = Stoped;    }}