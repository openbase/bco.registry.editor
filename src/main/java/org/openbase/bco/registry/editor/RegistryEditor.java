package org.openbase.bco.registry.editor;

/*
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2018 openbase.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessage;
import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.openbase.bco.registry.agent.lib.jp.JPAgentRegistryScope;
import org.openbase.bco.registry.agent.remote.AgentRegistryRemote;
import org.openbase.bco.registry.app.lib.jp.JPAppRegistryScope;
import org.openbase.bco.registry.app.remote.AppRegistryRemote;
import org.openbase.bco.registry.device.lib.jp.JPDeviceRegistryScope;
import org.openbase.bco.registry.device.remote.DeviceRegistryRemote;
import org.openbase.bco.registry.editor.struct.GenericGroupContainer;
import org.openbase.bco.registry.editor.struct.GenericListContainer;
import org.openbase.bco.registry.editor.util.RemotePool;
import org.openbase.bco.registry.editor.util.SendableType;
import org.openbase.bco.registry.editor.visual.GlobalTextArea;
import org.openbase.bco.registry.editor.visual.RegistryTreeTableView;
import org.openbase.bco.registry.editor.visual.TabPaneWithClearing;
import org.openbase.bco.registry.editor.visual.provider.*;
import org.openbase.bco.registry.location.lib.jp.JPLocationRegistryScope;
import org.openbase.bco.registry.location.remote.LocationRegistryRemote;
import org.openbase.bco.registry.scene.lib.jp.JPSceneRegistryScope;
import org.openbase.bco.registry.scene.remote.SceneRegistryRemote;
import org.openbase.bco.registry.unit.lib.jp.JPUnitRegistryScope;
import org.openbase.bco.registry.unit.remote.UnitRegistryRemote;
import org.openbase.bco.registry.user.activity.remote.UserActivityRegistryRemote;
import org.openbase.bco.registry.user.lib.jp.JPUserRegistryScope;
import org.openbase.bco.registry.user.remote.UserRegistryRemote;
import org.openbase.jps.core.JPService;
import org.openbase.jps.preset.JPReadOnly;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.jul.extension.rsb.com.RSBRemoteService;
import org.openbase.jul.extension.rsb.com.jp.JPRSBHost;
import org.openbase.jul.extension.rsb.com.jp.JPRSBPort;
import org.openbase.jul.extension.rsb.com.jp.JPRSBTransport;
import org.openbase.jul.pattern.Observable;
import org.openbase.jul.pattern.Observer;
import org.openbase.jul.pattern.Remote.ConnectionState;
import org.openbase.jul.schedule.GlobalCachedExecutorService;
import org.openbase.jul.visual.swing.image.ImageLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.registry.AgentRegistryDataType.AgentRegistryData;
import rst.domotic.registry.AppRegistryDataType.AppRegistryData;
import rst.domotic.registry.DeviceRegistryDataType.DeviceRegistryData;
import rst.domotic.registry.LocationRegistryDataType.LocationRegistryData;
import rst.domotic.registry.SceneRegistryDataType.SceneRegistryData;
import rst.domotic.registry.UnitRegistryDataType.UnitRegistryData;
import rst.domotic.registry.UserActivityRegistryDataType.UserActivityRegistryData;
import rst.domotic.registry.UserRegistryDataType.UserRegistryData;
import rst.domotic.unit.device.DeviceClassType.DeviceClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class RegistryEditor extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistryEditor.class);

    public static final String APP_NAME = "RegistryView";
    public static final int RESOLUTION_WIDTH = 1024;
    private final GlobalTextArea globalTextArea = GlobalTextArea.getInstance();

    private RemotePool remotePool;
    private MenuBar menuBar;
    private Menu fileMenu;
    private MenuItem resyncMenuItem;
    private TabPaneWithClearing registryTabPane, deviceRegistryTabPane, locationRegistryTabPane, userRegistryTabPane, agentRegistryTabPane, appRegistryTabPane, sceneRegistryTabPane, unitRegistryTabPane, userActivityRegistryTabPane;
    private Tab deviceRegistryTab, locationRegistryTab, sceneRegistryTab, agentRegistryTab, appRegistryTab, userRegistryTab, unitRegistryTab, userActivityRegistryTab;
    private Tab deviceClassTab, deviceConfigTab;
    private Tab locationConfigTab, connectionConfigTab;
    private Tab userConfigTab, userGroupConfigTab;
    private Tab agentClassTab, agentConfigTab;
    private Tab appClassTab, appConfigTab;
    private Tab sceneConfigTab;
    private Tab unitConfigTab, unitTemplateTab, unitGroupTab, serviceTemplateTab;
    private Tab userActivityClassTab, userActivityConfigTab;
    private ProgressIndicator deviceRegistryProgressIndicator, locationRegistryprogressIndicator, appRegistryprogressIndicator, agentRegistryProgressIndicator, sceneRegistryprogressIndicator, userRegistryProgessInidicator, unitRegistryProgressIndicator, userActivityRegistryProgressIndicator;
    private RegistryTreeTableView deviceClassTreeTableView, deviceConfigTreeTableView;
    private RegistryTreeTableView locationConfigTreeTableView, connectionConfigTreeTableView;
    private RegistryTreeTableView sceneConfigTreeTableView;
    private RegistryTreeTableView agentConfigTreeTableView, agentClassTreeTableView;
    private RegistryTreeTableView appConfigTreeTableView, appClassTreeTableView;
    private RegistryTreeTableView userConfigTreeTableView, authorizationGroupConfigTreeTableView;
    private RegistryTreeTableView dalUnitConfigTreeTableView, unitTemplateTreeTableView, unitGroupConfigTreeTableView, serviceTemplateTreeTableView;
    private RegistryTreeTableView userActivityClassTreeTableView, userActivityConfigTreeTableView;
    private Scene scene;
    private Map<String, Boolean> intialized;

    @Override
    public void init() throws Exception {
        super.init();
        remotePool = RemotePool.getInstance();
        intialized = new HashMap<>();
        intialized.put(DeviceRegistryData.class.getSimpleName(), Boolean.FALSE);
        intialized.put(LocationRegistryData.class.getSimpleName(), Boolean.FALSE);
        intialized.put(AgentRegistryData.class.getSimpleName(), Boolean.FALSE);
        intialized.put(AppRegistryData.class.getSimpleName(), Boolean.FALSE);
        intialized.put(SceneRegistryData.class.getSimpleName(), Boolean.FALSE);
        intialized.put(UserRegistryData.class.getSimpleName(), Boolean.FALSE);
        intialized.put(UnitRegistryData.class.getSimpleName(), Boolean.FALSE);
        intialized.put(UserActivityRegistryData.class.getSimpleName(), Boolean.FALSE);

        registryTabPane = new TabPaneWithClearing();
        registryTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        deviceRegistryTab = new Tab("DeviceRegistry");
        locationRegistryTab = new Tab("LocationRegistry");
        sceneRegistryTab = new Tab("SceneRegistry");
        agentRegistryTab = new Tab("AgentRegistry");
        appRegistryTab = new Tab("AppRegistry");
        userRegistryTab = new Tab("UserRegistry");
        unitRegistryTab = new Tab("UnitRegistry");
        userActivityRegistryTab = new Tab("UserActivityRegistry");

        registryTabPane.addTab(unitRegistryTab, SendableType.UNIT_CONFIG);
        registryTabPane.addTab(deviceRegistryTab, SendableType.DEVICE_CONFIG);
        registryTabPane.addTab(locationRegistryTab, SendableType.LOCATION_CONFIG);
        registryTabPane.addTab(sceneRegistryTab, SendableType.SCENE_CONFIG);
        registryTabPane.addTab(appRegistryTab, SendableType.APP_CONFIG);
        registryTabPane.addTab(agentRegistryTab, SendableType.AGENT_CONFIG);
        registryTabPane.addTab(userRegistryTab, SendableType.USER_CONFIG);
        registryTabPane.addTab(userActivityRegistryTab, SendableType.USER_ACTIVITY_CONFIG);

        deviceRegistryProgressIndicator = new ProgressIndicator();
        locationRegistryprogressIndicator = new ProgressIndicator();
        appRegistryprogressIndicator = new ProgressIndicator();
        agentRegistryProgressIndicator = new ProgressIndicator();
        sceneRegistryprogressIndicator = new ProgressIndicator();
        userRegistryProgessInidicator = new ProgressIndicator();
        unitRegistryProgressIndicator = new ProgressIndicator();
        userActivityRegistryProgressIndicator = new ProgressIndicator();

        deviceClassTreeTableView = new RegistryTreeTableView(SendableType.DEVICE_CLASS, this);
        deviceConfigTreeTableView = new RegistryTreeTableView(SendableType.DEVICE_CONFIG, this);
        locationConfigTreeTableView = new RegistryTreeTableView(SendableType.LOCATION_CONFIG, this);
        connectionConfigTreeTableView = new RegistryTreeTableView(SendableType.CONNECTION_CONFIG, this);
        sceneConfigTreeTableView = new RegistryTreeTableView(SendableType.SCENE_CONFIG, this);
        agentConfigTreeTableView = new RegistryTreeTableView(SendableType.AGENT_CONFIG, this);
        agentClassTreeTableView = new RegistryTreeTableView(SendableType.AGENT_CLASS, this);
        appConfigTreeTableView = new RegistryTreeTableView(SendableType.APP_CONFIG, this);
        appClassTreeTableView = new RegistryTreeTableView(SendableType.APP_CLASS, this);
        unitTemplateTreeTableView = new RegistryTreeTableView(SendableType.UNIT_TEMPLATE, this);
        serviceTemplateTreeTableView = new RegistryTreeTableView(SendableType.SERVICE_TEMPLATE, this);
        userConfigTreeTableView = new RegistryTreeTableView(SendableType.USER_CONFIG, this);
        authorizationGroupConfigTreeTableView = new RegistryTreeTableView(SendableType.AUTHORIZATION_GROUP_CONFIG, this);
        unitGroupConfigTreeTableView = new RegistryTreeTableView(SendableType.UNIT_GROUP_CONFIG, this);
        dalUnitConfigTreeTableView = new RegistryTreeTableView(SendableType.UNIT_CONFIG, this);
        userActivityClassTreeTableView = new RegistryTreeTableView(SendableType.USER_ACTIVITY_CLASS, this);
        userActivityConfigTreeTableView = new RegistryTreeTableView(SendableType.USER_ACTIVITY_CONFIG, this);

        unitRegistryTabPane = new TabPaneWithClearing();
        unitRegistryTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        unitConfigTab = new Tab("UnitConfig");
        unitTemplateTab = new Tab("UnitTemplate");
        serviceTemplateTab = new Tab("ServiceTemplate");
        unitGroupTab = new Tab("UnitGroup");
        unitConfigTab.setContent(dalUnitConfigTreeTableView.getVBox());
        unitTemplateTab.setContent(unitTemplateTreeTableView.getVBox());
        serviceTemplateTab.setContent(serviceTemplateTreeTableView.getVBox());
        unitGroupTab.setContent(unitGroupConfigTreeTableView.getVBox());
        unitRegistryTabPane.addTab(unitConfigTab, SendableType.UNIT_CONFIG);
        unitRegistryTabPane.addTab(unitTemplateTab, SendableType.UNIT_TEMPLATE);
        unitRegistryTabPane.addTab(serviceTemplateTab, SendableType.SERVICE_TEMPLATE);
        unitRegistryTabPane.addTab(unitGroupTab, SendableType.UNIT_GROUP_CONFIG);
//        unitRegistryTabPane.getTabs().addAll(unitConfigTab, unitTemplateTab, unitGroupTab);

        deviceRegistryTabPane = new TabPaneWithClearing();
        deviceRegistryTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        deviceClassTab = new Tab("DeviceClass");
        deviceConfigTab = new Tab("DeviceConfig");
        deviceClassTab.setContent(deviceClassTreeTableView.getVBox());
        deviceConfigTab.setContent(deviceConfigTreeTableView.getVBox());
        deviceRegistryTabPane.getTabs().addAll(deviceConfigTab, deviceClassTab);

        locationRegistryTabPane = new TabPaneWithClearing();
        locationRegistryTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        locationConfigTab = new Tab("LocationConfig");
        locationConfigTab.setContent(locationConfigTreeTableView.getVBox());
        connectionConfigTab = new Tab("ConnectionConfig");
        connectionConfigTab.setContent(connectionConfigTreeTableView.getVBox());
        locationRegistryTabPane.getTabs().addAll(locationConfigTab, connectionConfigTab);

        userRegistryTabPane = new TabPaneWithClearing();
        userRegistryTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        userConfigTab = new Tab("UserConfig");
        userConfigTab.setContent(userConfigTreeTableView.getVBox());
        userGroupConfigTab = new Tab("AuthorizationGroupConfig");
        userGroupConfigTab.setContent(authorizationGroupConfigTreeTableView.getVBox());
        userRegistryTabPane.getTabs().addAll(userConfigTab, userGroupConfigTab);

        agentRegistryTabPane = new TabPaneWithClearing();
        agentRegistryTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        agentClassTab = new Tab("AgentClass");
        agentClassTab.setContent(agentClassTreeTableView.getVBox());
        agentConfigTab = new Tab("AgentConfig");
        agentConfigTab.setContent(agentConfigTreeTableView.getVBox());
        agentRegistryTabPane.getTabs().addAll(agentConfigTab, agentClassTab);

        appRegistryTabPane = new TabPaneWithClearing();
        appRegistryTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        appClassTab = new Tab("AppClass");
        appClassTab.setContent(appClassTreeTableView.getVBox());
        appConfigTab = new Tab("AppConfig");
        appConfigTab.setContent(appConfigTreeTableView.getVBox());
        appRegistryTabPane.getTabs().addAll(appConfigTab, appClassTab);

        sceneRegistryTabPane = new TabPaneWithClearing();
        sceneRegistryTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        sceneConfigTab = new Tab("SceneConfig");
        sceneConfigTab.setContent(sceneConfigTreeTableView.getVBox());
        sceneRegistryTabPane.getTabs().addAll(sceneConfigTab);

        userActivityRegistryTabPane = new TabPaneWithClearing();
        userActivityRegistryTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        userActivityClassTab = new Tab("UserActivityClass");
        userActivityClassTab.setContent(userActivityClassTreeTableView.getVBox());
        userActivityConfigTab = new Tab("UserActivityConfig");
        userActivityConfigTab.setContent(userActivityConfigTreeTableView.getVBox());
        userActivityRegistryTabPane.getTabs().addAll(userActivityConfigTab, userActivityClassTab);

        resyncMenuItem = new MenuItem("Resync");
        resyncMenuItem.setOnAction((ActionEvent event) -> {
            LOGGER.info("resync triggered...");
            remotePool.getRemotes().stream().forEach((remote) -> {
                try {
                    LOGGER.info("request data for " + remote);
                    remote.requestData();
                } catch (CouldNotPerformException ex) {
                    printException(ex, LOGGER, LogLevel.ERROR);
                }
            });
        });
        fileMenu = new Menu("Registry");

        fileMenu.getItems().addAll(resyncMenuItem);
        menuBar = new MenuBar();
        menuBar.getMenus().add(fileMenu);

        scene = buildScene();
        registerObserver();
        LOGGER.debug("Init finished");
    }

    private SplitPane splitPane;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("BCO Registry Editor");
        try {
            LOGGER.debug("Try to load icon...");
            primaryStage.getIcons().add(SwingFXUtils.toFXImage(ImageLoader.getInstance().loadImage("registry-editor.png"), null));
            LOGGER.debug("App icon loaded...");
        } catch (Exception ex) {
            printException(ex, LOGGER, LogLevel.WARN);
        }

        primaryStage.setOnCloseRequest((WindowEvent event) -> {
            try {
                Platform.exit();
            } catch (Exception ex) {
                printException(ex, LOGGER, LogLevel.ERROR);
                System.exit(1);
            }
        });

        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public Scene buildScene() {
        LOGGER.info("Starting");
        splitPane = new SplitPane(/*registryTabPane, globalTextArea*/);
        splitPane.getItems().addAll(registryTabPane, globalTextArea);
        globalTextArea.addParent(splitPane);
        splitPane.setOrientation(Orientation.VERTICAL);

        VBox vBox = new VBox(menuBar, splitPane);
        Scene buildScene = new Scene(vBox, RESOLUTION_WIDTH, 576);
        buildScene.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            splitPane.setPrefHeight(newValue.doubleValue());
            splitPane.setDividerPositions(1);
        });

        deviceClassTreeTableView.addWidthProperty(buildScene.widthProperty());
        deviceConfigTreeTableView.addWidthProperty(buildScene.widthProperty());
        locationConfigTreeTableView.addWidthProperty(buildScene.widthProperty());
        connectionConfigTreeTableView.addWidthProperty(buildScene.widthProperty());
        sceneConfigTreeTableView.addWidthProperty(buildScene.widthProperty());
        agentConfigTreeTableView.addWidthProperty(buildScene.widthProperty());
        agentClassTreeTableView.addWidthProperty(buildScene.widthProperty());
        appConfigTreeTableView.addWidthProperty(buildScene.widthProperty());
        appClassTreeTableView.addWidthProperty(buildScene.widthProperty());
        unitTemplateTreeTableView.addWidthProperty(buildScene.widthProperty());
        serviceTemplateTreeTableView.addWidthProperty(buildScene.widthProperty());
        userConfigTreeTableView.addWidthProperty(buildScene.widthProperty());
        authorizationGroupConfigTreeTableView.addWidthProperty(buildScene.widthProperty());
        unitGroupConfigTreeTableView.addWidthProperty(buildScene.widthProperty());
        dalUnitConfigTreeTableView.addWidthProperty(buildScene.widthProperty());
        userActivityClassTreeTableView.addWidthProperty(buildScene.widthProperty());
        userActivityConfigTreeTableView.addWidthProperty(buildScene.widthProperty());

        deviceClassTreeTableView.addHeightProperty(buildScene.heightProperty());
        deviceConfigTreeTableView.addHeightProperty(buildScene.heightProperty());
        locationConfigTreeTableView.addHeightProperty(buildScene.heightProperty());
        connectionConfigTreeTableView.addHeightProperty(buildScene.heightProperty());
        sceneConfigTreeTableView.addHeightProperty(buildScene.heightProperty());
        agentConfigTreeTableView.addHeightProperty(buildScene.heightProperty());
        agentClassTreeTableView.addHeightProperty(buildScene.heightProperty());
        appConfigTreeTableView.addHeightProperty(buildScene.heightProperty());
        appClassTreeTableView.addHeightProperty(buildScene.heightProperty());
        unitTemplateTreeTableView.addHeightProperty(buildScene.heightProperty());
        serviceTemplateTreeTableView.addHeightProperty(buildScene.heightProperty());
        userConfigTreeTableView.addHeightProperty(buildScene.heightProperty());
        authorizationGroupConfigTreeTableView.addHeightProperty(buildScene.heightProperty());
        unitGroupConfigTreeTableView.addHeightProperty(buildScene.heightProperty());
        dalUnitConfigTreeTableView.addHeightProperty(buildScene.heightProperty());
        userActivityClassTreeTableView.addHeightProperty(buildScene.heightProperty());
        userActivityConfigTreeTableView.addHeightProperty(buildScene.heightProperty());

        return buildScene;
    }

    public void registerObserver() {
        GlobalCachedExecutorService.submit(() -> {
            LOGGER.debug("Register observer");
            final Map<RSBRemoteService, Future<Void>> registrationFutureMap = new HashMap<>();

            for (final RSBRemoteService remote : remotePool.getRemotes()) {
                registrationFutureMap.put(remote, GlobalCachedExecutorService.submit(() -> {
                    try {
                        LOGGER.info("Register observer for [" + remote + "]");
                        remote.addDataObserver((org.openbase.jul.pattern.Observable source, Object data) -> {
                            assert remote.isDataAvailable();
                            LOGGER.info("Received update for [" + remote + "]");
                            if (data == null) {
                                LOGGER.warn("Data for remote [" + remote + "] is null!");
                            }
                            updateTab(remote);
                        });
                        updateTab(remote);
                        if (remote.equals(remotePool.getDeviceRemote()) || remote.equals(remotePool.getUnitRemote())) {
//                            logger.info("Device tree cannot be created without activated location remote. Waiting for its activation...");
                            while (!registrationFutureMap.containsKey(remotePool.getLocationRemote())) {
                                Thread.sleep(10);
                            }
                            registrationFutureMap.get(remotePool.getLocationRemote()).get();
                        }
                    } catch (Exception ex) {
                        printException(ex, LOGGER, LogLevel.ERROR);
                    }
                    return null;
                }));

                remote.addConnectionStateObserver(new Observer<ConnectionState>() {

                    @Override
                    public void update(final Observable<ConnectionState> source, final ConnectionState connectionState) throws Exception {
                        LOGGER.debug("Remote connection state has changed to: " + connectionState);
                        Platform.runLater(() -> {
                            boolean disconnected = false;
                            if (connectionState != ConnectionState.CONNECTED) {
                                disconnected = true;
                            }

                            for (RegistryTreeTableView treeTable : getTreeTablesByRemote(remote)) {
                                treeTable.setDisconnected(disconnected);
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void stop() throws Exception {
        super.stop();

        // Call system exit to trigger all shutdown daemons.
        System.exit(0);
    }

    private void updateTab(final RSBRemoteService remote) {
        LOGGER.info("Update tab for remote [" + remote + "]");
        GlobalCachedExecutorService.submit(() -> {
            synchronized (remote) {
                Tab tab = getRegistryTabByRemote(remote);
                if (!remote.isConnected() || !remote.isActive() || !remote.isDataAvailable()) {
                    LOGGER.info("Set progress indicator for remote[" + remote + "] because [" + !remote.isConnected() + ", " + !remote.isActive() + ", " + !remote.isDataAvailable() + "]");
                    final Node node = getProgressindicatorByRemote(remote);
                    Platform.runLater(() -> {
                        tab.setContent(node);
                    });
                    return;
                }
                try {
                    GeneratedMessage data = remote.getData();
                    if (!intialized.get(data.getClass().getSimpleName())) {
                        LOGGER.debug(data.getClass().getSimpleName() + " is not yet initialized");
                        final Node node = fillTreeTableView(data);
                        Platform.runLater(() -> {
                            tab.setContent(node);
                        });
                    } else {
                        LOGGER.debug("Updating " + data.getClass().getSimpleName());
                        final Node node = updateTreeTableView(data);
                        Platform.runLater(() -> {
                            tab.setContent(node);
                        });
                    }
                } catch (CouldNotPerformException | InterruptedException ex) {
                    ExceptionPrinter.printHistory(new NotAvailableException("Registry", ex), LOGGER);
                    tab.setContent(new Label("Error: " + ex.getMessage()));
                }
            }
        });
    }

    private javafx.scene.Node fillTreeTableView(GeneratedMessage msg) throws InstantiationException, CouldNotPerformException, InterruptedException {
        LOGGER.debug("FillTreeTableView for [" + msg.getClass().getSimpleName() + "]");
        if (msg instanceof DeviceRegistryData) {
            DeviceRegistryData data = (DeviceRegistryData) msg;
            TreeItemDescriptorProvider company = new FieldDescriptorGroup(DeviceClass.newBuilder(), DeviceClass.COMPANY_FIELD_NUMBER);
            Descriptors.FieldDescriptor deviceClassField = data.toBuilder().getDescriptorForType().findFieldByNumber(DeviceRegistryData.DEVICE_CLASS_FIELD_NUMBER);
            deviceClassTreeTableView.setRoot(new GenericGroupContainer<>(deviceClassField.getName(), deviceClassField, data.toBuilder(), data.toBuilder().getDeviceClassBuilderList(), company));
            deviceClassTreeTableView.setReadOnlyMode(remotePool.isReadOnly(SendableType.DEVICE_CLASS.getDefaultInstanceForType()));
            deviceClassTreeTableView.getListDiff().diff(data.getDeviceClassList());

            TreeItemDescriptorProvider deviceClassId = new DeviceClassItemDescriptorProvider();
            TreeItemDescriptorProvider locationId = new LocationItemDescriptorProvider();
            Descriptors.FieldDescriptor deviceConfigfield = data.toBuilder().getDescriptorForType().findFieldByNumber(DeviceRegistryData.DEVICE_UNIT_CONFIG_FIELD_NUMBER);
            deviceConfigTreeTableView.setRoot(new GenericGroupContainer<>(deviceConfigfield.getName(), deviceConfigfield, data.toBuilder(), data.toBuilder().getDeviceUnitConfigBuilderList(), deviceClassId, locationId));
            deviceConfigTreeTableView.setReadOnlyMode(remotePool.isReadOnly(SendableType.DEVICE_CONFIG.getDefaultInstanceForType()));
            deviceConfigTreeTableView.getListDiff().diff(data.getDeviceUnitConfigList());

            intialized.put(msg.getClass().getSimpleName(), Boolean.TRUE);
            return deviceRegistryTabPane;
        } else if (msg instanceof LocationRegistryData) {
            LocationRegistryData data = (LocationRegistryData) msg;
            locationConfigTreeTableView.setRoot(new GenericListContainer<>(LocationRegistryData.LOCATION_UNIT_CONFIG_FIELD_NUMBER, data.toBuilder()));
            locationConfigTreeTableView.setReadOnlyMode(remotePool.isReadOnly(SendableType.LOCATION_CONFIG.getDefaultInstanceForType()));
            locationConfigTreeTableView.getListDiff().diff(data.getLocationUnitConfigList());

            connectionConfigTreeTableView.setRoot(new GenericListContainer<>(LocationRegistryData.CONNECTION_UNIT_CONFIG_FIELD_NUMBER, data.toBuilder()));
            connectionConfigTreeTableView.setReadOnlyMode(remotePool.isReadOnly(SendableType.CONNECTION_CONFIG.getDefaultInstanceForType()));
            connectionConfigTreeTableView.getListDiff().diff(data.getConnectionUnitConfigList());

            intialized.put(msg.getClass().getSimpleName(), Boolean.TRUE);
            return locationRegistryTabPane;
        } else if (msg instanceof SceneRegistryData) {
            SceneRegistryData data = (SceneRegistryData) msg;
            sceneConfigTreeTableView.setRoot(new GenericListContainer(SceneRegistryData.SCENE_UNIT_CONFIG_FIELD_NUMBER, data.toBuilder()));
            sceneConfigTreeTableView.setReadOnlyMode(remotePool.isReadOnly(SendableType.SCENE_CONFIG.getDefaultInstanceForType()));
            sceneConfigTreeTableView.getListDiff().diff(data.getSceneUnitConfigList());
            intialized.put(msg.getClass().getSimpleName(), Boolean.TRUE);
            return sceneRegistryTabPane;
        } else if (msg instanceof AppRegistryData) {
            AppRegistryData data = (AppRegistryData) msg;
            appConfigTreeTableView.setRoot(new GenericListContainer(AppRegistryData.APP_UNIT_CONFIG_FIELD_NUMBER, data.toBuilder()));
            appConfigTreeTableView.setReadOnlyMode(remotePool.isReadOnly(SendableType.APP_CONFIG.getDefaultInstanceForType()));
            appConfigTreeTableView.getListDiff().diff(data.getAppUnitConfigList());

            appClassTreeTableView.setRoot(new GenericListContainer(AppRegistryData.APP_CLASS_FIELD_NUMBER, data.toBuilder()));
            appClassTreeTableView.setReadOnlyMode(remotePool.isReadOnly(SendableType.APP_CLASS.getDefaultInstanceForType()));
            appClassTreeTableView.getListDiff().diff(data.getAppClassList());

            intialized.put(msg.getClass().getSimpleName(), Boolean.TRUE);
            return appRegistryTabPane;
        } else if (msg instanceof AgentRegistryData) {
            AgentRegistryData data = (AgentRegistryData) msg;
            TreeItemDescriptorProvider agentClassLabel = new AgentClassItemDescriptorProvider();
            Descriptors.FieldDescriptor agentConfigfield = data.toBuilder().getDescriptorForType().findFieldByNumber(AgentRegistryData.AGENT_UNIT_CONFIG_FIELD_NUMBER);
            agentConfigTreeTableView.setRoot(new GenericGroupContainer<>(agentConfigfield.getName(), agentConfigfield, data.toBuilder(), data.toBuilder().getAgentUnitConfigBuilderList(), agentClassLabel));
            agentConfigTreeTableView.setReadOnlyMode(remotePool.isReadOnly(SendableType.AGENT_CONFIG.getDefaultInstanceForType()));
            agentConfigTreeTableView.getListDiff().diff(data.getAgentUnitConfigList());

            agentClassTreeTableView.setRoot(new GenericListContainer(AgentRegistryData.AGENT_CLASS_FIELD_NUMBER, data.toBuilder()));
            agentClassTreeTableView.setReadOnlyMode(remotePool.isReadOnly(SendableType.AGENT_CLASS.getDefaultInstanceForType()));
            agentClassTreeTableView.getListDiff().diff(data.getAgentClassList());

            intialized.put(msg.getClass().getSimpleName(), Boolean.TRUE);
            return agentRegistryTabPane;
        } else if (msg instanceof UserRegistryData) {
            UserRegistryData data = (UserRegistryData) msg;
            userConfigTreeTableView.setRoot(new GenericListContainer<>(UserRegistryData.USER_UNIT_CONFIG_FIELD_NUMBER, data.toBuilder()));
            userConfigTreeTableView.setReadOnlyMode(remotePool.isReadOnly(SendableType.USER_CONFIG.getDefaultInstanceForType()));
            userConfigTreeTableView.getListDiff().diff(data.getUserUnitConfigList());

            authorizationGroupConfigTreeTableView.setRoot(new GenericListContainer<>(UserRegistryData.AUTHORIZATION_GROUP_UNIT_CONFIG_FIELD_NUMBER, data.toBuilder()));
            authorizationGroupConfigTreeTableView.setReadOnlyMode(remotePool.isReadOnly(SendableType.AUTHORIZATION_GROUP_CONFIG.getDefaultInstanceForType()));
            authorizationGroupConfigTreeTableView.getListDiff().diff(data.getAuthorizationGroupUnitConfigList());

            intialized.put(msg.getClass().getSimpleName(), Boolean.TRUE);
            return userRegistryTabPane;
        } else if (msg instanceof UnitRegistryData) {
            UnitRegistryData data = (UnitRegistryData) msg;

            TreeItemDescriptorProvider locationDescriptor = new LocationItemDescriptorProvider();
            TreeItemDescriptorProvider unitTypeDescriptor = new UnitTypeItemDescriptorProvider();
            Descriptors.FieldDescriptor dalUnitConfigField = data.toBuilder().getDescriptorForType().findFieldByNumber(UnitRegistryData.DAL_UNIT_CONFIG_FIELD_NUMBER);
            dalUnitConfigTreeTableView.setRoot(new GenericGroupContainer<>(dalUnitConfigField.getName(), dalUnitConfigField, data.toBuilder(), data.toBuilder().getDalUnitConfigBuilderList(), locationDescriptor, unitTypeDescriptor));
            dalUnitConfigTreeTableView.setReadOnlyMode(remotePool.isReadOnly(SendableType.UNIT_TEMPLATE.getDefaultInstanceForType()));
            dalUnitConfigTreeTableView.getListDiff().diff(data.getDalUnitConfigList());

            unitTemplateTreeTableView.setRoot(new GenericListContainer<>("unit_template", data.toBuilder()));
            unitTemplateTreeTableView.setReadOnlyMode(remotePool.isReadOnly(SendableType.UNIT_TEMPLATE.getDefaultInstanceForType()));
            unitTemplateTreeTableView.update(data.getUnitTemplateList());

            unitGroupConfigTreeTableView.setRoot(new GenericListContainer<>(UnitRegistryData.UNIT_GROUP_UNIT_CONFIG_FIELD_NUMBER, data.toBuilder()));
            unitGroupConfigTreeTableView.setReadOnlyMode(remotePool.isReadOnly(SendableType.UNIT_GROUP_CONFIG.getDefaultInstanceForType()));
            unitGroupConfigTreeTableView.getListDiff().diff(data.getUnitGroupUnitConfigList());

            serviceTemplateTreeTableView.setRoot(new GenericListContainer<>("service_template", data.toBuilder()));
            serviceTemplateTreeTableView.setReadOnlyMode(remotePool.isReadOnly(SendableType.SERVICE_TEMPLATE.getDefaultInstanceForType()));
            serviceTemplateTreeTableView.update(data.getServiceTemplateList());

            intialized.put(msg.getClass().getSimpleName(), Boolean.TRUE);
            return unitRegistryTabPane;
        } else if (msg instanceof UserActivityRegistryData) {
            UserActivityRegistryData data = (UserActivityRegistryData) msg;

//            TreeItemDescriptorProvider userActivityClassProvider = new UserActivityClassItemDescriptorProvider();
//            Descriptors.FieldDescriptor userActivityConfigField = data.toBuilder().getDescriptorForType().findFieldByNumber(UserActivityRegistryData.USER_ACTIVITY_CONFIG_FIELD_NUMBER);
//            userActivityConfigTreeTableView.setRoot(new GenericGroupContainer<>(userActivityConfigField.getName(), userActivityConfigField, data.toBuilder(), data.toBuilder().getUserActivityConfigList(), userActivityClassProvider));
            userActivityConfigTreeTableView.setRoot(new GenericListContainer<>("user_activity_config", data.toBuilder()));
            userActivityConfigTreeTableView.setReadOnlyMode(remotePool.isReadOnly(SendableType.USER_ACTIVITY_CONFIG.getDefaultInstanceForType()));
            userActivityConfigTreeTableView.getListDiff().diff(data.getUserActivityConfigList());

            userActivityClassTreeTableView.setRoot(new GenericListContainer<>("user_activity_class", data.toBuilder()));
            userActivityClassTreeTableView.setReadOnlyMode(remotePool.isReadOnly(SendableType.USER_ACTIVITY_CLASS.getDefaultInstanceForType()));
            userActivityClassTreeTableView.update(data.getUserActivityClassList());

            intialized.put(msg.getClass().getSimpleName(), Boolean.TRUE);
            return userActivityRegistryTabPane;
        }

        return null;
    }

    private javafx.scene.Node updateTreeTableView(GeneratedMessage msg) throws InstantiationException, CouldNotPerformException, InterruptedException {
        LOGGER.debug("UpdateTreeTableView for [" + msg.getClass().getSimpleName() + "]");
        if (msg instanceof DeviceRegistryData) {
            DeviceRegistryData data = (DeviceRegistryData) msg;
            deviceClassTreeTableView.update(data.getDeviceClassList());
            deviceConfigTreeTableView.update(data.getDeviceUnitConfigList());

            deviceClassTreeTableView.setReadOnlyMode(remotePool.getDeviceRemote().isDeviceClassRegistryReadOnly());
            deviceConfigTreeTableView.setReadOnlyMode(remotePool.getDeviceRemote().isDeviceConfigRegistryReadOnly());

            return deviceRegistryTabPane;
        } else if (msg instanceof LocationRegistryData) {
            LocationRegistryData data = (LocationRegistryData) msg;
            locationConfigTreeTableView.update(data.getLocationUnitConfigList());
            connectionConfigTreeTableView.update(data.getConnectionUnitConfigList());

            locationConfigTreeTableView.setReadOnlyMode(remotePool.getLocationRemote().isLocationConfigRegistryReadOnly());
            connectionConfigTreeTableView.setReadOnlyMode(remotePool.getLocationRemote().isConnectionConfigRegistryReadOnly());

            return locationRegistryTabPane;
        } else if (msg instanceof SceneRegistryData) {
            SceneRegistryData data = (SceneRegistryData) msg;
            sceneConfigTreeTableView.update(data.getSceneUnitConfigList());

            sceneConfigTreeTableView.setReadOnlyMode(remotePool.getSceneRemote().isSceneConfigRegistryReadOnly());

            return sceneRegistryTabPane;
        } else if (msg instanceof AppRegistryData) {
            AppRegistryData data = (AppRegistryData) msg;
            appConfigTreeTableView.update(data.getAppUnitConfigList());
            appClassTreeTableView.update(data.getAppClassList());

            appConfigTreeTableView.setReadOnlyMode(remotePool.getAppRemote().isAppConfigRegistryReadOnly());
            appClassTreeTableView.setReadOnlyMode(remotePool.getAppRemote().isAppClassRegistryReadOnly());

            return appRegistryTabPane;
        } else if (msg instanceof AgentRegistryData) {
            AgentRegistryData data = (AgentRegistryData) msg;
            agentConfigTreeTableView.update(data.getAgentUnitConfigList());
            agentClassTreeTableView.update(data.getAgentClassList());

            agentConfigTreeTableView.setReadOnlyMode(remotePool.getAgentRemote().isAgentConfigRegistryReadOnly());
            agentClassTreeTableView.setReadOnlyMode(remotePool.getAgentRemote().isAgentClassRegistryReadOnly());

            return agentRegistryTabPane;
        } else if (msg instanceof UserRegistryData) {
            UserRegistryData data = (UserRegistryData) msg;
            userConfigTreeTableView.update(data.getUserUnitConfigList());
            authorizationGroupConfigTreeTableView.update(data.getAuthorizationGroupUnitConfigList());

            userConfigTreeTableView.setReadOnlyMode(remotePool.getUserRemote().isUserConfigRegistryReadOnly());
            authorizationGroupConfigTreeTableView.setReadOnlyMode(remotePool.getUserRemote().isAuthorizationGroupConfigRegistryReadOnly());

            return userRegistryTabPane;
        } else if (msg instanceof UnitRegistryData) {
            UnitRegistryData data = (UnitRegistryData) msg;
            dalUnitConfigTreeTableView.update(data.getDalUnitConfigList());
            unitTemplateTreeTableView.update(data.getUnitTemplateList());
            unitGroupConfigTreeTableView.update(data.getUnitGroupUnitConfigList());
            serviceTemplateTreeTableView.update(data.getServiceTemplateList());

            dalUnitConfigTreeTableView.setReadOnlyMode(remotePool.getUnitRemote().isDalUnitConfigRegistryReadOnly());
            unitTemplateTreeTableView.setReadOnlyMode(remotePool.getUnitRemote().isUnitTemplateRegistryReadOnly());
            unitGroupConfigTreeTableView.setReadOnlyMode(remotePool.getUnitRemote().isUnitGroupConfigRegistryReadOnly());
            serviceTemplateTreeTableView.setReadOnlyMode(remotePool.getUnitRemote().isServiceTemplateRegistryReadOnly());

            return unitRegistryTabPane;
        } else if (msg instanceof UserActivityRegistryData) {
            UserActivityRegistryData data = (UserActivityRegistryData) msg;
            userActivityClassTreeTableView.update(data.getUserActivityClassList());
            userActivityConfigTreeTableView.update(data.getUserActivityConfigList());

            userActivityClassTreeTableView.setReadOnlyMode(remotePool.getUserActivityRemote().isUserActivityClassRegistryReadOnly());
            userActivityConfigTreeTableView.setReadOnlyMode(remotePool.getUserActivityRemote().isUserActivityConfigRegistryReadOnly());

            return userActivityRegistryTabPane;
        }
        return null;
    }

    private Tab getRegistryTabByRemote(RSBRemoteService remote) {
        if (remote instanceof DeviceRegistryRemote) {
            return deviceRegistryTab;
        } else if (remote instanceof LocationRegistryRemote) {
            return locationRegistryTab;
        } else if (remote instanceof SceneRegistryRemote) {
            return sceneRegistryTab;
        } else if (remote instanceof AppRegistryRemote) {
            return appRegistryTab;
        } else if (remote instanceof AgentRegistryRemote) {
            return agentRegistryTab;
        } else if (remote instanceof UserRegistryRemote) {
            return userRegistryTab;
        } else if (remote instanceof UnitRegistryRemote) {
            return unitRegistryTab;
        } else if (remote instanceof UserActivityRegistryRemote) {
            return userActivityRegistryTab;
        }
        return null;
    }

    private List<RegistryTreeTableView> getTreeTablesByRemote(RSBRemoteService remote) {
        List<RegistryTreeTableView> treeTableList = new ArrayList<>();
        if (remote instanceof DeviceRegistryRemote) {
            treeTableList.add(deviceClassTreeTableView);
            treeTableList.add(deviceConfigTreeTableView);
        } else if (remote instanceof LocationRegistryRemote) {
            treeTableList.add(locationConfigTreeTableView);
            treeTableList.add(connectionConfigTreeTableView);
        } else if (remote instanceof SceneRegistryRemote) {
            treeTableList.add(sceneConfigTreeTableView);
        } else if (remote instanceof AppRegistryRemote) {
            treeTableList.add(appClassTreeTableView);
            treeTableList.add(appConfigTreeTableView);
        } else if (remote instanceof AgentRegistryRemote) {
            treeTableList.add(agentClassTreeTableView);
            treeTableList.add(agentConfigTreeTableView);
        } else if (remote instanceof UserRegistryRemote) {
            treeTableList.add(userConfigTreeTableView);
            treeTableList.add(authorizationGroupConfigTreeTableView);
        } else if (remote instanceof UnitRegistryRemote) {
            treeTableList.add(dalUnitConfigTreeTableView);
            treeTableList.add(unitGroupConfigTreeTableView);
            treeTableList.add(unitTemplateTreeTableView);
            treeTableList.add(serviceTemplateTreeTableView);
        } else if (remote instanceof UserActivityRegistryRemote) {
            treeTableList.add(userActivityClassTreeTableView);
            treeTableList.add(userActivityConfigTreeTableView);
        }
        return treeTableList;
    }

    private ProgressIndicator getProgressindicatorByRemote(RSBRemoteService remote) {
        if (remote instanceof DeviceRegistryRemote) {
            return deviceRegistryProgressIndicator;
        } else if (remote instanceof LocationRegistryRemote) {
            return locationRegistryprogressIndicator;
        } else if (remote instanceof SceneRegistryRemote) {
            return sceneRegistryprogressIndicator;
        } else if (remote instanceof AppRegistryRemote) {
            return appRegistryprogressIndicator;
        } else if (remote instanceof AgentRegistryRemote) {
            return agentRegistryProgressIndicator;
        } else if (remote instanceof UserRegistryRemote) {
            return userRegistryProgessInidicator;
        } else if (remote instanceof UnitRegistryRemote) {
            return unitRegistryProgressIndicator;
        } else if (remote instanceof UserActivityRegistryRemote) {
            return userActivityRegistryProgressIndicator;
        }
        return null;
    }

    public RegistryTreeTableView getTreeTableViewBySendableType(SendableType sendableType) {
        switch (sendableType) {
            case AGENT_CLASS:
                return agentClassTreeTableView;
            case AGENT_CONFIG:
                return agentConfigTreeTableView;
            case APP_CLASS:
                return appClassTreeTableView;
            case APP_CONFIG:
                return appConfigTreeTableView;
            case AUTHORIZATION_GROUP_CONFIG:
                return authorizationGroupConfigTreeTableView;
            case CONNECTION_CONFIG:
                return connectionConfigTreeTableView;
            case DEVICE_CLASS:
                return deviceClassTreeTableView;
            case DEVICE_CONFIG:
                return deviceConfigTreeTableView;
            case LOCATION_CONFIG:
                return locationConfigTreeTableView;
            case SCENE_CONFIG:
                return sceneConfigTreeTableView;
            case UNIT_CONFIG:
                return dalUnitConfigTreeTableView;
            case UNIT_GROUP_CONFIG:
                return unitGroupConfigTreeTableView;
            case UNIT_TEMPLATE:
                return unitTemplateTreeTableView;
            case USER_CONFIG:
                return userConfigTreeTableView;
            case SERVICE_TEMPLATE:
                return serviceTemplateTreeTableView;
            case USER_ACTIVITY_CLASS:
                return userActivityClassTreeTableView;
            case USER_ACTIVITY_CONFIG:
                return userActivityConfigTreeTableView;
            default:
                return null;
        }
    }

    public void selectTabBySendableType(SendableType sendableType) {
        javafx.scene.Node node = getTreeTableViewBySendableType(sendableType);
        while (node.getParent() != null) {
            node = node.getParent();
            if (node instanceof TabPaneWithClearing) {
                ((TabPaneWithClearing) node).selectTabByType(sendableType);
            }
        }
    }

    public void selectMessageById(String id) throws CouldNotPerformException {
        GeneratedMessage msg = (GeneratedMessage) remotePool.getById(id);
        SendableType sendableType = SendableType.getTypeToMessage(msg);
        selectTabBySendableType(sendableType);
        getTreeTableViewBySendableType(sendableType).selectMessage(msg);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LOGGER.info("Starting " + APP_NAME + "...");

        /* Setup JPService */
        JPService.setApplicationName(APP_NAME);
        JPService.registerProperty(JPReadOnly.class);
        JPService.registerProperty(JPDeviceRegistryScope.class);
        JPService.registerProperty(JPLocationRegistryScope.class);
        JPService.registerProperty(JPSceneRegistryScope.class);
        JPService.registerProperty(JPAgentRegistryScope.class);
        JPService.registerProperty(JPAppRegistryScope.class);
        JPService.registerProperty(JPUserRegistryScope.class);
        JPService.registerProperty(JPUnitRegistryScope.class);
        JPService.registerProperty(JPRSBHost.class);
        JPService.registerProperty(JPRSBPort.class);
        JPService.registerProperty(JPRSBTransport.class);
        JPService.parseAndExitOnError(args);
        LauncherImpl.launchApplication(RegistryEditor.class, RegistryEditorPreloader.class, args);
        LOGGER.info(APP_NAME + " successfully started.");
    }

    public static void printException(Throwable th, Logger logger, LogLevel logLevel) {
        GlobalTextArea.getInstance().printException(th);
        ExceptionPrinter.printHistory(th, logger, logLevel);
    }

    public static <V> Future<V> runOnFxThread(final Callable<V> callable) {
        return runOnFxThread(callable, "task");
    }

    public static <V> Future<V> runOnFxThread(final Callable<V> callable, final String taskDescribtion) {
        try {
            if (Platform.isFxApplicationThread()) {
                return CompletableFuture.completedFuture(callable.call());
            }

            FutureTask<V> future = new FutureTask(() -> {
                try {
                    return callable.call();
                } catch (Exception ex) {
                    throw ExceptionPrinter.printHistoryAndReturnThrowable(new CouldNotPerformException("Could not perform " + taskDescribtion + "!", ex), LOGGER);
                }
            });
            Platform.runLater(future);
            return future;
        } catch (Exception ex) {
            ExceptionPrinter.printHistory("Could not perform " + taskDescribtion + "!", ex, LOGGER);
            final CompletableFuture<V> completableFuture = new CompletableFuture<>();
            completableFuture.completeExceptionally(ex);
            return completableFuture;
        }
    }
}
