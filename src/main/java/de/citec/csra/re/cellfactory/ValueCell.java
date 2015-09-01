/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.cellfactory;

import com.google.protobuf.Message;
import de.citec.csra.re.RegistryEditor;
import de.citec.csra.re.cellfactory.editing.DecimalTextField;
import de.citec.csra.re.cellfactory.editing.EnumComboBox;
import de.citec.csra.re.cellfactory.editing.LongDatePicker;
import de.citec.csra.re.cellfactory.editing.StringTextField;
import de.citec.csra.re.cellfactory.editing.ValueCheckBox;
import de.citec.csra.re.struct.GenericNodeContainer;
import de.citec.csra.re.struct.Leaf;
import de.citec.csra.re.struct.LeafContainer;
import de.citec.csra.re.struct.Node;
import de.citec.csra.re.util.SelectableLabel;
import de.citec.jul.exception.CouldNotPerformException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.Date;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import rst.configuration.EntryType;
import rst.homeautomation.state.ActivationStateType.ActivationState;

/**
 *
 * @author thuxohl
 */
public abstract class ValueCell extends RowCell {

    protected final Button applyButton, cancelButton;
    protected final HBox buttonLayout;
    protected LeafContainer leaf;

    protected SimpleObjectProperty<Boolean> changed = null;
    protected final ChangeListener<Boolean> changeListener;
    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public ValueCell() {
        super();
        applyButton = new Button("Apply");
        applyButton.setOnAction(null);
        cancelButton = new Button("Cancel");
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

    private javafx.scene.Node getEditingGraphic() {

        // TODO: check for cases where a message combo box is needed, e.g. location_id fields
        javafx.scene.Node graphic = null;
        if (leaf.getValue() instanceof String) {
            graphic = new StringTextField(this, (String) leaf.getValue());
        } else if (leaf.getValue() instanceof Enum) {
            if (leaf.getParent().getBuilder() instanceof ActivationState.Builder) {
                graphic = new ValueCheckBox(this, ActivationState.State.ACTIVE, ActivationState.State.DEACTIVE);
            } else {
                graphic = new EnumComboBox(this, leaf.getValue().getClass());
            }
        } else if (leaf.getValue() instanceof Float || leaf.getValue() instanceof Double) {
            graphic = new DecimalTextField(this, leaf.getValue().toString());
        } else if (leaf.getValue() instanceof Long) {
            graphic = new LongDatePicker(this, (Long) leaf.getValue());
        } else if (leaf.getValue() instanceof Boolean) {
            graphic = new ValueCheckBox(this, true, false);
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
            } else if ((((Leaf) item).getValue() != null)) {
                text = ((Leaf) item).getValue().toString();
            }
//            setText(text);
            setGraphic(new SelectableLabel(text));
        }

        if (item instanceof GenericNodeContainer) {
            GenericNodeContainer container = (GenericNodeContainer) item;
            String text = getBuilderDescription(container.getBuilder());
            if (text != null) {
                setGraphic(new SelectableLabel(text));
            } else {
                setGraphic(null);
            }
            if (container.isSendable()) {
                updateButtonListener(container.getChanged());
            } else {
                updateButtonListener(null);
            }
        } else {
            updateButtonListener(null);
        }
    }

    public String getBuilderDescription(Message.Builder builder) {
        if (builder instanceof EntryType.Entry.Builder) {
            EntryType.Entry.Builder entry = (EntryType.Entry.Builder) builder;
            return entry.getKey() + " = " + entry.getValue();
        } else if (remotePool.isSendableMessage(builder)) {
            try {
                return getDescription(builder);
            } catch (CouldNotPerformException ex) {
            }
        }
        return null;
    }

    public LeafContainer getLeaf() {
        return leaf;
    }

    public void commitEdit() {
        super.commitEdit(leaf);
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
            Thread thread = new Thread(
                    new Task<Boolean>() {
                        @Override
                        protected Boolean call() throws Exception {
                            RegistryEditor.setModified(false);
                            GenericNodeContainer container = (GenericNodeContainer) getItem();
                            Message msg = container.getBuilder().build();
                            try {
                                if (remotePool.contains(msg)) {
                                    remotePool.update(msg);
                                } else {
                                    remotePool.register(msg);
                                }
                                container.setChanged(false);
                            } catch (CouldNotPerformException ex) {
                                logger.warn("Could not register or update message [" + msg + "]", ex);
                            }
                            return true;
                        }
                    });
            thread.setDaemon(true);
            thread.start();
        }
    }

    private class CancelEventHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            Thread thread = new Thread(
                    new Task<Boolean>() {
                        @Override
                        protected Boolean call() throws Exception {
                            RegistryEditor.setModified(false);
                            GenericNodeContainer container = (GenericNodeContainer) getItem();
                            Message msg = container.getBuilder().build();
                            try {
                                if (!remotePool.contains(msg)) {
                                    container.getParent().getChildren().remove(container);
                                } else {
                                    int index = container.getParent().getChildren().indexOf(container);
                                    container.getParent().getChildren().set(index, new GenericNodeContainer("", remotePool.getById(getId(msg), msg)));
                                }
                            } catch (CouldNotPerformException ex) {
                                logger.warn("Could cancel update of [" + msg + "]", ex);
                            }
                            return true;
                        }
                    });
            thread.setDaemon(true);
            thread.start();
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

    public String getId(Message msg) throws CouldNotPerformException {
        try {
            Method method = msg.getClass().getMethod("getId");
            return (String) method.invoke(msg);
        } catch (IllegalAccessException | NoSuchMethodException | IllegalArgumentException | InvocationTargetException ex) {
            throw new CouldNotPerformException("Could not get id of [" + msg + "]", ex);
        }
    }

    public String getDescription(Message.Builder msg) throws CouldNotPerformException {
        try {
            Method method = msg.getClass().getMethod("getDescription");
            return (String) method.invoke(msg);
        } catch (IllegalAccessException | NoSuchMethodException | IllegalArgumentException | InvocationTargetException ex) {
            throw new CouldNotPerformException("Could not get description of [" + msg + "]", ex);
        }
    }
}
