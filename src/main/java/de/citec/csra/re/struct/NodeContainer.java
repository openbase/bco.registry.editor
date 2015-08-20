/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct;

import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessage;
import de.citec.csra.re.RegistryEditor;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author thuxohl
 * @param <MB>
 */
public class NodeContainer<MB extends GeneratedMessage.Builder> extends TreeItem<Node> implements Node {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final Descriptors.FieldDescriptor fieldDescriptor;
    protected final MB builder;
    protected final String descriptor;
    protected final boolean sendable;
    protected final SimpleObjectProperty<Boolean> changed;

    public NodeContainer(String descriptor, Descriptors.FieldDescriptor fieldDescriptor, MB builder) {
        assert fieldDescriptor != null;
        assert builder != null;
        assert descriptor != null;
        this.builder = builder;
        this.fieldDescriptor = fieldDescriptor;
        this.descriptor = descriptor;
        changed = new SimpleObjectProperty<>(false);
        sendable = false;
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
        RegistryEditor.setModified(change);
    }

    public boolean hasChanged() {
        return changed.getValue();
    }

    public Property<Boolean> getChanged() {
        return changed;
    }

    public void setSendableChanged() {
        NodeContainer sendableNode = this;
        while (!sendableNode.isSendable()) {
            sendableNode = (NodeContainer) sendableNode.getParent().getValue();
        }
        ((NodeContainer) sendableNode).setChanged(true);
    }

    @Override
    public Descriptors.FieldDescriptor getFieldDescriptor() {
        return fieldDescriptor;
    }
}
