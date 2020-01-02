package org.openbase.bco.registry.editor.visual;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2020 openbase.org
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

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import org.openbase.bco.registry.editor.struct.*;
import org.openbase.bco.registry.editor.util.FieldPathDescriptionProvider;
import org.openbase.bco.registry.editor.visual.cell.DescriptionCell;
import org.openbase.bco.registry.editor.visual.cell.ValueCell;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.extension.protobuf.ProtoBufBuilderProcessor;
import org.openbase.jul.extension.protobuf.processing.ProtoBufFieldProcessor;
import org.openbase.jul.processing.StringProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openbase.type.domotic.registry.TemplateRegistryDataType.TemplateRegistryData;
import org.openbase.type.domotic.unit.UnitConfigType.UnitConfig;
import org.openbase.type.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class RegistryTab<RD extends Message> extends TabWithStatusLabel {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistryTab.class);

    private static final double DESCRIPTION_PROPORTION = 0.3;
    private static final String FIELD_POSTFIX_READ_ONLY = "_registry_read_only";
    private static final String FIELD_POSTFIX_CONSISTENT = "_registry_consistent";

    private final StackPane stackPane;
    private final SearchBar searchBar;
    private final FieldDescriptor fieldDescriptor;
    private final TreeTableView<ValueType> treeTableView;
    private final TreeTableColumn<ValueType, ValueType> descriptionColumn, valueColumn;

    private boolean readOnly, consistent, initialized;
    private AbstractBuilderTreeItem<RD.Builder> root;
    private FieldPathDescriptionProvider[] fieldPathDescriptionProviders;
    private RD registryData;

    public RegistryTab(final FieldDescriptor fieldDescriptor, final RD registryData) {
        this(fieldDescriptor, registryData, null);
    }

    public RegistryTab(final FieldDescriptor fieldDescriptor, final RD registryData, final FieldPathDescriptionProvider[] fieldPathDescriptionProviders) {
        super(StringProcessor.transformToPascalCase(fieldDescriptor.getName()).
                replace("UnitConfig", "").
                replace("Config", "").
                replace("Class", "").
                replace("Template", ""));

        this.registryData = registryData;
        this.fieldDescriptor = fieldDescriptor;
        this.fieldPathDescriptionProviders = fieldPathDescriptionProviders;
        this.initialized = false;

        extractRegistryFlags();

        // init columns
        this.descriptionColumn = new TreeTableColumn<>();
        //TODO: tree items currently sort their children after creation
        //the problem is that if the description column is sorted new created children will not be sorted the same way
        this.descriptionColumn.setSortable(false);
        this.valueColumn = new TreeTableColumn<>();
        this.valueColumn.setSortable(false);

        descriptionColumn.setCellValueFactory(param -> {
            //TODO: why is this check needed when searching will collapse a tree item before searching?
            if (param == null || param.getValue() == null) {
                return null;
            }
            return param.getValue().valueProperty();
        });
        valueColumn.setCellValueFactory(param -> {
            //TODO: why is this check needed when searching will collapse a tree item before searching?
            if (param == null || param.getValue() == null) {
                return null;
            }
            return param.getValue().valueProperty();
        });
        descriptionColumn.setCellFactory(param -> new DescriptionCell());
        valueColumn.setCellFactory(param -> new ValueCell());

        // init tree table view
        this.treeTableView = new TreeTableView<>();
        treeTableView.getColumns().addAll(descriptionColumn, valueColumn);
        treeTableView.showRootProperty().setValue(false);
        treeTableView.setEditable(true);
        treeTableView.getStylesheets().add("default.css");

        setOnSelectionChanged(event -> {
            if (!initialized) {
                try {
                    init();
                } catch (InitializationException ex) {
                    ExceptionPrinter.printHistory(ex, LOGGER);
                }
            }
        });

        if (!(registryData instanceof TemplateRegistryData)) {
            // only generate context menu when not a template registry
            final ContextMenu contextMenu = new ContextMenu();
            final MenuItem addMenuItem = new MenuItem("Add");
            addMenuItem.setOnAction(event -> {
                try {
                    final Message.Builder builder = ProtoBufBuilderProcessor.addDefaultInstanceToRepeatedField(fieldDescriptor, registryData.toBuilder());
                    if (builder instanceof UnitConfig.Builder) {
                        final String unitTypeName = fieldDescriptor.getName().replace("_unit_config", "").toUpperCase();
                        try {
                            UnitType unitType = UnitType.valueOf(unitTypeName);
                            ((UnitConfig.Builder) builder).setUnitType(unitType);
                        } catch (IllegalArgumentException ex) {
                            // unit type not available from field descriptor, e.g. for dal units
                            // just continue without setting the unit type
                        }
                    }
                    BuilderTreeItem builderTreeItem = AbstractBuilderTreeItem.loadTreeItem(fieldDescriptor, builder, true);
                    builderTreeItem.setExpanded(true);
                    builderTreeItem.getChildren();
                    root.getChildren().add(builderTreeItem);
                } catch (CouldNotPerformException e) {
                    e.printStackTrace();
                }
            });
            contextMenu.getItems().add(addMenuItem);
            treeTableView.setContextMenu(contextMenu);
        }

        // create a search bar for the tree table view
        searchBar = new SearchBar(treeTableView);
        searchBar.setAlignment(Pos.TOP_RIGHT);
        searchBar.setPickOnBounds(false);
        treeTableView.setOnKeyReleased(event -> {
            // make ctrl + f focus the search text field
            if (event.getCode() == KeyCode.F && event.isControlDown()) {
                Platform.runLater(() -> searchBar.getSearchTextField().requestFocus());
            }
        });

        // create stack pane with search bar in front as the content of this tab
        stackPane = new StackPane();
        stackPane.getChildren().addAll(treeTableView, searchBar);
    }


    public void update(RD registryData) throws CouldNotPerformException {
        this.registryData = registryData;
        if (root == null) {
            // not yet initialized so just set the new data type
            return;
        }

        root.update(registryData.toBuilder());

        extractRegistryFlags();
        updateStatus();
    }

    public void init() throws InitializationException {
        try {
            this.initialized = true;
            updateStatus();

            //TODO: maybe put root creation in a separate thread - high cpu usage clicking on uninitialized tab
            // takes quite long because this is done on the GUI thread
            if (fieldPathDescriptionProviders != null) {
                root = new GroupTreeItem<>(fieldDescriptor, registryData.toBuilder(), true, fieldPathDescriptionProviders);
            } else {
                root = new BuilderListTreeItem<>(fieldDescriptor, registryData.toBuilder(), true);
            }
            root.setExpanded(true);

            treeTableView.setRoot(root);

            this.setInternalContent(stackPane);
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
        return StringProcessor.transformToPascalCase(fieldDescriptor.getName()) + Tab.class.getSimpleName();
    }

    private void extractRegistryFlags() {
        try {
            final String readOnlyFieldName = fieldDescriptor.getName() + FIELD_POSTFIX_READ_ONLY;
            final FieldDescriptor readOnlyFieldDescriptor = ProtoBufFieldProcessor.getFieldDescriptor(registryData, readOnlyFieldName);
            readOnly = (Boolean) registryData.getField(readOnlyFieldDescriptor);
        } catch (NotAvailableException ex) {
            LOGGER.warn("Could no read Datatype["+registryData.getClass().getSimpleName()+"] because it does not provide a read only flag.");
            // datatype invalid so apply worse case.
            readOnly = true;
        }

        try {
            final String consistentFieldName = fieldDescriptor.getName() + FIELD_POSTFIX_CONSISTENT;
            final FieldDescriptor consistentFieldDescriptor = ProtoBufFieldProcessor.getFieldDescriptor(registryData, consistentFieldName);
            consistent = (Boolean) registryData.getField(consistentFieldDescriptor);
        } catch (NotAvailableException ex) {
            LOGGER.warn("Could no read Datatype["+registryData.getClass().getSimpleName()+"] because it does not provide a consistency flag.");
            // datatype invalid so apply worse case.
            consistent = false;
        }
    }

    private void updateStatus() {
        if (!readOnly && consistent) {
            clearStatusLabel();
            return;
        }

        // TODO: update style and set tree table view to read only
        String statusDescription = "";
        if (readOnly) {
            statusDescription += "Read Only";
        }
        if (!consistent) {
            if (!statusDescription.isEmpty()) {
                statusDescription += " & ";
            }
            statusDescription += "Inconsistent";
        }
        setStatusText(statusDescription);
    }

    public void selectMessage(final String id) {
        internalSelectMessage(id, new ArrayList<>(treeTableView.getRoot().getChildren()));
    }

    private void internalSelectMessage(final String id, final List<TreeItem<ValueType>> treeItemList) {
        TreeItem<ValueType> valueTypeTreeItem = treeItemList.get(0);
        treeItemList.remove(0);
        if (valueTypeTreeItem instanceof RegistryMessageTreeItem) {
            if (((RegistryMessageTreeItem) valueTypeTreeItem).getId().equals(id)) {
                treeTableView.getSelectionModel().select(valueTypeTreeItem);
            }
        } else {
            treeItemList.addAll(valueTypeTreeItem.getChildren());
            internalSelectMessage(id, treeItemList);
        }
    }
}
