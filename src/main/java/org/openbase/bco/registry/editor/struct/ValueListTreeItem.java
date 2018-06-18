package org.openbase.bco.registry.editor.struct;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.Type;
import com.google.protobuf.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class ValueListTreeItem<MB extends Message.Builder> extends AbstractBuilderTreeItem<MB> {

    private final boolean modifiable;

    public ValueListTreeItem(final FieldDescriptor fieldDescriptor, final MB builder, final boolean modifiable) throws InitializationException {
        super(fieldDescriptor, builder);

        try {
            validateDescriptor();
            this.modifiable = modifiable;
        } catch (CouldNotPerformException ex) {
            throw new InitializationException(this, ex);
        }
    }

    private void validateDescriptor() throws CouldNotPerformException {
        if (getFieldDescriptor().getType() == Type.MESSAGE || !getFieldDescriptor().isRepeated()) {
            throw new CouldNotPerformException("FieldDescriptor[" + getFieldDescriptor() + "] of Message[" + extractMessageClass(getBuilder()) + "] is not a repeated message");
        }
    }

    public boolean isModifiable() {
        return modifiable;
    }

    @Override
    protected ObservableList<TreeItem<ValueType>> createChildren() {
        ObservableList<TreeItem<ValueType>> childList = FXCollections.observableArrayList();

        for (int i = 0; i < getBuilder().getRepeatedFieldCount(getFieldDescriptor()); i++) {
            //TODO: something else than genericTreeItem
            childList.add(new GenericTreeItem<>(getFieldDescriptor(), getBuilder().getRepeatedField(getFieldDescriptor(), i)));
        }

        return childList;
    }

    private Object getDefaultValue() throws CouldNotPerformException {
        switch (getFieldDescriptor().getType()) {
            case STRING:
                return "";
            case DOUBLE:
            case FLOAT:
            case INT32:
            case INT64:
                return 0;
            case BOOL:
                return true;
            case ENUM:
                return getFieldDescriptor().getEnumType().getValues().get(0);
            default:
                throw new CouldNotPerformException("Could not generate default element for type[" + getFieldDescriptor().getType().name() + "]");
        }
    }

    public void addElement() throws CouldNotPerformException {
        // generate default value
        final Object defaultValue = getDefaultValue();
        // add to builder
        getBuilder().addRepeatedField(getFieldDescriptor(), defaultValue);
        // add child tree item with this value
        //TODO: child has to now its index
        getChildren().add(new GenericTreeItem<>(getFieldDescriptor(), getDefaultValue()));
        // expand
        setExpanded(true);
    }
}
