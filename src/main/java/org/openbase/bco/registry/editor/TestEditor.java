package org.openbase.bco.registry.editor;

/*-
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

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;
import org.openbase.bco.registry.editor.util.FieldPathDescriptionProvider;
import org.openbase.bco.registry.editor.util.fieldpath.*;
import org.openbase.bco.registry.editor.visual.RegistryRemoteTab;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jps.core.JPService;
import org.openbase.jps.preset.JPVerbose;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.slf4j.LoggerFactory;
import rst.domotic.registry.ClassRegistryDataType.ClassRegistryData;
import rst.domotic.registry.UnitRegistryDataType.UnitRegistryData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class TestEditor extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            JPService.registerProperty(JPVerbose.class, true);
            String[] args = {"-v"};
            JPService.parseAndExitOnError(args);
//            final TreeTableColumn<ValueType, ValueType> descriptionColumn = new TreeTableColumn<>();
//            final TreeTableColumn<ValueType, ValueType> valueColumn = new TreeTableColumn<>();
//
////            valueColumn.setOnEditCommit(event -> {
////                System.out.println("Set value for treeItem[" + event.getRowValue().getClass().getSimpleName() + "] to [" + event.getNewValue().getValue() + "]");
////                event.getRowValue().setValue(event.getNewValue());
////            });
//
//            descriptionColumn.setPrefWidth(400);
//            valueColumn.setPrefWidth(368);
//            descriptionColumn.setCellValueFactory(param -> param.getValue().valueProperty());
//            valueColumn.setCellValueFactory(param -> param.getValue().valueProperty());
//            descriptionColumn.setCellFactory(param -> new SecondCell());
//            valueColumn.setCellFactory(param -> new TestCell());
//            final TreeTableView<ValueType> treeTableView = new TreeTableView<>();
//            treeTableView.getColumns().addAll(descriptionColumn, valueColumn);
//            treeTableView.showRootProperty().setValue(false);
//            treeTableView.setEditable(true);
//
//            FieldDescriptor fieldDescriptor = ProtoBufFieldProcessor.getFieldDescriptor(UnitRegistryData.getDefaultInstance(), UnitRegistryData.DAL_UNIT_CONFIG_FIELD_NUMBER);
//
//            FieldPathDescriptionProvider locationIdProvider = new FieldPathDescriptionProvider(new FieldDescriptorPath(UnitConfig.getDefaultInstance(), UnitConfig.PLACEMENT_CONFIG_FIELD_NUMBER, PlacementConfig.LOCATION_ID_FIELD_NUMBER)) {
//                @Override
//                public String generateDescription(Object value) {
//                    final String locationId = (String) value;
//                    try {
//                        return LabelProcessor.getBestMatch(Registries.getUnitRegistry().getUnitConfigById(locationId).getLabel());
//                    } catch (CouldNotPerformException ex) {
//                        return locationId;
//                    }
//                }
//            };
//            FieldPathDescriptionProvider unitTypeProvider = new FieldPathDescriptionProvider(new FieldDescriptorPath(UnitConfig.getDefaultInstance(), UnitConfig.UNIT_TYPE_FIELD_NUMBER)) {
//                @Override
//                public String generateDescription(Object value) {
//                    final EnumValueDescriptor unitType = (EnumValueDescriptor) value;
//                    return StringProcessor.transformUpperCaseToCamelCase(unitType.getName());
//                }
//            };
//            GroupTreeItem<Builder> root = new GroupTreeItem<>(fieldDescriptor, Registries.getUnitRegistry(true).getData().toBuilder(), true, locationIdProvider, unitTypeProvider);
////        printTypes(4, root);
//            root.setExpanded(true);
//            treeTableView.setRoot(root);
////            treeTableView.setShowRoot(true);
////            final UnitConfig unitConfig = Registries.getUnitRegistry().getDalUnitConfigs().get(0);
////            FieldDescriptor descriptor = ProtoBufFieldProcessor.getFieldDescriptor(unitConfig, UnitConfig.SCOPE_FIELD_NUMBER);
////            treeTableView.setRoot(new ScopeTreeItem(descriptor, unitConfig.toBuilder().getScopeBuilder()));

            final List<Integer> unitPriorityList = new ArrayList<>();
            unitPriorityList.add(UnitRegistryData.DAL_UNIT_CONFIG_FIELD_NUMBER);
            unitPriorityList.add(UnitRegistryData.LOCATION_UNIT_CONFIG_FIELD_NUMBER);
            unitPriorityList.add(UnitRegistryData.DEVICE_UNIT_CONFIG_FIELD_NUMBER);

            final TabPane globalTabPane = new TabPane();
            globalTabPane.getTabs().add(new RegistryRemoteTab<>(Registries.getUnitRegistry(), getUnitRegistryGrouping(), unitPriorityList));
            globalTabPane.getTabs().add(new RegistryRemoteTab<>(Registries.getClassRegistry(), getClassRegistryGrouping()));
            globalTabPane.getTabs().add(new RegistryRemoteTab<>(Registries.getTemplateRegistry()));
            globalTabPane.getTabs().add(new RegistryRemoteTab<>(Registries.getActivityRegistry()));

            final Scene scene = new Scene(globalTabPane, 1024, 768);
            scene.heightProperty().addListener((observable, oldValue, newValue) -> globalTabPane.setPrefHeight(newValue.doubleValue()));
            primaryStage.setScene(scene);
//            primaryStage.setMaximized(true);
            primaryStage.show();
        } catch (Exception ex) {
            ExceptionPrinter.printHistoryAndReturnThrowable(ex, LoggerFactory.getLogger(TestEditor.class));
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

    private void printTypes(int depth, TreeItem<?> treeItem) {
        if (depth == 0) {
            return;
        }
        System.out.println("TreeItem[" + treeItem.getClass().getSimpleName() + "]");
        if (treeItem.getChildren().size() > 0) {
            printTypes(depth - 1, treeItem.getChildren().get(0));
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();

        System.exit(0);
    }
}
