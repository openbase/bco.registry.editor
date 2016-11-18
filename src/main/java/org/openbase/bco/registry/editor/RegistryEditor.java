package org.openbase.bco.registry.editor;

/*
 * #%L
 * RegistryEditor
 * %%
 * Copyright (C) 2014 - 2016 openbase.org
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
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
import org.openbase.bco.registry.editor.visual.provider.DeviceClassItemDescriptorProvider;
import org.openbase.bco.registry.editor.visual.provider.FieldDescriptorGroup;
import org.openbase.bco.registry.editor.visual.provider.LocationItemDescriptorProvider;
import org.openbase.bco.registry.editor.visual.provider.TreeItemDescriptorProvider;
import org.openbase.bco.registry.editor.visual.provider.UnitTypeItemDescriptorProvider;
import org.openbase.bco.registry.location.lib.jp.JPLocationRegistryScope;
import org.openbase.bco.registry.location.remote.LocationRegistryRemote;
import org.openbase.bco.registry.scene.lib.jp.JPSceneRegistryScope;
import org.openbase.bco.registry.scene.remote.SceneRegistryRemote;
import org.openbase.bco.registry.unit.lib.jp.JPUnitRegistryScope;
import org.openbase.bco.registry.unit.remote.UnitRegistryRemote;
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
import org.openbase.jul.pattern.Observable;
import org.openbase.jul.pattern.Observer;
import org.openbase.jul.pattern.Remote.ConnectionState;
import org.openbase.jul.schedule.GlobalExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.registry.AgentRegistryDataType.AgentRegistryData;
import rst.domotic.registry.AppRegistryDataType.AppRegistryData;
import rst.domotic.registry.DeviceRegistryDataType.DeviceRegistryData;
import rst.domotic.registry.LocationRegistryDataType.LocationRegistryData;
import rst.domotic.registry.SceneRegistryDataType.SceneRegistryData;
import rst.domotic.registry.UnitRegistryDataType.UnitRegistryData;
import rst.domotic.registry.UserRegistryDataType.UserRegistryData;
import rst.domotic.unit.device.DeviceClassType.DeviceClass;

/**
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class RegistryEditor extends Application {

    //TODO: 
    // - differentiate in every tab between read only registry and consistent
    // - when the remote is disconnected -> show that in the read only label and deactivate apply and cancel buttons
    private static final Logger logger = LoggerFactory.getLogger(RegistryEditor.class);

    public static final String APP_NAME = "RegistryView";
    public static final int RESOLUTION_WIDTH = 1024;
    private final GlobalTextArea globalTextArea = GlobalTextArea.getInstance();

    private final RemotePool remotePool;
    private MenuBar menuBar;
    private Menu fileMenu;
    private MenuItem resyncMenuItem;
    private TabPaneWithClearing registryTabPane, deviceRegistryTabPane, locationRegistryTabPane, userRegistryTabPane, agentRegistryTabPane, appRegistryTabPane, sceneRegistryTabPane, unitRegistryTabPane;
    private Tab deviceRegistryTab, locationRegistryTab, sceneRegistryTab, agentRegistryTab, appRegistryTab, userRegistryTab, unitRegistryTab;
    private Tab deviceClassTab, deviceConfigTab;
    private Tab locationConfigTab, connectionConfigTab;
    private Tab userConfigTab, userGroupConfigTab;
    private Tab agentClassTab, agentConfigTab;
    private Tab appClassTab, appConfigTab;
    private Tab sceneConfigTab;
    private Tab unitConfigTab, unitTemplateTab, unitGroupTab;
    private ProgressIndicator deviceRegistryProgressIndicator, locationRegistryprogressIndicator, appRegistryprogressIndicator, agentRegistryProgressIndicator, sceneRegistryprogressIndicator, userRegistryProgessInidicator, unitRegistryProgressIndicator;
    private RegistryTreeTableView deviceClassTreeTableView, deviceConfigTreeTableView;
    private RegistryTreeTableView locationConfigTreeTableView, connectionConfigTreeTableView;
    private RegistryTreeTableView sceneConfigTreeTableView;
    private RegistryTreeTableView agentConfigTreeTableView, agentClassTreeTableView;
    private RegistryTreeTableView appConfigTreeTableView, appClassTreeTableView;
    private RegistryTreeTableView userConfigTreeTableView, authorizationGroupConfigTreeTableView;
    private RegistryTreeTableView dalUnitConfigTreeTableView, unitTemplateTreeTableView, unitGroupConfigTreeTableView;
    private final Map<String, Boolean> intialized;

    public RegistryEditor() throws InstantiationException, InterruptedException {
        remotePool = RemotePool.getInstance();
        intialized = new HashMap<>();
        intialized.put(DeviceRegistryData.class.getSimpleName(), Boolean.FALSE);
        intialized.put(LocationRegistryData.class.getSimpleName(), Boolean.FALSE);
        intialized.put(AgentRegistryData.class.getSimpleName(), Boolean.FALSE);
        intialized.put(AppRegistryData.class.getSimpleName(), Boolean.FALSE);
        intialized.put(SceneRegistryData.class.getSimpleName(), Boolean.FALSE);
        intialized.put(UserRegistryData.class.getSimpleName(), Boolean.FALSE);
        intialized.put(UnitRegistryData.class.getSimpleName(), Boolean.FALSE);
    }

    @Override
    public void init() throws Exception {
        super.init();
        remotePool.init();

        registryTabPane = new TabPaneWithClearing();
        registryTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        deviceRegistryTab = new Tab("DeviceRegistry");
        locationRegistryTab = new Tab("LocationRegistry");
        sceneRegistryTab = new Tab("SceneRegistry");
        agentRegistryTab = new Tab("AgentRegistry");
        appRegistryTab = new Tab("AppRegistry");
        userRegistryTab = new Tab("UserRegistry");
        unitRegistryTab = new Tab("UnitRegistry");

        registryTabPane.addTab(deviceRegistryTab, SendableType.DEVICE_CONFIG);
        registryTabPane.addTab(locationRegistryTab, SendableType.LOCATION_CONFIG);
        registryTabPane.addTab(sceneRegistryTab, SendableType.SCENE_CONFIG);
        registryTabPane.addTab(agentRegistryTab, SendableType.AGENT_CONFIG);
        registryTabPane.addTab(appRegistryTab, SendableType.APP_CONFIG);
        registryTabPane.addTab(userRegistryTab, SendableType.USER_CONFIG);
        registryTabPane.addTab(unitRegistryTab, SendableType.UNIT_CONFIG);
//        registryTabPane.getTabs().addAll(deviceRegistryTab, locationRegistryTab, sceneRegistryTab, agentRegistryTab, appRegistryTab, userRegistryTab, unitRegistryTab);

        deviceRegistryProgressIndicator = new ProgressIndicator();
        locationRegistryprogressIndicator = new ProgressIndicator();
        appRegistryprogressIndicator = new ProgressIndicator();
        agentRegistryProgressIndicator = new ProgressIndicator();
        sceneRegistryprogressIndicator = new ProgressIndicator();
        userRegistryProgessInidicator = new ProgressIndicator();
        unitRegistryProgressIndicator = new ProgressIndicator();

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
        userConfigTreeTableView = new RegistryTreeTableView(SendableType.USER_CONFIG, this);
        authorizationGroupConfigTreeTableView = new RegistryTreeTableView(SendableType.AUTHORIZATION_GROUP_CONFIG, this);
        unitGroupConfigTreeTableView = new RegistryTreeTableView(SendableType.UNIT_GROUP_CONFIG, this);
        dalUnitConfigTreeTableView = new RegistryTreeTableView(SendableType.UNIT_CONFIG, this);

        unitRegistryTabPane = new TabPaneWithClearing();
        unitRegistryTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        unitConfigTab = new Tab("UnitConfig");
        unitTemplateTab = new Tab("UnitTemplate");
        unitGroupTab = new Tab("UnitGroup");
        unitConfigTab.setContent(dalUnitConfigTreeTableView.getVBox());
        unitTemplateTab.setContent(unitTemplateTreeTableView.getVBox());
        unitGroupTab.setContent(unitGroupConfigTreeTableView.getVBox());
        unitRegistryTabPane.addTab(unitConfigTab, SendableType.UNIT_CONFIG);
        unitRegistryTabPane.addTab(unitTemplateTab, SendableType.UNIT_TEMPLATE);
        unitRegistryTabPane.addTab(unitGroupTab, SendableType.UNIT_GROUP_CONFIG);
//        unitRegistryTabPane.getTabs().addAll(unitConfigTab, unitTemplateTab, unitGroupTab);

        deviceRegistryTabPane = new TabPaneWithClearing();
        deviceRegistryTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        deviceClassTab = new Tab("DeviceClass");
        deviceConfigTab = new Tab("DeviceConfig");
        deviceClassTab.setContent(deviceClassTreeTableView.getVBox());
        deviceConfigTab.setContent(deviceConfigTreeTableView.getVBox());
        deviceRegistryTabPane.getTabs().addAll(deviceClassTab, deviceConfigTab);

        locationRegistryTabPane = new TabPaneWithClearing();
        locationRegistryTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        locationConfigTab = new Tab("LocationConfig");
        locationConfigTab.setContent(locationConfigTreeTableView.getVBox());
        connectionConfigTab = new Tab("ConnectionConfig");
        connectionConfigTab.setContent(connectionConfigTreeTableView.getVBox());
        locationRegistryTabPane.getTabs().addAll(connectionConfigTab, locationConfigTab);

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
        agentRegistryTabPane.getTabs().addAll(agentClassTab, agentConfigTab);

        appRegistryTabPane = new TabPaneWithClearing();
        appRegistryTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        appClassTab = new Tab("AppClass");
        appClassTab.setContent(appClassTreeTableView.getVBox());
        appConfigTab = new Tab("AppConfig");
        appConfigTab.setContent(appConfigTreeTableView.getVBox());
        appRegistryTabPane.getTabs().addAll(appClassTab, appConfigTab);

        sceneRegistryTabPane = new TabPaneWithClearing();
        sceneRegistryTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        sceneConfigTab = new Tab("SceneConfig");
        sceneConfigTab.setContent(sceneConfigTreeTableView.getVBox());
        sceneRegistryTabPane.getTabs().addAll(sceneConfigTab);

        resyncMenuItem = new MenuItem("Resync");
        resyncMenuItem.setOnAction((ActionEvent event) -> {
            remotePool.getRemotes().stream().forEach((remote) -> {
                try {
                    remote.requestData();
                } catch (CouldNotPerformException ex) {
                    printException(ex, logger, LogLevel.ERROR);
                }
            });
        });
        fileMenu = new Menu("Registry");

        fileMenu.getItems().addAll(resyncMenuItem);
        menuBar = new MenuBar();
        menuBar.getMenus().add(fileMenu);

        logger.info("Init finished");
    }

    private SplitPane splitPane;

    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.info("Starting");
        remotePool.getRemotes().stream().forEach((remote) -> {
            updateTab(remote);
        });

        splitPane = new SplitPane(/*registryTabPane, globalTextArea*/);
        splitPane.getItems().addAll(registryTabPane, globalTextArea);
        globalTextArea.addParent(splitPane);
        splitPane.setOrientation(Orientation.VERTICAL);

        VBox vBox = new VBox(menuBar, splitPane);
        Scene scene = new Scene(vBox, RESOLUTION_WIDTH, 576);
        scene.heightProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                splitPane.setPrefHeight(newValue.doubleValue());
                splitPane.setDividerPositions(1);
            }
        });

        deviceClassTreeTableView.addWidthProperty(scene.widthProperty());
        deviceConfigTreeTableView.addWidthProperty(scene.widthProperty());
        locationConfigTreeTableView.addWidthProperty(scene.widthProperty());
        connectionConfigTreeTableView.addWidthProperty(scene.widthProperty());
        sceneConfigTreeTableView.addWidthProperty(scene.widthProperty());
        agentConfigTreeTableView.addWidthProperty(scene.widthProperty());
        agentClassTreeTableView.addWidthProperty(scene.widthProperty());
        appConfigTreeTableView.addWidthProperty(scene.widthProperty());
        appClassTreeTableView.addWidthProperty(scene.widthProperty());
        unitTemplateTreeTableView.addWidthProperty(scene.widthProperty());
        userConfigTreeTableView.addWidthProperty(scene.widthProperty());
        authorizationGroupConfigTreeTableView.addWidthProperty(scene.widthProperty());
        unitGroupConfigTreeTableView.addWidthProperty(scene.widthProperty());
        dalUnitConfigTreeTableView.addWidthProperty(scene.widthProperty());

        deviceClassTreeTableView.addHeightProperty(scene.heightProperty());
        deviceConfigTreeTableView.addHeightProperty(scene.heightProperty());
        locationConfigTreeTableView.addHeightProperty(scene.heightProperty());
        connectionConfigTreeTableView.addHeightProperty(scene.heightProperty());
        sceneConfigTreeTableView.addHeightProperty(scene.heightProperty());
        agentConfigTreeTableView.addHeightProperty(scene.heightProperty());
        agentClassTreeTableView.addHeightProperty(scene.heightProperty());
        appConfigTreeTableView.addHeightProperty(scene.heightProperty());
        appClassTreeTableView.addHeightProperty(scene.heightProperty());
        unitTemplateTreeTableView.addHeightProperty(scene.heightProperty());
        userConfigTreeTableView.addHeightProperty(scene.heightProperty());
        authorizationGroupConfigTreeTableView.addHeightProperty(scene.heightProperty());
        unitGroupConfigTreeTableView.addHeightProperty(scene.heightProperty());
        dalUnitConfigTreeTableView.addHeightProperty(scene.heightProperty());

        primaryStage.setTitle("Registry Editor");
        try {
            logger.info("Try to load icon...");
            primaryStage.getIcons().add(new Image("registry-editor.png"));
            logger.info("App icon loaded...");
        } catch (Exception ex) {
            printException(ex, logger, LogLevel.WARN);
        }

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                try {
                    stop();
                } catch (Exception ex) {
                    printException(ex, logger, LogLevel.ERROR);
                    System.exit(1);
                }
            }
        });
        primaryStage.setScene(scene);
        primaryStage.show();

        logger.info(APP_NAME + " successfully started.");

        logger.info("Register observer");
        registerObserver();
    }

    public void registerObserver() throws Exception {

        final Map<RSBRemoteService, Future<Void>> registrationFutureMap = new HashMap<>();

        for (RSBRemoteService remote : remotePool.getRemotes()) {
            registrationFutureMap.put(remote, GlobalExecutionService.submit(new Callable<Void>() {

                @Override
                public Void call() throws Exception {
                    try {
                        remote.addDataObserver(new Observer() {

                            @Override
                            public void update(org.openbase.jul.pattern.Observable source, Object data) throws Exception {
                                updateTab(remote);
                            }
                        });
                        if (remote.equals(remotePool.getDeviceRemote())) {
//                            logger.info("Device tree cannot be created without activated location remote. Waiting for its activation...");
                            while (!registrationFutureMap.containsKey(remotePool.getLocationRemote())) {
                                Thread.yield();
                            }
                            registrationFutureMap.get(remotePool.getLocationRemote()).get();
                        }
                        remote.activate();
                    } catch (InterruptedException | CouldNotPerformException ex) {
                        printException(ex, logger, LogLevel.ERROR);
                    }
                    return null;
                }
            }));

            remote.addConnectionStateObserver(new Observer<ConnectionState>() {

                @Override
                public void update(Observable<ConnectionState> source, ConnectionState data) throws Exception {
                    logger.debug("Remote connection state has changed to: " + data);
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            boolean disonnected = false;
                            if (data != ConnectionState.CONNECTED) {
                                disonnected = true;
                            }

                            for (RegistryTreeTableView treeTable : getTreeTablesByRemote(remote)) {
                                treeTable.setDisconnected(disonnected);
                            }
                        }
                    });
                }
            });
        }

