
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.regedit.struct;

import com.google.protobuf.GeneratedMessage;
import de.citec.csra.regedit.RegistryEditor;
import de.citec.csra.regedit.struct.consistency.Configuration;
import de.citec.csra.regedit.struct.consistency.StructureConsistencyKeeper;
import de.citec.csra.regedit.struct.converter.Converter;
import de.citec.csra.regedit.struct.converter.ConverterSelector;
import de.citec.csra.regedit.util.FieldDescriptorUtil;
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
