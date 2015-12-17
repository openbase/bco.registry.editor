/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.regedit;

import de.citec.csra.regedit.util.SendableType;
import de.citec.csra.regedit.view.GlobalTextArea;
import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessage;
import de.citec.agm.remote.AgentRegistryRemote;
import de.citec.apm.remote.AppRegistryRemote;
import de.citec.csra.regedit.struct.GenericGroupContainer;
import de.citec.csra.regedit.struct.GenericListContainer;
import de.citec.csra.regedit.util.RemotePool;
import de.citec.csra.regedit.view.RegistryTreeTableView;
import de.citec.csra.regedit.view.provider.DeviceClassItemDescriptorProvider;
import de.citec.csra.regedit.view.provider.FieldDescriptorGroup;
import de.citec.csra.regedit.view.provider.LocationItemDescriptorProvider;
import de.citec.csra.regedit.view.provider.TreeItemDescriptorProvider;
import de.citec.dm.remote.DeviceRegistryRemote;
import de.citec.jp.JPAgentRegistryScope;
import de.citec.jp.JPAppRegistryScope;
import de.citec.jp.JPDeviceRegistryScope;
import de.citec.jp.JPLocationRegistryScope;
import de.citec.jp.JPSceneRegistryScope;
import org.dc.jps.core.JPService;
import org.dc.jps.preset.JPReadOnly;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.jul.exception.printer.ExceptionPrinter;
import de.citec.jul.exception.InstantiationException;
import de.citec.jul.exception.printer.LogLevel;
import de.citec.jul.extension.rsb.com.RSBRemoteService;
import de.citec.jul.pattern.Observable;
import de.citec.lm.remote.LocationRegistryRemote;
import de.citec.scm.remote.SceneRegistryRemote;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private TabPane registryTabPane, deviceRegistryTabPane, locationRegistryTabPane;
    private Tab deviceRegistryTab, locationRegistryTab, sceneRegistryTab, agentRegistryTab, appRegistryTab;
    private Tab deviceClassTab, deviceConfigTab, unitTemplateTab;
    private Tab locationConfigTab, connectionConfigTab;
    private ProgressIndicator deviceRegistryProgressIndicator, locationRegistryprogressIndicator, appRegistryprogressIndicator, agentRegistryProgressIndicator, sceneRegistryprogressIndicator;
    private RegistryTreeTableView deviceClassTreeTableView, deviceConfigTreeTableView, locationConfigTreeTableView, connectionConfigTreeTableView, sceneConfigTreeTableView, agentConfigTreeTableView, appConfigTreeTableView, unitTemplateTreeTableView;
    private final Map<String, Boolean> intialized;

    public RegistryEditor() throws InstantiationException {
        remotePool = RemotePool.getInstance();
        intialized = new HashMap<>();
        intialized.put(DeviceRegistry.class.getSimpleName(), Boolean.FALSE);
        intialized.put(LocationRegistry.class.getSimpleName(), Boolean.FALSE);
        intialized.put(AgentRegistry.class.getSimpleName(), Boolean.FALSE);
        intialized.put(AppRegistry.class.getSimpleName(), Boolean.FALSE);
        intialized.put(SceneRegistry.class.getSimpleName(), Boolean.FALSE);
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
        connectionConfigTreeTableView = new RegistryTreeTableView(SendableType.CONNECTION_CONFIG);
        sceneConfigTreeTableView = new RegistryTreeTableView(SendableType.SCENE_CONFIG);
        agentConfigTreeTableView = new RegistryTreeTableView(SendableType.AGENT_CONFIG);
        appConfigTreeTableView = new RegistryTreeTableView(SendableType.APP_CONFIG);
        unitTemplateTreeTableView = new RegistryTreeTableView(SendableType.UNIT_TEMPLATE);

        deviceRegistryTabPane = new TabPane();
        deviceRegistryTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        deviceClassTab = new Tab("DeviceClass");
        deviceConfigTab = new Tab("DeviceConfig");
        unitTemplateTab = new Tab("UnitTemplate");
        deviceClassTab.setContent(deviceClassTreeTableView.getVBox());
        deviceConfigTab.setContent(deviceConfigTreeTableView.getVBox());
        unitTemplateTab.setContent(unitTemplateTreeTableView.getVBox());
        deviceRegistryTabPane.getTabs().addAll(deviceClassTab, deviceConfigTab, unitTemplateTab);

        locationRegistryTabPane = new TabPane();
        locationRegistryTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        locationConfigTab = new Tab("LocationConfig");
        locationConfigTab.setContent(locationConfigTreeTableView.getVBox());
        connectionConfigTab = new Tab("ConnectionConfig");
        connectionConfigTab.setContent(connectionConfigTreeTableView.getVBox());
        locationRegistryTabPane.getTabs().addAll(connectionConfigTab, locationConfigTab);

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

        deviceClassTreeTableView.addHeightProperty(scene.heightProperty());
        deviceConfigTreeTableView.addHeightProperty(scene.heightProperty());
        locationConfigTreeTableView.addHeightProperty(scene.heightProperty());
        connectionConfigTreeTableView.addHeightProperty(scene.heightProperty());
        sceneConfigTreeTableView.addHeightProperty(scene.heightProperty());
        agentConfigTreeTableView.addHeightProperty(scene.heightProperty());
        appConfigTreeTableView.addHeightProperty(scene.heightProperty());
        unitTemplateTreeTableView.addHeightProperty(scene.heightProperty());

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

        final List<Future<Void>> registrationFutures = new ArrayList<>();

        for (RSBRemoteService remote : remotePool.getRemotes()) {
            registrationFutures.add(executerService.submit(new Callable<Void>() {

                @Override
                public Void call() throws Exception {
                    try {
                        remote.addObserver((Observable source, Object data) -> {
                            updateTab(remote);
                        });
                        remote.activate();
                    } catch (InterruptedException | CouldNotPerformException ex) {
                        printException(ex, logger, LogLevel.ERROR);
                    }
                    return null;
                }
            }));
        }

        for (Future future : registrationFutures) {
            future.get();
        }
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
                if (tab.equals(locationRegistryTab)) {
                    int i = 0;
                }
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
        }

        return null;
    }

    private javafx.scene.Node updateTreeTableView(GeneratedMessage msg) throws InstantiationException, CouldNotPerformException {
        if (msg instanceof DeviceRegistry) {
            DeviceRegistry data = (DeviceRegistry) msg;
            deviceClassTreeTableView.update(data.getDeviceClassList());
            deviceConfigTreeTableView.update(data.getDeviceConfigList());
            unitTemplateTreeTableView.update(data.getUnitTemplateList());
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
        JPService.parseAndExitOnError(args);

        launch(args);
    }

    public static void printException(Throwable th, Logger logger, LogLevel logLevel) {
        GlobalTextArea.getInstance().printException(th);
        ExceptionPrinter.printHistory(th, logger, logLevel);
    }
}
