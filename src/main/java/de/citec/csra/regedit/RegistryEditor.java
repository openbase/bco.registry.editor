/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.regedit;

import de.citec.csra.regedit.util.SendableType;
import de.citec.csra.regedit.view.RegistryTreeTableView;
import de.citec.csra.regedit.view.GlobalTextArea;
import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessage;
import de.citec.agm.remote.AgentRegistryRemote;
import de.citec.apm.remote.AppRegistryRemote;
import de.citec.csra.regedit.struct.GenericGroupContainer;
import de.citec.csra.regedit.struct.GenericListContainer;
import de.citec.csra.regedit.util.FieldDescriptorGroup;
import de.citec.csra.regedit.util.RemotePool;
import de.citec.dm.remote.DeviceRegistryRemote;
import de.citec.jp.JPAgentRegistryScope;
import de.citec.jp.JPAppRegistryScope;
import de.citec.jp.JPDeviceRegistryScope;
import de.citec.jp.JPLocationRegistryScope;
import de.citec.jp.JPSceneRegistryScope;
import de.citec.jps.core.JPService;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.jul.exception.printer.ExceptionPrinter;
import de.citec.jul.exception.InstantiationException;
import de.citec.jul.exception.printer.LogLevel;
import de.citec.jul.extension.rsb.com.RSBRemoteService;
import de.citec.jul.pattern.Observable;
import de.citec.lm.remote.LocationRegistryRemote;
import de.citec.scm.remote.SceneRegistryRemote;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.homeautomation.control.agent.AgentRegistryType.AgentRegistry;
import rst.homeautomation.control.app.AppRegistryType.AppRegistry;
import rst.homeautomation.control.scene.SceneRegistryType.SceneRegistry;
import rst.homeautomation.device.DeviceConfigType.DeviceConfig;
import rst.homeautomation.device.DeviceRegistryType.DeviceRegistry;
import rst.spatial.LocationRegistryType.LocationRegistry;
import rst.spatial.PlacementConfigType.PlacementConfig;

/**
 *
 * @author thuxohl
 */
public class RegistryEditor extends Application {

    private static final Logger logger = LoggerFactory.getLogger(RegistryEditor.class);

    public static final String APP_NAME = "RegistryView";
    public static final int RESOLUTION_WIDTH = 1024;
    private static boolean modified = false;
    private final Map<SendableType, Boolean> READ_ONLY_MAP = new HashMap<>();
    private final GlobalTextArea globalTextArea = GlobalTextArea.getInstance();

    private final RemotePool remotePool;
    private TabPane registryTabPane, deviceRegistryTabPane;
    private Tab deviceRegistryTab, locationRegistryTab, sceneRegistryTab, agentRegistryTab, appRegistryTab;
    private Tab deviceClassTab, deviceConfigTab, unitTemplateTab;
    private ProgressIndicator deviceRegistryProgressIndicator, locationRegistryprogressIndicator, appRegistryprogressIndicator, agentRegistryProgressIndicator, sceneRegistryprogressIndicator;
    private RegistryTreeTableView deviceClassTreeTableView, deviceConfigTreeTableView, locationConfigTreeTableView, sceneConfigTreeTableView, agentConfigTreeTableView, appConfigTreeTableView, unitTemplateTreeTableView;

