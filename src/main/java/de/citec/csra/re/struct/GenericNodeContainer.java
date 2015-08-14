/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.GeneratedMessage;
import de.citec.csra.re.util.FieldUtil;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.jul.exception.InstantiationException;
import de.citec.jul.exception.NotAvailableException;
import de.citec.jul.extension.rsb.scope.ScopeGenerator;
import org.slf4j.LoggerFactory;
import rst.rsb.ScopeType;

/**
 *
 * @author thuxohl
 * @param <MB>
 */
public class GenericNodeContainer<MB extends GeneratedMessage.Builder> extends NodeContainer<MB> {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    public GenericNodeContainer(String descriptor, final MB builder) throws InstantiationException {
        super(descriptor, null, builder);
        try {
            for (FieldDescriptor field : builder.getDescriptorForType().getFields()) {
                registerElement(field);
            }
        } catch (InstantiationException ex) {
            throw new InstantiationException(this, ex);
        }
    }

    public GenericNodeContainer(final int fieldNumber, final MB builder) throws InstantiationException {
        this(FieldUtil.getField(fieldNumber, builder), builder);
    }

    public GenericNodeContainer(final FieldDescriptor fieldDescriptor, final MB builder) throws InstantiationException {
        super(fieldDescriptor.getName(), fieldDescriptor, builder);
        try {
            if (fieldDescriptor == null) {
                throw new NotAvailableException("fieldDescriptor");
            }

            for (FieldDescriptor field : builder.getDescriptorForType().getFields()) {
                registerElement(field);
            }
        } catch (CouldNotPerformException ex) {
            throw new InstantiationException(this, ex);
        }
    }

    private void registerElement(FieldDescriptor field) throws InstantiationException {
        if (field.isRepeated()) {
            super.add(new GenericListContainer<>(field, builder));
        } else if (field.getType().equals(FieldDescriptor.Type.MESSAGE)) {
            if ("scope".equals(field.getName())) {
                registerLeaf(field);
            } else {
                this.add(new GenericNodeContainer(field, (GeneratedMessage.Builder) builder.getFieldBuilder(field)));
            }
        } else {
            registerLeaf(field);
        }
    }

    private void registerLeaf(FieldDescriptor field) {
        if (null != field.getName()) {
            switch (field.getName()) {
                case "id":
                    super.add(new LeafContainer(builder.getField(field), field, this, false));
                    break;
                case "scope":
                    super.add(new LeafContainer(ScopeGenerator.generateStringRep((ScopeType.Scope) builder.getField(field)), field, this, false));
                    break;
                default:
                    this.add(new LeafContainer(builder.getField(field), field, this));
                    break;
            }
        }
    }
}
