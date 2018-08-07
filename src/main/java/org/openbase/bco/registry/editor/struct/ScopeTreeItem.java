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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.editing.ScopeEditingGraphic;
import org.openbase.bco.registry.editor.struct.value.DescriptionGenerator;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.extension.rsb.scope.ScopeGenerator;
import rst.rsb.ScopeType.Scope;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class ScopeTreeItem extends BuilderTreeItem<Scope.Builder> {

    public ScopeTreeItem(FieldDescriptor fieldDescriptor, Scope.Builder builder) throws InitializationException {
        super(fieldDescriptor, builder);
    }

    @Override
    protected ObservableList<TreeItem<ValueType>> createChildren() {
        // empty list because scope will be represented as a string
        return FXCollections.observableArrayList();
    }

    @Override
    public Node getValueGraphic() {
        try {
            return new Label(ScopeGenerator.generateStringRep(getInternalValue().build()));
        } catch (CouldNotPerformException ex) {
            return new Label("Not Available");
        }
    }

    @Override
    public Control getEditingGraphic(final TreeTableCell<ValueType, ValueType> cell) {
        return new ScopeEditingGraphic(getValueCasted(), cell).getControl();
    }
}
