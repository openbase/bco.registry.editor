package org.openbase.bco.registry.editor.struct.editing.service;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2020 openbase.org
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

import org.openbase.type.domotic.state.StandbyStateType.StandbyState;
import org.openbase.type.domotic.state.StandbyStateType.StandbyState.Builder;
import org.openbase.type.domotic.state.StandbyStateType.StandbyState.State;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class StandbyStateEditingGraphic extends AbstractToggleEditingGraphic<StandbyState> {

    public StandbyStateEditingGraphic() {
        super(StandbyState.class);
    }

    @Override
    StandbyState getServiceState(boolean isSelected) {
        Builder builder = StandbyState.newBuilder();
        if (isSelected) {
            builder.setValue(State.STANDBY);
        } else {
            builder.setValue(State.RUNNING);
        }
        return builder.build();
    }

    @Override
    Boolean isSelected(StandbyState serviceState) {
        switch (serviceState.getValue()) {
            case STANDBY:
                return true;
            default:
                return false;
        }
    }
}