    public RegistryEditor() throws InstantiationException {
        remotePool = RemotePool.getInstance();
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
        registryTabPane.getTabs().addAll(deviceRegistryTab, locationRegistryTab, sceneRegistryTab, agentRegistryTab, appRegistryTab);

        deviceRegistryProgressIndicator = new ProgressIndicator();
        locationRegistryprogressIndicator = new ProgressIndicator();
        appRegistryprogressIndicator = new ProgressIndicator();
        agentRegistryProgressIndicator = new ProgressIndicator();
        sceneRegistryprogressIndicator = new ProgressIndicator();

        deviceClassTreeTableView = new RegistryTreeTableView(SendableType.DEVICE_CLASS);
        deviceConfigTreeTableView = new RegistryTreeTableView(SendableType.DEVICE_CONFIG);
        locationConfigTreeTableView = new RegistryTreeTableView(SendableType.LOCATION_CONFIG);
        sceneConfigTreeTableView = new RegistryTreeTableView(SendableType.SCENE_CONFIG);
        agentConfigTreeTableView = new RegistryTreeTableView(SendableType.AGENT_CONFIG);
        appConfigTreeTableView = new RegistryTreeTableView(SendableType.APP_CONFIG);
        unitTemplateTreeTableView = new RegistryTreeTableView(null);

        deviceRegistryTabPane = new TabPane();
        deviceRegistryTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        deviceClassTab = new Tab("DeviceClass");
        deviceConfigTab = new Tab("DeviceConfig");
        unitTemplateTab = new Tab("UnitTemplate");
        deviceClassTab.setContent(deviceClassTreeTableView);
        deviceConfigTab.setContent(deviceConfigTreeTableView);
        unitTemplateTab.setContent(unitTemplateTreeTableView);
        deviceRegistryTabPane.getTabs().addAll(deviceClassTab, deviceConfigTab, unitTemplateTab);

        logger.info("Init finished");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        logger.info("Starting");
        remotePool.getRemotes().stream().forEach((remote) -> {
            updateTab(remote);
        });

        VBox vBox = new VBox(registryTabPane, globalTextArea);
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
        sceneConfigTreeTableView.addWidthProperty(scene.widthProperty());
        agentConfigTreeTableView.addWidthProperty(scene.widthProperty());
        appConfigTreeTableView.addWidthProperty(scene.widthProperty());
        unitTemplateTreeTableView.addWidthProperty(scene.widthProperty());

        primaryStage.setTitle("Registry Editor");
        try {
            logger.info("Try to load icon...");
            primaryStage.getIcons().add(new Image("registry-editor.png"));
            logger.info("App icon loaded...");
        } catch (Exception ex) {
            printException(ex, logger, LogLevel.WARN);
        }
        primaryStage.setScene(scene);
        primaryStage.show();

        logger.info(APP_NAME + " successfully started.");

        logger.info("Register observer");
        registerObserver();
    }

    public void registerObserver() throws Exception {
        ExecutorService executerService = Executors.newFixedThreadPool(10);

        for (RSBRemoteService remote : remotePool.getRemotes()) {
            executerService.submit(new Callable<Void>() {

                @Override
                public Void call() throws Exception {
                    try {
                        remote.activate();
                        remote.addObserver((Observable source, Object data) -> {
                            updateTab(remote);
                        });
                        try {
                            remote.requestStatus();
                        } catch (CouldNotPerformException ex) {
                            printException(ex, logger, LogLevel.WARN);
                        }
                    } catch (InterruptedException | CouldNotPerformException ex) {
                        printException(ex, logger, LogLevel.ERROR);
                    }
                    return null;
                }
            });
        }
    }

    @Override
    public void stop() throws Exception {
        remotePool.shutdown();
        super.stop();
    }

