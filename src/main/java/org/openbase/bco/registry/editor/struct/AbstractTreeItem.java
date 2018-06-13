package org.openbase.bco.registry.editor.struct;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public abstract class AbstractTreeItem<MB extends Message.Builder> extends GenericTreeItem<MB> {

    private boolean childrenInitialized;


    public AbstractTreeItem(final FieldDescriptor fieldDescriptor, final MB value) {
        super(fieldDescriptor, value);

        this.childrenInitialized = false;
    }

    @Override
    public ObservableList<TreeItem<ValueType>> getChildren() {
        if (!childrenInitialized) {
            childrenInitialized = true;

            try {
                super.getChildren().addAll(createChildren());
            } catch (CouldNotPerformException ex) {
                ExceptionPrinter.printHistory(new CouldNotPerformException(
                        "Could not generate child items for field[" + fieldDescriptor.getName() + "] of message[" + extractMessageClass() + "]", ex), logger);
            }
        }
        return super.getChildren();
    }

    abstract protected ObservableList<TreeItem<ValueType>> createChildren() throws CouldNotPerformException;

    public MB getBuilder() {
        return getInternalValue();
    }

    protected String extractMessageClass() {
        return getBuilder().getClass().getName().split("\\$")[1];
    }
}
