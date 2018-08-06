package org.openbase.bco.registry.editor.struct;

/*
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

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.GeneratedMessage;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TreeItem;
import org.openbase.bco.registry.editor.struct.consistency.Configuration;
import org.openbase.bco.registry.editor.struct.consistency.StructureConsistencyKeeper;
import org.openbase.bco.registry.editor.struct.converter.Converter;
import org.openbase.bco.registry.editor.struct.converter.ConverterSelector;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.extension.protobuf.processing.ProtoBufFieldProcessor;
import org.openbase.jul.extension.rst.processing.LabelProcessor;
import org.openbase.jul.processing.StringProcessor;
import org.slf4j.LoggerFactory;
import rst.configuration.LabelType;

/**
 * @param <MB> The message builder type to use.
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public abstract class NodeContainer<MB extends GeneratedMessage.Builder> extends TreeItem<Node> implements Node {

    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(NodeContainer.class);

    protected final MB builder;
    protected final String descriptor;
    protected String displayedDescriptor;
    protected final Converter<Object> converter;

    protected final boolean sendable;
    protected final SimpleObjectProperty<Boolean> changed;

    public NodeContainer(String descriptor, MB builder) throws InstantiationException {
        assert builder != null;
        assert descriptor != null;
        this.builder = builder;
        this.descriptor = descriptor;
        this.converter = ConverterSelector.getConverter(builder);
        changed = new SimpleObjectProperty<>(false);
        if (this instanceof GenericNodeContainer) {
            sendable = Configuration.isSendable(builder);
        } else {
            sendable = false;
        }
        this.setValue(this);
        this.setDisplayedDescriptor();
    }

    private void setDisplayedDescriptor() {
        try {
            FieldDescriptor labelField = ProtoBufFieldProcessor.getFieldDescriptor(builder, "label");
            if (labelField != null) {
                final Object label = builder.getField(labelField);
                if (label instanceof LabelType.Label) {
                    displayedDescriptor = LabelProcessor.getBestMatch((LabelType.Label) label);
                    return;
                } else if (label instanceof String) {
                    displayedDescriptor = (String) label;
                    return;
                } else {
                    logger.warn("Unknown label type [" + label.getClass().getSimpleName() + "]");
                }
            }
            displayedDescriptor = StringProcessor.transformToCamelCase(descriptor).replace(",", " - ");
        } catch (Exception ex) {
            displayedDescriptor = StringProcessor.transformToCamelCase(descriptor).replace(",", " - ");
        }
    }

    protected void add(LeafContainer leaf) {
        this.getChildren().add(new TreeItem<>(leaf));
    }

    public void add(TreeItem<Node> node) {
        this.getChildren().add(node);
    }

    @Override
    public String getDescriptor() {
        return descriptor;
    }

    @Override
    public String getDisplayedDescriptor() {
        return displayedDescriptor;
    }

    @Override
    public NodeContainer getContext() {
        return this;
    }

    public MB getBuilder() {
        return builder;
    }

    public boolean isSendable() {
        return sendable;
    }

    public void setChanged(boolean change) {
        changed.set(change);
    }

    public boolean hasChanged() {
        return changed.getValue();
    }

    public SimpleObjectProperty<Boolean> getChanged() {
        return changed;
    }

    public void setSendableChanged() {
        NodeContainer sendableNode = this;
        while (!sendableNode.isSendable()) {
            if (sendableNode.getParent() == null) {
                return;
            }
            sendableNode = (NodeContainer) sendableNode.getParent().getValue();
        }
        ((NodeContainer) sendableNode).setChanged(true);
    }

    public GeneratedMessage.Builder getSendableType() {
        NodeContainer sendableNode = this;
        while (!sendableNode.isSendable()) {
            sendableNode = (NodeContainer) sendableNode.getParent().getValue();
        }
        return ((NodeContainer) sendableNode).getBuilder();
    }

    public void updateBuilder(String fieldName, Object value, int index) throws CouldNotPerformException, InterruptedException {
        if (index == -1) {
            converter.updateBuilder(fieldName, value);
        } else {
            builder.setRepeatedField(ProtoBufFieldProcessor.getFieldDescriptor(builder, fieldName), index, value);
        }
        StructureConsistencyKeeper.keepStructure(this, fieldName);
        setSendableChanged();
    }
}
