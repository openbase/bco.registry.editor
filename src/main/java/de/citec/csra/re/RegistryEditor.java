/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re;

import de.citec.csra.re.column.DeviceClassColumn;
import de.citec.csra.re.column.DescriptorColumn;
import de.citec.csra.re.column.DeviceConfigColumn;
import de.citec.csra.dm.remote.DeviceRegistryRemote;
import de.citec.csra.lm.remote.LocationRegistryRemote;
import de.citec.csra.re.column.LocationConfigColumn;
import de.citec.csra.re.struct.node.DeviceClassList;
import de.citec.csra.re.struct.node.DeviceConfigList;
import de.citec.csra.re.struct.node.LocationConfigListContainer;
import de.citec.csra.re.struct.node.Node;
import de.citec.jp.JPDeviceRegistryScope;
import de.citec.jp.JPLocationRegistryScope;
import de.citec.jps.core.JPService;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.jul.exception.ExceptionPrinter;
import de.citec.jul.exception.InstantiationException;
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

	private final DeviceRegistryRemote deviceRemote;
	private final LocationRegistryRemote locationRemote;
	private TabPane registryTabPane, tabDeviceRegistryPane;
	private Tab tabDeviceRegistry, tabLocationRegistry, tabDeviceClass, tabDeviceConfig;
	private ProgressIndicator progressDeviceRegistryIndicator;
	private ProgressIndicator progressLocationRegistryIndicator;
	private TreeTableView<Node> deviceClassTreeTableView;
	private TreeTableView<Node> deviceConfigTreeTableView;
	private TreeTableView<Node> locationConfigTreeTableView;

	public RegistryEditor() throws InstantiationException {
		this.deviceRemote = new DeviceRegistryRemote();
		this.locationRemote = new LocationRegistryRemote();
	}

	@Override
	public void init() throws Exception {
		super.init();
		deviceRemote.init(JPService.getProperty(JPDeviceRegistryScope.class).getValue());
		locationRemote.init(JPService.getProperty(JPLocationRegistryScope.class).getValue());

		registryTabPane = new TabPane();
		registryTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

		tabDeviceRegistry = new Tab("DeviceRegistry");
		tabLocationRegistry = new Tab("LocationRegistry");
		registryTabPane.getTabs().addAll(tabDeviceRegistry, tabLocationRegistry);

		progressDeviceRegistryIndicator = new ProgressIndicator();
		progressLocationRegistryIndicator = new ProgressIndicator();

		deviceClassTreeTableView = new TreeTableView<>();
		deviceClassTreeTableView.setEditable(true);
		deviceClassTreeTableView.setShowRoot(false);
		deviceClassTreeTableView.getColumns().addAll(new DescriptorColumn(deviceRemote, locationRemote), new DeviceClassColumn(deviceRemote, locationRemote));
		deviceClassTreeTableView.setContextMenu(new TreeTableViewContextMenu(deviceClassTreeTableView, DeviceClass.getDefaultInstance()));

		deviceConfigTreeTableView = new TreeTableView<>();
		deviceConfigTreeTableView.setEditable(true);
		deviceConfigTreeTableView.setShowRoot(false);
		deviceConfigTreeTableView.getColumns().addAll(new DescriptorColumn(deviceRemote, locationRemote), new DeviceConfigColumn(deviceRemote, locationRemote));
		deviceConfigTreeTableView.setContextMenu(new TreeTableViewContextMenu(deviceConfigTreeTableView, DeviceConfig.getDefaultInstance()));

		tabDeviceRegistryPane = new TabPane();
		tabDeviceRegistryPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
		tabDeviceClass = new Tab("DeviceClass");
		tabDeviceConfig = new Tab("DeviceConfig");
		tabDeviceClass.setContent(deviceClassTreeTableView);
		tabDeviceConfig.setContent(deviceConfigTreeTableView);
		tabDeviceRegistryPane.getTabs().addAll(tabDeviceClass, tabDeviceConfig);
		tabDeviceRegistry.setContent(tabDeviceRegistryPane);

		locationConfigTreeTableView = new TreeTableView<>();
		locationConfigTreeTableView.setEditable(true);
		locationConfigTreeTableView.setShowRoot(false);
		locationConfigTreeTableView.getColumns().addAll(new DescriptorColumn(deviceRemote, locationRemote), new LocationConfigColumn(deviceRemote, locationRemote));
		locationConfigTreeTableView.setContextMenu(new TreeTableViewContextMenu(locationConfigTreeTableView, LocationConfig.getDefaultInstance()));

		tabLocationRegistry.setContent(locationConfigTreeTableView);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			deviceRemote.activate();
			deviceRemote.addObserver((Observable<DeviceRegistryType.DeviceRegistry> source, DeviceRegistryType.DeviceRegistry data) -> {
				updateTabDeviceRegistry();
			});

			try {
				deviceRemote.requestStatus();
			} catch (CouldNotPerformException ex) {
				ExceptionPrinter.printHistory(logger, ex);
			}

			locationRemote.activate();
			locationRemote.addObserver((Observable<LocationRegistryType.LocationRegistry> source, LocationRegistryType.LocationRegistry data) -> {
				updateTabLocationRegistry();
			});

			locationRemote.requestStatus();

			Scene scene = new Scene(registryTabPane, RESOLUTION_WIDTH, 576);
			primaryStage.setTitle("Registry Editor");
//        primaryStage.setFullScreen(true);
//        primaryStage.setFullScreenExitKeyCombination(KeyCombination.ALT_ANY);
			primaryStage.setScene(scene);
			primaryStage.show();

			updateTabLocationRegistry();
			updateTabDeviceRegistry();
			logger.info(APP_NAME + " successfully started.");
		} catch (Exception ex) {
			throw ExceptionPrinter.printHistory(logger, ex);
		}
	}

	@Override
	public void stop() throws Exception {
		deviceRemote.shutdown();
		locationRemote.shutdown();
		// TODO fix that the registry editor won't shut down
		super.stop();
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
					DeviceRegistryType.DeviceRegistry data = deviceRemote.getData();
					deviceClassTreeTableView.setRoot(new DeviceClassList(data.toBuilder()));
					deviceConfigTreeTableView.setRoot(new DeviceConfigList(data.toBuilder()));
					tabDeviceRegistry.setContent(tabDeviceRegistryPane);

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
					LocationRegistryType.LocationRegistry data = locationRemote.getData();
					LocationRegistryType.LocationRegistry.Builder rootLocations = LocationRegistryType.LocationRegistry.newBuilder();
					for (LocationConfig locationConfig : data.getLocationConfigsList()) {
						if (locationConfig.getRoot()) {
							rootLocations.addLocationConfigs(locationConfig);
						}
					}
					locationConfigTreeTableView.setRoot(new LocationConfigListContainer(rootLocations));
				} catch (CouldNotPerformException ex) {
					logger.error("Location registry not available!", ex);
					tabLocationRegistry.setContent(new Label("Error: " + ex.getMessage()));
				}
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
		JPService.registerProperty(JPDeviceRegistryScope.class);
		JPService.registerProperty(JPLocationRegistryScope.class);
		launch(args);
	}
}
