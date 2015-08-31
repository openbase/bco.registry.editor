/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re;

import de.citec.agm.remote.AgentRegistryRemote;
import de.citec.apm.remote.AppRegistryRemote;
import de.citec.csra.re.column.AgentConfigColumn;
import de.citec.csra.re.column.AppConfigColumn;
import de.citec.csra.re.column.DeviceClassColumn;
import de.citec.csra.re.column.DescriptorColumn;
import de.citec.csra.re.column.DeviceConfigColumn;
import de.citec.lm.remote.LocationRegistryRemote;
import de.citec.csra.re.column.LocationConfigColumn;
import de.citec.csra.re.column.SceneConfigColumn;
import de.citec.csra.re.column.UnitTemplateColumn;
import de.citec.csra.re.struct.node.AgentConfigContainer;
import de.citec.csra.re.struct.node.AppConfigContainer;
import de.citec.csra.re.struct.node.DeviceClassList;
import de.citec.csra.re.struct.node.DeviceConfigList;
import de.citec.csra.re.struct.node.GenericListContainer;
import de.citec.csra.re.struct.node.LocationConfigListContainer;
import de.citec.csra.re.struct.node.Node;
import de.citec.csra.re.struct.node.SceneConfigContainer;
import de.citec.csra.re.struct.node.UnitTemplateListContainer;
import de.citec.dm.remote.DeviceRegistryRemote;
import de.citec.jp.JPAgentRegistryScope;
import de.citec.jp.JPAppRegistryScope;
import de.citec.jp.JPDeviceRegistryScope;
import de.citec.jp.JPLocationRegistryScope;
import de.citec.jp.JPSceneRegistryScope;
import de.citec.jps.core.JPService;
import de.citec.jps.preset.JPReadOnly;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.jul.exception.ExceptionPrinter;
import de.citec.jul.exception.InstantiationException;
import de.citec.jul.pattern.Observable;
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
import rst.homeautomation.control.agent.AgentConfigType;
import rst.homeautomation.control.agent.AgentRegistryType;
import rst.homeautomation.control.app.AppConfigType;
import rst.homeautomation.control.app.AppRegistryType;
import rst.homeautomation.control.scene.SceneConfigType;
import rst.homeautomation.control.scene.SceneRegistryType;
import rst.homeautomation.device.DeviceClassType.DeviceClass;
import rst.homeautomation.device.DeviceConfigType.DeviceConfig;
import rst.homeautomation.device.DeviceRegistryType;
import rst.spatial.LocationConfigType.LocationConfig;
import rst.spatial.LocationRegistryType;

/**
 *
 * @author thuxohl
 */
public class RegistryEditor extends Application {

    private static final Logger logger = LoggerFactory.getLogger(RegistryEditor.class);

    public static final String APP_NAME = "RegistryView";
    public static final int RESOLUTION_WIDTH = 1024;

    private static boolean modified = false;
    private final DeviceRegistryRemote deviceRemote;
    private final LocationRegistryRemote locationRemote;
    private final SceneRegistryRemote sceneRemote;
    private final AgentRegistryRemote agentRemote;
    private final AppRegistryRemote appRemote;
    private TabPane registryTabPane, tabDeviceRegistryPane;
    private Tab tabDeviceRegistry, tabLocationRegistry, tabSceneRegistry, tabAgentRegistry, tabDeviceClass, tabDeviceConfig, tabAppRegistry, tabUnitTemplateRegistry;
    private ProgressIndicator progressDeviceRegistryIndicator;
    private ProgressIndicator progressLocationRegistryIndicator;
    private ProgressIndicator progressSceneRegistryIndicator;
    private ProgressIndicator progressAgentRegistryIndicator;
    private ProgressIndicator progressAppRegistryIndicator;
    private TreeTableView<Node> deviceClassTreeTableView;
    private TreeTableView<Node> deviceConfigTreeTableView;
    private TreeTableView<Node> locationConfigTreeTableView;
    private TreeTableView<Node> sceneConfigTreeTableView;
    private TreeTableView<Node> agentConfigTreeTableView;
    private TreeTableView<Node> appConfigTreeTableView;
    private TreeTableView<Node> unitTemplateTreeTableView;

    public RegistryEditor() throws InstantiationException {
        this.deviceRemote = new DeviceRegistryRemote();
        this.locationRemote = new LocationRegistryRemote();
        this.sceneRemote = new SceneRegistryRemote();
        this.agentRemote = new AgentRegistryRemote();
        this.appRemote = new AppRegistryRemote();
    }

