/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re;

import static de.citec.csra.dm.DeviceManager.DEFAULT_SCOPE;
import de.citec.csra.dm.remote.DeviceRegistryRemote;
import de.citec.csra.re.struct.node.DeviceClassList;
import de.citec.csra.re.struct.node.DeviceClassContainer;
import de.citec.csra.re.struct.node.DeviceConfigContainer;
import de.citec.csra.re.struct.node.DeviceConfigList;
import de.citec.csra.re.struct.node.Node;
import de.citec.jp.JPDeviceRegistryScope;
import de.citec.jps.core.JPService;
import de.citec.jul.exception.NotAvailableException;
import de.citec.jul.pattern.Observable;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeTableView;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.homeautomation.device.DeviceClassType;
import rst.homeautomation.device.DeviceClassType.DeviceClass;
import rst.homeautomation.device.DeviceConfigType;
import rst.homeautomation.device.DeviceConfigType.DeviceConfig;
import rst.homeautomation.device.DeviceRegistryType;

/**
 *
 * @author thuxohl
 */
public class RegistryEditor extends Application {

    private static final Logger logger = LoggerFactory.getLogger(RegistryEditor.class);

    public static final String APP_NAME = "RegistryView";
    public static final int RESOLUTION_WIDTH = 1024;

    private final DeviceRegistryRemote remote;
    private TabPane registryTabPane, tabDeviceRegistryPane;
    private Tab tabDeviceRegistry, tabLocationRegistry, tabDeviceClass, tabDeviceConfig;
    private ProgressIndicator progressDeviceRegistryIndicator;
    private ProgressIndicator progressLocationRegistryIndicator;
    private TreeTableView<Node> deviceClassTreeTableView;
    private TreeTableView<Node> deviceConfigTreeTableView;

    public RegistryEditor() {
        this.remote = new DeviceRegistryRemote();
    }

    @Override
    public void init() throws Exception {
        super.init();
        remote.init(JPService.getProperty(JPDeviceRegistryScope.class).getValue());

        registryTabPane = new TabPane();
        registryTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        tabDeviceRegistry = new Tab("DeviceRegistry");
        tabLocationRegistry = new Tab("LocationRegistry");
        registryTabPane.getTabs().addAll(tabDeviceRegistry, tabLocationRegistry);

        progressDeviceRegistryIndicator = new ProgressIndicator();
        progressLocationRegistryIndicator = new ProgressIndicator();
        tabLocationRegistry.setContent(progressLocationRegistryIndicator);

        deviceClassTreeTableView = new TreeTableView<>();
        deviceClassTreeTableView.setEditable(true);
        deviceClassTreeTableView.setShowRoot(false);
        deviceClassTreeTableView.getColumns().addAll(new DescriptorColumn(remote), new DeviceClassColumn(remote));
        deviceClassTreeTableView.setContextMenu(new TreeTableViewContextMenu(deviceClassTreeTableView, DeviceClass.getDefaultInstance()));

        deviceConfigTreeTableView = new TreeTableView<>();
        deviceConfigTreeTableView.setEditable(true);
        deviceConfigTreeTableView.setShowRoot(false);
        deviceConfigTreeTableView.getColumns().addAll(new DescriptorColumn(remote), new DeviceConfigColumn(remote));
        deviceConfigTreeTableView.setContextMenu(new TreeTableViewContextMenu(deviceConfigTreeTableView, DeviceConfig.getDefaultInstance()));

        tabDeviceRegistryPane = new TabPane();
        tabDeviceRegistryPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabDeviceClass = new Tab("DeviceClass");
        tabDeviceConfig = new Tab("DeviceConfig");
        tabDeviceClass.setContent(deviceClassTreeTableView);
        tabDeviceConfig.setContent(deviceConfigTreeTableView);
        tabDeviceRegistryPane.getTabs().addAll(tabDeviceClass, tabDeviceConfig);
        tabDeviceRegistry.setContent(tabDeviceRegistryPane);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        remote.activate();
        remote.addObserver((Observable<DeviceRegistryType.DeviceRegistry> source, DeviceRegistryType.DeviceRegistry data) -> {
            updateDynamicNodes();
        });
        remote.requestStatus();
        Scene scene = new Scene(registryTabPane, RESOLUTION_WIDTH, 576);
        primaryStage.setTitle("Registry Editor");
//        primaryStage.setFullScreen(true);
//        primaryStage.setFullScreenExitKeyCombination(KeyCombination.ALT_ANY);
        primaryStage.setScene(scene);
        primaryStage.show();

        updateDynamicNodes();
    }

    @Override
    public void stop() throws Exception {
        remote.shutdown();
        super.stop();
    }

    private void updateDynamicNodes() {
        Platform.runLater(() -> {
            if (!remote.isConnected()) {
                tabDeviceRegistry.setContent(progressDeviceRegistryIndicator);
                return;
            }
            try {
                DeviceRegistryType.DeviceRegistry data = remote.getData();
                deviceClassTreeTableView.setRoot(new DeviceClassList(data.toBuilder()));
                deviceConfigTreeTableView.setRoot(new DeviceConfigList(data.toBuilder()));
                tabDeviceRegistry.setContent(tabDeviceRegistryPane);
                
            } catch (NotAvailableException ex) {
                logger.error("Device classes not available!", ex);
                tabDeviceRegistry.setContent(new Label("Error: " + ex.getMessage()));
            }
        });

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        logger.info("Start " + APP_NAME + "...");

        /* Setup JPService */
        JPService.setApplicationName(APP_NAME);
        JPService.registerProperty(JPDeviceRegistryScope.class, DEFAULT_SCOPE);
        launch(args);
    }
}
