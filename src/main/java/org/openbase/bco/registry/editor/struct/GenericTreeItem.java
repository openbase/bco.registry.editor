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
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.processing.StringProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class GenericTreeItem<V> extends TreeItem<ValueType> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final FieldDescriptor fieldDescriptor;

    private Node descriptionGraphic;
    private Node valueGraphic;
    private boolean editable;

    public GenericTreeItem(final FieldDescriptor fieldDescriptor, final V value, final Boolean editable) {
        this.fieldDescriptor = fieldDescriptor;
        this.editable = editable;
        this.setValue(new ValueType<>(value, this));
    }

    public FieldDescriptor getFieldDescriptor() {
        return fieldDescriptor;
    }

    @SuppressWarnings("unchecked")
    V getInternalValue() {
        return (V) getValue().getValue();
    }

    public Node getEditingGraphic(final TreeTableCell<ValueType, ValueType> cell) {
        return null;
    }

    /**
     * Get the graphic which is displayed in the description column for this tree item.
     * If the description graphic is not yet initialized this will result in a call to {@link #createDescriptionGraphic()}.
     * Else the current description graphic is just returned.
     *
     * @return the graphic displayed in the description column.
     */
    public final Node getDescriptionGraphic() {
        if (descriptionGraphic == null) {
            updateDescriptionGraphic();
        }
        return descriptionGraphic;
    }

    /**
     * Get the graphic which is displayed in the value column for this tree item.
     * If the value graphic is not yet initialized this will result in a call to {@link #createValueGraphic()}.
     * Else the current value graphic is just returned.
     *
     * @return the graphic displayed in the value column.
     */
    public final Node getValueGraphic() {
        if (valueGraphic == null) {
            updateValueGraphic();
        }
        return valueGraphic;
    }

    protected boolean isEditable() {
        return editable;
    }

    @SuppressWarnings("unchecked")
    protected ValueType<V> getValueCasted() {
        return (ValueType<V>) getValue();
    }

    /**
     * Create a default graphic displayed in the description column. This will create a label from the field descriptor
     * name. If a tree item needs a different representation is should overwrite this method.
     * If the description graphic changes for a tree item if its internal value changes it should register an event
     * handler on the {@link #valueChangedEvent()} by calling {@link #addEventHandler(EventType, EventHandler)}.
     * This event handler should call {@link #updateDescriptionGraphic()} ()} to generate and save new description graphic.
     *
     * @return a graphic displayed in the description column
     */
    protected Node createDescriptionGraphic() {
        return new Label(StringProcessor.transformToCamelCase(fieldDescriptor.getName()));
    }

    /**
     * Create a default graphic displayed in the value column. This method does not create a graphic.
     * If a tree item needs a representation it should overwrite this method.
     * If the description graphic changes for a tree item if its internal value changes it should register an event
     * handler on the {@link #valueChangedEvent()} by calling {@link #addEventHandler(EventType, EventHandler)}.
     * This event handler should call {@link #updateValueGraphic()} to generate and save new value graphic.
     *
     * @return a graphic displayed in the value column
     */
    protected Node createValueGraphic() {
        return null;
    }

    /**
     * Update the current description graphic by setting it to the result of {@link #createDescriptionGraphic()}.
     */
    protected final void updateDescriptionGraphic() {
        this.descriptionGraphic = createDescriptionGraphic();
    }

    /**
     * Update the current value graphic by setting it to the result of {@link #createValueGraphic()} ()}.
     */
    protected final void updateValueGraphic() {
        this.valueGraphic = createValueGraphic();
    }

    /**
     * Set the graphic displayed in the description column for this tree item.
     * Use this method with care regarding to the fact that a tree item may want to update its
     * graphic when its internal value changes. Therefore the value set here may be overwritten.
     *
     * @param descriptionGraphic the graphic displayed in the description column of this tree item
     */
    public void setDescriptionGraphic(final Node descriptionGraphic) {
        this.descriptionGraphic = descriptionGraphic;
    }

    /**
     * Set the graphic displayed in the value column for this tree item.
     * Use this method with care regarding to the fact that a tree item may want to update its
     * graphic when its internal value changes. Therefore the value set here may be overwritten.
     *
     * @param valueGraphic the graphic displayed in the value column of this tree item
     */
    public void setValueGraphic(final Node valueGraphic) {
        this.valueGraphic = valueGraphic;
    }

    public void update(final V value) throws CouldNotPerformException {
        this.setValue(getValueCasted().createNew(value));
    }
}
