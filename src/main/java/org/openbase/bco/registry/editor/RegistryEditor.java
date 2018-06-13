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
import org.openbase.bco.registry.activity.lib.jp.JPActivityRegistryScope;
import org.openbase.bco.registry.activity.remote.ActivityRegistryRemote;
import org.openbase.bco.registry.clazz.lib.jp.JPClassRegistryScope;
import org.openbase.bco.registry.clazz.remote.ClassRegistryRemote;
import org.openbase.bco.registry.editor.struct.GenericGroupContainer;
import org.openbase.bco.registry.editor.struct.GenericListContainer;
import org.openbase.bco.registry.editor.util.SendableType;
import org.openbase.bco.registry.editor.visual.GlobalTextArea;
import org.openbase.bco.registry.editor.visual.RegistryTreeTableView;
import org.openbase.bco.registry.editor.visual.TabPaneWithClearing;
import org.openbase.bco.registry.editor.visual.provider.*;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.bco.registry.template.lib.jp.JPTemplateRegistryScope;
import org.openbase.bco.registry.template.remote.TemplateRegistryRemote;
import org.openbase.bco.registry.unit.lib.jp.JPUnitRegistryScope;
import org.openbase.bco.registry.unit.remote.UnitRegistryRemote;
import org.openbase.jps.core.JPService;
import org.openbase.jps.preset.JPReadOnly;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.jul.extension.rsb.com.jp.JPRSBHost;
import org.openbase.jul.extension.rsb.com.jp.JPRSBPort;
import org.openbase.jul.extension.rsb.com.jp.JPRSBTransport;
import org.openbase.jul.pattern.Remote.ConnectionState;
import org.openbase.jul.schedule.GlobalCachedExecutorService;
import org.openbase.jul.storage.registry.RegistryRemote;
import org.openbase.jul.visual.swing.image.ImageLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.registry.ActivityRegistryDataType.ActivityRegistryData;
import rst.domotic.registry.ClassRegistryDataType.ClassRegistryData;
import rst.domotic.registry.TemplateRegistryDataType.TemplateRegistryData;
import rst.domotic.registry.UnitRegistryDataType.UnitRegistryData;
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

    private final List<RegistryTreeTableView> registryTreeTableViewList = new ArrayList<>();

    private MenuBar menuBar;
    private MenuItem resyncMenuItem;
    private TabPaneWithClearing globalTabPane, unitRegistryTabPane, classRegistryTabPane,
            templateRegistryTabPane, activityRegistryTabPane;
    private Tab classRegistryTab, templateRegistryTab, unitRegistryTab, activityRegistryTab;
    private ProgressIndicator classRegistryProgressIndicator, templateRegistryProgressIndicator,
            unitRegistryProgressIndicator, activityRegistryProgressIndicator;

    private RegistryTreeTableView deviceClassTreeTableView, appClassTreeTableView, agentClassTreeTableView;
    private RegistryTreeTableView unitTemplateTreeTableView, serviceTemplateTreeTableView, activityTemplateTreeTableView;
    private RegistryTreeTableView dalUnitConfigTreeTableView, deviceConfigTreeTableView, locationConfigTreeTableView,
            connectionConfigTreeTableView, unitGroupConfigTreeTableView, appConfigTreeTableView,
            agentConfigTreeTableView, sceneConfigTreeTableView, userConfigTreeTableView, authorizationGroupConfigTreeTableView;
    private RegistryTreeTableView activityConfigTreeTableView;

    private Scene scene;
    private Map<String, Boolean> initialized;

    @Override
    public void init() throws Exception {
        super.init();
        initialized = new HashMap<>();
        initialized.put(ClassRegistryData.class.getSimpleName(), Boolean.FALSE);
        initialized.put(TemplateRegistryData.class.getSimpleName(), Boolean.FALSE);
        initialized.put(UnitRegistryData.class.getSimpleName(), Boolean.FALSE);
        initialized.put(ActivityRegistryData.class.getSimpleName(), Boolean.FALSE);

        globalTabPane = new TabPaneWithClearing();
        globalTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        templateRegistryTab = new Tab("TemplateRegistry");
        classRegistryTab = new Tab("ClassRegistry");
        unitRegistryTab = new Tab("UnitRegistry");
        activityRegistryTab = new Tab("ActivityRegistry");

        globalTabPane.getTabs().addAll(unitRegistryTab, classRegistryTab, templateRegistryTab, activityRegistryTab);

        classRegistryProgressIndicator = new ProgressIndicator();
        templateRegistryProgressIndicator = new ProgressIndicator();
        unitRegistryProgressIndicator = new ProgressIndicator();
        activityRegistryProgressIndicator = new ProgressIndicator();

        unitTemplateTreeTableView = new RegistryTreeTableView(SendableType.UNIT_TEMPLATE, this);
        serviceTemplateTreeTableView = new RegistryTreeTableView(SendableType.SERVICE_TEMPLATE, this);
        activityTemplateTreeTableView = new RegistryTreeTableView(SendableType.ACTIVITY_TEMPLATE, this);

        deviceClassTreeTableView = new RegistryTreeTableView(SendableType.DEVICE_CLASS, this);
        appClassTreeTableView = new RegistryTreeTableView(SendableType.APP_CLASS, this);
        agentClassTreeTableView = new RegistryTreeTableView(SendableType.AGENT_CLASS, this);

        dalUnitConfigTreeTableView = new RegistryTreeTableView(SendableType.UNIT_CONFIG, this);
        deviceConfigTreeTableView = new RegistryTreeTableView(SendableType.DEVICE_CONFIG, this);
        locationConfigTreeTableView = new RegistryTreeTableView(SendableType.LOCATION_CONFIG, this);
        connectionConfigTreeTableView = new RegistryTreeTableView(SendableType.CONNECTION_CONFIG, this);
        agentConfigTreeTableView = new RegistryTreeTableView(SendableType.AGENT_CONFIG, this);
        appConfigTreeTableView = new RegistryTreeTableView(SendableType.APP_CONFIG, this);
        sceneConfigTreeTableView = new RegistryTreeTableView(SendableType.SCENE_CONFIG, this);
        unitGroupConfigTreeTableView = new RegistryTreeTableView(SendableType.UNIT_GROUP_CONFIG, this);
        userConfigTreeTableView = new RegistryTreeTableView(SendableType.USER_CONFIG, this);
        authorizationGroupConfigTreeTableView = new RegistryTreeTableView(SendableType.AUTHORIZATION_GROUP_CONFIG, this);

        activityConfigTreeTableView = new RegistryTreeTableView(SendableType.ACTIVITY_CONFIG, this);

        registryTreeTableViewList.add(unitTemplateTreeTableView);
        registryTreeTableViewList.add(serviceTemplateTreeTableView);
        registryTreeTableViewList.add(activityTemplateTreeTableView);
        registryTreeTableViewList.add(deviceClassTreeTableView);
        registryTreeTableViewList.add(appClassTreeTableView);
        registryTreeTableViewList.add(agentClassTreeTableView);
        registryTreeTableViewList.add(dalUnitConfigTreeTableView);
        registryTreeTableViewList.add(deviceConfigTreeTableView);
        registryTreeTableViewList.add(locationConfigTreeTableView);
        registryTreeTableViewList.add(connectionConfigTreeTableView);
        registryTreeTableViewList.add(agentConfigTreeTableView);
        registryTreeTableViewList.add(appConfigTreeTableView);
        registryTreeTableViewList.add(sceneConfigTreeTableView);
        registryTreeTableViewList.add(unitGroupConfigTreeTableView);
        registryTreeTableViewList.add(userConfigTreeTableView);
        registryTreeTableViewList.add(authorizationGroupConfigTreeTableView);
        registryTreeTableViewList.add(activityConfigTreeTableView);

        unitRegistryTabPane = new TabPaneWithClearing();
        unitRegistryTabPane.addTab("DALUnitConfig", dalUnitConfigTreeTableView);
        unitRegistryTabPane.addTab("DeviceUnitConfig", deviceConfigTreeTableView);
        unitRegistryTabPane.addTab("LocationUnitConfig", locationConfigTreeTableView);
        unitRegistryTabPane.addTab("ConnectionUnitConfig", connectionConfigTreeTableView);
        unitRegistryTabPane.addTab("AgentUnitConfig", agentConfigTreeTableView);
        unitRegistryTabPane.addTab("AppUnitConfig", appConfigTreeTableView);
        unitRegistryTabPane.addTab("SceneUnitConfig", sceneConfigTreeTableView);
        unitRegistryTabPane.addTab("UnitGroupUnitConfig", unitGroupConfigTreeTableView);
        unitRegistryTabPane.addTab("UserUnitConfig", userConfigTreeTableView);
        unitRegistryTabPane.addTab("AuthorizationUnitConfig", authorizationGroupConfigTreeTableView);

        classRegistryTabPane = new TabPaneWithClearing();
        classRegistryTabPane.addTab("DeviceClass", deviceClassTreeTableView);
        classRegistryTabPane.addTab("AppClass", appClassTreeTableView);
        classRegistryTabPane.addTab("AgentClass", agentClassTreeTableView);

        templateRegistryTabPane = new TabPaneWithClearing();
        templateRegistryTabPane.addTab("UnitTemplate", unitTemplateTreeTableView);
        templateRegistryTabPane.addTab("ServiceTemplate", serviceTemplateTreeTableView);
        templateRegistryTabPane.addTab("ActivityTemplate", activityTemplateTreeTableView);

        activityRegistryTabPane = new TabPaneWithClearing();
        activityRegistryTabPane.addTab("ActivityConfig", activityConfigTreeTableView);

        resyncMenuItem = new MenuItem("Resync");
        resyncMenuItem.setOnAction((ActionEvent event) -> {
            LOGGER.info("resync triggered...");

            try {
                for (RegistryRemote registryRemote : Registries.getRegistries()) {
                    LOGGER.info("request data for " + registryRemote);
                    registryRemote.requestData();
                }
            } catch (CouldNotPerformException ex) {
                printException(ex, LOGGER, LogLevel.ERROR);
            }
        });

        final Menu registryMenu = new Menu("Registry");

        registryMenu.getItems().addAll(resyncMenuItem);
        menuBar = new MenuBar();
        menuBar.getMenus().add(registryMenu);

        scene = buildScene();
        registerObserver();
        LOGGER.debug("Init finished");
    }

    private SplitPane splitPane;

    @Override
    public void start(Stage primaryStage) {
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

    private Scene buildScene() {
        LOGGER.info("Starting");
        splitPane = new SplitPane();
        splitPane.getItems().addAll(globalTabPane, globalTextArea);
        globalTextArea.addParent(splitPane);
        splitPane.setOrientation(Orientation.VERTICAL);

        VBox vBox = new VBox(menuBar, splitPane);
        Scene buildScene = new Scene(vBox, RESOLUTION_WIDTH, 576);
        buildScene.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            splitPane.setPrefHeight(newValue.doubleValue());
            splitPane.setDividerPositions(1);
        });

        for (RegistryTreeTableView registryTreeTableView : registryTreeTableViewList) {
            registryTreeTableView.addWidthProperty(buildScene.widthProperty());
            registryTreeTableView.addHeightProperty(buildScene.heightProperty());
        }

        return buildScene;
    }

    private void registerObserver() {
        GlobalCachedExecutorService.submit(() -> {
            LOGGER.debug("Register observer");
            final Map<RegistryRemote, Future<Void>> registrationFutureMap = new HashMap<>();

            try {
                for (final RegistryRemote registryRemote : Registries.getRegistries()) {
                    registrationFutureMap.put(registryRemote, GlobalCachedExecutorService.submit(() -> {
                        try {
                            LOGGER.info("Register observer for [" + registryRemote + "]");
                            registryRemote.addDataObserver((org.openbase.jul.pattern.Observable source, Object data) -> {
                                assert registryRemote.isDataAvailable();
                                LOGGER.info("Received update for [" + registryRemote + "]");
                                if (data == null) {
                                    LOGGER.warn("Data for remote [" + registryRemote + "] is null!");
                                }
                                updateTab(registryRemote);
                            });
                            if (registryRemote.isDataAvailable()) {
                                updateTab(registryRemote);
                            }
                        } catch (Exception ex) {
                            printException(ex, LOGGER, LogLevel.ERROR);
                        }
                        return null;
                    }));

                    registryRemote.addConnectionStateObserver((source, connectionState) -> {
                        LOGGER.debug("Remote connection state has changed to: " + connectionState);
                        Platform.runLater(() -> {
                            boolean disconnected = false;
                            if (connectionState != ConnectionState.CONNECTED) {
                                disconnected = true;
                            }

                            for (RegistryTreeTableView treeTable : getTreeTablesByRemote(registryRemote)) {
                                treeTable.setDisconnected(disconnected);
                            }
                        });
                    });
                }
            } catch (CouldNotPerformException ex) {
                printException(new CouldNotPerformException("Could not register observer", ex));
            }
        });

    }

    @Override
    public void stop() throws Exception {
        super.stop();

        // Call system exit to trigger all shutdown daemons.
        System.exit(0);
    }

    private void updateTab(final RegistryRemote registryRemote) {
        LOGGER.info("Update tab for remote [" + registryRemote + "]");
        GlobalCachedExecutorService.submit(() -> {
            synchronized (registryRemote) {
                Tab tab = getRegistryTabByRemote(registryRemote);
                if (!registryRemote.isConnected() || !registryRemote.isActive() || !registryRemote.isDataAvailable()) {
                    LOGGER.info("Set progress indicator for remote[" + registryRemote + "] because [" + !registryRemote.isConnected() + ", " + !registryRemote.isActive() + ", " + !registryRemote.isDataAvailable() + "]");
                    final Node node = getProgressIndicatorByRemote(registryRemote);
                    Platform.runLater(() -> tab.setContent(node));
                    return;
                }
                try {
                    GeneratedMessage data = (GeneratedMessage) registryRemote.getData();
                    if (!initialized.get(data.getClass().getSimpleName())) {
                        LOGGER.debug(data.getClass().getSimpleName() + " is not yet initialized");
                        final Node node = fillTreeTableView(data);
                        Platform.runLater(() -> tab.setContent(node));
                    } else {
                        LOGGER.debug("Updating " + data.getClass().getSimpleName());
                        final Node node = updateTreeTableView(data);
                        Platform.runLater(() -> tab.setContent(node));
                    }
                } catch (CouldNotPerformException | InterruptedException ex) {
                    ExceptionPrinter.printHistory(new NotAvailableException("Registry", ex), LOGGER);
                    tab.setContent(new Label("Error: " + ex.getMessage()));
                }
            }
        });
    }

    private javafx.scene.Node fillTreeTableView(GeneratedMessage registryData) throws CouldNotPerformException, InterruptedException {
        LOGGER.debug("FillTreeTableView for [" + registryData.getClass().getSimpleName() + "]");
        if (registryData instanceof ClassRegistryData) {
            ClassRegistryData data = (ClassRegistryData) registryData;
            TreeItemDescriptorProvider company = new FieldDescriptorGroup(DeviceClass.newBuilder(), DeviceClass.COMPANY_FIELD_NUMBER);
            Descriptors.FieldDescriptor deviceClassField = data.toBuilder().getDescriptorForType().findFieldByNumber(ClassRegistryData.DEVICE_CLASS_FIELD_NUMBER);
            deviceClassTreeTableView.setRoot(new GenericGroupContainer<>(deviceClassField.getName(), deviceClassField, data.toBuilder(), data.toBuilder().getDeviceClassBuilderList(), company));
            deviceClassTreeTableView.setReadOnlyMode(Registries.isReadOnly(SendableType.DEVICE_CLASS.getDefaultInstanceForType()));
            deviceClassTreeTableView.getListDiff().diff(data.getDeviceClassList());

            appClassTreeTableView.setRoot(new GenericListContainer(ClassRegistryData.APP_CLASS_FIELD_NUMBER, data.toBuilder()));
            appClassTreeTableView.setReadOnlyMode(Registries.isReadOnly(SendableType.APP_CLASS.getDefaultInstanceForType()));
            appClassTreeTableView.getListDiff().diff(data.getAppClassList());

            agentClassTreeTableView.setRoot(new GenericListContainer(ClassRegistryData.AGENT_CLASS_FIELD_NUMBER, data.toBuilder()));
            agentClassTreeTableView.setReadOnlyMode(Registries.isReadOnly(SendableType.AGENT_CLASS.getDefaultInstanceForType()));
            agentClassTreeTableView.getListDiff().diff(data.getAgentClassList());

            initialized.put(data.getClass().getSimpleName(), Boolean.TRUE);
            return classRegistryTabPane;
        } else if (registryData instanceof TemplateRegistryData) {
            TemplateRegistryData data = (TemplateRegistryData) registryData;

            unitTemplateTreeTableView.setRoot(new GenericListContainer<>(TemplateRegistryData.UNIT_TEMPLATE_FIELD_NUMBER, data.toBuilder()));
            unitTemplateTreeTableView.setReadOnlyMode(Registries.isReadOnly(SendableType.UNIT_TEMPLATE.getDefaultInstanceForType()));
            unitTemplateTreeTableView.getListDiff().diff(data.getUnitTemplateList());

            serviceTemplateTreeTableView.setRoot(new GenericListContainer<>(TemplateRegistryData.SERVICE_TEMPLATE_FIELD_NUMBER, data.toBuilder()));
            serviceTemplateTreeTableView.setReadOnlyMode(Registries.isReadOnly(SendableType.SERVICE_TEMPLATE.getDefaultInstanceForType()));
            serviceTemplateTreeTableView.getListDiff().diff(data.getServiceTemplateList());

            activityTemplateTreeTableView.setRoot(new GenericListContainer<>(TemplateRegistryData.ACTIVITY_TEMPLATE_FIELD_NUMBER, data.toBuilder()));
            activityTemplateTreeTableView.setReadOnlyMode(Registries.isReadOnly(SendableType.ACTIVITY_TEMPLATE.getDefaultInstanceForType()));
            activityTemplateTreeTableView.getListDiff().diff(data.getActivityTemplateList());

            initialized.put(data.getClass().getSimpleName(), Boolean.TRUE);
            return templateRegistryTabPane;
        } else if (registryData instanceof UnitRegistryData) {
            UnitRegistryData data = (UnitRegistryData) registryData;

            TreeItemDescriptorProvider locationDescriptor = new LocationItemDescriptorProvider();
            TreeItemDescriptorProvider unitTypeDescriptor = new UnitTypeItemDescriptorProvider();
            Descriptors.FieldDescriptor dalUnitConfigField = data.toBuilder().getDescriptorForType().findFieldByNumber(UnitRegistryData.DAL_UNIT_CONFIG_FIELD_NUMBER);
            dalUnitConfigTreeTableView.setRoot(new GenericGroupContainer<>(dalUnitConfigField.getName(), dalUnitConfigField, data.toBuilder(), data.toBuilder().getDalUnitConfigBuilderList(), locationDescriptor, unitTypeDescriptor));
            dalUnitConfigTreeTableView.setReadOnlyMode(Registries.isReadOnly(SendableType.UNIT_TEMPLATE.getDefaultInstanceForType()));
            dalUnitConfigTreeTableView.getListDiff().diff(data.getDalUnitConfigList());

            unitGroupConfigTreeTableView.setRoot(new GenericListContainer<>(UnitRegistryData.UNIT_GROUP_UNIT_CONFIG_FIELD_NUMBER, data.toBuilder()));
            unitGroupConfigTreeTableView.setReadOnlyMode(Registries.isReadOnly(SendableType.UNIT_GROUP_CONFIG.getDefaultInstanceForType()));
            unitGroupConfigTreeTableView.getListDiff().diff(data.getUnitGroupUnitConfigList());

            TreeItemDescriptorProvider deviceClassId = new DeviceClassItemDescriptorProvider();
            TreeItemDescriptorProvider locationId = new LocationItemDescriptorProvider();
            Descriptors.FieldDescriptor deviceConfigfield = data.toBuilder().getDescriptorForType().findFieldByNumber(UnitRegistryData.DEVICE_UNIT_CONFIG_FIELD_NUMBER);
            deviceConfigTreeTableView.setRoot(new GenericGroupContainer<>(deviceConfigfield.getName(), deviceConfigfield, data.toBuilder(), data.toBuilder().getDeviceUnitConfigBuilderList(), deviceClassId, locationId));
            deviceConfigTreeTableView.setReadOnlyMode(Registries.isReadOnly(SendableType.DEVICE_CONFIG.getDefaultInstanceForType()));
            deviceConfigTreeTableView.getListDiff().diff(data.getDeviceUnitConfigList());

            locationConfigTreeTableView.setRoot(new GenericListContainer<>(UnitRegistryData.LOCATION_UNIT_CONFIG_FIELD_NUMBER, data.toBuilder()));
            locationConfigTreeTableView.setReadOnlyMode(Registries.isReadOnly(SendableType.LOCATION_CONFIG.getDefaultInstanceForType()));
            locationConfigTreeTableView.getListDiff().diff(data.getLocationUnitConfigList());

            connectionConfigTreeTableView.setRoot(new GenericListContainer<>(UnitRegistryData.CONNECTION_UNIT_CONFIG_FIELD_NUMBER, data.toBuilder()));
            connectionConfigTreeTableView.setReadOnlyMode(Registries.isReadOnly(SendableType.CONNECTION_CONFIG.getDefaultInstanceForType()));
            connectionConfigTreeTableView.getListDiff().diff(data.getConnectionUnitConfigList());

            sceneConfigTreeTableView.setRoot(new GenericListContainer(UnitRegistryData.SCENE_UNIT_CONFIG_FIELD_NUMBER, data.toBuilder()));
            sceneConfigTreeTableView.setReadOnlyMode(Registries.isReadOnly(SendableType.SCENE_CONFIG.getDefaultInstanceForType()));
            sceneConfigTreeTableView.getListDiff().diff(data.getSceneUnitConfigList());

            appConfigTreeTableView.setRoot(new GenericListContainer(UnitRegistryData.APP_UNIT_CONFIG_FIELD_NUMBER, data.toBuilder()));
            appConfigTreeTableView.setReadOnlyMode(Registries.isReadOnly(SendableType.APP_CONFIG.getDefaultInstanceForType()));
            appConfigTreeTableView.getListDiff().diff(data.getAppUnitConfigList());

            TreeItemDescriptorProvider agentClassLabel = new AgentClassItemDescriptorProvider();
            Descriptors.FieldDescriptor agentConfigfield = data.toBuilder().getDescriptorForType().findFieldByNumber(UnitRegistryData.AGENT_UNIT_CONFIG_FIELD_NUMBER);
            agentConfigTreeTableView.setRoot(new GenericGroupContainer<>(agentConfigfield.getName(), agentConfigfield, data.toBuilder(), data.toBuilder().getAgentUnitConfigBuilderList(), agentClassLabel));
            agentConfigTreeTableView.setReadOnlyMode(Registries.isReadOnly(SendableType.AGENT_CONFIG.getDefaultInstanceForType()));
            agentConfigTreeTableView.getListDiff().diff(data.getAgentUnitConfigList());

            userConfigTreeTableView.setRoot(new GenericListContainer<>(UnitRegistryData.USER_UNIT_CONFIG_FIELD_NUMBER, data.toBuilder()));
            userConfigTreeTableView.setReadOnlyMode(Registries.isReadOnly(SendableType.USER_CONFIG.getDefaultInstanceForType()));
            userConfigTreeTableView.getListDiff().diff(data.getUserUnitConfigList());

            authorizationGroupConfigTreeTableView.setRoot(new GenericListContainer<>(UnitRegistryData.AUTHORIZATION_GROUP_UNIT_CONFIG_FIELD_NUMBER, data.toBuilder()));
            authorizationGroupConfigTreeTableView.setReadOnlyMode(Registries.isReadOnly(SendableType.AUTHORIZATION_GROUP_CONFIG.getDefaultInstanceForType()));
            authorizationGroupConfigTreeTableView.getListDiff().diff(data.getAuthorizationGroupUnitConfigList());

            initialized.put(data.getClass().getSimpleName(), Boolean.TRUE);
            return unitRegistryTabPane;
        } else if (registryData instanceof ActivityRegistryData) {
            ActivityRegistryData data = (ActivityRegistryData) registryData;

            activityConfigTreeTableView.setRoot(new GenericListContainer<>(ActivityRegistryData.ACTIVITY_CONFIG_FIELD_NUMBER, data.toBuilder()));
            activityConfigTreeTableView.setReadOnlyMode(Registries.isReadOnly(SendableType.ACTIVITY_CONFIG.getDefaultInstanceForType()));
            activityConfigTreeTableView.getListDiff().diff(data.getActivityConfigList());

            initialized.put(data.getClass().getSimpleName(), Boolean.TRUE);
            return activityRegistryTabPane;
        }

        return null;
    }

    private javafx.scene.Node updateTreeTableView(GeneratedMessage msg) throws CouldNotPerformException, InterruptedException {
        LOGGER.debug("UpdateTreeTableView for [" + msg.getClass().getSimpleName() + "]");
        if (msg instanceof ClassRegistryData) {
            ClassRegistryData data = (ClassRegistryData) msg;
            deviceClassTreeTableView.update(data.getDeviceClassList());
            agentClassTreeTableView.update(data.getAgentClassList());
            appClassTreeTableView.update(data.getAppClassList());

            deviceClassTreeTableView.setReadOnlyMode(Registries.getClassRegistry().isDeviceClassRegistryReadOnly());
            agentClassTreeTableView.setReadOnlyMode(Registries.getClassRegistry().isAgentClassRegistryReadOnly());
            appClassTreeTableView.setReadOnlyMode(Registries.getClassRegistry().isAppClassRegistryReadOnly());

            return classRegistryTabPane;
        } else if (msg instanceof TemplateRegistryData) {
            TemplateRegistryData data = (TemplateRegistryData) msg;

            unitTemplateTreeTableView.update(data.getUnitTemplateList());
            serviceTemplateTreeTableView.update(data.getServiceTemplateList());
            activityTemplateTreeTableView.update(data.getActivityTemplateList());

            unitTemplateTreeTableView.setReadOnlyMode(Registries.getTemplateRegistry().isUnitTemplateRegistryReadOnly());
            serviceTemplateTreeTableView.setReadOnlyMode(Registries.getTemplateRegistry().isServiceTemplateRegistryReadOnly());
            activityTemplateTreeTableView.setReadOnlyMode(Registries.getTemplateRegistry().isActivityTemplateRegistryReadOnly());

            return templateRegistryTabPane;
        } else if (msg instanceof UnitRegistryData) {
            UnitRegistryData data = (UnitRegistryData) msg;
            dalUnitConfigTreeTableView.update(data.getDalUnitConfigList());
            unitGroupConfigTreeTableView.update(data.getUnitGroupUnitConfigList());
            deviceConfigTreeTableView.update(data.getDeviceUnitConfigList());
            locationConfigTreeTableView.update(data.getLocationUnitConfigList());
            connectionConfigTreeTableView.update(data.getConnectionUnitConfigList());
            sceneConfigTreeTableView.update(data.getSceneUnitConfigList());
            userConfigTreeTableView.update(data.getUserUnitConfigList());
            authorizationGroupConfigTreeTableView.update(data.getAuthorizationGroupUnitConfigList());

            dalUnitConfigTreeTableView.setReadOnlyMode(Registries.getUnitRegistry().isDalUnitConfigRegistryReadOnly());
            unitGroupConfigTreeTableView.setReadOnlyMode(Registries.getUnitRegistry().isUnitGroupConfigRegistryReadOnly());
            deviceConfigTreeTableView.setReadOnlyMode(Registries.getUnitRegistry().isDeviceUnitRegistryReadOnly());
            locationConfigTreeTableView.setReadOnlyMode(Registries.getUnitRegistry().isLocationUnitRegistryReadOnly());
            connectionConfigTreeTableView.setReadOnlyMode(Registries.getUnitRegistry().isConnectionUnitRegistryReadOnly());
            sceneConfigTreeTableView.setReadOnlyMode(Registries.getUnitRegistry().isSceneUnitRegistryReadOnly());
            agentConfigTreeTableView.setReadOnlyMode(Registries.getUnitRegistry().isAgentUnitRegistryReadOnly());
            appConfigTreeTableView.setReadOnlyMode(Registries.getUnitRegistry().isAppUnitRegistryReadOnly());
            userConfigTreeTableView.setReadOnlyMode(Registries.getUnitRegistry().isUserUnitRegistryReadOnly());
            authorizationGroupConfigTreeTableView.setReadOnlyMode(Registries.getUnitRegistry().isAuthorizationGroupUnitRegistryReadOnly());

            return unitRegistryTabPane;
        } else if (msg instanceof ActivityRegistryData) {
            ActivityRegistryData data = (ActivityRegistryData) msg;
            activityConfigTreeTableView.update(data.getActivityConfigList());

            activityConfigTreeTableView.setReadOnlyMode(Registries.getActivityRegistry().isActivityConfigRegistryReadOnly());

            return activityRegistryTabPane;
        }
        return null;
    }

    private Tab getRegistryTabByRemote(final RegistryRemote registryRemote) {
        if (registryRemote instanceof ClassRegistryRemote) {
            return classRegistryTab;
        } else if (registryRemote instanceof TemplateRegistryRemote) {
            return templateRegistryTab;
        } else if (registryRemote instanceof UnitRegistryRemote) {
            return unitRegistryTab;
        } else if (registryRemote instanceof ActivityRegistryRemote) {
            return activityRegistryTab;
        }
        return null;
    }

    private List<RegistryTreeTableView> getTreeTablesByRemote(final RegistryRemote registryRemote) {
        List<RegistryTreeTableView> treeTableList = new ArrayList<>();
        if (registryRemote instanceof ClassRegistryRemote) {
            treeTableList.add(deviceClassTreeTableView);
            treeTableList.add(agentClassTreeTableView);
            treeTableList.add(appClassTreeTableView);
        } else if (registryRemote instanceof TemplateRegistryRemote) {
            treeTableList.add(unitTemplateTreeTableView);
            treeTableList.add(serviceTemplateTreeTableView);
            treeTableList.add(activityTemplateTreeTableView);
        } else if (registryRemote instanceof UnitRegistryRemote) {
            treeTableList.add(dalUnitConfigTreeTableView);
            treeTableList.add(deviceConfigTreeTableView);
            treeTableList.add(locationConfigTreeTableView);
            treeTableList.add(connectionConfigTreeTableView);
            treeTableList.add(sceneConfigTreeTableView);
            treeTableList.add(appConfigTreeTableView);
            treeTableList.add(agentConfigTreeTableView);
            treeTableList.add(userConfigTreeTableView);
            treeTableList.add(authorizationGroupConfigTreeTableView);
        } else if (registryRemote instanceof ActivityRegistryRemote) {
            treeTableList.add(activityConfigTreeTableView);
        }
        return treeTableList;
    }

    private ProgressIndicator getProgressIndicatorByRemote(RegistryRemote remote) {
        if (remote instanceof ClassRegistryRemote) {
            return classRegistryProgressIndicator;
        } else if (remote instanceof TemplateRegistryRemote) {
            return templateRegistryProgressIndicator;
        } else if (remote instanceof UnitRegistryRemote) {
            return unitRegistryProgressIndicator;
        } else if (remote instanceof ActivityRegistryRemote) {
            return activityRegistryProgressIndicator;
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
            case ACTIVITY_TEMPLATE:
                return activityTemplateTreeTableView;
            case ACTIVITY_CONFIG:
                return activityConfigTreeTableView;
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
        GeneratedMessage msg = (GeneratedMessage) Registries.getById(id);
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
        JPService.registerProperty(JPClassRegistryScope.class);
        JPService.registerProperty(JPTemplateRegistryScope.class);
        JPService.registerProperty(JPActivityRegistryScope.class);
        JPService.registerProperty(JPUnitRegistryScope.class);
        JPService.registerProperty(JPRSBHost.class);
        JPService.registerProperty(JPRSBPort.class);
        JPService.registerProperty(JPRSBTransport.class);
        JPService.parseAndExitOnError(args);
        LauncherImpl.launchApplication(RegistryEditor.class, RegistryEditorPreloader.class, args);
        LOGGER.info(APP_NAME + " successfully started.");
    }

    /**
     * Print a throwable using the logger of the RegistryEditor class.
     *
     * @param throwable the throwable printed
     */
    public static void printException(final Throwable throwable) {
        printException(throwable, LOGGER);
    }

    public static void printException(final Throwable throwable, final Logger logger) {
        printException(throwable, logger, LogLevel.ERROR);
    }

    public static void printException(final Throwable throwable, final Logger logger, final LogLevel logLevel) {
        GlobalTextArea.getInstance().printException(throwable);
        ExceptionPrinter.printHistory(throwable, logger, logLevel);
    }

    public static <V> Future<V> runOnFxThread(final Callable<V> callable) {
        return runOnFxThread(callable, "task");
    }

    public static <V> Future<V> runOnFxThread(final Callable<V> callable, final String taskDescription) {
        try {
            if (Platform.isFxApplicationThread()) {
                return CompletableFuture.completedFuture(callable.call());
            }

            FutureTask<V> future = new FutureTask(() -> {
                try {
                    return callable.call();
                } catch (Exception ex) {
                    throw ExceptionPrinter.printHistoryAndReturnThrowable(new CouldNotPerformException("Could not perform " + taskDescription + "!", ex), LOGGER);
                }
            });
            Platform.runLater(future);
            return future;
        } catch (Exception ex) {
            ExceptionPrinter.printHistory("Could not perform " + taskDescription + "!", ex, LOGGER);
            final CompletableFuture<V> completableFuture = new CompletableFuture<>();
            completableFuture.completeExceptionally(ex);
            return completableFuture;
        }
    }
}
