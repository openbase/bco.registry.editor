/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.regedit.struct;

import com.google.protobuf.Descriptors;
import static com.google.protobuf.Descriptors.FieldDescriptor.Type.MESSAGE;
import com.google.protobuf.GeneratedMessage;
import de.citec.csra.regedit.util.FieldDescriptorUtil;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.jul.exception.InstantiationException;
import de.citec.jul.exception.NotAvailableException;
import de.citec.jul.extension.protobuf.BuilderProcessor;
import java.util.List;

/**
 *
 * @author Divine Threepwood
 * @param <MB>
 * @param <RFM>
 * @param <RFMB>
 */
public class GenericListContainer<MB extends GeneratedMessage.Builder<MB>, RFM extends GeneratedMessage, RFMB extends RFM.Builder<RFMB>> extends NodeContainer<MB> {

    private final Descriptors.FieldDescriptor fieldDescriptor;
    // TODO: thuxohl maybe compare to a list of fieldNames? or is the message also important?
    private final boolean modifiable;

    public GenericListContainer(int repeatedFieldNumber, final MB builder) throws InstantiationException {
        this(FieldDescriptorUtil.getField(repeatedFieldNumber, builder), builder);
    }

    public GenericListContainer(final Descriptors.FieldDescriptor repeatedFieldDescriptor, final MB builder) throws InstantiationException {
        super(repeatedFieldDescriptor.getName(), builder);
        modifiable = true;
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
    }

    public GenericListContainer(final String descriptor, final Descriptors.FieldDescriptor repeatedFieldDescriptor, final MB builder, List<RFMB> childBuilderList) throws InstantiationException {
        super(descriptor, builder);
        modifiable = true;
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
                    addElement(builder.getField(fieldDescriptor).getClass().getEnumConstants()[0]);
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
                registerElement(element);
            } else {
                builder.addRepeatedField(fieldDescriptor, element);
                registerElement(element, builder.getRepeatedFieldCount(fieldDescriptor) - 1);
            }
            setExpanded(true);
            setSendableChanged();
        } catch (CouldNotPerformException ex) {
            throw new CouldNotPerformException("Could not add element [" + element + "] to field [" + fieldDescriptor.getName() + "]!", ex);
        }
    }

    private void registerElement(Object element) throws CouldNotPerformException {
        super.add(new GenericNodeContainer<>(fieldDescriptor, (RFMB) element));
    }

    private void registerElement(Object element, int index) {
        super.add(new LeafContainer(element, fieldDescriptor.getName(), this, index));
    }

    public boolean isModifiable() {
        return modifiable;
    }
}
