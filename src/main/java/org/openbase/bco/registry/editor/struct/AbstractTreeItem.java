package org.openbase.bco.registry.editor.struct;

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.FatalImplementationErrorException;
import org.openbase.jul.exception.printer.ExceptionPrinter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public abstract class AbstractTreeItem<MB extends Message.Builder> extends GenericTreeItem<MB> {

    private boolean childrenInitialized;


    public AbstractTreeItem(final FieldDescriptor fieldDescriptor, final MB value) {
        super(fieldDescriptor, value);

        this.childrenInitialized = false;

        this.expandedProperty().addListener((observable, oldValue, newValue) -> {
            // initialize children of children as soon as this item is expanded
            // this way the view knows can see which tree items are leaves and which are not
            if (newValue) {
                getChildren().forEach(TreeItem::getChildren);
            }
        });
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

    private String extractMessageClass() {
        return extractMessageClass(getBuilder());
    }

    private String extractMessageClass(final Message.Builder builder) {
        return builder.getClass().getName().split("\\$")[1];
    }

    @SuppressWarnings("unchecked")
    protected BuilderTreeItem loadTreeItem(final FieldDescriptor fieldDescriptor, final Message.Builder builder) throws CouldNotPerformException {
        final String className = getClass().getPackage().getName() + "." + extractMessageClass(builder) + "TreeItem";

        try {
            // load class
            Class<? extends BuilderTreeItem> treeItemClass = (Class<? extends BuilderTreeItem>) getClass().getClassLoader().loadClass(className);

            // get constructor
            Constructor<? extends BuilderTreeItem> constructor = treeItemClass.getConstructor(fieldDescriptor.getClass(), builder.getClass());

            // invoke constructor
            return constructor.newInstance(fieldDescriptor, builder);
        } catch (ClassNotFoundException ex) {
            // no class found so use the default tree item
            return new BuilderTreeItem(fieldDescriptor, builder);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException ex) {
            // could not get or invoke constructor which is only the case on an implementation error
            throw ExceptionPrinter.printHistoryAndReturnThrowable(new FatalImplementationErrorException("Could not invoke constructor[" + className + "] for type[" + extractMessageClass(builder) + "]", this, ex), logger);
        }
    }

}
