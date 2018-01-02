package org.openbase.bco.registry.editor.visual.cell.editing;

/*
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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.openbase.bco.registry.editor.RegistryEditor;
import org.openbase.bco.registry.editor.visual.cell.ValueCell;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.LogLevel;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class StringTextField extends TextField {

    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(StringTextField.class);

    public StringTextField(ValueCell cell, String text) {
        super();
        setText(text);
        Platform.runLater(() -> {
            requestFocus();
        });
        focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            try {
                if (newValue) {
                    selectAll();
                } else {
                    cell.getLeaf().setValue(getText());
                    // even though commit is called the text property won't change fast enough without this line?!?
                    cell.setGraphic(new Label(getText()));
                    cell.commitEdit(cell.getLeaf());
                }
            } catch (InterruptedException ex) {
                RegistryEditor.printException(new CouldNotPerformException("Event handing skipped!", ex), logger, LogLevel.WARN);
            }
        });
        setOnKeyReleased((KeyEvent event) -> {
            try {
                if (event.getCode().equals(KeyCode.ESCAPE)) {
                    cell.cancelEdit();
                } else if (event.getCode().equals(KeyCode.ENTER)) {
                    cell.getLeaf().setValue(getText());
                    cell.setGraphic(new Label(getText()));
                    cell.commitEdit(cell.getLeaf());
                }
            } catch (InterruptedException ex) {
                RegistryEditor.printException(new CouldNotPerformException("Event handing skipped!", ex), logger, LogLevel.WARN);
            }
        });
    }
}
