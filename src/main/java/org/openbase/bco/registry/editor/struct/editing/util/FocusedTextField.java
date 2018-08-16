package org.openbase.bco.registry.editor.struct.editing.util;

import javafx.application.Platform;
import javafx.scene.control.TextField;

/**
 * A textfield that will select all its text when focused.
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class FocusedTextField extends TextField {

    /**
     * Create a new textfield that selects all its text when focused.
     */
    public FocusedTextField() {
        super();

        focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                Platform.runLater(this::selectAll);
            }
        });
    }
}
