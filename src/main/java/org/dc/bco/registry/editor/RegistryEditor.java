/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dc.bco.registry.editor;

import org.dc.bco.registry.editor.util.SendableType;
import org.dc.bco.registry.editor.visual.GlobalTextArea;
import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessage;
import org.dc.bco.registry.agent.remote.AgentRegistryRemote;
import org.dc.bco.registry.app.remote.AppRegistryRemote;
import org.dc.bco.registry.editor.struct.GenericGroupContainer;
import org.dc.bco.registry.editor.struct.GenericListContainer;
import org.dc.bco.registry.editor.util.RemotePool;
import org.dc.bco.registry.editor.visual.RegistryTreeTableView;
import org.dc.bco.registry.editor.visual.provider.DeviceClassItemDescriptorProvider;
import org.dc.bco.registry.editor.visual.provider.FieldDescriptorGroup;
import org.dc.bco.registry.editor.visual.provider.LocationItemDescriptorProvider;
import org.dc.bco.registry.editor.visual.provider.TreeItemDescriptorProvider;
import org.dc.bco.registry.device.remote.DeviceRegistryRemote;
import org.dc.bco.registry.agent.lib.jp.JPAgentRegistryScope;
import org.dc.bco.registry.app.lib.jp.JPAppRegistryScope;
import org.dc.bco.registry.device.lib.jp.JPDeviceRegistryScope;
import org.dc.bco.registry.location.lib.jp.JPLocationRegistryScope;
import org.dc.bco.registry.scene.lib.jp.JPSceneRegistryScope;
import org.dc.bco.registry.user.lib.jp.JPUserRegistryScope;
import org.dc.jps.core.JPService;
import org.dc.jps.preset.JPReadOnly;
import org.dc.jul.exception.CouldNotPerformException;
import org.dc.jul.exception.printer.ExceptionPrinter;
import org.dc.jul.exception.InstantiationException;
import org.dc.jul.exception.printer.LogLevel;
import org.dc.jul.extension.rsb.com.RSBRemoteService;
import org.dc.jul.pattern.Observable;
import org.dc.bco.registry.location.remote.LocationRegistryRemote;
import org.dc.bco.registry.user.remote.UserRegistryRemote;
import org.dc.bco.registry.scene.remote.SceneRegistryRemote;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.authorization.UserRegistryType.UserRegistry;
import rst.homeautomation.control.agent.AgentRegistryType.AgentRegistry;
import rst.homeautomation.control.app.AppRegistryType.AppRegistry;
import rst.homeautomation.control.scene.SceneRegistryType.SceneRegistry;
import rst.homeautomation.device.DeviceClassType.DeviceClass;
import rst.homeautomation.device.DeviceRegistryType.DeviceRegistry;
import rst.spatial.LocationRegistryType.LocationRegistry;

/**
 *
 * @author thuxohl
 */
public class RegistryEditor extends Application {

    private static final Logger logger = LoggerFactory.getLogger(RegistryEditor.class);

    public static final String APP_NAME = "RegistryView";
    public static final int RESOLUTION_WIDTH = 1024;
    private final GlobalTextArea globalTextArea = GlobalTextArea.getInstance();

    private final RemotePool remotePool;
    private MenuBar menuBar;
    private Menu fileMenu;
    private MenuItem sortMenuItem, resyncMenuItem;
    private TabPane registryTabPane, deviceRegistryTabPane, locationRegistryTabPane, userRegistryTabPane;
    private Tab deviceRegistryTab, locationRegistryTab, sceneRegistryTab, agentRegistryTab, appRegistryTab, userRegistryTab;
    private Tab deviceClassTab, deviceConfigTab, unitTemplateTab, unitGroupTab;
    private Tab locationConfigTab, connectionConfigTab;
    private Tab userConfigTab, userGroupConfigTab;
    private ProgressIndicator deviceRegistryProgressIndicator, locationRegistryprogressIndicator, appRegistryprogressIndicator, agentRegistryProgressIndicator, sceneRegistryprogressIndicator, userRegistryProgessInidicator;
    private RegistryTreeTableView deviceClassTreeTableView, deviceConfigTreeTableView, locationConfigTreeTableView, connectionConfigTreeTableView, sceneConfigTreeTableView, agentConfigTreeTableView, appConfigTreeTableView, unitTemplateTreeTableView, userConfigTreeTableview, userGroupConfigTreeTableView, unitGroupConfigTreeTableView;
    private final Map<String, Boolean> intialized;

