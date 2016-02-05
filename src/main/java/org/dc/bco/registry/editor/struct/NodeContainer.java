package org.dc.bco.registry.editor.struct;

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

import com.google.protobuf.GeneratedMessage;
import org.dc.bco.registry.editor.struct.consistency.Configuration;
import org.dc.bco.registry.editor.struct.consistency.StructureConsistencyKeeper;
import org.dc.bco.registry.editor.struct.converter.Converter;
import org.dc.bco.registry.editor.struct.converter.ConverterSelector;
import org.dc.bco.registry.editor.util.FieldDescriptorUtil;
import org.dc.jul.exception.CouldNotPerformException;
import org.dc.jul.exception.InstantiationException;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author thuxohl
 * @param <MB>
 */
public abstract class NodeContainer<MB extends GeneratedMessage.Builder> extends TreeItem<Node> implements Node {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final MB builder;
    protected final String descriptor;
    protected final Converter converter;

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

    public void updateBuilder(String fieldName, Object value, int index) throws CouldNotPerformException {
        if (index == -1) {
            converter.updateBuilder(fieldName, value);
        } else {
            builder.setRepeatedField(FieldDescriptorUtil.getFieldDescriptor(fieldName, builder), index, value);
        }
        StructureConsistencyKeeper.keepStructure(this, fieldName);
        setSendableChanged();
    }
}
