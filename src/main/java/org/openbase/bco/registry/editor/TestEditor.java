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

import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.stage.Stage;
import org.openbase.bco.registry.editor.struct.GroupTreeItem;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.editor.util.FieldPathDescriptionProvider;
import org.openbase.bco.registry.editor.visual.cell.SecondCell;
import org.openbase.bco.registry.editor.visual.cell.TestCell;
import org.openbase.bco.registry.editor.util.FieldDescriptorPath;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jps.core.JPService;
import org.openbase.jps.preset.JPVerbose;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.extension.protobuf.processing.ProtoBufFieldProcessor;
import org.openbase.jul.extension.rst.processing.LabelProcessor;
import org.openbase.jul.processing.StringProcessor;
import rst.domotic.registry.UnitRegistryDataType.UnitRegistryData;
import rst.domotic.registry.UnitRegistryDataType.UnitRegistryData.Builder;
import rst.domotic.unit.UnitConfigType.UnitConfig;
import rst.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;
import rst.spatial.PlacementConfigType.PlacementConfig;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class TestEditor extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        JPService.registerProperty(JPVerbose.class, true);
        String[] args = {"-v"};
        JPService.parseAndExitOnError(args);
        final TreeTableColumn<ValueType, ValueType> descriptionColumn = new TreeTableColumn<>();
        final TreeTableColumn<ValueType, ValueType> valueColumn = new TreeTableColumn<>();

        descriptionColumn.setPrefWidth(400);
        valueColumn.setPrefWidth(368);
        descriptionColumn.setCellValueFactory(param -> param.getValue().valueProperty());
        valueColumn.setCellValueFactory(param -> param.getValue().valueProperty());
        descriptionColumn.setCellFactory(param -> new SecondCell());
        valueColumn.setCellFactory(param -> new TestCell());
        final TreeTableView<ValueType> treeTableView = new TreeTableView<>();
        treeTableView.getColumns().addAll(descriptionColumn, valueColumn);
        treeTableView.showRootProperty().setValue(false);
        treeTableView.setEditable(true);


        FieldDescriptor fieldDescriptor = ProtoBufFieldProcessor.getFieldDescriptor(UnitRegistryData.getDefaultInstance(), UnitRegistryData.DAL_UNIT_CONFIG_FIELD_NUMBER);

        FieldPathDescriptionProvider locationIdProvider = new FieldPathDescriptionProvider(new FieldDescriptorPath(UnitConfig.getDefaultInstance(), UnitConfig.PLACEMENT_CONFIG_FIELD_NUMBER, PlacementConfig.LOCATION_ID_FIELD_NUMBER)) {
            @Override
            public String generateDescription(Object value) {
                final String locationId = (String) value;
                try {
                    return LabelProcessor.getBestMatch(Registries.getUnitRegistry().getUnitConfigById(locationId).getLabel());
                } catch (CouldNotPerformException ex) {
                    return locationId;
                }
            }
        };
        FieldPathDescriptionProvider unitTypeProvider = new FieldPathDescriptionProvider(new FieldDescriptorPath(UnitConfig.getDefaultInstance(), UnitConfig.UNIT_TYPE_FIELD_NUMBER)) {
            @Override
            public String generateDescription(Object value) {
                final EnumValueDescriptor unitType = (EnumValueDescriptor) value;
                return StringProcessor.transformUpperCaseToCamelCase(unitType.getName());
            }
        };
        GroupTreeItem<Builder> root = new GroupTreeItem<>(fieldDescriptor, Registries.getUnitRegistry(true).getData().toBuilder(), true, locationIdProvider, unitTypeProvider);
//        printTypes(4, root);
        root.setExpanded(true);
        treeTableView.setRoot(root);

        final Scene scene = new Scene(treeTableView, 1024, 768);
        primaryStage.setScene(scene);
        primaryStage.show();
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
