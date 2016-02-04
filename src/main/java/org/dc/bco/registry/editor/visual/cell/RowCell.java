package org.dc.bco.registry.editor.visual.cell;

/*
 * #%L
 * RegistryEditor
 * %%
 * Copyright (C) 2014 - 2016 DivineCooperation
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

import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message;
import org.dc.bco.registry.editor.RegistryEditor;
import org.dc.bco.registry.editor.struct.GenericGroupContainer;
import org.dc.bco.registry.editor.struct.Leaf;
import org.dc.bco.registry.editor.struct.LeafContainer;
import org.dc.bco.registry.editor.struct.GenericListContainer;
import org.dc.bco.registry.editor.struct.GenericNodeContainer;
import org.dc.bco.registry.editor.struct.Node;
import org.dc.bco.registry.editor.struct.NodeContainer;
import org.dc.bco.registry.editor.util.FieldDescriptorUtil;
import org.dc.bco.registry.editor.util.RSTDefaultInstances;
import org.dc.bco.registry.editor.util.RemotePool;
import org.dc.jul.exception.CouldNotPerformException;
import org.dc.jul.exception.InstantiationException;
import org.dc.jul.exception.printer.LogLevel;
import org.dc.jul.extension.protobuf.BuilderProcessor;
import java.util.ArrayList;
import java.util.List;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeTableCell;
import org.slf4j.LoggerFactory;

/**
 * Cell factory to manage similar options for all cells in a row. Initializes
 * and manages the context menu for all child cells.
 *
 * @author thuxohl
 */
public abstract class RowCell extends TreeTableCell<Node, Node> {

    protected final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    protected RemotePool remotePool;
    private final ContextMenu contextMenu;
    private final MenuItem addMenuItem, removeMenuItem;

    public RowCell() {
        try {
            remotePool = RemotePool.getInstance();
        } catch (InstantiationException ex) {
            RegistryEditor.printException(ex, logger, LogLevel.WARN);
        }

        addMenuItem = new MenuItem("Add");
        removeMenuItem = new MenuItem("Remove");
        contextMenu = new ContextMenu(addMenuItem, removeMenuItem);

        EventHandlerImpl eventHandler = new EventHandlerImpl();
        addMenuItem.setOnAction(eventHandler);
        removeMenuItem.setOnAction(eventHandler);

        this.setFocused(true);
        this.setEditable(true);
    }

    @Override
    protected void updateItem(Node item, boolean empty) {
        super.updateItem(item, empty);

        if (getTableColumn().getTreeTableView().isEditable()
                && (item instanceof GenericListContainer
                && ((GenericListContainer) item).isModifiable())
                || item instanceof GenericGroupContainer) {
            addMenuItem.setVisible(true);
            removeMenuItem.setVisible(false);
            setContextMenu(contextMenu);
        } else if (getTableColumn().getTreeTableView().isEditable()
                && (item instanceof NodeContainer
                && ((NodeContainer) item).getParent().getValue() instanceof GenericListContainer
                && ((GenericListContainer) ((NodeContainer) item).getParent().getValue()).isModifiable())
                || (item instanceof LeafContainer
                && ((LeafContainer) item).getParent() instanceof GenericListContainer)
                && ((GenericListContainer) ((LeafContainer) item).getParent().getValue()).isModifiable()) {
            addMenuItem.setVisible(true);
            removeMenuItem.setVisible(true);
            setContextMenu(contextMenu);
        } else {
            setContextMenu(null);
        }
    }

