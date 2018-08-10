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
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.BuilderLeafTreeItem;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.bco.registry.editor.struct.editing.EnablingStateEditingGraphic;
import org.openbase.jul.exception.InitializationException;
import rst.domotic.state.EnablingStateType.EnablingState;
import rst.domotic.state.EnablingStateType.EnablingState.Builder;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class EnablingStateTreeItem extends BuilderLeafTreeItem<Builder> {

    public EnablingStateTreeItem(final FieldDescriptor fieldDescriptor, final EnablingState.Builder builder, Boolean editable) throws InitializationException {
        super(fieldDescriptor, builder, editable);
    }

    @Override
    protected Node createValueGraphic() {
        return new Label(getBuilder().getValue().name());
    }

    @Override
    public Control getEditingGraphic(final TreeTableCell<ValueType, ValueType> cell) {
        return new EnablingStateEditingGraphic(getValue(), cell).getControl();
    }
}
