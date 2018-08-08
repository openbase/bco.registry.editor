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
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.editing.MetaConfigEntryEditingGraphic;
import org.openbase.jul.exception.InitializationException;
import rst.configuration.EntryType.Entry.Builder;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class EntryTreeItem extends BuilderLeafTreeItem<Builder> {

    public EntryTreeItem(FieldDescriptor fieldDescriptor, Builder value) throws InitializationException {
        super(fieldDescriptor, value);
    }

    @Override
    public Node getValueGraphic() {
        if(!getBuilder().hasKey() && !getBuilder().hasValue()) {
            return new Label("");
        }
        return new Label(getBuilder().getKey() + " = " + getBuilder().getValue());
    }

    @Override
    public Control getEditingGraphic(final TreeTableCell<ValueType, ValueType> cell) {
        return new MetaConfigEntryEditingGraphic(getValue(), cell).getControl();
    }
}
