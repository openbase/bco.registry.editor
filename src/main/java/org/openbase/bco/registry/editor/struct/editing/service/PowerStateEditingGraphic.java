package org.openbase.bco.registry.editor.struct.editing.service;

import rst.domotic.state.PowerStateType.PowerState;
import rst.domotic.state.PowerStateType.PowerState.Builder;
import rst.domotic.state.PowerStateType.PowerState.State;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class PowerStateEditingGraphic extends AbstractToggleEditingGraphic<PowerState> {

    public PowerStateEditingGraphic() {
        super(PowerState.class);
    }

    @Override
    PowerState getServiceState(final boolean isSelected) {
        final Builder builder = PowerState.newBuilder();
        if (isSelected) {
            builder.setValue(State.ON);
        } else {
            builder.setValue(State.OFF);
        }
        return builder.build();
    }

    @Override
    Boolean isSelected(PowerState serviceState) {
        switch (serviceState.getValue()) {
            case ON:
                return true;
            default:
                return false;
        }
    }
}
