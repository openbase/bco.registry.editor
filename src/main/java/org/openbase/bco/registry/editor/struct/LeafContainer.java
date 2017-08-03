package org.openbase.bco.registry.editor.struct;

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
import com.google.protobuf.ProtocolMessageEnum;
import org.openbase.bco.registry.editor.RegistryEditor;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.printer.LogLevel;
import org.openbase.jul.processing.StringProcessor;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class LeafContainer implements Leaf {

    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(LeafContainer.class);

    private final boolean editable;
    private final String fieldName;
    private final String displayedDescriptor;
    private final NodeContainer parent;
    private final int index;
    private Object value;

    public LeafContainer(Object value, String fieldName, NodeContainer parent) {
        this(value, fieldName, parent, true, -1);
    }

    public LeafContainer(Object value, String fieldName, NodeContainer parent, int index) {
        this(value, fieldName, parent, true, index);
    }

    public LeafContainer(Object value, String fieldName, NodeContainer parent, boolean editable) {
        this(value, fieldName, parent, editable, -1);
    }

    public LeafContainer(Object value, String fieldName, NodeContainer parent, boolean editable, int index) {
        this.value = value;
        this.fieldName = fieldName;
        this.parent = parent;
        this.index = index;
        this.editable = editable;
        this.displayedDescriptor = StringProcessor.transformToCamelCase(fieldName).replace(",", " - ");
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public String getDescriptor() {
        return fieldName;
    }

    @Override
    public String getDisplayedDescriptor() {
        return displayedDescriptor;
    }

    public NodeContainer getParent() {
        return parent;
    }

    public int getIndex() {
        return index;
    }

    public boolean getEditable() {
        return editable;
    }

    @Override
    public void setValue(Object value) throws InterruptedException {
        this.value = value;

        try {
            if (value instanceof ProtocolMessageEnum) {
                parent.updateBuilder(getDescriptor(), (ProtocolMessageEnum) value, index);
            } else {
                parent.updateBuilder(getDescriptor(), value, index);
            }
        } catch (CouldNotPerformException ex) {
            RegistryEditor.printException(ex, logger, LogLevel.ERROR);
        }
    }

    @Override
    public Node getContext() {
        return this;
    }
}