    private class EventHandlerImpl implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            Thread thread = new Thread(
                    new Task<Boolean>() {

                        @Override
                        protected Boolean call() throws Exception {
                            if (event.getSource().equals(addMenuItem)) {
                                addAction(RowCell.this.getItem());
                            } else if (event.getSource().equals(removeMenuItem)) {
                                removeAction(RowCell.this.getItem());
                            }
                            return true;
                        }

                    });
            thread.setDaemon(true);
            thread.start();
        }

        private void addAction(Node add) {
            try {
                if (add instanceof GenericNodeContainer) {
                    GeneratedMessage.Builder builder = ((NodeContainer) add).getBuilder();
                    GenericListContainer parent = (GenericListContainer) ((NodeContainer) add).getParent().getValue();
                    GeneratedMessage.Builder addedBuilder = RSTDefaultInstances.getDefaultBuilder(builder);
                    addGroupValues(parent, addedBuilder, ((NodeContainer) add).getBuilder());
                    parent.addElement(addedBuilder);
                } else if (add instanceof LeafContainer) {
                    GenericListContainer parentNode = (GenericListContainer) ((LeafContainer) add).getParent();
                    parentNode.addNewDefaultElement();
                } else if (add instanceof GenericListContainer) {
                    GenericListContainer parentNode = (GenericListContainer) add;
                    if (parentNode.getFieldDescriptor().getType() == Descriptors.FieldDescriptor.Type.MESSAGE) {
                        GeneratedMessage.Builder addedBuilder = RSTDefaultInstances.getDefaultBuilder((GeneratedMessage.Builder) parentNode.getBuilder().newBuilderForField(parentNode.getFieldDescriptor()));

                        if (!parentNode.getChildren().isEmpty()) {
                            addGroupValues(parentNode, addedBuilder, ((NodeContainer) parentNode.getChildren().get(0)).getBuilder());
                        }
                        parentNode.addElement(addedBuilder);
                    } else {
                        parentNode.addNewDefaultElement();
                    }
                } else if (add instanceof GenericGroupContainer) {
                    GenericGroupContainer container = (GenericGroupContainer) add;
                    GeneratedMessage.Builder addedBuilder = RSTDefaultInstances.getDefaultBuilder(BuilderProcessor.addDefaultInstanceToRepeatedField(container.getFieldDescriptor(), container.getBuilder()));
                    NodeContainer child = (NodeContainer) container.getChildren().get(0);
                    while (child instanceof GenericGroupContainer || child instanceof GenericListContainer) {
                        child = (NodeContainer) child.getChildren().get(0);
                    }
                    addGroupValues(container, addedBuilder, child.getBuilder());
                    container.add(new GenericNodeContainer("", addedBuilder));
                    container.setExpanded(true);
                }
            } catch (CouldNotPerformException ex) {
                RegistryEditor.printException(ex, logger, LogLevel.ERROR);
            }
        }

        private void addGroupValues(NodeContainer startingContainer, GeneratedMessage.Builder builder, GeneratedMessage.Builder groupExample) throws CouldNotPerformException {
            NodeContainer groupContainer = startingContainer;
            GenericGroupContainer parent;
            while (groupContainer.getParent() != null && groupContainer.getParent().getValue() instanceof GenericGroupContainer) {
                parent = (GenericGroupContainer) groupContainer.getParent().getValue();
                parent.getFieldGroup().setValue(builder, parent.getFieldGroup().getValue(groupExample));
                groupContainer = parent;
            }
        }

        private void removeAction(Node nodeToRemove) {
            // check if the removed item is an instance of classes that have to be directly
            // removed in the registry [deviceClass, deviceConfig, locationConfig]
            if (nodeToRemove instanceof NodeContainer && ((NodeContainer) nodeToRemove).isSendable()) {
                NodeContainer removed = (NodeContainer) nodeToRemove;
                try {
                    Message message = removed.getBuilder().build();
                    logger.info("Removing message with Id [" + FieldDescriptorUtil.getId(message) + "]");
                    if (!"".equals(FieldDescriptorUtil.getId(message)) && remotePool.contains(message)) {
                        remotePool.remove(message);
                    }
                } catch (CouldNotPerformException ex) {
                    RegistryEditor.printException(ex, logger, LogLevel.WARN);
                }
            } else if (nodeToRemove instanceof Leaf) {
                removeNodeFromRepeatedField(((LeafContainer) nodeToRemove).getParent(), ((LeafContainer) nodeToRemove).getIndex());
            } else {
                removeNodeFromRepeatedField((NodeContainer) ((NodeContainer) nodeToRemove).getParent().getValue(), nodeToRemove);
            }
        }
    }

    private void removeNodeFromRepeatedField(NodeContainer parent, Node nodeToRemove) {
        removeNodeFromRepeatedField(parent, parent.getChildren().indexOf(nodeToRemove));
    }

    private void removeNodeFromRepeatedField(NodeContainer parent, int index) {
        Descriptors.FieldDescriptor field = FieldDescriptorUtil.getFieldDescriptor(parent.getDescriptor(), parent.getBuilder());
        List updatedList = new ArrayList((List) parent.getBuilder().getField(field));
        updatedList.remove(index);
        parent.getBuilder().clearField(field);
        parent.getBuilder().setField(field, updatedList);
        parent.setSendableChanged();
        parent.getChildren().remove(index);
    }
}