    public RegistryEditor() throws InstantiationException {
        remotePool = RemotePool.getInstance();
        intialized = new HashMap<>();
        intialized.put(DeviceRegistry.class.getSimpleName(), Boolean.FALSE);
        intialized.put(LocationRegistry.class.getSimpleName(), Boolean.FALSE);
        intialized.put(AgentRegistry.class.getSimpleName(), Boolean.FALSE);
        intialized.put(AppRegistry.class.getSimpleName(), Boolean.FALSE);
        intialized.put(SceneRegistry.class.getSimpleName(), Boolean.FALSE);
        intialized.put(UserRegistry.class.getSimpleName(), Boolean.FALSE);
    }

    @Override
    public void init() throws Exception {
        super.init();
        remotePool.init();

        registryTabPane = new TabPane();
        registryTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        deviceRegistryTab = new Tab("DeviceRegistry");
        locationRegistryTab = new Tab("LocationRegistry");
        sceneRegistryTab = new Tab("SceneRegistry");
        agentRegistryTab = new Tab("AgentRegistry");
        appRegistryTab = new Tab("AppRegistry");
        userRegistryTab = new Tab("UserRegistry");
        registryTabPane.getTabs().addAll(deviceRegistryTab, locationRegistryTab, sceneRegistryTab, agentRegistryTab, appRegistryTab, userRegistryTab);

        deviceRegistryProgressIndicator = new ProgressIndicator();
        locationRegistryprogressIndicator = new ProgressIndicator();
        appRegistryprogressIndicator = new ProgressIndicator();
        agentRegistryProgressIndicator = new ProgressIndicator();
        sceneRegistryprogressIndicator = new ProgressIndicator();
        userRegistryProgessInidicator = new ProgressIndicator();

        deviceClassTreeTableView = new RegistryTreeTableView(SendableType.DEVICE_CLASS);
        deviceConfigTreeTableView = new RegistryTreeTableView(SendableType.DEVICE_CONFIG);
        locationConfigTreeTableView = new RegistryTreeTableView(SendableType.LOCATION_CONFIG);
        connectionConfigTreeTableView = new RegistryTreeTableView(SendableType.CONNECTION_CONFIG);
        sceneConfigTreeTableView = new RegistryTreeTableView(SendableType.SCENE_CONFIG);
        agentConfigTreeTableView = new RegistryTreeTableView(SendableType.AGENT_CONFIG);
        appConfigTreeTableView = new RegistryTreeTableView(SendableType.APP_CONFIG);
        unitTemplateTreeTableView = new RegistryTreeTableView(SendableType.UNIT_TEMPLATE);
        userConfigTreeTableview = new RegistryTreeTableView(SendableType.USER_CONFIG);
        userGroupConfigTreeTableView = new RegistryTreeTableView(SendableType.USER_GROUP_CONFIG);
        unitGroupConfigTreeTableView = new RegistryTreeTableView(SendableType.UNIT_GROUP_CONFIG);

        deviceRegistryTabPane = new TabPane();
        deviceRegistryTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        deviceClassTab = new Tab("DeviceClass");
        deviceConfigTab = new Tab("DeviceConfig");
        unitTemplateTab = new Tab("UnitTemplate");
        unitGroupTab = new Tab("UnitGroup");
        deviceClassTab.setContent(deviceClassTreeTableView.getVBox());
        deviceConfigTab.setContent(deviceConfigTreeTableView.getVBox());
        unitTemplateTab.setContent(unitTemplateTreeTableView.getVBox());
        unitGroupTab.setContent(unitGroupConfigTreeTableView.getVBox());
        deviceRegistryTabPane.getTabs().addAll(deviceClassTab, deviceConfigTab, unitTemplateTab, unitGroupTab);

        locationRegistryTabPane = new TabPane();
        locationRegistryTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        locationConfigTab = new Tab("LocationConfig");
        locationConfigTab.setContent(locationConfigTreeTableView.getVBox());
        connectionConfigTab = new Tab("ConnectionConfig");
        connectionConfigTab.setContent(connectionConfigTreeTableView.getVBox());
        locationRegistryTabPane.getTabs().addAll(connectionConfigTab, locationConfigTab);

        userRegistryTabPane = new TabPane();
        userRegistryTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        userConfigTab = new Tab("UserConfig");
        userConfigTab.setContent(userConfigTreeTableview.getVBox());
        userGroupConfigTab = new Tab("UserGroupConfig");
        userGroupConfigTab.setContent(userGroupConfigTreeTableView.getVBox());
        userRegistryTabPane.getTabs().addAll(userConfigTab, userGroupConfigTab);

        sortMenuItem = new MenuItem("Sort");
        sortMenuItem.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
//                agentConfigTreeTableView.sort();
//                appConfigTreeTableView.sort();
//                deviceClassTreeTableView.sort();
//                deviceConfigTreeTableView.sort();
//                locationConfigTreeTableView.sort();
//                sceneConfigTreeTableView.sort();
                logger.info("Sorting unitTemplateTreeTableView");
                unitTemplateTreeTableView.sort();
            }
        });
        resyncMenuItem = new MenuItem("resync");
        resyncMenuItem.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                logger.info("Resyncing");
                for (RSBRemoteService remote : remotePool.getRemotes()) {
                    if (remote.isConnected()) {
                        logger.info("Remote is connected [" + remote.getClass().getSimpleName() + "]");
                        try {
                            GeneratedMessage data = remote.getData();
                            logger.info("Got data");
                            getRegistryTabByRemote(remote).setContent(updateTreeTableView(data));
                        } catch (CouldNotPerformException ex) {
                            java.util.logging.Logger.getLogger(RegistryEditor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        });
        fileMenu = new Menu("File");
        fileMenu.getItems().addAll(sortMenuItem, resyncMenuItem);
        menuBar = new MenuBar(fileMenu);

        logger.info("Init finished");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.info("Starting");
        remotePool.getRemotes().stream().forEach((remote) -> {
            updateTab(remote);
        });

        VBox vBox = new VBox(menuBar, registryTabPane, globalTextArea);
        Scene scene = new Scene(vBox, RESOLUTION_WIDTH, 576);
        scene.heightProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                registryTabPane.setPrefHeight(newValue.doubleValue() * 0.80);
                globalTextArea.setPrefHeight(newValue.doubleValue() * 0.20);
            }
        });

        deviceClassTreeTableView.addWidthProperty(scene.widthProperty());
        deviceConfigTreeTableView.addWidthProperty(scene.widthProperty());
        locationConfigTreeTableView.addWidthProperty(scene.widthProperty());
        connectionConfigTreeTableView.addWidthProperty(scene.widthProperty());
        sceneConfigTreeTableView.addWidthProperty(scene.widthProperty());
        agentConfigTreeTableView.addWidthProperty(scene.widthProperty());
        appConfigTreeTableView.addWidthProperty(scene.widthProperty());
        unitTemplateTreeTableView.addWidthProperty(scene.widthProperty());
        userConfigTreeTableview.addWidthProperty(scene.widthProperty());
        userGroupConfigTreeTableView.addWidthProperty(scene.widthProperty());
        unitGroupConfigTreeTableView.addWidthProperty(scene.widthProperty());

        deviceClassTreeTableView.addHeightProperty(scene.heightProperty());
        deviceConfigTreeTableView.addHeightProperty(scene.heightProperty());
        locationConfigTreeTableView.addHeightProperty(scene.heightProperty());
        connectionConfigTreeTableView.addHeightProperty(scene.heightProperty());
        sceneConfigTreeTableView.addHeightProperty(scene.heightProperty());
        agentConfigTreeTableView.addHeightProperty(scene.heightProperty());
        userConfigTreeTableview.addHeightProperty(scene.heightProperty());
        userGroupConfigTreeTableView.addHeightProperty(scene.heightProperty());
        unitGroupConfigTreeTableView.addHeightProperty(scene.heightProperty());

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

    private final ExecutorService executerService = Executors.newCachedThreadPool();

    public void registerObserver() throws Exception {

        final Map<RSBRemoteService, Future<Void>> registrationFutureMap = new HashMap<>();

        for (RSBRemoteService remote : remotePool.getRemotes()) {
            registrationFutureMap.put(remote, executerService.submit(new Callable<Void>() {

                @Override
                public Void call() throws Exception {
                    try {
                        remote.addObserver((Observable source, Object data) -> {
                            updateTab(remote);
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
        executerService.shutdown();
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
                } catch (CouldNotPerformException ex) {
                    logger.error("Registry not available!", ex);
                    tab.setContent(new Label("Error: " + ex.getMessage()));
                }
            }
        });
    }

    private javafx.scene.Node fillTreeTableView(GeneratedMessage msg) throws InstantiationException, CouldNotPerformException {
        if (msg instanceof DeviceRegistry) {
            DeviceRegistry data = (DeviceRegistry) msg;
            TreeItemDescriptorProvider company = new FieldDescriptorGroup(DeviceClass.newBuilder(), DeviceClass.COMPANY_FIELD_NUMBER);
            Descriptors.FieldDescriptor deviceClassfield = data.toBuilder().getDescriptorForType().findFieldByNumber(DeviceRegistry.DEVICE_CLASS_FIELD_NUMBER);
            deviceClassTreeTableView.setRoot(new GenericGroupContainer<>(deviceClassfield.getName(), deviceClassfield, data.toBuilder(), data.toBuilder().getDeviceClassBuilderList(), company));
            deviceClassTreeTableView.setReadOnlyMode(remotePool.isReadOnly(SendableType.DEVICE_CLASS));
            deviceClassTreeTableView.getListDiff().diff(data.getDeviceClassList());

            TreeItemDescriptorProvider deviceClassId = new DeviceClassItemDescriptorProvider();
            TreeItemDescriptorProvider locationId = new LocationItemDescriptorProvider();
            Descriptors.FieldDescriptor deviceConfigfield = data.toBuilder().getDescriptorForType().findFieldByNumber(DeviceRegistry.DEVICE_CONFIG_FIELD_NUMBER);
            deviceConfigTreeTableView.setRoot(new GenericGroupContainer<>(deviceConfigfield.getName(), deviceConfigfield, data.toBuilder(), data.toBuilder().getDeviceConfigBuilderList(), deviceClassId, locationId));
            deviceConfigTreeTableView.setReadOnlyMode(remotePool.isReadOnly(SendableType.DEVICE_CONFIG));
            deviceConfigTreeTableView.getListDiff().diff(data.getDeviceConfigList());

            unitTemplateTreeTableView.setRoot(new GenericListContainer<>(DeviceRegistry.UNIT_TEMPLATE_FIELD_NUMBER, data.toBuilder()));
            unitTemplateTreeTableView.setReadOnlyMode(remotePool.isReadOnly(SendableType.UNIT_TEMPLATE));
            unitTemplateTreeTableView.getListDiff().diff(data.getUnitTemplateList());

            unitGroupConfigTreeTableView.setRoot(new GenericListContainer<>(DeviceRegistry.UNIT_GROUP_CONFIG_FIELD_NUMBER, data.toBuilder()));
            unitGroupConfigTreeTableView.setReadOnlyMode(remotePool.isReadOnly(SendableType.UNIT_GROUP_CONFIG));
            unitGroupConfigTreeTableView.getListDiff().diff(data.getUnitGroupConfigList());

            intialized.put(msg.getClass().getSimpleName(), Boolean.TRUE);
            return deviceRegistryTabPane;
        } else if (msg instanceof LocationRegistry) {
            LocationRegistry data = (LocationRegistry) msg;
            locationConfigTreeTableView.setRoot(new GenericListContainer<>(LocationRegistry.LOCATION_CONFIG_FIELD_NUMBER, data.toBuilder()));
            locationConfigTreeTableView.setReadOnlyMode(remotePool.isReadOnly(SendableType.LOCATION_CONFIG));
            locationConfigTreeTableView.getListDiff().diff(data.getLocationConfigList());

            connectionConfigTreeTableView.setRoot(new GenericListContainer<>(LocationRegistry.CONNECTION_CONFIG_FIELD_NUMBER, data.toBuilder()));
            connectionConfigTreeTableView.setReadOnlyMode(remotePool.isReadOnly(SendableType.CONNECTION_CONFIG));
            connectionConfigTreeTableView.getListDiff().diff(data.getConnectionConfigList());

            intialized.put(msg.getClass().getSimpleName(), Boolean.TRUE);
            return locationRegistryTabPane;
        } else if (msg instanceof SceneRegistry) {
            SceneRegistry data = (SceneRegistry) msg;
            sceneConfigTreeTableView.setRoot(new GenericListContainer(SceneRegistry.SCENE_CONFIG_FIELD_NUMBER, data.toBuilder()));
            sceneConfigTreeTableView.setReadOnlyMode(remotePool.isReadOnly(SendableType.SCENE_CONFIG));
            sceneConfigTreeTableView.getListDiff().diff(data.getSceneConfigList());
            intialized.put(msg.getClass().getSimpleName(), Boolean.TRUE);
            return sceneConfigTreeTableView;
        } else if (msg instanceof AppRegistry) {
            AppRegistry data = (AppRegistry) msg;
            appConfigTreeTableView.setRoot(new GenericListContainer(AppRegistry.APP_CONFIG_FIELD_NUMBER, data.toBuilder()));
            appConfigTreeTableView.setReadOnlyMode(remotePool.isReadOnly(SendableType.APP_CONFIG));
            appConfigTreeTableView.getListDiff().diff(data.getAppConfigList());
            intialized.put(msg.getClass().getSimpleName(), Boolean.TRUE);
            return appConfigTreeTableView;
        } else if (msg instanceof AgentRegistry) {
            AgentRegistry data = (AgentRegistry) msg;
            agentConfigTreeTableView.setRoot(new GenericListContainer(AgentRegistry.AGENT_CONFIG_FIELD_NUMBER, data.toBuilder()));
            agentConfigTreeTableView.setReadOnlyMode(remotePool.isReadOnly(SendableType.AGENT_CONFIG));
            agentConfigTreeTableView.getListDiff().diff(data.getAgentConfigList());
            intialized.put(msg.getClass().getSimpleName(), Boolean.TRUE);
            return agentConfigTreeTableView;
        } else if (msg instanceof UserRegistry) {
            UserRegistry data = (UserRegistry) msg;
            userConfigTreeTableview.setRoot(new GenericListContainer<>(UserRegistry.USER_CONFIG_FIELD_NUMBER, data.toBuilder()));
            userConfigTreeTableview.setReadOnlyMode(remotePool.isReadOnly(SendableType.USER_CONFIG));
            userConfigTreeTableview.getListDiff().diff(data.getUserConfigList());

            userGroupConfigTreeTableView.setRoot(new GenericListContainer<>(UserRegistry.USER_GROUP_CONFIG_FIELD_NUMBER, data.toBuilder()));
            userGroupConfigTreeTableView.setReadOnlyMode(remotePool.isReadOnly(SendableType.USER_GROUP_CONFIG));
            userGroupConfigTreeTableView.getListDiff().diff(data.getUserGroupConfigList());

            intialized.put(msg.getClass().getSimpleName(), Boolean.TRUE);
            return userRegistryTabPane;
        }

        return null;
    }

    private javafx.scene.Node updateTreeTableView(GeneratedMessage msg) throws InstantiationException, CouldNotPerformException {
        if (msg instanceof DeviceRegistry) {
            DeviceRegistry data = (DeviceRegistry) msg;
            deviceClassTreeTableView.update(data.getDeviceClassList());
            deviceConfigTreeTableView.update(data.getDeviceConfigList());
            unitTemplateTreeTableView.update(data.getUnitTemplateList());
            unitGroupConfigTreeTableView.update(data.getUnitGroupConfigList());
            return deviceRegistryTabPane;
        } else if (msg instanceof LocationRegistry) {
            LocationRegistry data = (LocationRegistry) msg;
            locationConfigTreeTableView.update(data.getLocationConfigList());
            connectionConfigTreeTableView.update(data.getConnectionConfigList());
            return locationRegistryTabPane;
        } else if (msg instanceof SceneRegistry) {
            SceneRegistry data = (SceneRegistry) msg;
            sceneConfigTreeTableView.update(data.getSceneConfigList());
            return sceneConfigTreeTableView;
        } else if (msg instanceof AppRegistry) {
            AppRegistry data = (AppRegistry) msg;
            appConfigTreeTableView.update(data.getAppConfigList());
            return appConfigTreeTableView;
        } else if (msg instanceof AgentRegistry) {
            AgentRegistry data = (AgentRegistry) msg;
            agentConfigTreeTableView.update(data.getAgentConfigList());
            return agentConfigTreeTableView;
        } else if (msg instanceof UserRegistry) {
            UserRegistry data = (UserRegistry) msg;
            userConfigTreeTableview.update(data.getUserConfigList());
            userGroupConfigTreeTableView.update(data.getUserGroupConfigList());
            return userRegistryTabPane;
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
        }
        return null;
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
        }
        return null;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        logger.info("Start " + APP_NAME + "...");

        /* Setup JPService */
        JPService.setApplicationName(APP_NAME);
        JPService.registerProperty(JPReadOnly.class);
        JPService.registerProperty(JPDeviceRegistryScope.class);
        JPService.registerProperty(JPLocationRegistryScope.class);
        JPService.registerProperty(JPSceneRegistryScope.class);
        JPService.registerProperty(JPAgentRegistryScope.class);
        JPService.registerProperty(JPAppRegistryScope.class);
        JPService.registerProperty(JPUserRegistryScope.class);
        JPService.parseAndExitOnError(args);

        launch(args);
    }

    public static void printException(Throwable th, Logger logger, LogLevel logLevel) {
        GlobalTextArea.getInstance().printException(th);
        ExceptionPrinter.printHistory(th, logger, logLevel);
    }
}
