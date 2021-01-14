package org.openbase.bco.registry.editor.struct.editing;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2021 openbase.org
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

import javafx.scene.control.DatePicker;
import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.jul.extension.type.processing.TimestampJavaTimeTransform;
import org.openbase.type.timing.TimestampType.Timestamp;
import org.openbase.type.timing.TimestampType.Timestamp.Builder;

import java.time.ZoneId;
import java.util.Date;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class TimestampEditingGraphic extends AbstractBuilderEditingGraphic<DatePicker, Builder> {

    public TimestampEditingGraphic(final ValueType<Timestamp.Builder> valueType, final TreeTableCell<ValueType, ValueType> treeTableCell) {
        super(new DatePicker(), valueType, treeTableCell);
        getControl().setOnAction((event) -> commitEdit());
    }

    @Override
    protected boolean updateBuilder(Builder builder) {
        final long newTime = getControl().getValue().toEpochDay() * 24 * 60 * 60 * 1000 * 1000;

        if (newTime == builder.getTime()) {
            return false;
        }

        builder.setTime(newTime);
        return true;
    }

    @Override
    protected void init(Timestamp.Builder value) {
        final Date date = new Date(TimestampJavaTimeTransform.transform(value));
        getControl().setValue(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }
}
