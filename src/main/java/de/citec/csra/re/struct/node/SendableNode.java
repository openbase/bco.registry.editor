/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import com.google.protobuf.GeneratedMessage;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author thuxohl
 * @param <MB>
 */
public abstract class SendableNode<MB extends GeneratedMessage.Builder> extends VariableNode<MB> {
    
    private String color;
    private final SimpleObjectProperty<Boolean> changed;
    private boolean newNode;

    public SendableNode(String descriptor, MB value) {
        super(descriptor, value);
        changed = new SimpleObjectProperty<>(false);
        newNode = false;
        color = "white";
    }

    public void setChanged(boolean change) {
        changed.set(change);
        if(change) {
            setColor("yellow");
        } else {
            setColor("white");
        }
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
