package org.openbase.bco.registry.editor.struct.editing;

import javafx.scene.control.DatePicker;
import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.openbase.jul.extension.rst.processing.TimestampJavaTimeTransform;
import rst.timing.TimestampType.Timestamp;

import java.time.ZoneId;
import java.util.Date;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class TimestampEditingGraphic extends AbstractEditingGraphic<DatePicker, Timestamp.Builder> {

    public TimestampEditingGraphic(final ValueType<Timestamp.Builder> valueType, final TreeTableCell<Object, Object> treeTableCell) {
        super(new DatePicker(), valueType, treeTableCell);
        getControl().setOnAction((event) -> commitEdit());
    }

    @Override
    protected Timestamp.Builder getCurrentValue() {
        final Timestamp.Builder timestamp = getValueType().getValue();
        final long timeInMicros = getControl().getValue().toEpochDay() * 24 * 60 * 60 * 1000 * 1000;
        return timestamp.setTime(timeInMicros);
    }

    @Override
    protected void init(Timestamp.Builder value) {
        final Date date = new Date(TimestampJavaTimeTransform.transform(value));
        getControl().setValue(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }
}
