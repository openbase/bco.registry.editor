package org.openbase.bco.registry.editor.struct.editing.service;

import rst.domotic.state.StandbyStateType.StandbyState;
import rst.domotic.state.StandbyStateType.StandbyState.Builder;
import rst.domotic.state.StandbyStateType.StandbyState.State;

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
