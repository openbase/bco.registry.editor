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
import org.openbase.jul.exception.InitializationException;
import rst.domotic.state.InventoryStateType.InventoryState.Builder;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class InventoryStateTreeItem extends BuilderTreeItem<Builder> {

    public InventoryStateTreeItem(FieldDescriptor fieldDescriptor, Builder builder, Boolean editable) throws InitializationException {
        super(fieldDescriptor, builder, editable);

        this.addEventHandler(valueChangedEvent(), event -> {
            // TODO: if value die not come from timestamp, update the timestamp to now

        });
    }
}
