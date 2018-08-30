package org.openbase.bco.registry.editor.struct;

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

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import de.jensd.fx.glyphs.materialicons.MaterialIcon;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.jul.pattern.Observable;
import org.openbase.jul.pattern.Observer;
import org.openbase.jul.processing.StringProcessor;
import org.openbase.jul.visual.javafx.geometry.svg.SVGGlyphIcon;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public abstract class AbstractListTreeItem<MB extends Message.Builder> extends AbstractBuilderTreeItem<MB> {

    private final boolean modifiable;

    public AbstractListTreeItem(final FieldDescriptor fieldDescriptor, final MB builder, final boolean modifiable) throws InitializationException {
        this(fieldDescriptor, builder, modifiable, StringProcessor.transformToCamelCase(fieldDescriptor.getName()) + "List");
    }

    public AbstractListTreeItem(final FieldDescriptor fieldDescriptor, final MB builder, final boolean modifiable, final String description) throws InitializationException {
        super(fieldDescriptor, builder, false);
        this.modifiable = modifiable;
        try {
            validateDescriptor();
        } catch (CouldNotPerformException ex) {
            throw new InitializationException(this, ex);
        }
        setDescriptionText(description);

        addDescriptionGraphicObserver(new Observer<Node>() {
            @Override
            public void update(Observable<Node> source, Node data) throws Exception {
                final Label label = (Label) data;
                if (isModifiable()) {
                    final HBox hBox = new HBox();
                    hBox.setSpacing(3);
                    final SVGGlyphIcon svgGlyphIcon = new SVGGlyphIcon(MaterialIcon.ADD, label.getHeight(), false);
                    svgGlyphIcon.setForegroundIconColor(Color.GREEN);
                    svgGlyphIcon.setOnMouseClicked(event -> {
                        try {
                            addElement();
                        } catch (CouldNotPerformException e) {
                            logger.warn("Could not add new element", e);
                        }
                    });
                    label.heightProperty().addListener((observable, oldValue, newValue) -> {
                        svgGlyphIcon.setPrefHeight(newValue.doubleValue());
                    });
                    hBox.getChildren().addAll(label, svgGlyphIcon);
                    setDescriptionGraphic(hBox);
                }
            }
        });
    }

    protected void validateDescriptor() throws CouldNotPerformException {
        if (!getFieldDescriptor().isRepeated()) {
            throw new CouldNotPerformException("FieldDescriptor[" + getFieldDescriptor().getName() + "] of Message["
                    + extractSimpleMessageClass(getBuilder()) + "] is not a repeated message");
        }
    }

    public boolean isModifiable() {
        return modifiable;
    }

    protected void updateChildGraphic(final GenericTreeItem treeItem) {
        treeItem.addDescriptionGraphicObserver(new Observer<Node>() {
            @Override
            public void update(Observable<Node> source, Node data) throws Exception {
                if (!isModifiable()) {
                    return;
                }

                // create horizontal box
                final HBox hBox = new HBox();
                hBox.setSpacing(3);
                // create icon and add remove on mouse click
                final SVGGlyphIcon svgGlyphIcon = new SVGGlyphIcon(MaterialIcon.REMOVE, ((Region) data).getHeight(), false);
                svgGlyphIcon.setForegroundIconColor(Color.RED);
                svgGlyphIcon.setOnMouseClicked(event -> {
                    if (treeItem instanceof RegistryMessageTreeItem) {
                        ((RegistryMessageTreeItem) treeItem).handleRemoveEvent();
                        return;
                    }

                    ArrayList updatedList = new ArrayList((List) getBuilder().getField(getFieldDescriptor()));
                    // if the internal value is a builder it has to be build first because the list
                    // consists of build messages
                    Object toRemove;
                    if (treeItem.getInternalValue() instanceof Message.Builder) {
                        toRemove = ((Message.Builder) treeItem.getInternalValue()).build();
                    } else {
                        toRemove = treeItem.getInternalValue();
                    }
                    updatedList.remove(toRemove);
                    getBuilder().clearField(getFieldDescriptor());
                    getBuilder().setField(getFieldDescriptor(), updatedList);
                    getChildren().remove(treeItem);
                    try {
                        AbstractListTreeItem.this.update(getBuilder());
                    } catch (CouldNotPerformException ex) {
                        logger.error("Could not update list tree item", ex);
                    }
                });
                // add description graphic and icon to box
                hBox.getChildren().addAll(data, svgGlyphIcon);
                // set graphic for child
                treeItem.setDescriptionGraphic(hBox);
            }
        });
    }

    protected abstract void addElement() throws CouldNotPerformException;
}
