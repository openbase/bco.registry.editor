package org.openbase.bco.registry.editor.visual;

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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.openbase.jul.exception.printer.ExceptionPrinter;

/**
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class GlobalTextArea extends TextArea {

    private static GlobalTextArea globalTextArea;

    private SplitPane parent;

    public static GlobalTextArea getInstance() {
        if (globalTextArea == null) {
            globalTextArea = new GlobalTextArea();
        }
        return globalTextArea;
    }

    public GlobalTextArea() {
        super();
        this.parent = null;
        ContextMenu contextMenu = new ContextMenu();
        MenuItem clear = new MenuItem("Clear");
        clear.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                clearText();
            }
        });
        contextMenu.getItems().add(clear);

        this.setContextMenu(contextMenu);
        this.setEditable(false);
        this.setFont(Font.font("Monospaced"));
        this.setMinHeight(0);
    }

    public void printException(Throwable th) {
        this.clear();
        this.setText(ExceptionPrinter.getHistory(th));
        resize(getText());
    }

    public void clearText() {
        this.clear();
        if (parent != null) {
            parent.setDividerPositions(1);
        }
    }

    public void putText(String text) {
        this.setText(text);
        resize(text);
    }

    private void resize(String text) {
        Text helper = new Text();
        helper.setText(text);
        helper.setFont(this.getFont());
        helper.setWrappingWidth(Double.MAX_VALUE);
        this.setPrefHeight(helper.getLayoutBounds().getHeight());
        if (parent != null) {
            parent.setDividerPositions(1.0 - ((this.getPrefHeight() + 50) / parent.getPrefHeight()));
        }
    }

    public void addParent(SplitPane splitPane) {
        this.parent = splitPane;
    }
}
