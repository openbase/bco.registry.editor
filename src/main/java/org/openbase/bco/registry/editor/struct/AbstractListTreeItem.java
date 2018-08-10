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
import org.openbase.jul.processing.StringProcessor;
import org.openbase.jul.visual.javafx.geometry.svg.SVGGlyphIcon;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public abstract class AbstractListTreeItem<MB extends Message.Builder> extends AbstractBuilderTreeItem<MB> {

    private final boolean modifiable;

    private String description;

    public AbstractListTreeItem(final FieldDescriptor fieldDescriptor, final MB builder, final boolean modifiable) throws InitializationException {
        super(fieldDescriptor, builder, false);
        this.modifiable = modifiable;
        try {
            validateDescriptor();
        } catch (CouldNotPerformException ex) {
            throw new InitializationException(this, ex);
        }
        setDescription(fieldDescriptor);
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

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setDescription(final FieldDescriptor fieldDescriptor) {
        setDescription(StringProcessor.transformToCamelCase(fieldDescriptor.getName()) + "List");
    }

    @Override
    protected Node createDescriptionGraphic() {
        final Label label = new Label(description);
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
            return hBox;
        }
        return label;
    }

    protected void updateChildGraphic(final GenericTreeItem treeItem) throws CouldNotPerformException {
        // if is modifiable add symbol to remove to child
        if (isModifiable()) {
            // retrieve current description graphic
            final Region region = (Region) treeItem.getDescriptionGraphic();
            // create horizontal box
            final HBox hBox = new HBox();
            hBox.setSpacing(3);
            // create icon and add remove on mouse click
            final SVGGlyphIcon svgGlyphIcon = new SVGGlyphIcon(MaterialIcon.REMOVE, region.getHeight(), false);
            svgGlyphIcon.setForegroundIconColor(Color.RED);
            svgGlyphIcon.setOnMouseClicked(event -> {
                if (treeItem instanceof RegistryMessageTreeItem) {
                    ((RegistryMessageTreeItem) treeItem).handleRemoveEvent();
                    return;
                }

                ArrayList updatedList = new ArrayList((List) getBuilder().getField(getFieldDescriptor()));
                // it the internal value is a builder it has to be build first because the list
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
                this.setValue(getValueCasted().createNew(getBuilder()));
            });
            // add description graphic and icon to box
            hBox.getChildren().addAll(region, svgGlyphIcon);
            // set graphic for child
            treeItem.setDescriptionGraphic(hBox);
        }
    }

    protected abstract void addElement() throws CouldNotPerformException;
}