//        remotePool.getAgentRemote().isActive();
//        for(Map.Entry<RSBRemoteService, Future<Void>> entry : registrationFutureMap.entrySet()) {
//            entry.getValue().get();
//        }
//        registrationFutureMap.get(remotePool.getLocationRemote()).get();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        remotePool.shutdown();
        //TODO: search why it will not shutdown without system exit
        System.exit(0);
    }

    private void updateTab(RSBRemoteService remote) {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                Tab tab = getRegistryTabByRemote(remote);
                if (!remote.isConnected() || !remote.isActive()) {
                    tab.setContent(getProgressindicatorByRemote(remote));
                    return;
                }
                try {
                    GeneratedMessage data = remote.getData();
                    if (!intialized.get(data.getClass().getSimpleName())) {
//                        logger.info(data.getClass().getSimpleName() + " is not yet initialized");
                        tab.setContent(fillTreeTableView(data));
                    } else {
//                        logger.info("Updating " + data.getClass().getSimpleName());
                        tab.setContent(updateTreeTableView(data));
                    }
                } catch (CouldNotPerformException | InterruptedException ex) {
                    ExceptionPrinter.printHistory(new NotAvailableException("Registry", ex), logger);
                    tab.setContent(new Label("Error: " + ex.getMessage()));
                }
            }
        });
    }

    private javafx.scene.Node fillTreeTableView(GeneratedMessage msg) throws InstantiationException, CouldNotPerformException, InterruptedException {
        if (msg instanceof DeviceRegistryData) {
            DeviceRegistryData data = (DeviceRegistryData) msg;
            TreeItemDescriptorProvider company = new FieldDescriptorGroup(DeviceClass.newBuilder(), DeviceClass.COMPANY_FIELD_NUMBER);
            Descriptors.FieldDescriptor deviceClassfield = data.toBuilder().getDescriptorForType().findFieldByNumber(DeviceRegistryData.DEVICE_CLASS_FIELD_NUMBER);
            deviceClassTreeTableView.setRoot(new GenericGroupContainer<>(deviceClassfield.getName(), deviceClassfield, data.toBuilder(), data.toBuilder().getDeviceClassBuilderList(), company));
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
            agentConfigTreeTableView.setRoot(new GenericListContainer(AgentRegistryData.AGENT_UNIT_CONFIG_FIELD_NUMBER, data.toBuilder()));
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

            unitTemplateTreeTableView.setRoot(new GenericListContainer<>(UnitRegistryData.UNIT_TEMPLATE_FIELD_NUMBER, data.toBuilder()));
            unitTemplateTreeTableView.setReadOnlyMode(remotePool.isReadOnly(SendableType.UNIT_TEMPLATE.getDefaultInstanceForType()));
            unitTemplateTreeTableView.getListDiff().diff(data.getUnitTemplateList());

            unitGroupConfigTreeTableView.setRoot(new GenericListContainer<>(UnitRegistryData.UNIT_GROUP_UNIT_CONFIG_FIELD_NUMBER, data.toBuilder()));
            unitGroupConfigTreeTableView.setReadOnlyMode(remotePool.isReadOnly(SendableType.UNIT_GROUP_CONFIG.getDefaultInstanceForType()));
            unitGroupConfigTreeTableView.getListDiff().diff(data.getUnitGroupUnitConfigList());

            intialized.put(msg.getClass().getSimpleName(), Boolean.TRUE);
            return unitRegistryTabPane;
        }

        return null;
    }

    private javafx.scene.Node updateTreeTableView(GeneratedMessage msg) throws InstantiationException, CouldNotPerformException, InterruptedException {
        if (msg instanceof DeviceRegistryData) {
            DeviceRegistryData data = (DeviceRegistryData) msg;
            deviceClassTreeTableView.update(data.getDeviceClassList());
            deviceConfigTreeTableView.update(data.getDeviceUnitConfigList());
            return deviceRegistryTabPane;
        } else if (msg instanceof LocationRegistryData) {
            LocationRegistryData data = (LocationRegistryData) msg;
            locationConfigTreeTableView.update(data.getLocationUnitConfigList());
            connectionConfigTreeTableView.update(data.getConnectionUnitConfigList());
            return locationRegistryTabPane;
        } else if (msg instanceof SceneRegistryData) {
            SceneRegistryData data = (SceneRegistryData) msg;
            sceneConfigTreeTableView.update(data.getSceneUnitConfigList());
            return sceneRegistryTabPane;
        } else if (msg instanceof AppRegistryData) {
            AppRegistryData data = (AppRegistryData) msg;
            appConfigTreeTableView.update(data.getAppUnitConfigList());
            appClassTreeTableView.update(data.getAppClassList());
            return appRegistryTabPane;
        } else if (msg instanceof AgentRegistryData) {
            AgentRegistryData data = (AgentRegistryData) msg;
            agentConfigTreeTableView.update(data.getAgentUnitConfigList());
            agentClassTreeTableView.update(data.getAgentClassList());
            return agentRegistryTabPane;
        } else if (msg instanceof UserRegistryData) {
            UserRegistryData data = (UserRegistryData) msg;
            userConfigTreeTableView.update(data.getUserUnitConfigList());
            authorizationGroupConfigTreeTableView.update(data.getAuthorizationGroupUnitConfigList());
            return userRegistryTabPane;
        } else if (msg instanceof UnitRegistryData) {
            UnitRegistryData data = (UnitRegistryData) msg;
            dalUnitConfigTreeTableView.update(data.getDalUnitConfigList());
            unitTemplateTreeTableView.update(data.getUnitTemplateList());
            unitGroupConfigTreeTableView.update(data.getUnitGroupUnitConfigList());
            return unitRegistryTabPane;
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
        logger.info("Starting " + APP_NAME + "...");

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
        JPService.parseAndExitOnError(args);

        launch(args);
    }

    public static void printException(Throwable th, Logger logger, LogLevel logLevel) {
        GlobalTextArea.getInstance().printException(th);
        ExceptionPrinter.printHistory(th, logger, logLevel);
    }
}
