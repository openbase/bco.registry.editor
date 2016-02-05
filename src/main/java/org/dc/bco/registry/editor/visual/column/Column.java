package org.dc.bco.registry.editor.visual.column;

/*
 * #%L
 * RegistryEditor
 * %%
 * Copyright (C) 2014 - 2016 DivineCooperation
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

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import org.dc.bco.registry.editor.struct.Node;
import org.slf4j.LoggerFactory;

/**
 *
 * @author thuxohl
 */
public abstract class Column extends TreeTableColumn<Node, Node> {

    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(Column.class);

    public Column(String text) {
        super(text);
        this.setCellValueFactory(new TreeItemPropertyValueFactory<>("context"));
    }

    public abstract void addWidthProperty(ReadOnlyDoubleProperty widthProperty);
}
