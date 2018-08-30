package org.openbase.bco.registry.editor.struct.preset;

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
import org.openbase.bco.registry.editor.struct.BuilderTreeItem;
import org.openbase.bco.registry.editor.struct.GenericTreeItem;
import org.openbase.bco.registry.editor.struct.LeafTreeItem;
import org.openbase.bco.registry.editor.struct.editing.EditingGraphicFactory;
import org.openbase.bco.registry.editor.struct.editing.LocationIdEditingGraphic;
import org.openbase.bco.registry.editor.struct.editing.UserIdEditingGraphic;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.extension.rsb.scope.ScopeGenerator;
import org.openbase.jul.extension.rst.processing.TimestampProcessor;
import rst.domotic.state.InventoryStateType.InventoryState;
import rst.domotic.state.InventoryStateType.InventoryState.Builder;

import java.util.Set;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class InventoryStateTreeItem extends BuilderTreeItem<Builder> {

    public InventoryStateTreeItem(FieldDescriptor fieldDescriptor, Builder builder, Boolean editable) throws InitializationException {
        super(fieldDescriptor, builder, editable);

        this.addEventHandler(valueChangedEvent(), event -> {
            GenericTreeItem source = (GenericTreeItem) event.getSource();

            if (!(source instanceof TimestampTreeItem)) {
                try {
                    TimestampProcessor.updateTimestampWithCurrentTime(getBuilder().getTimestampBuilder());
                    source.update(getBuilder().getTimestampBuilder());
                } catch (CouldNotPerformException ex) {
                    logger.warn("Could not update timestamp", ex);
                }
            }
        });
    }

    @Override
    protected Set<Integer> getFilteredFields() {
        final Set<Integer> filteredFields = super.getFilteredFields();
        filteredFields.add(InventoryState.LAST_VALUE_OCCURRENCE_FIELD_NUMBER);
        return filteredFields;
    }

    @Override
    protected GenericTreeItem createChild(final FieldDescriptor field, final Boolean editable) throws CouldNotPerformException {
        switch (field.getNumber()) {
            case InventoryState.LOCATION_ID_FIELD_NUMBER:
                final LeafTreeItem<String> leaf = new LeafTreeItem<>(field, getBuilder().getLocationId(), getBuilder(), editable);
                leaf.setEditingGraphicFactory(EditingGraphicFactory.getInstance(LocationIdEditingGraphic.class));
                leaf.setDescriptionGenerator(value -> {
                    try {
                        return ScopeGenerator.generateStringRep(Registries.getUnitRegistry().getUnitConfigById(value).getScope());
                    } catch (CouldNotPerformException e) {
                        return value;
                    }
                });
                return leaf;
            case InventoryState.BORROWER_ID_FIELD_NUMBER:
                final LeafTreeItem<String> borrowerLeaf = new LeafTreeItem<>(field, getBuilder().getBorrowerId(), getBuilder(), editable);
                borrowerLeaf.setEditingGraphicFactory(EditingGraphicFactory.getInstance(UserIdEditingGraphic.class));
                borrowerLeaf.setDescriptionGenerator(value -> {
                    try {
                        return Registries.getUnitRegistry().getUnitConfigById(value).getUserConfig().getUserName();
                    } catch (CouldNotPerformException e) {
                        return value;
                    }
                });

            default:
                return super.createChild(field, editable);
        }
    }
}
