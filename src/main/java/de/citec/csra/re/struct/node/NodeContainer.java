/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import com.google.protobuf.GeneratedMessage;
import de.citec.csra.re.struct.leaf.LeafContainer;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectWrapper;
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

    private final Property<String> descriptor;
    protected final MB builder;

    public NodeContainer(String descriptor, MB builder) {
        assert descriptor != null;
        assert builder != null;
        this.builder = builder;
        this.setValue(this);
        this.descriptor = new ReadOnlyObjectWrapper<>(descriptor);
    }

    protected void add(LeafContainer leave) {
        this.getChildren().add(new TreeItem<>(leave));
    }

    public void add(TreeItem<Node> node) {
        this.getChildren().add(node);
    }

    // TODO tamino: generic not specified ! S == Object
    public <S> void add(S value, String descriptor) {
        this.add(new LeafContainer(value, descriptor, this));
    }

    // TODO tamino: generic not specified ! S == Object
    public <S> void add(S value, String descriptor, int index) {
        this.add(new LeafContainer(value, descriptor, this, index));
    }
    
    // TODO tamino: generic not specified ! S == Object
    public <S> void add(S value, String descriptor, boolean editable) {
        this.add(new LeafContainer(value, descriptor, this, editable));
    }

    @Override
    public String getDescriptor() {
        return descriptor.getValue();
    }

    @Override
    public NodeContainer getContext() {
        return this;
    }

    public MB getBuilder() {
        return builder;
    }

    public void setSendableChanged() {
        NodeContainer sendable = this;
        while (sendable instanceof LocationConfigContainer || !(sendable instanceof SendableNode)) {
            if (sendable instanceof LocationConfigContainer && ((LocationConfigContainer) sendable).getBuilder().getRoot()) {
                break;
            }
            sendable = (NodeContainer) sendable.getParent().getValue();
        }
        logger.info("Sendable ["+sendable.getDescriptor()+"] set to change");
        ((SendableNode) sendable).setChanged(true);
    }
}
