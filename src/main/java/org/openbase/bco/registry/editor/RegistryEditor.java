package org.openbase.bco.registry.editor;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2022 openbase.org
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

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.openbase.bco.registry.editor.util.FieldPathDescriptionProvider;
import org.openbase.bco.registry.editor.util.fieldpath.*;
import org.openbase.bco.registry.editor.visual.LoginPanel;
import org.openbase.bco.registry.editor.visual.RegistryRemoteTab;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.bco.registry.remote.login.BCOLogin;
import org.openbase.jps.core.JPService;
import org.openbase.jps.preset.JPVerbose;
import org.openbase.jul.communication.jp.JPComHost;
import org.openbase.jul.communication.jp.JPComPort;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openbase.type.domotic.registry.ClassRegistryDataType.ClassRegistryData;
import org.openbase.type.domotic.registry.UnitRegistryDataType.UnitRegistryData;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class RegistryEditor extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistryEditor.class);

    @Override
    public void init() throws Exception {
        super.init();
        BCOLogin.getSession().autoLogin(false);
    }

    @Override
    public void start(Stage primaryStage) {
        try {

            primaryStage.setTitle("BCO Registry Editor");
            try {
                LOGGER.debug("Try to load icon...");
                primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/registry-editor.png")));
                LOGGER.debug("App icon loaded...");
            } catch (Exception ex) {
                ExceptionPrinter.printHistory(ex, LOGGER, LogLevel.WARN);
            }

            final LoginPanel loginPanel = new LoginPanel();

            final List<Integer> unitPriorityList = new ArrayList<>();
            unitPriorityList.add(UnitRegistryData.DAL_UNIT_CONFIG_FIELD_NUMBER);
            unitPriorityList.add(UnitRegistryData.LOCATION_UNIT_CONFIG_FIELD_NUMBER);
            unitPriorityList.add(UnitRegistryData.DEVICE_UNIT_CONFIG_FIELD_NUMBER);

            final TabPane globalTabPane = new TabPane();
            globalTabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
            globalTabPane.getTabs().add(new RegistryRemoteTab<>(Registries.getUnitRegistry(), getUnitRegistryGrouping(), unitPriorityList));
            globalTabPane.getTabs().add(new RegistryRemoteTab<>(Registries.getClassRegistry(), getClassRegistryGrouping()));
            globalTabPane.getTabs().add(new RegistryRemoteTab<>(Registries.getTemplateRegistry()));
            globalTabPane.getTabs().add(new RegistryRemoteTab<>(Registries.getActivityRegistry()));

            final StackPane stackPane = new StackPane();
            // make login panel appear in the top right with small margins at the top and right
            loginPanel.setAlignment(Pos.TOP_RIGHT);
            loginPanel.setPadding(new Insets(5, 5, 0, 0));
            // make events path through to layers below if they do not hit the login panel
            loginPanel.setPickOnBounds(false);
            stackPane.getChildren().addAll(globalTabPane, loginPanel);
            final Scene scene = new Scene(stackPane, 1600, 900);

            scene.heightProperty().addListener((observable, oldValue, newValue) -> globalTabPane.setPrefHeight(newValue.doubleValue()));
            primaryStage.setScene(scene);
//            primaryStage.setMaximized(true);
            primaryStage.show();
        } catch (Exception ex) {
            ExceptionPrinter.printHistoryAndReturnThrowable(ex, LoggerFactory.getLogger(RegistryEditor.class));
        }
    }

    private Map<Integer, FieldPathDescriptionProvider[]> getUnitRegistryGrouping() {
        final AgentUnitAgentClassFieldPath agentUnitAgentClassFieldPath = new AgentUnitAgentClassFieldPath();
        final DeviceUnitDeviceClassFieldPath deviceUnitDeviceClassFieldPath = new DeviceUnitDeviceClassFieldPath();
        final UnitLocationFieldPath unitLocationFieldPath = new UnitLocationFieldPath();
        final UnitTypeFieldPath unitTypeFieldPath = new UnitTypeFieldPath();

        final Map<Integer, FieldPathDescriptionProvider[]> unitRegistryGroupingMap = new HashMap<>();

        final FieldPathDescriptionProvider[] dalUnitGrouping = {unitLocationFieldPath, unitTypeFieldPath};
        unitRegistryGroupingMap.put(UnitRegistryData.DAL_UNIT_CONFIG_FIELD_NUMBER, dalUnitGrouping);

        final FieldPathDescriptionProvider[] deviceUnitGrouping = {deviceUnitDeviceClassFieldPath, unitLocationFieldPath};
        unitRegistryGroupingMap.put(UnitRegistryData.DEVICE_UNIT_CONFIG_FIELD_NUMBER, deviceUnitGrouping);

        final FieldPathDescriptionProvider[] agentUnitGrouping = {agentUnitAgentClassFieldPath};
        unitRegistryGroupingMap.put(UnitRegistryData.AGENT_UNIT_CONFIG_FIELD_NUMBER, agentUnitGrouping);

        return unitRegistryGroupingMap;
    }

    private Map<Integer, FieldPathDescriptionProvider[]> getClassRegistryGrouping() {
        final DeviceClassCompanyFieldPath deviceClassCompanyFieldPath = new DeviceClassCompanyFieldPath();
        final Map<Integer, FieldPathDescriptionProvider[]> classRegistryGroupingMap = new HashMap<>();

        final FieldPathDescriptionProvider[] deviceClassGrouping = {deviceClassCompanyFieldPath};
        classRegistryGroupingMap.put(ClassRegistryData.DEVICE_CLASS_FIELD_NUMBER, deviceClassGrouping);

        return classRegistryGroupingMap;
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.exit(0);
    }

    public static void main(String[] args) {
        JPService.registerProperty(JPVerbose.class, true);
        JPService.registerProperty(JPComHost.class);
        JPService.registerProperty(JPComPort.class);
        JPService.parseAndExitOnError(args);

        RegistryEditor.launch(args);
    }
}
