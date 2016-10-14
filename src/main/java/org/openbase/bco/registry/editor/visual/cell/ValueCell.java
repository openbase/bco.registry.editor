package org.openbase.bco.registry.editor.visual.cell;

/*
 * #%L
 * RegistryEditor
 * %%
 * Copyright (C) 2014 - 2016 openbase.org
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
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.openbase.bco.registry.editor.RegistryEditor;
import org.openbase.bco.registry.editor.struct.GenericGroupContainer;
import org.openbase.bco.registry.editor.struct.GenericNodeContainer;
import org.openbase.bco.registry.editor.struct.Leaf;
import org.openbase.bco.registry.editor.struct.LeafContainer;
import org.openbase.bco.registry.editor.struct.Node;
import org.openbase.bco.registry.editor.struct.consistency.Configuration;
import org.openbase.bco.registry.editor.util.SelectableLabel;
import org.openbase.bco.registry.editor.visual.GlobalTextArea;
import org.openbase.bco.registry.editor.visual.RegistryTreeTableView;
import org.openbase.bco.registry.editor.visual.cell.editing.DecimalTextField;
import org.openbase.bco.registry.editor.visual.cell.editing.EnumComboBox;
import org.openbase.bco.registry.editor.visual.cell.editing.LongDatePicker;
import org.openbase.bco.registry.editor.visual.cell.editing.MessageComboBox;
import org.openbase.bco.registry.editor.visual.cell.editing.StringTextField;
import org.openbase.bco.registry.editor.visual.cell.editing.UserConfigComboBoxConverter;
import org.openbase.bco.registry.editor.visual.cell.editing.ValueCheckBox;
import org.openbase.bco.registry.editor.visual.provider.DeviceClassItemDescriptorProvider;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.jul.extension.protobuf.processing.ProtoBufFieldProcessor;
import org.openbase.jul.extension.rsb.scope.ScopeGenerator;
import org.openbase.jul.schedule.GlobalExecutionService;
import rst.configuration.EntryType;
import rst.domotic.state.InventoryStateType.InventoryState;
import rst.domotic.unit.authorizationgroup.AuthorizationGroupConfigType.AuthorizationGroupConfig;

/**
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class ValueCell extends RowCell {

    protected final Button applyButton, cancelButton;
    protected final HBox buttonLayout;
    protected LeafContainer leaf;
    private javafx.scene.control.Control graphic;

    protected SimpleObjectProperty<Boolean> changed = null;
    protected final ChangeListener<Boolean> changeListener;
    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public ValueCell() throws InterruptedException {
        super();
        applyButton = new Button("Apply");
//        ((RegistryTreeTableView) this.getTableColumn().getTreeTableView()).addDisconnectedObserver(new Observer<Boolean>() {
//            
//            @Override
//            public void update(Observable<Boolean> source, Boolean data) throws Exception {
//                applyButton.setDisable(data);
//            }
//        });
        applyButton.setOnAction(new ApplyEventHandler());
        cancelButton = new Button("Cancel");
        cancelButton.setOnAction(new CancelEventHandler());
        buttonLayout = new HBox(applyButton, cancelButton);
        this.changeListener = new ChangedListener();
    }

    @Override
    public void startEdit() {
        super.startEdit();

        if (getItem() instanceof Leaf && ((LeafContainer) getItem()).getEditable()) {
            leaf = ((LeafContainer) getItem());
            setGraphic(getEditingGraphic());
        }
    }

    private javafx.scene.control.Control getEditingGraphic() {
        graphic = null;
        Message type = MessageComboBox.getMessageEnumBoxType(leaf.getDescriptor(), leaf.getParent().getBuilder());
        if (type != null) {
            try {
                graphic = new MessageComboBox(this, leaf.getParent().getBuilder(), leaf.getDescriptor());
            } catch (InstantiationException ex) {
                RegistryEditor.printException(ex, logger, LogLevel.ERROR);
            }
        } else if (leaf.getValue() instanceof String) {
            graphic = new StringTextField(this, (String) leaf.getValue());
        } else if (leaf.getValue() instanceof EnumValueDescriptor) {
            graphic = new EnumComboBox(this, (EnumValueDescriptor) leaf.getValue());
        } else if (leaf.getValue() instanceof Float || leaf.getValue() instanceof Double) {
            graphic = new DecimalTextField(this, leaf.getValue().toString());
        } else if (leaf.getValue() instanceof Long) {
            graphic = new LongDatePicker(this, (Long) leaf.getValue());
        } else if (leaf.getValue() instanceof Boolean) {
            graphic = new ValueCheckBox(this, true, false);
        }
        if (graphic != null) {
            graphic.setPrefWidth(this.getWidth() * 5 / 8);
            // if still not in focus -> wrap in platform run later
//            Platform.runLater(new Runnable() {
//
//                @Override
//                public void run() {
//                    graphic.requestFocus();
//                }
//            });
        }
        return graphic;
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setGraphic(null);
    }

    @Override
    public void updateItem(Node item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setGraphic(null);
            setText("");
            setContextMenu(null);
        } else if (item instanceof Leaf) {
            String text = "";
            if (((Leaf) item).getValue() instanceof Long) {
                text = LongDatePicker.DATE_CONVERTER.format(new Date((Long) ((Leaf) item).getValue()));
            } else if (((Leaf) item).getValue() instanceof Double) {
                text = decimalFormat.format(((Double) ((Leaf) item).getValue()));
            } else if (((Leaf) item).getValue() instanceof EnumValueDescriptor) {
                text = (((EnumValueDescriptor) ((Leaf) item).getValue()).getName());
            } else if ((((Leaf) item).getValue() != null)) {
                if ("location_id".equals(item.getDescriptor()) || "parent_id".equals(item.getDescriptor()) || "child_id".equals(item.getDescriptor()) || "tile_id".equals(item.getDescriptor())) {
                    try {
                        text = ScopeGenerator.generateStringRep(remotePool.getLocationRemote().getLocationConfigById((String) ((Leaf) item).getValue()).getScope());
                    } catch (CouldNotPerformException ex) {
                        text = ((Leaf) item).getValue().toString();
                    }
                } else if ("member_id".equals(item.getDescriptor()) && ((LeafContainer) item).getParent().getBuilder() instanceof AuthorizationGroupConfig.Builder) {
                    try {
                        text = new UserConfigComboBoxConverter().getText(remotePool.getUserRemote().getUserConfigById((String) ((Leaf) item).getValue()));
                    } catch (CouldNotPerformException ex) {
                        text = ((Leaf) item).getValue().toString();
                    }
                } else if ("owner_id".equals(item.getDescriptor()) && ((LeafContainer) item).getParent().getBuilder() instanceof InventoryState.Builder) {
                    try {
                        text = new UserConfigComboBoxConverter().getText(remotePool.getUserRemote().getUserConfigById((String) ((Leaf) item).getValue()));
                    } catch (CouldNotPerformException ex) {
                        text = ((Leaf) item).getValue().toString();
                    }
                } else {
                    text = ((Leaf) item).getValue().toString();
                }
            }

            if (((LeafContainer) item).getEditable()) {
                setText(text);
                setGraphic(null);
            } else {
                setGraphic(SelectableLabel.makeSelectable(new Label(text)));
            }
        }

        if (item instanceof GenericNodeContainer) {
            GenericNodeContainer container = (GenericNodeContainer) item;
            String text = getBuilderDescription(container.getBuilder());
            if (text != null) {
                setGraphic(SelectableLabel.makeSelectable(new Label(text)));
            } else {
                setText("");
                setGraphic(null);
            }
            if (container.isSendable()) {
                updateButtonListener(container.getChanged());
                if (container.hasChanged()) {
                    setGraphic(buttonLayout);
                }

                try {
                    if ("".equals(ProtoBufFieldProcessor.getId(container.getBuilder()))) {
                        container.setChanged(true);
                    }
                } catch (CouldNotPerformException ex) {
                    RegistryEditor.printException(ex, logger, LogLevel.WARN);
                }
            } else {
                updateButtonListener(null);
            }
        } else {
            updateButtonListener(null);
        }

        // ==================== TODO:tamino redesign
        if (item instanceof GenericGroupContainer) {
            if (((GenericGroupContainer) item).getParent().getValue() instanceof GenericGroupContainer) {
                GenericGroupContainer parent = (GenericGroupContainer) ((GenericGroupContainer) item).getParent().getValue();
                if (parent.getFieldGroup() instanceof DeviceClassItemDescriptorProvider) {
                    try {
                        String text = remotePool.getDeviceRemote().getDeviceClassById((String) parent.getValueMap().get(getItem())).getDescription();
                        setGraphic(SelectableLabel.makeSelectable(new Label(text)));
                    } catch (CouldNotPerformException ex) {
                        RegistryEditor.printException(ex, logger, LogLevel.DEBUG);
                    }
                }
            }
        }
        // ============================================
    }

    public String getBuilderDescription(Message.Builder builder) {
        if (builder instanceof EntryType.Entry.Builder) {
            EntryType.Entry.Builder entry = (EntryType.Entry.Builder) builder;
            return entry.getKey() + " = " + entry.getValue();
        } else if (Configuration.isSendable(builder)) {
            try {
                return ProtoBufFieldProcessor.getDescription(builder);
            } catch (CouldNotPerformException ex) {
            }
        }
        return null;
    }

    public LeafContainer getLeaf() {
        return leaf;
    }

    private void updateButtonListener(SimpleObjectProperty<Boolean> property) {
        if (changed != null) {
            changed.removeListener(changeListener);
        }
        changed = property;
        if (changed != null) {
            changed.addListener(changeListener);
        }
    }

    private class ApplyEventHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            logger.debug("new apply event");
            GlobalTextArea.getInstance().clearText();
            GenericNodeContainer container = (GenericNodeContainer) getItem();
            Builder builder = container.getBuilder();

            if (!builder.isInitialized()) {
                if (ProtoBufFieldProcessor.checkIfSomeButNotAllRequiredFieldsAreSet(builder)) {
                    List<String> missingFieldList = builder.findInitializationErrors();
                    String missingFields = "";
                    missingFields = missingFieldList.stream().map((error) -> error + "\n").reduce(missingFields, String::concat);

                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.setResizable(true);
                    alert.setTitle("Message initialization error!");
                    alert.setHeaderText("Missing some required fields!");
                    alert.setContentText("Initialize them with default values or clear them?");

                    ButtonType initButton = new ButtonType("Init");
                    ButtonType clearButton = new ButtonType("Clear");
                    ButtonType cancelButton = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
                    alert.getButtonTypes().setAll(initButton, clearButton, cancelButton);

                    TextArea textArea = new TextArea(missingFields);
                    textArea.setEditable(false);
                    textArea.setWrapText(true);
                    Label label = new Label("Missing fields:");

                    textArea.setMaxWidth(Double.MAX_VALUE);
                    textArea.setMaxHeight(Double.MAX_VALUE);
                    GridPane.setVgrow(textArea, Priority.ALWAYS);
                    GridPane.setHgrow(textArea, Priority.ALWAYS);

                    GridPane expContent = new GridPane();
                    expContent.setMaxWidth(Double.MAX_VALUE);
                    expContent.add(label, 0, 0);
                    expContent.add(textArea, 0, 1);

                    alert.getDialogPane().setExpandableContent(expContent);

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == clearButton) {
                        ProtoBufFieldProcessor.clearRequiredFields(builder);
                    } else if (result.get() == initButton) {
                        ProtoBufFieldProcessor.initRequiredFieldsWithDefault(builder);
                    } else {
                        return;
                    }
                } else {
                    while (!builder.findInitializationErrors().isEmpty()) {
                        ProtoBufFieldProcessor.clearRequiredFields(builder);
                    }
                }
            }

            GlobalExecutionService.submit(new Callable<Boolean>() {

                @Override
                public Boolean call() throws Exception {
                    GenericNodeContainer container = (GenericNodeContainer) getItem();
                    Message msg;

                    try {
                        msg = container.getBuilder().build();
                        container.setChanged(false);
                        if (remotePool.contains(msg)) {
                            remotePool.update(msg).get();
                        } else {
                            msg = remotePool.register(msg).get();
                            System.out.println("Succesfully registered new message:\n" + msg);
                            container.getParent().getChildren().remove(container);
                        }
                    } catch (CouldNotPerformException ex) {
                        RegistryEditor.printException(ex, logger, LogLevel.ERROR);
                        container.setChanged(true);
                    }
                    return true;
                }
            });
        }
    }

    private class CancelEventHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            GlobalTextArea.getInstance().clearText();
            GlobalExecutionService.submit(new Callable<Boolean>() {

                @Override
                public Boolean call() throws Exception {
                    GenericNodeContainer container = (GenericNodeContainer) getItem();
                    try {
                        if ("".equals(ProtoBufFieldProcessor.getId(container.getBuilder()))) {
                            container.getParent().getChildren().remove(container);
                        } else {
                            int index = container.getParent().getChildren().indexOf(container);
                            GenericNodeContainer oldNode = new GenericNodeContainer(container.getBuilder().getDescriptorForType().getName(), (GeneratedMessage.Builder) remotePool.getById(ProtoBufFieldProcessor.getId(container.getBuilder()), container.getBuilder()).toBuilder());
                            RegistryTreeTableView.expandEqually(container, oldNode);
                            container.getParent().getChildren().set(index, oldNode);
                        }
                    } catch (Exception ex) {
                        RegistryEditor.printException(ex, logger, LogLevel.ERROR);
                        logger.warn("Could not cancel update of [" + container.getBuilder() + "]", ex);
                    }
                    return true;
                }
            });
        }
    }

    private class ChangedListener implements ChangeListener<Boolean> {

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    if (newValue) {
                        setGraphic(buttonLayout);
                    } else {
                        setGraphic(null);
                    }
                }
            });
        }
    }
}
