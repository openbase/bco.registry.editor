package org.openbase.bco.registry.editor.struct.value;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2018 openbase.org
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
            this.setPrefWidth(cell.getTableColumn().getPrefWidth());

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
            System.out.println("Convert text[" + text + "] into scope");
            final Scope.Builder scope = Scope.newBuilder();
            String[] split = text.split("/");
            for (int i = 0; i < split.length; i++) {
                scope.addComponent(ScopeGenerator.convertIntoValidScopeComponent(split[i]));
            }
            return scope;
        }
    }
}
