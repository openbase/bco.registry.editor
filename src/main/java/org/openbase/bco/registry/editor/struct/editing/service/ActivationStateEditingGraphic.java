package org.openbase.bco.registry.editor.struct.editing.service;

import rst.domotic.state.ActivationStateType.ActivationState;
import rst.domotic.state.ActivationStateType.ActivationState.Builder;
import rst.domotic.state.ActivationStateType.ActivationState.State;

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
            builder.setValue(State.DEACTIVE);
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
