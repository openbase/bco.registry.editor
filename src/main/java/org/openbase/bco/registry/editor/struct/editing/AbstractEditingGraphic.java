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
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.TreeTableCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import org.openbase.bco.registry.editor.struct.ValueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Abstract class managing the configuration of an editing graphic in a tree table cell.
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public abstract class AbstractEditingGraphic<GRAPHIC extends Node, V> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final GRAPHIC control;
    private final ValueType<V> valueType;
    private final TreeTableCell<ValueType, ValueType> treeTableCell;
    private final Set<Node> internalElements;

    private ChangeListener<Boolean> focusChangeListener;

    /**
     * Create a new editing graphic.
     *
     * @param control       the node which will be displayed in the tree table cell
     * @param valueType     the value for which the editing graphic is created
     * @param treeTableCell the tree table cell on which changes are notified, displays the given value type
     */
    AbstractEditingGraphic(final GRAPHIC control, final ValueType<V> valueType, final TreeTableCell<ValueType, ValueType> treeTableCell) {
        this.control = control;
        this.valueType = valueType;
        this.treeTableCell = treeTableCell;
        this.internalElements = new HashSet<>();

        // allow implementations to init further internal components depending on the current value
        this.init(valueType.getValue());
        // add all elements to a list for focus handling
        this.addInternalElements(internalElements, control);
        // init listeners for focus handling
        this.initFocusLossHandling();
        // if the control component already had a key handler integrate it, e.g. needed for ScrollingComboBox
        final EventHandler<? super KeyEvent> oldKeyHandler = this.control.getOnKeyReleased();
        // add key released handling
        this.control.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case ENTER:
                    // commit on enter released
                    commitEdit();
                    break;
                case ESCAPE:
                    //TODO: this does not work anymore for all nodes, e.g. PermissionEditingGraphic, search why
                    // cancel on escape released
                    // remove focus handler, this is needed because commit causes focus loss which would result in doubled commits
                    // if the initial commit is not started because of the focus loss
                    for (final Node internalElement : internalElements) {
                        internalElement.focusedProperty().removeListener(focusChangeListener);
                    }
                    treeTableCell.cancelEdit();
                    break;
                default:
                    if (oldKeyHandler != null) {
                        oldKeyHandler.handle(event);
                    }
            }
        });

        // initially request focus for control element
        Platform.runLater(() -> getControl().requestFocus());
    }

    /**
     * Recursively add all nodes to the internal element set. The given node is added and if it
     * is a region its children will be recursively added.
     *
     * @param internalElements the set to which the nodes will be added
     * @param node             the node from which the recursive process starts
     */
    private void addInternalElements(final Set<Node> internalElements, final Node node) {
        internalElements.add(node);
        if (node instanceof Region) {
            for (Node internalNode : ((Region) node).getChildrenUnmodifiable()) {
                addInternalElements(internalElements, internalNode);
            }
        }
    }

    /**
     * Compute if none of the internal components currently owns the focus.
     *
     * @return true if no internal components is focused, else false
     */
    private boolean lostFocus() {
        for (final Node internalElement : internalElements) {
            if (internalElement.focusedProperty().get()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Add change listener to all internal components that will trigger commit edit if no internal component is
     * focused.
     */
    private void initFocusLossHandling() {
        // create change listener
        focusChangeListener = (observable, oldValue, newValue) -> {
            if (lostFocus()) {
                commitEdit();
            }
        };
        // add to all components
        for (final Node internalElement : internalElements) {
            internalElement.focusedProperty().addListener(focusChangeListener);
        }
    }

    /**
     * Commit the current value. In case the currently setup value does not differ from the starting one
     * the editing will just be cancelled. This is done so that the apply and cancel buttons are not displayed
     * unnecessarily.
     */
    protected void commitEdit() {
        // remove focus handler, this is needed because commit causes focus loss which would result in doubled commits
        // if the initial commit is not started because of the focus loss
        for (final Node internalElement : internalElements) {
            internalElement.focusedProperty().removeListener(focusChangeListener);
        }
        // compute the currently setup value
        V currentValue = getCurrentValue();
        // test if the value has changed
        if (valueHasChanged()) {
            // update the value type with the new value and commit the change
            treeTableCell.commitEdit(valueType.createNew(currentValue));
        } else {
            // cancel the change
            treeTableCell.cancelEdit();
        }
    }

    /**
     * Test if the setup value differs from the starting one. This is the default implementation
     *
     * @return
     */
    protected boolean valueHasChanged() {
        return !valueType.getValue().equals(getCurrentValue());
    }

    public GRAPHIC getControl() {
        return control;
    }

    public ValueType<V> getValueType() {
        return valueType;
    }

    /**
     * Compute the value the user set graphically.
     *
     * @return the current value the user setup
     */
    protected abstract V getCurrentValue();

    /**
     * Initialize internal components to display the current value.
     *
     * @param value the currently displayed value
     */
    protected abstract void init(V value);
}
