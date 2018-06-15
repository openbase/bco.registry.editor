package org.openbase.bco.registry.editor.struct;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.GeneratedMessage.Builder;
import com.google.protobuf.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.extension.protobuf.BuilderProcessor;

import static com.google.protobuf.Descriptors.FieldDescriptor.Type.MESSAGE;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class ListTreeItem<MB extends Message.Builder> extends AbstractTreeItem<MB> {

    private final boolean modifiable;

    public ListTreeItem(final FieldDescriptor fieldDescriptor, final MB builder, final boolean modifiable) {
        super(fieldDescriptor, builder);

        this.modifiable = modifiable;
    }

    public boolean isModifiable() {
        return modifiable;
    }

    @Override
    protected ObservableList<TreeItem<ValueType>> createChildren() throws CouldNotPerformException {
        System.out.println("Create children for list");
        ObservableList<TreeItem<ValueType>> childList = FXCollections.observableArrayList();

        if (fieldDescriptor.getType() == MESSAGE) {
            // TODO: BuilderProcessor should work on messages and not generated messages
            for (Message.Builder childBuilder : BuilderProcessor.extractRepeatedFieldBuilderList(fieldDescriptor, (Builder) getBuilder())) {
                System.out.println("add new message");
                childList.add(loadTreeItem(fieldDescriptor, childBuilder));
            }
        } else {
            for (int i = 0; i < getBuilder().getRepeatedFieldCount(fieldDescriptor); i++) {
                childList.add(new GenericTreeItem<>(fieldDescriptor, getBuilder().getRepeatedField(fieldDescriptor, i)));
            }
        }

        return childList;
    }

    public void addNewDefaultElement() throws CouldNotPerformException {
        try {
            switch (fieldDescriptor.getType()) {
                case MESSAGE:
                    Message.Builder defaultInstance = BuilderProcessor.addDefaultInstanceToRepeatedField(fieldDescriptor.getName(), (GeneratedMessage.Builder) getBuilder());
                    getChildren().add(loadTreeItem(fieldDescriptor, getBuilder()));
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
                    throw new NotAvailableException("Default value for type[" + fieldDescriptor.getType().name() + "]");
            }
            setExpanded(true);
        } catch (CouldNotPerformException ex) {
            throw new CouldNotPerformException("Could not add default element to field [" + fieldDescriptor.getName() + "]!", ex);
        }
    }

    public void addElement(Object element) throws CouldNotPerformException {
        try {
            if (fieldDescriptor.getType() == MESSAGE) {
                // add the new message builder to the repeated field
                BuilderProcessor.addMessageToRepeatedField(fieldDescriptor, (GeneratedMessage.Builder) element, (GeneratedMessage.Builder) getBuilder());
                // extract the last value from the builder list which should be the new value
                Builder builder = BuilderProcessor.extractRepeatedFieldBuilderList(fieldDescriptor, (Builder) getBuilder()).get(getBuilder().getRepeatedFieldCount(fieldDescriptor) - 1);
                // create tree item and add it
                getChildren().add(loadTreeItem(fieldDescriptor, builder));
                getChildren().get(this.getChildren().size() - 1).setExpanded(true);
            } else {
                // add to repeated field
                getBuilder().addRepeatedField(fieldDescriptor, element);
                // add value
                getChildren().add(new GenericTreeItem<>(fieldDescriptor, element));
            }
            setExpanded(true);
        } catch (CouldNotPerformException ex) {
            throw new CouldNotPerformException("Could not add element [" + element + "] to field [" + fieldDescriptor.getName() + "]!", ex);
        }
    }
}
