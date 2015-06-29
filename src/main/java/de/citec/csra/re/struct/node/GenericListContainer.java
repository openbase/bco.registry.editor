/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessage;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.jul.exception.ExceptionPrinter;
import de.citec.jul.exception.InstantiationException;
import de.citec.jul.exception.NotAvailableException;
import de.citec.jul.extension.protobuf.BuilderProcessor;
import java.lang.reflect.Constructor;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Divine Threepwood
 * @param <MB>
 * @param <RFM>
 */
public class GenericListContainer<MB extends GeneratedMessage.Builder<MB>, RFM extends GeneratedMessage, RFMB extends RFM.Builder<RFMB>> extends NodeContainer<MB> {

    private final Descriptors.FieldDescriptor repeatedFieldDescriptor;
    private final Class<? extends NodeContainer> elementNodeContainerClass;

    public GenericListContainer(final int repeatedFieldNumber, final MB builder, final Class<? extends NodeContainer> elementNodeContainerClass) throws InstantiationException {
        this(getFieldName(repeatedFieldNumber, builder), repeatedFieldNumber, builder, elementNodeContainerClass);
    }

    public GenericListContainer(final String name, final int repeatedFieldNumber, final MB builder, final Class<? extends NodeContainer> elementNodeContainerClass) throws InstantiationException {
        super(name, builder);

        try {
            this.elementNodeContainerClass = elementNodeContainerClass;
            this.repeatedFieldDescriptor = builder.getDescriptorForType().findFieldByNumber(repeatedFieldNumber);
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

    public void addNewDefaultElement() throws CouldNotPerformException {
        addElement((RFMB) BuilderProcessor.addDefaultInstanceToRepeatedField(repeatedFieldDescriptor, getBuilder()));
    }

    public void addElement(RFM element) throws CouldNotPerformException {
        addElement((RFMB) element.toBuilder());
    }

    public void addElement(RFMB elementBuilder) throws CouldNotPerformException {
        try {
            BuilderProcessor.addMessageToRepeatedField(repeatedFieldDescriptor, elementBuilder, (GeneratedMessage.Builder) getBuilder());
            registerElement(elementBuilder);
        } catch (CouldNotPerformException ex) {
            throw new CouldNotPerformException("Could not add Element!", ex);
        }
    }

    private void registerElement(RFMB elementBuilder) throws CouldNotPerformException {
        try {
            Constructor<? extends NodeContainer> constructor;
            try {
                constructor = elementNodeContainerClass.getConstructor(elementBuilder.getClass());
            } catch (Exception ex) {
                throw new CouldNotPerformException("Could not find suitable constructor!! ", ex);
            }

            NodeContainer nodeContainer;
            try {
                nodeContainer = constructor.newInstance(elementBuilder);
            } catch (Exception ex) {
                throw new CouldNotPerformException("Could not instanciate child node container!", ex);
            }

            super.add(nodeContainer);
        } catch (CouldNotPerformException ex) {
            throw new CouldNotPerformException("Could not register element!", ex);
        }
    }

    public static String getFieldName(final int repeatedFieldNumber, final GeneratedMessage.Builder builder) {
        Descriptors.FieldDescriptor repeatedFieldDescriptor = builder.getDescriptorForType().findFieldByNumber(repeatedFieldNumber);
        if (repeatedFieldDescriptor == null) {
            ExceptionPrinter.printHistory(LoggerFactory.getLogger(GenericListContainer.class), new NotAvailableException("repeatedFieldDescriptor"));
            return "?";
        }
        return repeatedFieldDescriptor.getName();
    }

}
