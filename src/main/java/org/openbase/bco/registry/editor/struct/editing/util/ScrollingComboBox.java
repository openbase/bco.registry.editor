package org.openbase.bco.registry.editor.struct.editing.util;

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

import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyEvent;
import org.openbase.bco.registry.editor.util.DescriptionGenerator;

/**
 * Enhancement of a combo box which will scroll through the presented list view
 * on key presses. Additionally it will generate cells for display from a description generator.
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class ScrollingComboBox<V> extends ComboBox<V> {

    /**
     * Value in milli seconds of how long a delay between two key released events is waited
     * before a new search string will be initialized.
     */
    private static final long DEFAULT_ALLOWED_DELAY_BETWEEN_KEY_EVENTS = 500;

    private final DescriptionGenerator<V> descriptionGenerator;

    public ScrollingComboBox(final DescriptionGenerator<V> descriptionGenerator) {
        super();

        this.descriptionGenerator = descriptionGenerator;
        this.setCellFactory(param -> new Cell());
        this.setButtonCell(new Cell());
        this.setOnKeyReleased(new KeyEventHandler(DEFAULT_ALLOWED_DELAY_BETWEEN_KEY_EVENTS));
        this.setOnShowing(event -> {
            // scroll to currently selected item in popup list when it is shown
            if (getSelectionModel().getSelectedItem() != null) {
                // Without the platform run later this causes weird effects, the list view splits from the button of the combo box
                Platform.runLater(() -> scrollToItem(getSelectionModel().getSelectedItem()));
            }
        });
    }

    public DescriptionGenerator<V> getDescriptionGenerator() {
        return descriptionGenerator;
    }

    @SuppressWarnings("unchecked")
    private void scrollToItem(final V item) {
        // no scrolling needed if all items are displayed anyway
        if (getItems().size() <= getVisibleRowCount()) {
            return;
        }

        ComboBoxListViewSkin<V> skin = (ComboBoxListViewSkin<V>) ScrollingComboBox.this.getSkin();
        ((ListView<V>) skin.getPopupContent()).scrollTo(item);
    }

    private class Cell extends ListCell<V> {

        @Override
        protected void updateItem(V item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                setText(descriptionGenerator.getDescription(item));
            }
        }
    }

    private class KeyEventHandler implements EventHandler<KeyEvent> {

        private final long delayBetweenEvents;

        private long timeOfLastRelease;
        private String typedText;

        KeyEventHandler(final long delayBetweenEvents) {
            this.delayBetweenEvents = delayBetweenEvents;
            timeOfLastRelease = 0;
            typedText = "";
        }


        @Override
        public void handle(KeyEvent event) {
            if (!ScrollingComboBox.this.isShowing()) {
                // do nothing if the popup list is not showing
                return;
            }

            if (!event.getCode().isLetterKey()) {
                // if not letter is pressed reset last release time and typed text
                timeOfLastRelease = 0;
                typedText = "";
                return;
            }

            if (System.currentTimeMillis() - timeOfLastRelease <= delayBetweenEvents) {
                // if typed within delay extend typed text
                typedText += event.getText().toLowerCase();
            } else {
                // typed outside delay so begin anew
                typedText = event.getText().toLowerCase();
            }
            // save time of this event
            timeOfLastRelease = System.currentTimeMillis();

            for (final V item : ScrollingComboBox.this.getItems()) {
                // iterate over all items in combo box
                if (descriptionGenerator.getDescription(item).toLowerCase().startsWith(typedText)) {
                    // displayed description for item starts with the typed text so scroll to it
                    scrollToItem(item);
                    break;
                }
            }
        }
    }
}
