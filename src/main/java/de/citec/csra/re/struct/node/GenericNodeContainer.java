/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.GeneratedMessage;
import de.citec.csra.re.struct.leaf.LeafContainer;
import de.citec.jul.exception.ExceptionPrinter;
import de.citec.jul.exception.InstantiationException;
import de.citec.jul.extension.rsb.scope.ScopeGenerator;
import javafx.scene.control.TreeItem;
import org.slf4j.LoggerFactory;
import rst.rsb.ScopeType;

/**
 *
 * @author thuxohl
 * @param <MB>
 */
public class GenericNodeContainer<MB extends GeneratedMessage.Builder> extends TreeItem<Node> implements Node {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
    
    private final MB builder;
    private final FieldDescriptor fieldDescriptor;

    public GenericNodeContainer(final FieldDescriptor fieldDescriptor, final MB builder) {
        this.builder = builder;

        this.fieldDescriptor = fieldDescriptor;
        try {
            for (FieldDescriptor field : builder.getDescriptorForType().getFields()) {
                registerElement(field);
            }
        } catch (InstantiationException ex) {
            ExceptionPrinter.printHistory(logger, ex);
        }
    }

    @Override
    public String getDescriptor() {
        return fieldDescriptor.getName();
    }

    @Override
    public Node getContext() {
        return this;
    }

    private void registerElement(FieldDescriptor field) throws InstantiationException {
        if (field.isRepeated()) {
            // TODO: generic list container should not register by class.. should use generic node container
            this.add(new GenericListContainer<>(field.getNumber(), builder, ServiceTemplateContainer.class));
        } else if (field.getType().equals(FieldDescriptor.Type.MESSAGE)) {
            this.add(new GenericNodeContainer(field, (GeneratedMessage.Builder) builder.getFieldBuilder(field)));
        } else {
            registerLeaf(field);
        }
    }

    private void add(LeafContainer leaf) {
        this.getChildren().add(new TreeItem<>(leaf));
    }

    private void add(TreeItem<Node> node) {
        this.getChildren().add(node);
    }

    // TODO tamino: generic not specified ! S == Object
    private <S> void add(S value, String descriptor) {
        this.add(new LeafContainer(value, descriptor, null));
    }

    // TODO tamino: generic not specified ! S == Object
    private <S> void add(S value, String descriptor, int index) {
        this.add(new LeafContainer(value, descriptor, null, index));
    }

    // TODO tamino: generic not specified ! S == Object
    private <S> void add(S value, String descriptor, boolean editable) {
        this.add(new LeafContainer(value, descriptor, null, editable));
    }

    private void registerLeaf(FieldDescriptor field) {
        if (null != field.getName()) switch (field.getName()) {
            case "id":
                this.add(new LeafContainer(builder.getField(field), field.getName(), null, false));
                break;
            case "scope":
                this.add(new LeafContainer(ScopeGenerator.generateStringRep((ScopeType.Scope) builder.getField(field)), field.getName(), null, false));
                break;
            default:
                this.add(new LeafContainer(builder.getField(field), field.getName(), null));
                break;
        }
    }
}
