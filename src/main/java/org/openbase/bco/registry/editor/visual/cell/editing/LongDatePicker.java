package org.openbase.bco.registry.editor.visual.cell.editing;

/*
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2017 openbase.org
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import org.openbase.bco.registry.editor.visual.cell.ValueCell;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.ExceptionPrinter;
import org.openbase.jul.exception.printer.LogLevel;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class LongDatePicker extends DatePicker {

    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(LongDatePicker.class);

    public static final DateFormat DATE_CONVERTER = new SimpleDateFormat("dd/MM/yyyy");

    public LongDatePicker(ValueCell cell, Long time) {
        super();
        Date date = new Date(time);
        setValue(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                try {
                    if (getValue() != null && getValue().toEpochDay() != (Long) cell.getLeaf().getValue()) {
                        cell.getLeaf().setValue(getValue().toEpochDay() * 24 * 60 * 60 * 1000);
                        cell.setGraphic(new Label(DATE_CONVERTER.format(new Date((Long) cell.getLeaf().getValue()))));
                        cell.commitEdit(cell.getLeaf());
                    }
                } catch (InterruptedException ex) {
                    ExceptionPrinter.printHistory(new CouldNotPerformException("Event handing skipped!", ex), logger, LogLevel.WARN);
                }
            }
        });
    }
}
