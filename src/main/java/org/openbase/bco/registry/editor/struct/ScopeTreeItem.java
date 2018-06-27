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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import org.openbase.bco.registry.editor.struct.value.DescriptionGenerator;
import org.openbase.bco.registry.editor.struct.value.EditingGraphicFactory;
import org.openbase.bco.registry.editor.struct.value.ScopeEditingGraphicFactory;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.extension.rsb.scope.ScopeGenerator;
import rst.rsb.ScopeType.Scope;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class ScopeTreeItem extends BuilderTreeItem<Scope.Builder> {

    public ScopeTreeItem(FieldDescriptor fieldDescriptor, Scope.Builder builder) {
        super(fieldDescriptor, builder);
        this.valueProperty().addListener(new ChangeListener<ValueType>() {
            @Override
            public void changed(ObservableValue<? extends ValueType> observable, ValueType oldValue, ValueType newValue) {
                logger.info("Value has changed to[" + newValue.getValue() + "]");
            }
        });
    }

    @Override
    protected ObservableList<TreeItem<ValueType>> createChildren() {
        // empty list because scope will be represented as a string
        return FXCollections.observableArrayList();
    }

    @Override
    protected DescriptionGenerator<Scope.Builder> getDescriptionGenerator() {
        //TODO: maybe descriptor can just be a helper class so that not that many will be generated?
        return new DescriptionGenerator<Scope.Builder>() {
            @Override
            public String getValueDescription(Scope.Builder value) {
                try {
                    return ScopeGenerator.generateStringRep(value.build());
                } catch (CouldNotPerformException ex) {
                    logger.error("Could not generate string rep for scope", ex);
                    return "";
                }
            }

            @Override
            public String getDescription(Scope.Builder value) {
                return getFieldDescriptor().getName();
            }
        };
    }

    @Override
    protected EditingGraphicFactory<Scope.Builder> getEditingGraphicFactory() {
        return ScopeEditingGraphicFactory.getInstance();
    }
}
