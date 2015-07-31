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

/**
 *
 * @author thuxohl
 * @param <MB>
 */
public abstract class SendableNode<MB extends GeneratedMessage.Builder> extends VariableNode<MB> {

    private final SimpleObjectProperty<Boolean> changed;
    private boolean newNode;

    public SendableNode(String descriptor, Descriptors.FieldDescriptor fieldDescriptor, MB value) {
        super(descriptor, fieldDescriptor, value);
        changed = new SimpleObjectProperty<>(false);
        newNode = false;
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

    public boolean getNewNode() {
        return newNode;
    }

    public void setNewNode(boolean newNode) {
        this.newNode = newNode;
    }
}
