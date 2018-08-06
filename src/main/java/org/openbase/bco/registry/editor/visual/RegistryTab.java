package org.openbase.bco.registry.editor.visual;

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
import com.google.protobuf.Message;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import org.openbase.bco.registry.editor.struct.AbstractBuilderTreeItem;
import org.openbase.bco.registry.editor.struct.BuilderListTreeItem;
import org.openbase.bco.registry.editor.struct.GroupTreeItem;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.editor.util.FieldDescriptorPath;
import org.openbase.bco.registry.editor.util.FieldPathDescriptionProvider;
import org.openbase.bco.registry.editor.visual.cell.SecondCell;
import org.openbase.bco.registry.editor.visual.cell.TestCell;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.extension.rst.processing.LabelProcessor;
import org.openbase.jul.processing.StringProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rst.domotic.unit.UnitConfigType.UnitConfig;
import rst.domotic.unit.location.LocationConfigType.LocationConfig;
import rst.spatial.PlacementConfigType.PlacementConfig;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class RegistryTab<RD extends Message> extends TabWithStatusLabel {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistryTab.class);

    private static final double DESCRIPTION_PROPORTION = 0.3;

    private final FieldDescriptor fieldDescriptor;
    private AbstractBuilderTreeItem<RD.Builder> root;
    private boolean initialized = false;
    private final TreeTableView<ValueType> treeTableView;
    private final TreeTableColumn<ValueType, ValueType> descriptionColumn, valueColumn;

    private RD registryData;

    public RegistryTab(final FieldDescriptor fieldDescriptor, final RD registryData) {
        super(StringProcessor.transformToCamelCase(fieldDescriptor.getName()).
                replace("UnitConfig", "").
                replace("Config", "").
                replace("Class", "").
                replace("Template", ""));

        this.registryData = registryData;
        this.fieldDescriptor = fieldDescriptor;

        // init columns
        this.descriptionColumn = new TreeTableColumn<>();
        this.valueColumn = new TreeTableColumn<>();

        descriptionColumn.setCellValueFactory(param -> param.getValue().valueProperty());
        valueColumn.setCellValueFactory(param -> param.getValue().valueProperty());
        descriptionColumn.setCellFactory(param -> new SecondCell());
        valueColumn.setCellFactory(param -> new TestCell());

        // init tree table view
        this.treeTableView = new TreeTableView<>();
        treeTableView.getColumns().addAll(descriptionColumn, valueColumn);
        treeTableView.showRootProperty().setValue(false);
        treeTableView.setEditable(true);

        setOnSelectionChanged(event -> {
            if (!initialized) {
                try {
                    init();
                } catch (InitializationException e) {
                    //TODO print exception correctly
                    e.printStackTrace();
                }
            }
        });
    }

    public void update(RD registryData) throws CouldNotPerformException {
        if (root == null) {
            throw new NotAvailableException("");
        }

        root.update(registryData.toBuilder());
    }

    public void init() throws InitializationException {
        try {
            this.initialized = true;

            if (fieldDescriptor.getName().equals("dal_unit_config")) {
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

                root = new GroupTreeItem<>(fieldDescriptor, registryData.toBuilder(), true, locationIdProvider, unitTypeProvider);
            } else if (fieldDescriptor.getName().equals("location_unit_config")) {
                FieldPathDescriptionProvider locationType = new FieldPathDescriptionProvider(new FieldDescriptorPath(UnitConfig.getDefaultInstance(), UnitConfig.LOCATION_CONFIG_FIELD_NUMBER, LocationConfig.TYPE_FIELD_NUMBER)) {
                    @Override
                    public String generateDescription(Object value) {
                        final EnumValueDescriptor unitType = (EnumValueDescriptor) value;
                        return StringProcessor.transformUpperCaseToCamelCase(unitType.getName());
                    }
                };
                root = new GroupTreeItem<>(fieldDescriptor, registryData.toBuilder(), true, locationType);
            } else {
                // init root node
                //TODO how to realize grouping here
                root = new BuilderListTreeItem<>(fieldDescriptor, registryData.toBuilder(), true);
            }
            root.setExpanded(true);

            treeTableView.setRoot(root);

            this.setInternalContent(treeTableView);
        } catch (CouldNotPerformException ex) {
            throw new InitializationException(this, ex);
        }
    }

    public void manageWidth() {
        getTabPane().widthProperty().addListener((observable, oldValue, newValue) -> {
            descriptionColumn.setPrefWidth(DESCRIPTION_PROPORTION * newValue.doubleValue());
            valueColumn.setPrefWidth((1.0 - DESCRIPTION_PROPORTION) * newValue.doubleValue());
        });
    }

    @Override
    public String toString() {
        return StringProcessor.transformToCamelCase(fieldDescriptor.getName()) + Tab.class.getSimpleName();
    }
}
