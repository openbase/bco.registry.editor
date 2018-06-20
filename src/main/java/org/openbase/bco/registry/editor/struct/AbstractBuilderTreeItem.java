package org.openbase.bco.registry.editor.struct;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2018 openbase.org
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
public abstract class AbstractBuilderTreeItem<MB extends Message.Builder> extends GenericTreeItem<MB> {

    private boolean childrenInitialized;

    public AbstractBuilderTreeItem(final FieldDescriptor fieldDescriptor, final MB value) {
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

    public MB getBuilder() {
        return getInternalValue();
    }

    @Override
    public ObservableList<TreeItem<ValueType>> getChildren() {
        if (!childrenInitialized) {
            childrenInitialized = true;

            try {
                super.getChildren().addAll(createChildren());
            } catch (CouldNotPerformException ex) {
                ExceptionPrinter.printHistory(new CouldNotPerformException(
                        "Could not generate child items for[" + getClass().getSimpleName() + "] with value type[" + getInternalValue().getClass().getSimpleName() + "]", ex), logger);
            }
        }
        return super.getChildren();
    }

    abstract protected ObservableList<TreeItem<ValueType>> createChildren() throws CouldNotPerformException;

    protected String extractMessageClass(final Message.Builder builder) {
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