    @Override
    public void init() throws Exception {
        super.init();
        deviceRemote.init(JPService.getProperty(JPDeviceRegistryScope.class).getValue());
        locationRemote.init(JPService.getProperty(JPLocationRegistryScope.class).getValue());
        sceneRemote.init(JPService.getProperty(JPSceneRegistryScope.class).getValue());
        agentRemote.init(JPService.getProperty(JPAgentRegistryScope.class).getValue());
        appRemote.init(JPService.getProperty(JPAppRegistryScope.class).getValue());

        registryTabPane = new TabPane();
        registryTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        tabDeviceRegistry = new Tab("DeviceRegistry");
        tabLocationRegistry = new Tab("LocationRegistry");
        tabSceneRegistry = new Tab("SceneRegistry");
        tabAgentRegistry = new Tab("AgentRegistry");
        tabAppRegistry = new Tab("AppRegistry");
        registryTabPane.getTabs().addAll(tabDeviceRegistry, tabLocationRegistry, tabSceneRegistry, tabAgentRegistry, tabAppRegistry);

        progressDeviceRegistryIndicator = new ProgressIndicator();
        progressLocationRegistryIndicator = new ProgressIndicator();
        progressSceneRegistryIndicator = new ProgressIndicator();
        progressAgentRegistryIndicator = new ProgressIndicator();
        progressAppRegistryIndicator = new ProgressIndicator();

        deviceClassTreeTableView = new TreeTableView<>();
        deviceClassTreeTableView.setEditable(true);
        deviceClassTreeTableView.setShowRoot(false);
        deviceClassTreeTableView.getColumns().addAll(getDescriptorColumn(), new DeviceClassColumn(deviceRemote));
        deviceClassTreeTableView.setContextMenu(new TreeTableViewContextMenu(deviceClassTreeTableView, DeviceClass.getDefaultInstance()));

        deviceConfigTreeTableView = new TreeTableView<>();
        deviceConfigTreeTableView.setEditable(true);
        deviceConfigTreeTableView.setShowRoot(false);
        deviceConfigTreeTableView.getColumns().addAll(getDescriptorColumn(), new DeviceConfigColumn(deviceRemote, locationRemote));
        deviceConfigTreeTableView.setContextMenu(new TreeTableViewContextMenu(deviceConfigTreeTableView, DeviceConfig.getDefaultInstance()));

        unitTemplateTreeTableView = new TreeTableView<>();
        unitTemplateTreeTableView.setEditable(true);
        unitTemplateTreeTableView.setShowRoot(false);
        unitTemplateTreeTableView.getColumns().addAll(getDescriptorColumn(), new UnitTemplateColumn(deviceRemote));

        tabDeviceRegistryPane = new TabPane();
        tabDeviceRegistryPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabDeviceClass = new Tab("DeviceClass");
        tabDeviceConfig = new Tab("DeviceConfig");
        tabUnitTemplateRegistry = new Tab("UnitTemplate");
        tabDeviceClass.setContent(deviceClassTreeTableView);
        tabDeviceConfig.setContent(deviceConfigTreeTableView);
        tabUnitTemplateRegistry.setContent(unitTemplateTreeTableView);
        tabDeviceRegistryPane.getTabs().addAll(tabDeviceClass, tabDeviceConfig, tabUnitTemplateRegistry);
        tabDeviceRegistry.setContent(tabDeviceRegistryPane);

        locationConfigTreeTableView = new TreeTableView<>();
        locationConfigTreeTableView.setEditable(true);
        locationConfigTreeTableView.setShowRoot(false);
        locationConfigTreeTableView.getColumns().addAll(getDescriptorColumn(), new LocationConfigColumn(locationRemote));
        locationConfigTreeTableView.setContextMenu(new TreeTableViewContextMenu(locationConfigTreeTableView, LocationConfig.getDefaultInstance()));

        tabLocationRegistry.setContent(locationConfigTreeTableView);

        sceneConfigTreeTableView = new TreeTableView<>();
        sceneConfigTreeTableView.setEditable(true);
        sceneConfigTreeTableView.setShowRoot(false);
        sceneConfigTreeTableView.getColumns().addAll(getDescriptorColumn(), new SceneConfigColumn(sceneRemote, locationRemote));
        sceneConfigTreeTableView.setContextMenu(new TreeTableViewContextMenu(sceneConfigTreeTableView, SceneConfigType.SceneConfig.getDefaultInstance()));

        tabSceneRegistry.setContent(sceneConfigTreeTableView);

        agentConfigTreeTableView = new TreeTableView<>();
        agentConfigTreeTableView.setEditable(true);
        agentConfigTreeTableView.setShowRoot(false);
        agentConfigTreeTableView.getColumns().addAll(getDescriptorColumn(), new AgentConfigColumn(agentRemote, locationRemote));
        agentConfigTreeTableView.setContextMenu(new TreeTableViewContextMenu(agentConfigTreeTableView, AgentConfigType.AgentConfig.getDefaultInstance()));

        tabAgentRegistry.setContent(agentConfigTreeTableView);

        appConfigTreeTableView = new TreeTableView<>();
        appConfigTreeTableView.setEditable(true);
        appConfigTreeTableView.setShowRoot(false);
        appConfigTreeTableView.getColumns().addAll(getDescriptorColumn(), new AppConfigColumn(appRemote, locationRemote));
        appConfigTreeTableView.setContextMenu(new TreeTableViewContextMenu(appConfigTreeTableView, AppConfigType.AppConfig.getDefaultInstance()));

        tabAppRegistry.setContent(appConfigTreeTableView);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        updateTabLocationRegistry();
        updateTabDeviceRegistry();
        updateTabSceneRegistry();
        updateTabAgentRegistry();
        updateTabAppRegistry();

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

        ExecutorService executerService = Executors.newFixedThreadPool(10);

        executerService.submit(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                try {
                    deviceRemote.activate();
                    deviceRemote.addObserver((Observable<DeviceRegistryType.DeviceRegistry> source, DeviceRegistryType.DeviceRegistry data) -> {
                        updateTabDeviceRegistry();
                    });

                    try {
                        deviceRemote.requestStatus();
                    } catch (CouldNotPerformException ex) {
                        ExceptionPrinter.printHistoryAndReturnThrowable(logger, ex);
                    }
                } catch (Exception ex) {
                    throw ExceptionPrinter.printHistoryAndReturnThrowable(logger, ex);
                }
                return null;
            }
        });

        executerService.submit(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                try {
                    locationRemote.activate();
                    locationRemote.addObserver((Observable<LocationRegistryType.LocationRegistry> source, LocationRegistryType.LocationRegistry data) -> {
                        updateTabLocationRegistry();
                    });

                    try {
                        locationRemote.requestStatus();
                    } catch (CouldNotPerformException ex) {
                        ExceptionPrinter.printHistoryAndReturnThrowable(logger, ex);
                    }
                } catch (Exception ex) {
                    throw ExceptionPrinter.printHistoryAndReturnThrowable(logger, ex);
                }
                return null;
            }
        });

        executerService.submit(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                try {
                    sceneRemote.activate();
                    sceneRemote.addObserver((Observable<SceneRegistryType.SceneRegistry> source, SceneRegistryType.SceneRegistry data) -> {
                        updateTabSceneRegistry();
                    });

                    try {
                        sceneRemote.requestStatus();
                    } catch (CouldNotPerformException ex) {
                        ExceptionPrinter.printHistoryAndReturnThrowable(logger, ex);
                    }
                } catch (Exception ex) {
                    throw ExceptionPrinter.printHistoryAndReturnThrowable(logger, ex);
                }
                return null;
            }
        });

        executerService.submit(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                try {
                    agentRemote.activate();
                    agentRemote.addObserver((Observable<AgentRegistryType.AgentRegistry> source, AgentRegistryType.AgentRegistry data) -> {
                        updateTabAgentRegistry();
                    });

                    try {
                        agentRemote.requestStatus();
                    } catch (CouldNotPerformException ex) {
                        ExceptionPrinter.printHistoryAndReturnThrowable(logger, ex);
                    }
                } catch (Exception ex) {
                    throw ExceptionPrinter.printHistoryAndReturnThrowable(logger, ex);
                }
                return null;
            }
        });

        executerService.submit(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                try {
                    appRemote.activate();
                    appRemote.addObserver((Observable<AppRegistryType.AppRegistry> source, AppRegistryType.AppRegistry data) -> {
                        updateTabAppRegistry();
                    });

                    try {
                        appRemote.requestStatus();
                    } catch (CouldNotPerformException ex) {
                        ExceptionPrinter.printHistoryAndReturnThrowable(logger, ex);
                    }
                } catch (Exception ex) {
                    throw ExceptionPrinter.printHistoryAndReturnThrowable(logger, ex);
                }
                return null;
            }
        });
    }

    @Override
    public void stop() throws Exception {
        deviceRemote.shutdown();
        locationRemote.shutdown();
        sceneRemote.shutdown();
        agentRemote.shutdown();
        appRemote.shutdown();
        super.stop();
    }

    public DescriptorColumn getDescriptorColumn() {
        return new DescriptorColumn(deviceRemote, locationRemote, sceneRemote, agentRemote, appRemote);

    }

    private void updateTabDeviceRegistry() {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                if (!deviceRemote.isConnected()) {
                    tabDeviceRegistry.setContent(progressDeviceRegistryIndicator);
                    return;
                }
                try {
                    if (!modified) {
                        DeviceRegistryType.DeviceRegistry data = deviceRemote.getData();
                        deviceClassTreeTableView.setRoot(new DeviceClassList(data.toBuilder()));
                        deviceConfigTreeTableView.setRoot(new DeviceConfigList(data.toBuilder()));
                        unitTemplateTreeTableView.setRoot(new UnitTemplateListContainer(data.toBuilder()));
                        tabDeviceRegistry.setContent(tabDeviceRegistryPane);
                    }

                } catch (CouldNotPerformException ex) {
                    logger.error("Device registry not available!", ex);
                    tabDeviceRegistry.setContent(new Label("Error: " + ex.getMessage()));
                }
            }
        });
    }

    private void updateTabLocationRegistry() {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                if (!locationRemote.isConnected()) {
                    tabLocationRegistry.setContent(progressLocationRegistryIndicator);
                    return;
                }
                try {
                    if (!modified) {
                        LocationRegistryType.LocationRegistry data = locationRemote.getData();
                        locationConfigTreeTableView.setRoot(new LocationConfigListContainer(data.toBuilder()));
                        tabLocationRegistry.setContent(locationConfigTreeTableView);
                    }
                } catch (CouldNotPerformException ex) {
                    logger.error("Location registry not available!", ex);
                    tabLocationRegistry.setContent(new Label("Error: " + ex.getMessage()));
                }
            }
        });
    }

    private void updateTabSceneRegistry() {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                if (!sceneRemote.isConnected()) {
                    tabSceneRegistry.setContent(progressSceneRegistryIndicator);
                    return;
                }
                try {
                    if (!modified) {
                        SceneRegistryType.SceneRegistry data = sceneRemote.getData();
                        sceneConfigTreeTableView
                                .setRoot(new GenericListContainer(SceneRegistryType.SceneRegistry.SCENE_CONFIG_FIELD_NUMBER, data.toBuilder(), SceneConfigContainer.class));
                        tabSceneRegistry.setContent(sceneConfigTreeTableView);
                    }
                } catch (CouldNotPerformException ex) {
                    logger.error("Scene registry not available!", ex);
                    tabSceneRegistry.setContent(new Label("Error: " + ex.getMessage()));
                }
            }
        });
    }

    private void updateTabAgentRegistry() {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                if (!agentRemote.isConnected()) {
                    tabAgentRegistry.setContent(progressAgentRegistryIndicator);
                    return;
                }
                try {
                    if (!modified) {
                        AgentRegistryType.AgentRegistry data = agentRemote.getData();
                        agentConfigTreeTableView
                                .setRoot(new GenericListContainer(AgentRegistryType.AgentRegistry.AGENT_CONFIG_FIELD_NUMBER, data.toBuilder(), AgentConfigContainer.class));
                        tabAgentRegistry.setContent(agentConfigTreeTableView);
                    }
                } catch (CouldNotPerformException ex) {
                    logger.error("Agent registry not available!", ex);
                    tabAgentRegistry.setContent(new Label("Error: " + ex.getMessage()));
                }
            }
        });
    }

    private void updateTabAppRegistry() {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                if (!appRemote.isConnected()) {
                    tabAppRegistry.setContent(progressAppRegistryIndicator);
                    return;
                }
                try {
                    if (!modified) {
                        AppRegistryType.AppRegistry data = appRemote.getData();
                        appConfigTreeTableView
                                .setRoot(new GenericListContainer(AppRegistryType.AppRegistry.APP_CONFIG_FIELD_NUMBER, data.toBuilder(), AppConfigContainer.class));
                        tabAppRegistry.setContent(appConfigTreeTableView);
                    }
                } catch (CouldNotPerformException ex) {
                    logger.error("App registry not available!", ex);
                    tabAppRegistry.setContent(new Label("Error: " + ex.getMessage()));
                }
            }
        });
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
        JPService.registerProperty(JPReadOnly.class);
        JPService.registerProperty(JPDeviceRegistryScope.class);
        JPService.registerProperty(JPLocationRegistryScope.class);
        JPService.registerProperty(JPSceneRegistryScope.class);
        JPService.registerProperty(JPAgentRegistryScope.class);
        JPService.registerProperty(JPAppRegistryScope.class);
        JPService.parseAndExitOnError(args);

        launch(args);
    }
}