    private void updateTab(RSBRemoteService remote) {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                Tab tab = getRegistryTabByRemote(remote);
                if (!remote.isConnected()) {
                    tab.setContent(getProgressindicatorByRemote(remote));
                    return;
                }
                try {
                    if (!modified) {
                        GeneratedMessage data = remote.getData();
                        tab.setContent(fillTreeTableView(data));
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
            READ_ONLY_MAP.put(SendableType.DEVICE_CLASS, data.getDeviceClassRegistryReadOnly());
            READ_ONLY_MAP.put(SendableType.DEVICE_CONFIG, data.getDeviceConfigRegistryReadOnly());
            READ_ONLY_MAP.put(SendableType.UNIT_TEMPLATE_CONFIG, data.getUnitTemplateRegistryReadOnly());
            deviceClassTreeTableView.setRoot(new GenericListContainer<>(DeviceRegistry.DEVICE_CLASS_FIELD_NUMBER, data.toBuilder()));
            setReadOnlyMode(deviceClassTreeTableView, SendableType.DEVICE_CLASS);

            FieldDescriptorGroup deviceClassId = new FieldDescriptorGroup(DeviceConfig.newBuilder(), DeviceConfig.DEVICE_CLASS_ID_FIELD_NUMBER);
            FieldDescriptorGroup locationId = new FieldDescriptorGroup(DeviceConfig.newBuilder(), DeviceConfig.PLACEMENT_CONFIG_FIELD_NUMBER, PlacementConfig.LOCATION_ID_FIELD_NUMBER);
            Descriptors.FieldDescriptor field = data.toBuilder().getDescriptorForType().findFieldByNumber(DeviceRegistry.DEVICE_CONFIG_FIELD_NUMBER);
            deviceConfigTreeTableView.setRoot(new GenericGroupContainer<>(field.getName(), field, data.toBuilder(), data.toBuilder().getDeviceConfigBuilderList(), deviceClassId, locationId));
            setReadOnlyMode(deviceConfigTreeTableView, SendableType.DEVICE_CONFIG);

            unitTemplateTreeTableView.setRoot(new GenericListContainer<>(DeviceRegistry.UNIT_TEMPLATE_FIELD_NUMBER, data.toBuilder()));
            setReadOnlyMode(unitTemplateTreeTableView, SendableType.UNIT_TEMPLATE_CONFIG);
            return deviceRegistryTabPane;
        } else if (msg instanceof LocationRegistry) {
            LocationRegistry data = (LocationRegistry) msg;
            READ_ONLY_MAP.put(SendableType.LOCATION_CONFIG, data.getLocationConfigRegistryReadOnly());
            locationConfigTreeTableView.setRoot(new GenericListContainer<>(LocationRegistry.LOCATION_CONFIG_FIELD_NUMBER, data.toBuilder()));
            setReadOnlyMode(locationConfigTreeTableView, SendableType.LOCATION_CONFIG);
            return locationConfigTreeTableView;
        } else if (msg instanceof SceneRegistry) {
            SceneRegistry data = (SceneRegistry) msg;
            READ_ONLY_MAP.put(SendableType.SCENE_CONFIG, data.getSceneConfigRegistryReadOnly());
            sceneConfigTreeTableView.setRoot(new GenericListContainer(SceneRegistry.SCENE_CONFIG_FIELD_NUMBER, data.toBuilder()));
            setReadOnlyMode(sceneConfigTreeTableView, SendableType.SCENE_CONFIG);
            return sceneConfigTreeTableView;
        } else if (msg instanceof AppRegistry) {
            AppRegistry data = (AppRegistry) msg;
            READ_ONLY_MAP.put(SendableType.APP_CONFIG, data.getAppConfigRegistryReadOnly());
            appConfigTreeTableView.setRoot(new GenericListContainer(AppRegistry.APP_CONFIG_FIELD_NUMBER, data.toBuilder()));
            setReadOnlyMode(appConfigTreeTableView, SendableType.APP_CONFIG);
            appRegistryTab.setContent(appConfigTreeTableView);
        } else if (msg instanceof AgentRegistry) {
            AgentRegistry data = (AgentRegistry) msg;
            READ_ONLY_MAP.put(SendableType.AGENT_CONFIG, data.getAgentConfigRegistryReadOnly());
            agentConfigTreeTableView.setRoot(new GenericListContainer(AgentRegistry.AGENT_CONFIG_FIELD_NUMBER, data.toBuilder()));
            setReadOnlyMode(agentConfigTreeTableView, SendableType.AGENT_CONFIG);
            return agentConfigTreeTableView;
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
        }
        return null;
    }

    public static void setModified(boolean value) {
        modified = value;
    }

    private void setReadOnlyMode(RegistryTreeTableView treeTableView, SendableType type) {
        if (READ_ONLY_MAP.get(type)) {
            treeTableView.getStylesheets().add("read_only.css");
        } else {
            treeTableView.getStylesheets().add("default.css");
        }
        treeTableView.setEditable(!READ_ONLY_MAP.get(type));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        logger.info("Start " + APP_NAME + "...");

        /* Setup JPService */
        JPService.setApplicationName(APP_NAME);
        JPService.registerProperty(JPDeviceRegistryScope.class);
        JPService.registerProperty(JPLocationRegistryScope.class);
        JPService.registerProperty(JPSceneRegistryScope.class);
        JPService.registerProperty(JPAgentRegistryScope.class);
        JPService.registerProperty(JPAppRegistryScope.class);
        JPService.parseAndExitOnError(args);

        launch(args);
    }

    public static void printException(Throwable th, Logger logger, LogLevel logLevel) {
        GlobalTextArea.getInstance().printException(th);
        ExceptionPrinter.printHistory(th, logger, logLevel);
    }
}