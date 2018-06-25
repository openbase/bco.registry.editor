package org.openbase.bco.registry.editor.struct.editing;

import javafx.scene.control.Control;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public abstract class AbstractEditingGraphic {

    private final Control control;

    public AbstractEditingGraphic(final Control control) {
        this.control = control;
    }
}
