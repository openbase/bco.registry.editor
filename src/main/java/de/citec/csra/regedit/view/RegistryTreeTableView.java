/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.regedit.view;

import com.google.protobuf.GeneratedMessage;
import de.citec.csra.regedit.struct.GenericGroupContainer;
import de.citec.csra.regedit.struct.GenericListContainer;
import de.citec.csra.regedit.struct.GenericNodeContainer;
import de.citec.csra.regedit.util.SendableType;
import de.citec.csra.regedit.view.column.Column;
import de.citec.csra.regedit.view.column.DescriptorColumn;
import de.citec.csra.regedit.view.column.ValueColumn;
import de.citec.csra.regedit.struct.Node;
import de.citec.csra.regedit.struct.NodeContainer;
import de.citec.csra.regedit.util.FieldDescriptorUtil;
import de.citec.csra.regedit.util.RemotePool;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.jul.exception.InstantiationException;
import de.citec.jul.extension.protobuf.ProtobufListDiff;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeSortMode;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class RegistryTreeTableView extends TreeTableView<Node> {

    private static final Logger logger = LoggerFactory.getLogger(RegistryTreeTableView.class);

    private final DescriptorColumn descriptorColumn;
    private final SendableType type;
    private final ProtobufListDiff listDiff;
    private final Label readOnlyLabel;
    private final VBox vBox;
    private final RemotePool remotePool;

    public RegistryTreeTableView(SendableType type) throws InstantiationException {
        this.type = type;
        this.setEditable(true);
        this.setShowRoot(false);
        this.descriptorColumn = new DescriptorColumn();
        this.getColumns().addAll(descriptorColumn, new ValueColumn());
        if (type != null) {
            this.setContextMenu(new TreeTableViewContextMenu(this, type));
        }
        setSortMode(TreeSortMode.ALL_DESCENDANTS);
        getSortOrder().add(descriptorColumn);

        this.listDiff = new ProtobufListDiff();

        this.readOnlyLabel = new Label("Read-Only-Mode");
        this.readOnlyLabel.setAlignment(Pos.CENTER);
        this.readOnlyLabel.setStyle("-fx-text-background-color: rgb(255,128,0); -fx-font-weight: bold;");

        this.vBox = new VBox();
        this.vBox.setAlignment(Pos.CENTER);
        this.vBox.getChildren().addAll(readOnlyLabel, this);

        this.remotePool = RemotePool.getInstance();
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

    public void update(List<? extends GeneratedMessage> messageList) throws CouldNotPerformException {
        // get all changes
        listDiff.diff(messageList);

        // Remove all removed messages
        for (Object message : listDiff.getRemovedMessageMap().getMessages()) {
            logger.info("Removed message [" + message + "]");
            GeneratedMessage msg = (GeneratedMessage) message;
            NodeContainer nodeToRemove = getNodeByMessage(new ArrayList(this.getRoot().getChildren()), msg);
            if (nodeToRemove.hasChanged()) {
                GlobalTextArea.getInstance().setStyle("-fx-text-background-color: red");
                GlobalTextArea.getInstance().setText("WARNING: Message [" + nodeToRemove.getBuilder().build() + "] has been removed from the global database!");
                continue;
            }
            nodeToRemove.getParent().getChildren().remove(nodeToRemove);
        }
        for (Object message : listDiff.getUpdatedMessageMap().getMessages()) {
            logger.info("Updated message [" + message + "]");
            GeneratedMessage msg = (GeneratedMessage) message;
            NodeContainer nodeToRemove = getNodeByMessage(new ArrayList(this.getRoot().getChildren()), msg);
            logger.info("Found old node to remove to update [" + nodeToRemove.getBuilder().build() + "]");
            if (nodeToRemove.hasChanged()) {
                GlobalTextArea.getInstance().setStyle("-fx-text-background-color: red");
                GlobalTextArea.getInstance().setText("WARNING: Message [" + nodeToRemove.getBuilder().build() + "] has been changed in the global database!\nTo discard your changes and receive the new ones press 'Cance'\nTo overwrite the global changes with yours press 'Apply'");
                continue;
            }
            GenericListContainer parent = (GenericListContainer) nodeToRemove.getParent();
            parent.getChildren().set(parent.getChildren().indexOf(nodeToRemove), new GenericNodeContainer(parent.getFieldDescriptor(), (GeneratedMessage.Builder) msg.toBuilder()));
        }
        for (Object message : listDiff.getNewMessageMap().getMessages()) {
            logger.info("New message [" + message + "]");
            GeneratedMessage msg = (GeneratedMessage) message;
            if (this.getRoot() instanceof GenericGroupContainer) {
                getAccordingParent(this.getRoot().getChildren(), msg).registerElement(msg.toBuilder());
            } else {
                ((GenericListContainer) this.getRoot()).registerElement(msg.toBuilder());
            }
        }

        setReadOnlyMode(remotePool.isReadOnly(type));
    }

    private NodeContainer getNodeByMessage(List<TreeItem<Node>> nodes, GeneratedMessage msg) {
        if (nodes.isEmpty()) {
            return null;
        }

        try {
            if (FieldDescriptorUtil.getId(msg).equals(FieldDescriptorUtil.getId(((NodeContainer) nodes.get(0)).getBuilder()))) {
                logger.info("Found node to remove with id [" + FieldDescriptorUtil.getId(((NodeContainer) nodes.get(0)).getBuilder()) + "]");
                return (NodeContainer) nodes.get(0);
            } else {
                nodes.remove(0);
                nodes.addAll(nodes.get(0).getChildren());
                return getNodeByMessage(nodes, msg);
            }
        } catch (CouldNotPerformException ex) {
            // all searched messages must have an id field... else this method will run over all treeitems and then return null
            nodes.remove(0);
            nodes.addAll(nodes.get(0).getChildren());
            return getNodeByMessage(nodes, msg);
        }
    }

    private GenericListContainer getAccordingParent(List<TreeItem<Node>> nodes, GeneratedMessage msg) {
        if (nodes.isEmpty()) {
            return null;
        }

        if (nodes.get(0) instanceof GenericGroupContainer) {
            GenericGroupContainer group = (GenericGroupContainer) nodes.get(0);
            GenericGroupContainer parent = (GenericGroupContainer) group.getParent();
            if (group.getDescriptor().equals(parent.getFieldGroup().getValue(msg))) {
                return getAccordingParent(group.getChildren(), msg);
            } else {
                nodes.remove(0);
                return getAccordingParent(nodes, msg);
            }
        } else if (nodes.get(0) instanceof GenericListContainer) {
            GenericListContainer list = (GenericListContainer) nodes.get(0);
            GenericGroupContainer parent = (GenericGroupContainer) list.getParent();
            if (list.getDescriptor().equals(parent.getFieldGroup().getValue(msg))) {
                return list;
            } else {
                nodes.remove(0);
                return getAccordingParent(nodes, msg);
            }
        }
        return null;
    }

    public void setReadOnlyMode(boolean readOnly) {
        if (readOnly) {
            getStylesheets().add("read_only.css");
            setContextMenu(null);
        } else {
            getStylesheets().add("default.css");
            setContextMenu(new TreeTableViewContextMenu(this, type));
        }
        setEditableWithReadOnlyLabel(!readOnly);
    }

    public void setEditableWithReadOnlyLabel(boolean editable) {
        if (!editable) {
            vBox.getChildren().clear();
            vBox.getChildren().addAll(readOnlyLabel, this);
        } else {
            vBox.getChildren().remove(readOnlyLabel);
        }
        super.setEditable(editable);
    }

    public Label getReadOnlyLabel() {
        return readOnlyLabel;
    }

    public VBox getVBox() {
        return vBox;
    }

    public ProtobufListDiff getListDiff() {
        return listDiff;
    }
}
