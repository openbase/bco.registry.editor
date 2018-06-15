package org.openbase.bco.registry.editor.struct.value;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.extension.rsb.scope.ScopeGenerator;
import rst.rsb.ScopeType.Scope;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class ScopeEditingGraphicFactory implements EditingGraphicFactory<Scope.Builder> {

    private static ScopeEditingGraphicFactory instance;

    public static synchronized ScopeEditingGraphicFactory getInstance() {
        if (instance == null) {
            instance = new ScopeEditingGraphicFactory();
        }

        return instance;
    }

    private ScopeEditingGraphicFactory() {
    }

    @Override
    public Control getEditingGraphic(ValueType<Scope.Builder> valueType, final TreeTableCell<ValueType<Scope.Builder>, ValueType<Scope.Builder>> cell) {
        return new ScopeTextField(valueType, cell);
    }

    public class ScopeTextField extends TextField {

        ScopeTextField(final ValueType<Scope.Builder> valueType, final TreeTableCell<ValueType<Scope.Builder>, ValueType<Scope.Builder>> cell) {
            super();

            try {
                setText(ScopeGenerator.generateStringRep(valueType.getValue().build()));
            } catch (CouldNotPerformException e) {
                setText("");
            }

            Platform.runLater(this::requestFocus);
            focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                if (newValue) {
                    // came into focus so select all text
                    selectAll();
                } else {
                    valueType.setValue(convertToScope(getText()));
                    cell.commitEdit(valueType);
                }
            });
            setOnKeyReleased((KeyEvent event) -> {
                if (event.getCode().equals(KeyCode.ESCAPE)) {
                    cell.cancelEdit();
                } else if (event.getCode().equals(KeyCode.ENTER)) {
                    valueType.setValue(convertToScope(getText()));
                    cell.commitEdit(valueType);
                }
            });
        }

        private Scope.Builder convertToScope(final String text) {
            final Scope.Builder scope = Scope.newBuilder();
            String[] split = text.split("/");
            for (int i = 0; i < split.length; i++) {
                scope.setComponent(i, ScopeGenerator.convertIntoValidScopeComponent(split[i]));
            }
            return scope;
        }
    }
}
