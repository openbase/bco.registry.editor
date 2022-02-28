package org.openbase.bco.registry.editor.struct.editing.service;

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
import javafx.scene.control.CheckBox;
import org.openbase.bco.registry.editor.struct.editing.ServiceStateAttributeEditingGraphic;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public abstract class AbstractToggleEditingGraphic<STATE extends Message> extends AbstractServiceStateEditingGraphic<STATE, CheckBox> {

    AbstractToggleEditingGraphic(Class<STATE> serviceStateClass) {
        super(new CheckBox(), serviceStateClass);
    }

    @Override
    void internalInit(STATE state) {
        getGraphic().setSelected(isSelected(state));
    }

    @Override
    public STATE getServiceState() {
        return getServiceState(getGraphic().isSelected());
    }

    @Override
    public void addCommitEditEventHandler(final ServiceStateAttributeEditingGraphic editingGraphic) {
        getGraphic().setOnAction(event -> editingGraphic.commitEdit());
    }

    abstract STATE getServiceState(boolean isSelected);

    abstract Boolean isSelected(final STATE serviceState);
}
