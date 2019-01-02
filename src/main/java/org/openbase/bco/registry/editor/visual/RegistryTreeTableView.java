package org.openbase.bco.registry.editor.visual;

/*
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2019 openbase.org
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

import com.google.protobuf.*;
import com.google.protobuf.Message;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeSortMode;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.VBox;
import org.openbase.bco.registry.editor.RegistryEditorOld;
import org.openbase.bco.registry.editor.util.SendableType;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.extension.protobuf.ProtobufListDiff;
import org.openbase.jul.pattern.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openbase.type.com.ScopeType.Scope;

import java.util.ArrayList;
import java.util.List;

import static org.openbase.jul.extension.protobuf.processing.ProtoBufFieldProcessor.getFieldDescriptor;

/**
 * @param <M>  The message type to use.
 * @param <MB> The message builder type to use.
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class RegistryTreeTableView<M extends AbstractMessage, MB extends M.Builder<MB>> extends TreeTableView<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistryTreeTableView.class);

//    private final DescriptorColumn descriptorColumn;
    private final SendableType type;
    private final ProtobufListDiff<?, M, ?> listDiff;
    private final VBox vBox;
    private final Label statusInfoLabel;
    private final List<Observer<Object, Boolean>> disconnectionObserver;
    private final RegistryEditorOld registryEditor;

    public RegistryTreeTableView(SendableType type, RegistryEditorOld registryEditor) throws InstantiationException, InterruptedException {
        this.type = type;
        this.setEditable(true);
        this.setShowRoot(false);
        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.getSelectionModel().setCellSelectionEnabled(false);
        this.disconnectionObserver = new ArrayList<>();
//        this.descriptorColumn = new DescriptorColumn();
//        ValueColumn valueColumn = new ValueColumn();
//        this.getColumns().addAll(descriptorColumn, valueColumn);
        if (type != null && type != SendableType.UNIT_TEMPLATE && type != SendableType.ACTIVITY_TEMPLATE) {
            System.out.println("Set Context Menu for type [" + type.name() + "]");
//            this.setContextMenu(new TreeTableViewContextMenu(this, type));
        }
        setSortMode(TreeSortMode.ALL_DESCENDANTS);
//        getSortOrder().addAll(descriptorColumn, valueColumn);
        this.listDiff = new ProtobufListDiff<>();

        this.statusInfoLabel = new Label("Status Info Label");
        this.statusInfoLabel.setAlignment(Pos.CENTER);

        this.vBox = new VBox();
        this.vBox.setAlignment(Pos.CENTER);
        this.vBox.getChildren().addAll(statusInfoLabel, this);

        this.registryEditor = registryEditor;
    }

    public void addWidthProperty(ReadOnlyDoubleProperty widthProperty) {
//        getColumns().forEach((column) -> {
//            ((Column) column).addWidthProperty(widthProperty);
//        });
    }

    public void addHeightProperty(ReadOnlyDoubleProperty heightProperty) {
        heightProperty.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            RegistryTreeTableView.this.setPrefHeight(newValue.doubleValue());
        });
    }

//    public DescriptorColumn getDescriptorColumn() {
//        return descriptorColumn;
//    }

    public SendableType getSendableType() {
        return type;
    }

    public void update(List<M> messageList) throws CouldNotPerformException, InterruptedException {
        // TODO tamino: fix update issue and remove workaround.
        // Workaround: Hack for registry restart bug, should be removed after fixing update because empty registries can not be displayed anymore.
//        while (true) {
//            Registries.getUnitRegistry().waitForData();
//            Registries.getRegistries(true);
//            if (Registries.getUnitRegistry().getData().getDeviceUnitConfigCount() != 0) {
//                break;
//            }
//            Thread.sleep(100);
//        }
//        // =======================================
//
//        // get all changes
//        listDiff.diff(messageList);
//        // Remove all removed messages
//        for (M msg : listDiff.getRemovedMessageMap().getMessages()) {
//            NodeContainer nodeToRemove = getNodeByMessage(new ArrayList(this.getRoot().getChildren()), msg);
//            if (nodeToRemove.hasChanged()) {
//                GlobalTextArea.getInstance().setStyle("-fx-text-background-color: red");
//                GlobalTextArea.getInstance().putText("WARNING: Message [" + nodeToRemove.getBuilder().build() + "] has been removed from the global database!");
//                continue;
//            }
//            TreeItem<NodeInterface> parent = nodeToRemove.getParent();
//            parent.getChildren().remove(nodeToRemove);
//            if (SendableType.getTypeToMessage(msg) != null) {
//                TreeItem<NodeInterface> removed;
//                while (parent.getChildren().isEmpty() && parent.getParent() != null) {
//                    removed = parent;
//                    parent = (NodeContainer) parent.getParent();
//                    parent.getChildren().remove(removed);
//                }
//            }
//        }
//        for (M msg : listDiff.getUpdatedMessageMap().getMessages()) {
//            NodeContainer nodeToRemove = getNodeByMessage(new ArrayList(this.getRoot().getChildren()), msg);
//            if (nodeToRemove.hasChanged()) {
//                Platform.runLater(() -> {
//                    GlobalTextArea.getInstance().setStyle("-fx-text-background-color: red");
//                    String difference = diffString(nodeToRemove.getBuilder(), msg);
//                    try {
//                        GlobalTextArea.getInstance().putText("WARNING: Message [" + ScopeGenerator.generateStringRep(getScope(nodeToRemove.getBuilder())) + "] has been changed in the global database!\nTo discard your changes and receive the new ones press 'Cancel'\nTo overwrite the global changes with yours press 'Apply'\nDifferences between local and gloabl:\n" + difference);
//                    } catch (CouldNotPerformException ex) {
//                        GlobalTextArea.getInstance().putText("WARNING: Message with descriptor [" + nodeToRemove.getDescriptor() + "] has been changed in the global database!\nTo discard your changes and receive the new ones press 'Cancel'\nTo overwrite the global changes with yours press 'Apply'\nDifferences between local and gloabl:\n" + difference);
//                    }
//                });
//                continue;
//            }
//            GenericListContainer parent = (GenericListContainer) nodeToRemove.getParent();
//            GenericListContainer accordingParent = getAccordingParent(new ArrayList<>(this.getRoot().getChildren()), msg);
//            if (accordingParent != null) {
//                if (accordingParent.equals(parent)) {
//                    GenericNodeContainer updatedNode = new GenericNodeContainer(parent.getFieldDescriptor(), (Message.Builder) msg.toBuilder());
//                    expandEqually(nodeToRemove, updatedNode);
//                    parent.getChildren().set(parent.getChildren().indexOf(nodeToRemove), updatedNode);
//                } else {
//                    parent.getChildren().remove(nodeToRemove);
//                    accordingParent.registerElement(msg.toBuilder());
//                }
//            } else {
//                // according parent either is null if no group exists where aunit can be placed or there are no groups for this tableview
//                if (this.getRoot() instanceof GenericGroupContainer) {
//                    parent.getChildren().remove(nodeToRemove);
//                    GenericGroupContainer rootNode = (GenericGroupContainer) this.getRoot();
//                    rootNode.addItemWithNewGroup(msg);
//                } else {
//                    GenericNodeContainer updatedNode = new GenericNodeContainer(parent.getFieldDescriptor(), (Message.Builder) msg.toBuilder());
//                    expandEqually(nodeToRemove, updatedNode);
//                    parent.getChildren().set(parent.getChildren().indexOf(nodeToRemove), updatedNode);
//                }
//            }
//        }
//        for (M msg : listDiff.getNewMessageMap().getMessages()) {
//            if (msg instanceof UnitConfig) {
//                UnitConfig a = (UnitConfig) msg;
//                LOGGER.info("New Message [" + a.getUnitType().name() + ", " + a.getLabel() + "]");
//            }
//            //logger.info("New message [" + message + "]");
//            if (this.getRoot() instanceof GenericGroupContainer) {
//                GenericListContainer parent = getAccordingParent(new ArrayList<>(this.getRoot().getChildren()), msg);
//                //parent is null if the new entry belongs to a group that did not exists before
//                if (parent == null) {
//                    GenericGroupContainer rootNode = (GenericGroupContainer) this.getRoot();
//                    rootNode.addItemWithNewGroup(msg);
//                } else {
//                    parent.registerElement(msg.toBuilder());
//                }
//            } else {
//                if (getSendableType() == SendableType.UNIT_TEMPLATE) {
//                    ((GenericListContainer) this.getRoot()).registerElement(msg.toBuilder(), ((UnitTemplate) msg).getType().name());
//                } else if (getSendableType() == SendableType.SERVICE_TEMPLATE) {
//                    ((GenericListContainer) this.getRoot()).registerElement(msg.toBuilder(), ((ServiceTemplate) msg).getType().name());
//                } else {
//                    ((GenericListContainer) this.getRoot()).registerElement(msg.toBuilder());
//                }
//            }
//        }
//
//        setReadOnlyMode(Registries.isReadOnly(type.getDefaultInstanceForType()));
    }

    public String diffString(MessageOrBuilder origin, MessageOrBuilder updated) {
        String res = "";
        List<String> diff = diff(origin, updated);
        for (int i = 0; i < diff.size(); i++) {
            if (i == diff.size() - 1) {
                res += diff.get(i);
            } else {
                res += diff.get(i) + "\n";
            }
        }
        return res;
    }

    public List<String> diff(MessageOrBuilder origin, MessageOrBuilder updated) {
        List<String> fieldDiffList = new ArrayList<>();
        for (Descriptors.FieldDescriptor field : origin.getDescriptorForType().getFields()) {
            String fieldName = field.getName();
            if (field.isRepeated()) {
                int originCount = origin.getRepeatedFieldCount(field);
                int updatedCount = updated.getRepeatedFieldCount(field);
                if (originCount != updatedCount) {
                    fieldDiffList.add(fieldName + ": Different counts[" + originCount + ", " + updatedCount + "]. Difference may be incorrect!");
                }
                if (field.getType() == Descriptors.FieldDescriptor.Type.MESSAGE) {
                    for (int i = 0; i < Math.min(originCount, updatedCount); ++i) {
                        for (String diff : diff((Message) origin.getRepeatedField(field, i), (Message) updated.getRepeatedField(field, i))) {
                            fieldDiffList.add(fieldName + "[" + i + "]." + diff);
                        }
                    }
                } else {
                    for (int i = 0; i < Math.min(originCount, updatedCount); ++i) {
                        if (!origin.getRepeatedField(field, i).equals(updated.getRepeatedField(field, i))) {
                            fieldDiffList.add(fieldName + "[" + i + "]" + "\t[" + origin.getRepeatedField(field, i).toString() + "]\t[" + updated.getRepeatedField(field, i).toString() + "]");
                        }
                    }
                }
            } else {
                if (field.getType() == Descriptors.FieldDescriptor.Type.MESSAGE) {
                    for (String diff : diff((Message) origin.getField(field), (Message) updated.getField(field))) {
                        fieldDiffList.add(fieldName + "." + diff);
                    }
                } else {
                    if (!origin.getField(field).equals(updated.getField(field))) {
                        fieldDiffList.add(fieldName + "\t[" + origin.getField(field).toString() + "]\t[" + updated.getField(field).toString() + "]");
                    }
                }
            }
        }
        return fieldDiffList;
    }

    public Scope getScope(final MessageOrBuilder msg) throws CouldNotPerformException {
        try {
            return (Scope) msg.getField(getFieldDescriptor(msg, "scope"));
        } catch (Exception ex) {
            throw new CouldNotPerformException("Could not get label of [" + msg + "]", ex);
        }
    }

//    public void selectMessage(Message msg) throws CouldNotPerformException {
//        TreeItem container = getNodeByMessage(new ArrayList<>(getRoot().getChildren()), msg);
//        container.setExpanded(true);
//        while (container.getParent() != null) {
//            container = container.getParent();
//            container.setExpanded(true);
//        }
//    }

//    private NodeContainer getNodeByMessage(List<TreeItem<NodeInterface>> nodes, Message msg) {
//        if (nodes.isEmpty()) {
//            return null;
//        }
//
//        try {
//            // if the search has progressed too far leaf containers can be under the searched nodes
//            // they are ignored and the remaining nodes are tested
//            if (!(nodes.get(0) instanceof NodeContainer)) {
//                nodes.remove(0);
//                return getNodeByMessage(nodes, msg);
//            }
//
//            if (ProtoBufFieldProcessor.getId(msg).equals(ProtoBufFieldProcessor.getId(((NodeContainer) nodes.get(0)).getBuilder()))) {
//                return (NodeContainer) nodes.get(0);
//            } else {
//                nodes.addAll(nodes.get(0).getChildren());
//                nodes.remove(0);
//                return getNodeByMessage(nodes, msg);
//            }
//        } catch (CouldNotPerformException ex) {
//            // all searched messages must have an id field... else this method will run over all treeitems and then return null
//            nodes.addAll(nodes.get(0).getChildren());
//            nodes.remove(0);
//            return getNodeByMessage(nodes, msg);
//        }
//    }

//    private GenericListContainer getAccordingParent(List<TreeItem<NodeInterface>> nodes, Message msg) throws CouldNotPerformException, InterruptedException {
//        if (nodes.isEmpty()) {
//            return null;
//        }
//
//        if (nodes.get(0) instanceof GenericGroupContainer) {
//            GenericGroupContainer group = (GenericGroupContainer) nodes.get(0);
//            GenericGroupContainer parent = (GenericGroupContainer) group.getParent();
//            if (group.getDescriptor().equals(parent.getFieldGroup().getDescriptor(msg))) {
//                return getAccordingParent(new ArrayList<>(group.getChildren()), msg);
//            } else {
//                nodes.remove(0);
//                return getAccordingParent(nodes, msg);
//            }
//        } else if (nodes.get(0) instanceof GenericListContainer) {
//            GenericListContainer list = (GenericListContainer) nodes.get(0);
//            GenericGroupContainer parent = (GenericGroupContainer) list.getParent();
//            if (list.getDescriptor().equals(parent.getFieldGroup().getDescriptor(msg))) {
//                return list;
//            } else {
//                nodes.remove(0);
//                return getAccordingParent(nodes, msg);
//            }
//        }
//        return null;
//    }

//    public static void expandEqually(TreeItem<NodeInterface> origin, TreeItem<NodeInterface> update) {
//        if (origin.isExpanded()) {
//            update.setExpanded(true);
//        }
//
//        for (TreeItem<NodeInterface> originChild : origin.getChildren()) {
//            for (TreeItem<NodeInterface> updatedChild : update.getChildren()) {
//                if (originChild.getValue().getDescriptor().equals(updatedChild.getValue().getDescriptor())) {
//                    expandEqually(originChild, updatedChild);
//                }
//            }
//        }
//    }

    public void setReadOnlyMode(boolean readOnly) {
        RegistryEditorOld.runOnFxThread(() -> {
            try {
                if (!Registries.isConsistent(type.getDefaultInstanceForType())) {
                    statusInfoLabel.setText("Registry inconsistent!");
                    statusInfoLabel.setStyle("-fx-text-background-color: rgb(255,0,0); -fx-font-weight: bold;");
                } else {
                    statusInfoLabel.setText("Read-Only-Mode");
                    statusInfoLabel.setStyle("-fx-text-background-color: rgb(255,128,0); -fx-font-weight: bold;");
                }
            } catch (CouldNotPerformException ex) {
                ExceptionPrinter.printHistory("Failed to call is consistent for registry [" + type.name() + "]", ex, LOGGER);
            }
            if (readOnly) {
                getStylesheets().clear();
                getStylesheets().add("read_only.css");
                setContextMenu(null);
            } else {
                getStylesheets().clear();
                getStylesheets().add("default.css");
//                if (type != null && type != SendableType.UNIT_TEMPLATE && type != SendableType.ACTIVITY_TEMPLATE) {
//                    setContextMenu(new TreeTableViewContextMenu(RegistryTreeTableView.this, type));
//                }
            }
            setEditableWithReadOnlyLabel(!readOnly);
            return null;
        });
    }

    public void setEditableWithReadOnlyLabel(final boolean editable) {
        RegistryEditorOld.runOnFxThread(() -> {
            if (!editable) {
                vBox.getChildren().clear();
                vBox.getChildren().addAll(statusInfoLabel, this);
            } else {
                vBox.getChildren().remove(statusInfoLabel);
            }
            super.setEditable(editable);
            return null;
        });
    }

    public void setDisconnected(boolean disconnected) {
        RegistryEditorOld.runOnFxThread(() -> {
            if (disconnected) {
                statusInfoLabel.setText("Registry disconnected!");
                statusInfoLabel.setStyle("-fx-text-background-color: rgb(120,120,120); -fx-font-weight: bold;");
                vBox.getChildren().clear();
                vBox.getChildren().addAll(statusInfoLabel, this);
            } else {
                vBox.getChildren().clear();
                vBox.getChildren().addAll(this);
            }

            notifiyDisconnection(disconnected);
            return null;
        });
    }

    public void addDisconnectedObserver(Observer<Object, Boolean> observer) {
        disconnectionObserver.add(observer);
    }

    public void removeDisconnectedObserver(Observer<Object, Boolean> observer) {
        disconnectionObserver.remove(observer);
    }

    private void notifiyDisconnection(Boolean connected) {
        for (Observer<Object, Boolean> observer : disconnectionObserver) {
            try {
                observer.update(null, connected);
            } catch (Exception ex) {
                LOGGER.warn("Could not notify connection to value cell!");
            }
        }
    }

    public RegistryEditorOld getRegistryEditor() {
        return registryEditor;
    }

    public Label getStatusInfoLabel() {
        return statusInfoLabel;
    }

    public VBox getVBox() {
        return vBox;
    }

    public ProtobufListDiff getListDiff() {
        return listDiff;
    }
}
