package org.openbase.bco.registry.editor.struct.editing;

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
import javafx.scene.control.Control;
import javafx.scene.control.TreeTableCell;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public abstract class AbstractEditingGraphic<GRAPHIC extends Control, V> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final GRAPHIC control;
    private final ValueType<V> valueType;
    private final TreeTableCell<Object, Object> treeTableCell;

    public AbstractEditingGraphic(final GRAPHIC control, final ValueType<V> valueType, final TreeTableCell<Object, Object> treeTableCell) {
        this.control = control;
        this.valueType = valueType;
        this.treeTableCell = treeTableCell;
        this.init(valueType.getValue());
        this.control.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                System.out.println("Commit because of focus loss");
                commitEdit();
            }
        });

        Platform.runLater(() -> getControl().requestFocus());
    }

    protected void commitEdit() {
        treeTableCell.commitEdit(valueType.createNew(getCurrentValue()));
    }

    public GRAPHIC getControl() {
        return control;
    }

    public ValueType<V> getValueType() {
        return valueType;
    }

    protected abstract V getCurrentValue();

    protected abstract void init(V value);
}