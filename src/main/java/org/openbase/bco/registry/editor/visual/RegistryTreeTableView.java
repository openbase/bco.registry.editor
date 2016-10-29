package org.openbase.bco.registry.editor.visual;

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
import com.google.protobuf.GeneratedMessage;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeSortMode;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.VBox;
import org.openbase.bco.registry.editor.RegistryEditor;
import org.openbase.bco.registry.editor.struct.GenericGroupContainer;
import org.openbase.bco.registry.editor.struct.GenericListContainer;
import org.openbase.bco.registry.editor.struct.GenericNodeContainer;
import org.openbase.bco.registry.editor.struct.Node;
import org.openbase.bco.registry.editor.struct.NodeContainer;
import org.openbase.bco.registry.editor.util.RemotePool;
import org.openbase.bco.registry.editor.util.SendableType;
import org.openbase.bco.registry.editor.visual.column.Column;
import org.openbase.bco.registry.editor.visual.column.DescriptorColumn;
import org.openbase.bco.registry.editor.visual.column.ValueColumn;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.extension.protobuf.ProtobufListDiff;
import org.openbase.jul.extension.protobuf.processing.ProtoBufFieldProcessor;
import org.openbase.jul.pattern.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class RegistryTreeTableView extends TreeTableView<Node> {

    private static final Logger logger = LoggerFactory.getLogger(RegistryTreeTableView.class);

    private final DescriptorColumn descriptorColumn;
    private final SendableType type;
    private final ProtobufListDiff listDiff;
    private final VBox vBox;
    private final RemotePool remotePool;
    private final Label statusInfoLabel;
    private final List<Observer<Boolean>> disconnectionObserver;
    private final RegistryEditor registryEditor;

    public RegistryTreeTableView(SendableType type, RegistryEditor registryEditor) throws InstantiationException, InterruptedException {
        this.type = type;
        this.setEditable(true);
        this.setShowRoot(false);
        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.getSelectionModel().setCellSelectionEnabled(false);
        this.disconnectionObserver = new ArrayList<>();
        this.descriptorColumn = new DescriptorColumn();
        ValueColumn valueColumn = new ValueColumn();
        this.getColumns().addAll(descriptorColumn, valueColumn);
        if (type != null) {
            this.setContextMenu(new TreeTableViewContextMenu(this, type));
        }
        setSortMode(TreeSortMode.ALL_DESCENDANTS);
        getSortOrder().addAll(descriptorColumn, valueColumn);
        this.listDiff = new ProtobufListDiff();

        this.statusInfoLabel = new Label("Status Info Label");
        this.statusInfoLabel.setAlignment(Pos.CENTER);

        this.vBox = new VBox();
        this.vBox.setAlignment(Pos.CENTER);
        this.vBox.getChildren().addAll(statusInfoLabel, this);

        this.remotePool = RemotePool.getInstance();

        this.registryEditor = registryEditor;
    }

    public void addWidthProperty(ReadOnlyDoubleProperty widthProperty) {
        for (Object column : getColumns()) {
            ((Column) column).addWidthProperty(widthProperty);
        }
    }

    public void addHeightProperty(ReadOnlyDoubleProperty heightProperty) {
        heightProperty.addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                RegistryTreeTableView.this.setPrefHeight(newValue.doubleValue());
            }
        });
    }

    public DescriptorColumn getDescriptorColumn() {
        return descriptorColumn;
    }

    public SendableType getSendableType() {
        return type;
    }

    public void update(List<? extends GeneratedMessage> messageList) throws CouldNotPerformException, InterruptedException {
        // get all changes
        listDiff.diff(messageList);
        // Remove all removed messages
        for (Object message : listDiff.getRemovedMessageMap().getMessages()) {
//            logger.info("Removed message [" + message + "]");
            GeneratedMessage msg = (GeneratedMessage) message;
            NodeContainer nodeToRemove = getNodeByMessage(new ArrayList(this.getRoot().getChildren()), msg);
            if (nodeToRemove.hasChanged()) {
                GlobalTextArea.getInstance().setStyle("-fx-text-background-color: red");
                GlobalTextArea.getInstance().putText("WARNING: Message [" + nodeToRemove.getBuilder().build() + "] has been removed from the global database!");
                continue;
            }
            TreeItem<Node> parent = nodeToRemove.getParent();
            parent.getChildren().remove(nodeToRemove);
            if (SendableType.getTypeToMessage(msg) != null) {
                TreeItem<Node> removed;
                while (parent.getChildren().isEmpty() && parent.getParent() != null) {
                    removed = parent;
                    parent = (NodeContainer) parent.getParent();
                    parent.getChildren().remove(removed);
                }
            }
        }
        for (Object message : listDiff.getUpdatedMessageMap().getMessages()) {
//            logger.info("Updated message [" + message + "]");
            GeneratedMessage msg = (GeneratedMessage) message;
            NodeContainer nodeToRemove = getNodeByMessage(new ArrayList(this.getRoot().getChildren()), msg);
//            logger.info("Found old node to remove to update [" + nodeToRemove.getBuilder().build() + "]");
            if (nodeToRemove.hasChanged()) {
                GlobalTextArea.getInstance().setStyle("-fx-text-background-color: red");
                GlobalTextArea.getInstance().putText("WARNING: Message [" + nodeToRemove.getBuilder().build() + "] has been changed in the global database!\nTo discard your changes and receive the new ones press 'Cancel'\nTo overwrite the global changes with yours press 'Apply'");
                continue;
            }
            GenericListContainer parent = (GenericListContainer) nodeToRemove.getParent();
            GenericNodeContainer updatedNode = new GenericNodeContainer(parent.getFieldDescriptor(), (GeneratedMessage.Builder) msg.toBuilder());
            expandEqually(nodeToRemove, updatedNode);
            parent.getChildren().set(parent.getChildren().indexOf(nodeToRemove), updatedNode);
        }
        for (Object message : listDiff.getNewMessageMap().getMessages()) {
            //logger.info("New message [" + message + "]");
            GeneratedMessage msg = (GeneratedMessage) message;
            if (this.getRoot() instanceof GenericGroupContainer) {
                GenericListContainer parent = getAccordingParent(new ArrayList<>(this.getRoot().getChildren()), msg);
                //parent is null if the new entry belongs to a group that did not exists before
                if (parent == null) {
//                    GenericGroupContainer test = (GenericGroupContainer) this.getRoot();
//                    List<GeneratedMessage.Builder> singleList = new ArrayList<>();
//                    GeneratedMessage.Builder hallo = (GeneratedMessage.Builder) msg.toBuilder();
//                    singleList.add(hallo);
//                    if (test.isLastGroup()) {
//                        this.getRoot().add(new GenericListContainer<>(test.getFieldGroup().getDescriptor(msg.toBuilder()), test.getFieldGroup(), test.getBuilder(), singleList));
//                    } else {
//                        this.getRoot().add(new GenericGroupContainer<>(test.getFieldGroup().getDescriptor(msg.toBuilder()), test.getFieldGroup(), test.getBuilder(), singleList, test.getChildGroups()));
//                    }
                } else {
                    parent.registerElement(msg.toBuilder());
                }
            } else {
                ((GenericListContainer) this.getRoot()).registerElement(msg.toBuilder());
            }
        }

        setReadOnlyMode(remotePool.isReadOnly(type.getDefaultInstanceForType()));
    }

    public void selectMessage(GeneratedMessage msg) throws CouldNotPerformException {
        TreeItem container = getNodeByMessage(new ArrayList<>(getRoot().getChildren()), msg);
        container.setExpanded(true);
        while (container.getParent() != null) {
            container = container.getParent();
            container.setExpanded(true);
        }
    }

    private NodeContainer getNodeByMessage(List<TreeItem<Node>> nodes, GeneratedMessage msg) {
        if (nodes.isEmpty()) {
            return null;
        }

        try {
            if (ProtoBufFieldProcessor.getId(msg).equals(ProtoBufFieldProcessor.getId(((NodeContainer) nodes.get(0)).getBuilder()))) {
                return (NodeContainer) nodes.get(0);
            } else {
                nodes.addAll(nodes.get(0).getChildren());
                nodes.remove(0);
                return getNodeByMessage(nodes, msg);
            }
        } catch (CouldNotPerformException ex) {
            // all searched messages must have an id field... else this method will run over all treeitems and then return null
            nodes.addAll(nodes.get(0).getChildren());
            nodes.remove(0);
            return getNodeByMessage(nodes, msg);
        }
    }

    private GenericListContainer getAccordingParent(List<TreeItem<Node>> nodes, GeneratedMessage msg) throws CouldNotPerformException, InterruptedException {
        if (nodes.isEmpty()) {
            return null;
        }

        if (nodes.get(0) instanceof GenericGroupContainer) {
            GenericGroupContainer group = (GenericGroupContainer) nodes.get(0);
            GenericGroupContainer parent = (GenericGroupContainer) group.getParent();
            if (group.getDescriptor().equals(parent.getFieldGroup().getDescriptor(msg))) {
                return getAccordingParent(new ArrayList<>(group.getChildren()), msg);
            } else {
                nodes.remove(0);
                return getAccordingParent(nodes, msg);
            }
        } else if (nodes.get(0) instanceof GenericListContainer) {
            GenericListContainer list = (GenericListContainer) nodes.get(0);
            GenericGroupContainer parent = (GenericGroupContainer) list.getParent();
            if (list.getDescriptor().equals(parent.getFieldGroup().getDescriptor(msg))) {
                return list;
            } else {
                nodes.remove(0);
                return getAccordingParent(nodes, msg);
            }
        }
        return null;
    }

    public static void expandEqually(TreeItem origin, TreeItem update) {
        if (origin.isExpanded()) {
            update.setExpanded(true);
        }

        for (int i = 0; i < origin.getChildren().size(); i++) {
            if (i < update.getChildren().size()) {
                expandEqually((TreeItem) origin.getChildren().get(i), (TreeItem) update.getChildren().get(i));
            }
        }
    }

    public void setReadOnlyMode(boolean readOnly) {
        try {
            if (!remotePool.isConsistent(type.getDefaultInstanceForType())) {
                statusInfoLabel.setText("Registry inconsistent!");
                statusInfoLabel.setStyle("-fx-text-background-color: rgb(255,0,0); -fx-font-weight: bold;");
            } else {
                statusInfoLabel.setText("Read-Only-Mode");
                statusInfoLabel.setStyle("-fx-text-background-color: rgb(255,128,0); -fx-font-weight: bold;");
            }
        } catch (CouldNotPerformException ex) {
            ExceptionPrinter.printHistory("Failed to call is consistent for registry [" + type.name() + "]", ex, logger);
        }
        if (readOnly) {
            getStylesheets().clear();
            getStylesheets().add("read_only.css");
            setContextMenu(null);
        } else {
            getStylesheets().clear();
            getStylesheets().add("default.css");
            setContextMenu(new TreeTableViewContextMenu(this, type));
        }
        setEditableWithReadOnlyLabel(!readOnly);
    }

    public void setEditableWithReadOnlyLabel(boolean editable) {
        if (!editable) {
            vBox.getChildren().clear();
            vBox.getChildren().addAll(statusInfoLabel, this);
        } else {
            vBox.getChildren().remove(statusInfoLabel);
        }
        super.setEditable(editable);
    }

    public void setDisconnected(boolean disconnected) {
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
    }

    public void addDisconnectedObserver(Observer<Boolean> observer) {
        disconnectionObserver.add(observer);
    }

    public void removeDisconnectedObserver(Observer<Boolean> observer) {
        disconnectionObserver.remove(observer);
    }

    private void notifiyDisconnection(Boolean connected) {
        for (Observer<Boolean> observer : disconnectionObserver) {
            try {
                observer.update(null, connected);
            } catch (Exception ex) {
                logger.warn("Could not notify connection to value cell!");
            }
        }
    }

    public RegistryEditor getRegistryEditor() {
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
