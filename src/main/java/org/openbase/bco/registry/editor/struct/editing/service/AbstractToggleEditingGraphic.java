package org.openbase.bco.registry.editor.struct.editing.service;

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
