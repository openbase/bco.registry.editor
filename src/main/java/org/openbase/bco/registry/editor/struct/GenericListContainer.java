package org.openbase.bco.registry.editor.struct;

/*
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2017 openbase.org
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
import static com.google.protobuf.Descriptors.FieldDescriptor.Type.MESSAGE;
import com.google.protobuf.GeneratedMessage;
import java.util.List;
import org.openbase.bco.registry.editor.struct.consistency.Configuration;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.extension.protobuf.BuilderProcessor;
import org.openbase.jul.extension.protobuf.processing.ProtoBufFieldProcessor;

/**
 *
 * @author Divine Threepwood
 * @param <MB>
 * @param <RFM>
 * @param <RFMB>
 */
public class GenericListContainer<MB extends GeneratedMessage.Builder<MB>, RFM extends GeneratedMessage, RFMB extends RFM.Builder<RFMB>> extends NodeContainer<MB> {

    private final Descriptors.FieldDescriptor fieldDescriptor;
    private final boolean modifiable;

    public GenericListContainer(int repeatedFieldNumber, final MB builder) throws InstantiationException {
        this(ProtoBufFieldProcessor.getFieldDescriptor(builder, repeatedFieldNumber), builder);
    }

    public GenericListContainer(String descriptor, final MB builder) throws InstantiationException {
        super(descriptor, builder);
        this.fieldDescriptor = ProtoBufFieldProcessor.getFieldDescriptor(builder, descriptor);
        this.modifiable = true;
        this.displayedDescriptor = descriptor;
    }

    public GenericListContainer(final Descriptors.FieldDescriptor repeatedFieldDescriptor, final MB builder) throws InstantiationException {
        super(repeatedFieldDescriptor.getName(), builder);
        modifiable = Configuration.isModifiableList(builder, repeatedFieldDescriptor.getName());
        try {
            if (repeatedFieldDescriptor == null) {
                throw new NotAvailableException("repeatedFieldDescriptor");
            }
            this.fieldDescriptor = repeatedFieldDescriptor;

            if (repeatedFieldDescriptor.getType() == MESSAGE) {
                for (GeneratedMessage.Builder childBuilder : BuilderProcessor.extractRepeatedFieldBuilderList(repeatedFieldDescriptor, builder)) {
                    registerElement((RFMB) childBuilder);
                }
            } else {
                List<Object> valueList = (List<Object>) builder.getField(repeatedFieldDescriptor);
                for (int i = 0; i < valueList.size(); i++) {
                    registerElement(valueList.get(i), i);
                }
            }
        } catch (CouldNotPerformException ex) {
            throw new InstantiationException(this, ex);
        }
        this.displayedDescriptor = descriptor;
    }

    public GenericListContainer(final String descriptor, final Descriptors.FieldDescriptor repeatedFieldDescriptor, final MB builder, List<RFMB> childBuilderList) throws InstantiationException {
        super(descriptor, builder);
        modifiable = Configuration.isModifiableList(builder, repeatedFieldDescriptor.getName());
        try {
            if (repeatedFieldDescriptor == null) {
                throw new NotAvailableException("repeatedFieldDescriptor");
            }

            this.fieldDescriptor = repeatedFieldDescriptor;
            for (GeneratedMessage.Builder childBuilder : childBuilderList) {
                registerElement((RFMB) childBuilder);
            }
        } catch (CouldNotPerformException ex) {
            throw new InstantiationException(this, ex);
        }
        this.displayedDescriptor = descriptor;
    }

    public void addNewDefaultElement() throws CouldNotPerformException {
        try {
            switch (fieldDescriptor.getType()) {
                case MESSAGE:
                    registerElement((RFMB) BuilderProcessor.addDefaultInstanceToRepeatedField(fieldDescriptor, getBuilder()));
                    break;
                case STRING:
                    addElement("");
                    break;
                case DOUBLE:
                case FLOAT:
                case INT32:
                case INT64:
                    addElement(0);
                    break;
                case BOOL:
                    addElement(true);
                    break;
                case ENUM:
                    addElement(fieldDescriptor.getEnumType().getValues().get(0));
                    break;
                default:
                    registerElement(null);
            }
            setExpanded(true);
            setSendableChanged();
        } catch (CouldNotPerformException ex) {
            throw new CouldNotPerformException("Could not add default element to field [" + fieldDescriptor.getName() + "]!", ex);
        }
    }

    public void addElement(Object element) throws CouldNotPerformException {
        try {
            if (fieldDescriptor.getType() == MESSAGE) {
                BuilderProcessor.addMessageToRepeatedField(fieldDescriptor, (RFMB) element, (GeneratedMessage.Builder) getBuilder());
                registerElement(BuilderProcessor.extractRepeatedFieldBuilderList(fieldDescriptor, builder).get(builder.getRepeatedFieldCount(fieldDescriptor) - 1));
            } else {
                builder.addRepeatedField(fieldDescriptor, element);
                registerElement(element, builder.getRepeatedFieldCount(fieldDescriptor) - 1);
            }
            this.getChildren().get(this.getChildren().size() - 1).setExpanded(true);
            setExpanded(true);
            setSendableChanged();
        } catch (CouldNotPerformException ex) {
            throw new CouldNotPerformException("Could not add element [" + element + "] to field [" + fieldDescriptor.getName() + "]!", ex);
        }
    }

    public void registerElement(Object element) throws CouldNotPerformException {
        super.add(new GenericNodeContainer<>(fieldDescriptor, (RFMB) element));
    }

    public void registerElement(Object element, String descriptor) throws CouldNotPerformException {
        super.add(new GenericNodeContainer<>(descriptor, (RFMB) element));
    }

    private void registerElement(Object element, int index) {
        super.add(new LeafContainer(element, fieldDescriptor.getName(), this, Configuration.isModifiableField(builder, descriptor), index));
    }

    public boolean isModifiable() {
        return modifiable;
    }

    public Descriptors.FieldDescriptor getFieldDescriptor() {
        return fieldDescriptor;
    }
}
