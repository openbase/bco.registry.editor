package org.openbase.bco.registry.editor.visual;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2022 openbase.org
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

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class TabWithStatusLabel extends Tab {

    private final Label statusLabel;
    private final VBox vBox;

    private Region internalContent;

    public TabWithStatusLabel(final String text) {
        super(text);

        this.statusLabel = new Label();
        this.statusLabel.setAlignment(Pos.CENTER);

        this.vBox = new VBox();
        this.vBox.setAlignment(Pos.CENTER);

        this.setContent(vBox);
    }

    protected void setStatusText(final String text) {
        statusLabel.setText(text);

        vBox.getChildren().clear();
        vBox.getChildren().add(statusLabel);
        if (internalContent != null) {
            vBox.getChildren().add(internalContent);
        }
    }

    protected void clearStatusLabel() {
        statusLabel.setText("");
        vBox.getChildren().remove(statusLabel);
    }

    protected void setInternalContent(final Region content) {
        if (internalContent == content) {
            return;
        }

        internalContent = content;
        vBox.getChildren().clear();
        if (!statusLabel.getText().isEmpty()) {
            vBox.getChildren().add(statusLabel);
        }
        vBox.heightProperty().addListener((observable, oldValue, newValue) -> content.setPrefHeight(newValue.doubleValue()));
        content.setPrefHeight(vBox.getHeight());
        vBox.getChildren().add(content);
    }
}
