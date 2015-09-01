/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re;

import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessage;
import de.citec.agm.remote.AgentRegistryRemote;
import de.citec.apm.remote.AppRegistryRemote;
import de.citec.csra.re.TreeTableViewContextMenu.SendableType;
import de.citec.csra.re.struct.GenericGroupContainer;
import de.citec.csra.re.struct.GenericListContainer;
import de.citec.csra.re.struct.Node;
import de.citec.csra.re.util.FieldDescriptorGroup;
import de.citec.csra.re.util.RemotePool;
import de.citec.dm.remote.DeviceRegistryRemote;
import de.citec.jp.JPAgentRegistryScope;
import de.citec.jp.JPAppRegistryScope;
import de.citec.jp.JPDeviceRegistryScope;
import de.citec.jp.JPLocationRegistryScope;
import de.citec.jp.JPSceneRegistryScope;
import de.citec.jps.core.JPService;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.jul.exception.ExceptionPrinter;
import de.citec.jul.exception.InstantiationException;
import de.citec.jul.extension.rsb.com.RSBRemoteService;
import de.citec.jul.pattern.Observable;
import de.citec.lm.remote.LocationRegistryRemote;
import de.citec.scm.remote.SceneRegistryRemote;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
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

    private final RemotePool remotePool;
    private TabPane registryTabPane, deviceRegistryTabPane;
    private Tab deviceRegistryTab, locationRegistryTab, sceneRegistryTab, agentRegistryTab, appRegistryTab;
    private Tab deviceClassTab, deviceConfigTab;
    private ProgressIndicator progressIndicator;
    private TreeTableView<Node> deviceClassTreeTableView, deviceConfigTreeTableView, locationConfigTreeTableView, sceneConfigTreeTableView, agentConfigTreeTableView, appConfigTreeTableView;

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

        progressIndicator = new ProgressIndicator();

        deviceClassTreeTableView = new RegistryTreeTableView(SendableType.DEVICE_CLASS);
        deviceConfigTreeTableView = new RegistryTreeTableView(SendableType.DEVICE_CONFIG);
        locationConfigTreeTableView = new RegistryTreeTableView(SendableType.LOCATION_CONFIG);
        sceneConfigTreeTableView = new RegistryTreeTableView(SendableType.SCENE_CONFIG);
        agentConfigTreeTableView = new RegistryTreeTableView(SendableType.AGENT_CONFIG);
        appConfigTreeTableView = new RegistryTreeTableView(SendableType.APP_CONFIG);

        deviceRegistryTabPane = new TabPane();
        deviceRegistryTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        deviceClassTab = new Tab("DeviceClass");
        deviceConfigTab = new Tab("DeviceConfig");
        deviceClassTab.setContent(deviceClassTreeTableView);
        deviceConfigTab.setContent(deviceConfigTreeTableView);
        deviceRegistryTabPane.getTabs().addAll(deviceClassTab, deviceConfigTab);

        logger.info("Init finished");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        logger.info("Starting");
        for (RSBRemoteService remote : remotePool.getRemotes()) {
            updateTab(remote);
        }

        Scene scene = new Scene(registryTabPane, RESOLUTION_WIDTH, 576);
        scene.getStylesheets().add("test.css");
        primaryStage.setTitle("Registry Editor");
        try {
            logger.info("Try to load icon...");
            primaryStage.getIcons().add(new Image("registry-editor.png"));
            logger.info("App icon loaded...");
        } catch (Exception ex) {
            ExceptionPrinter.printHistory(logger, new CouldNotPerformException("Could not load app icon!", ex));
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
                            throw ExceptionPrinter.printHistoryAndReturnThrowable(logger, ex);
                        }
                    } catch (Exception ex) {
                        throw ExceptionPrinter.printHistoryAndReturnThrowable(logger, ex);
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
                    tab.setContent(progressIndicator);
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

    private javafx.scene.Node fillTreeTableView(GeneratedMessage msg) throws InstantiationException {
        if (msg instanceof DeviceRegistry) {
            DeviceRegistry data = (DeviceRegistry) msg;
            deviceClassTreeTableView.setRoot(new GenericListContainer<>(DeviceRegistry.DEVICE_CLASS_FIELD_NUMBER, data.toBuilder()));
            FieldDescriptorGroup deviceClassId = new FieldDescriptorGroup(DeviceConfig.newBuilder(), DeviceConfig.DEVICE_CLASS_ID_FIELD_NUMBER);
            FieldDescriptorGroup locationId = new FieldDescriptorGroup(DeviceConfig.newBuilder(), DeviceConfig.PLACEMENT_CONFIG_FIELD_NUMBER, PlacementConfig.LOCATION_ID_FIELD_NUMBER);
            Descriptors.FieldDescriptor field = data.toBuilder().getDescriptorForType().findFieldByNumber(DeviceRegistry.DEVICE_CONFIG_FIELD_NUMBER);
            deviceConfigTreeTableView.setRoot(new GenericGroupContainer<>(field.getName(), field, data.toBuilder(), data.toBuilder().getDeviceConfigBuilderList(), deviceClassId, locationId));
            return deviceRegistryTabPane;
        } else if (msg instanceof LocationRegistry) {
            LocationRegistry data = (LocationRegistry) msg;
            locationConfigTreeTableView.setRoot(new GenericListContainer<>(LocationRegistry.LOCATION_CONFIG_FIELD_NUMBER, data.toBuilder()));
            return locationConfigTreeTableView;
        } else if (msg instanceof SceneRegistry) {
            SceneRegistry data = (SceneRegistry) msg;
            sceneConfigTreeTableView.setRoot(new GenericListContainer(SceneRegistry.SCENE_CONFIG_FIELD_NUMBER, data.toBuilder()));
            return sceneConfigTreeTableView;
        } else if (msg instanceof AppRegistry) {
            AppRegistry data = (AppRegistry) msg;
            appConfigTreeTableView.setRoot(new GenericListContainer(AppRegistry.APP_CONFIG_FIELD_NUMBER, data.toBuilder()));
            appRegistryTab.setContent(appConfigTreeTableView);
        } else if (msg instanceof AgentRegistry) {
            AgentRegistry data = (AgentRegistry) msg;
            agentConfigTreeTableView.setRoot(new GenericListContainer(AgentRegistry.AGENT_CONFIG_FIELD_NUMBER, data.toBuilder()));
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

    public static void setModified(boolean value) {
        modified = value;
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
}
