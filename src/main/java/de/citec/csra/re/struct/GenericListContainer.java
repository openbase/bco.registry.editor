/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct;

import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessage;
import de.citec.csra.re.util.FieldUtil;
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

    public GenericListContainer(int repeatedFieldNumber, final MB builder) throws InstantiationException {
        this(FieldUtil.getField(repeatedFieldNumber, builder), builder);
    }

    public GenericListContainer(final Descriptors.FieldDescriptor repeatedFieldDescriptor, final MB builder) throws InstantiationException {
        super(repeatedFieldDescriptor.getName(), repeatedFieldDescriptor, builder);

        try {
            if (repeatedFieldDescriptor == null) {
                throw new NotAvailableException("repeatedFieldDescriptor");
            }

            for (GeneratedMessage.Builder childBuilder : BuilderProcessor.extractRepeatedFieldBuilderList(repeatedFieldDescriptor, builder)) {
                registerElement((RFMB) childBuilder);
            }
        } catch (CouldNotPerformException ex) {
            throw new InstantiationException(this, ex);
        }
    }

    public GenericListContainer(final String descriptor, final Descriptors.FieldDescriptor repeatedFieldDescriptor, final MB builder, List<RFMB> childBuilderList) throws InstantiationException {
        super(descriptor, repeatedFieldDescriptor, builder);

        try {
            for (GeneratedMessage.Builder childBuilder : childBuilderList) {
                registerElement((RFMB) childBuilder);
            }
        } catch (CouldNotPerformException ex) {
            throw new InstantiationException(this, ex);
        }
    }

    public void addNewDefaultElement() throws CouldNotPerformException {
        try {
            registerElement((RFMB) BuilderProcessor.addDefaultInstanceToRepeatedField(fieldDescriptor, getBuilder()));
            setExpanded(true);
            setSendableChanged();
        } catch (CouldNotPerformException ex) {
            throw new CouldNotPerformException("Could not add Element!", ex);
        }
    }

    public void addElement(RFM element) throws CouldNotPerformException {
        addElement((RFMB) element.toBuilder());
    }

    public void addElement(RFMB elementBuilder) throws CouldNotPerformException {
        try {
            BuilderProcessor.addMessageToRepeatedField(fieldDescriptor, elementBuilder, (GeneratedMessage.Builder) getBuilder());
            registerElement(elementBuilder);
            setExpanded(true);
            setSendableChanged();
        } catch (CouldNotPerformException ex) {
            throw new CouldNotPerformException("Could not add Element!", ex);
        }
    }

    private void registerElement(RFMB elementBuilder) throws CouldNotPerformException {
        super.add(new GenericNodeContainer<>(fieldDescriptor, elementBuilder));
    }
}
