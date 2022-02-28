package org.openbase.bco.registry.editor.struct.editing;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2022 openbase.org
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

import com.google.protobuf.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.editor.struct.editing.util.ScrollingComboBox;
import org.openbase.bco.registry.editor.util.DescriptionGenerator;
import org.openbase.jul.exception.CouldNotPerformException;

import java.util.Comparator;
import java.util.List;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public abstract class AbstractMessageEditingGraphic<M extends Message> extends AbstractEditingGraphic<ScrollingComboBox<M>, String> {

    AbstractMessageEditingGraphic(final DescriptionGenerator<M> descriptionGenerator, ValueType<String> valueType, TreeTableCell<ValueType, ValueType> treeTableCell) {
        super(new ScrollingComboBox<>(descriptionGenerator), valueType, treeTableCell);
        getControl().setOnAction((event) -> commitEdit());
    }

    @Override
    public void commitEdit() {
        if (getControl().getSelectionModel().getSelectedItem() != null) {
            super.commitEdit();
        }
    }

    private ObservableList<M> createSortedList() throws CouldNotPerformException {
        List<M> messages = getMessages();
        messages.sort(Comparator.comparing(getControl().getDescriptionGenerator()::getDescription));
        return FXCollections.observableArrayList(messages);
    }


    @Override
    protected String getCurrentValue() {
        logger.info("New value {}", getCurrentValue(getControl().getSelectionModel().getSelectedItem()));
        return getCurrentValue(getControl().getSelectionModel().getSelectedItem());
    }

    @Override
    protected void init(final String value) {
        try {
            getControl().setItems(createSortedList());
            if (!value.isEmpty()) {
                logger.info("Current value {}", value);
                getControl().setValue(getMessage(value));
            }
        } catch (CouldNotPerformException ex) {
            logger.error("Could create message list", ex);
        }
    }

    /**
     * Get a list of all messages displayed by this message editing graphic.
     *
     * @return a list of all messages displayed.
     *
     * @throws CouldNotPerformException if the list cannot be created.
     */
    protected abstract List<M> getMessages() throws CouldNotPerformException;

    /**
     * Get the current string value representing the currently selected message. This is usually the id of the message.
     *
     * @param message the currently selected message.
     *
     * @return a string representation for the message which is usually the id.
     */
    protected abstract String getCurrentValue(final M message);

    /**
     * Get the message represented by the provided value. This is needed to initialize the combo box to select
     * the returned message.
     *
     * @param value a string representing the message which is usually the id.
     *
     * @return the message represented by the given value.
     *
     * @throws CouldNotPerformException if the message could not be retrieved.
     */
    protected abstract M getMessage(final String value) throws CouldNotPerformException;
}
