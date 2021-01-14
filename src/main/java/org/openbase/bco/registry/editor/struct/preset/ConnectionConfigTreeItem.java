package org.openbase.bco.registry.editor.struct.preset;

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
import org.openbase.bco.registry.editor.struct.BuilderTreeItem;
import org.openbase.bco.registry.editor.struct.GenericTreeItem;
import org.openbase.bco.registry.editor.struct.ValueListTreeItem;
import org.openbase.bco.registry.editor.struct.editing.ConnectionUnitIdEditingGraphic;
import org.openbase.bco.registry.editor.struct.editing.EditingGraphicFactory;
import org.openbase.bco.registry.editor.struct.editing.TileIdEditingGraphic;
import org.openbase.bco.registry.editor.util.UnitScopeDescriptionGenerator;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.type.domotic.unit.connection.ConnectionConfigType.ConnectionConfig;
import org.openbase.type.domotic.unit.connection.ConnectionConfigType.ConnectionConfig.Builder;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class ConnectionConfigTreeItem extends BuilderTreeItem<ConnectionConfig.Builder> {

    public ConnectionConfigTreeItem(final FieldDescriptor fieldDescriptor, final Builder builder, final Boolean editable) throws InitializationException {
        super(fieldDescriptor, builder, editable);
    }

    @Override
    protected GenericTreeItem createChild(final FieldDescriptor field, final Boolean editable) throws CouldNotPerformException {
        switch (field.getNumber()) {
            case ConnectionConfig.TILE_ID_FIELD_NUMBER:
                final ValueListTreeItem tileIdList = new ValueListTreeItem<>(field, getBuilder(), editable);
                tileIdList.setEditingGraphicFactory(EditingGraphicFactory.getInstance(TileIdEditingGraphic.class));
                tileIdList.setDescriptionGenerator(new UnitScopeDescriptionGenerator());
                return tileIdList;
            case ConnectionConfig.UNIT_ID_FIELD_NUMBER:
                final ValueListTreeItem unitIdList = new ValueListTreeItem<>(field, getBuilder(), editable);
                unitIdList.setEditingGraphicFactory(EditingGraphicFactory.getInstance(ConnectionUnitIdEditingGraphic.class));
                unitIdList.setDescriptionGenerator(new UnitScopeDescriptionGenerator());
                return unitIdList;
            default:
                return super.createChild(field, editable);
        }
    }
}
