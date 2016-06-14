package org.openbase.bco.registry.editor.visual.cell.editing;

/*
 * #%L
 * RegistryEditor
 * %%
 * Copyright (C) 2014 - 2016 openbase.org
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

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import org.openbase.bco.registry.editor.visual.cell.ValueCell;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class ValueCheckBox extends CheckBox {

    private final Object selected;
    private final Object unselected;
    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(ValueCheckBox.class);

    public ValueCheckBox(ValueCell cell) {
        this(cell, true, false);
    }

    public ValueCheckBox(ValueCell cell, Object selected, Object unselected) {
        super();
        this.selected = selected;
        this.unselected = unselected;
        setSelected(cell.getLeaf().getValue().equals(selected));
        setVisible(true);
        setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                 try {
                Object value;
                if (isSelected()) {
                    value = selected;
                } else {
                    value = unselected;
                }
                cell.getLeaf().setValue(value);
                cell.setText(value.toString());
                cell.commitEdit(cell.getLeaf());
                } catch (InterruptedException ex) {
                    ExceptionPrinter.printHistory(new CouldNotPerformException("Event handing skipped!", ex), logger, LogLevel.WARN);
                }
            }
        });
    }
}
