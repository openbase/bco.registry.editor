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

import org.openbase.type.domotic.state.ActivationStateType.ActivationState;
import org.openbase.type.domotic.state.ActivationStateType.ActivationState.Builder;
import org.openbase.type.domotic.state.ActivationStateType.ActivationState.State;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class ActivationStateEditingGraphic extends AbstractToggleEditingGraphic<ActivationState> {

    public ActivationStateEditingGraphic() {
        super(ActivationState.class);
    }

    @Override
    ActivationState getServiceState(boolean isSelected) {
        final Builder builder = ActivationState.newBuilder();
        if (isSelected) {
            builder.setValue(State.ACTIVE);
        } else {
            builder.setValue(State.INACTIVE);
        }
        return builder.build();
    }

    @Override
    Boolean isSelected(ActivationState serviceState) {
        switch (serviceState.getValue()) {
            case ACTIVE:
                return true;
            default:
                return false;
        }
    }
}
