package org.openbase.bco.registry.editor.struct;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2021 openbase.org
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
import org.openbase.bco.registry.editor.struct.preset.UnitConfigTreeItem;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.FatalImplementationErrorException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public abstract class AbstractBuilderTreeItem<MB extends Message.Builder> extends GenericTreeItem<MB> {

    private boolean childrenInitialized;

    public AbstractBuilderTreeItem(final FieldDescriptor fieldDescriptor, final MB value, final Boolean editable) {
        super(fieldDescriptor, value, editable);

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
                final ObservableList<TreeItem<ValueType>> children = createChildren();
                children.sort(Comparator.comparing(o -> o.getValue().getTreeItem().getDescriptionText()));
                super.getChildren().addAll(children);
            } catch (CouldNotPerformException ex) {
                ExceptionPrinter.printHistory(new CouldNotPerformException(
                        "Could not generate child items for[" + getClass().getSimpleName() + "] with value type[" + getInternalValue().getClass().getSimpleName() + "]", ex), logger);
            }
        }
        return super.getChildren();
    }

    abstract protected ObservableList<TreeItem<ValueType>> createChildren() throws CouldNotPerformException;

    protected boolean childrenInitialized() {
        return childrenInitialized;
    }

    public static String extractSimpleMessageClass(final Message.Builder builder) {
        String className = "";
        final String[] split = builder.getClass().getName().split("\\$");
        for (int i = 1; i < split.length - 1; i++) {
            className += split[i];
        }
        return className;
    }

    @SuppressWarnings("unchecked")
    public static Class<? extends BuilderTreeItem> getPresetClass(final String className) throws NotAvailableException {
        try {
            return (Class<? extends BuilderTreeItem>) AbstractBuilderTreeItem.class.getClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new NotAvailableException(className);
        }
    }

    public static String getPresetClassName(final Message.Builder builder) {
        return UnitConfigTreeItem.class.getPackage().getName() + "." + extractSimpleMessageClass(builder) + "TreeItem";
    }

    @SuppressWarnings("unchecked")
    public static BuilderTreeItem loadTreeItem(final FieldDescriptor fieldDescriptor, final Message.Builder builder, final Boolean editable) throws CouldNotPerformException {
        final String className = getPresetClassName(builder);

        try {
            // load class
            Class<? extends BuilderTreeItem> treeItemClass = getPresetClass(className);

            // get constructor
            Constructor<? extends BuilderTreeItem> constructor = treeItemClass.getConstructor(fieldDescriptor.getClass(), builder.getClass(), editable.getClass());

            // invoke constructor
            return constructor.newInstance(fieldDescriptor, builder, editable);
        } catch (NotAvailableException ex) {
            // no class found so use the default tree item
            return new BuilderTreeItem(fieldDescriptor, builder, editable);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException ex) {
            // could not get or invoke constructor which is only the case on an implementation error
            throw ExceptionPrinter.printHistoryAndReturnThrowable(new FatalImplementationErrorException("Could not invoke constructor[" + className + "] for type[" + extractSimpleMessageClass(builder) + "]", AbstractBuilderTreeItem.class, ex), LoggerFactory.getLogger(AbstractBuilderTreeItem.class));
        }
    }
}
