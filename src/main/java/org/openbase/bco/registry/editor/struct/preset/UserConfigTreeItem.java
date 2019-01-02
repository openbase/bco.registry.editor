package org.openbase.bco.registry.editor.struct.preset;

/*-
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2019 openbase.org
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
import org.openbase.bco.registry.editor.struct.BuilderTreeItem;
import org.openbase.bco.registry.editor.struct.GenericTreeItem;
import org.openbase.bco.registry.editor.struct.LeafTreeItem;
import org.openbase.bco.registry.editor.struct.editing.EditingGraphicFactory;
import org.openbase.bco.registry.editor.struct.editing.LanguageEditingGraphic;
import org.openbase.bco.registry.editor.struct.editing.util.LanguageComboBox;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.InitializationException;
import org.openbase.type.domotic.unit.user.UserConfigType.UserConfig;
import org.openbase.type.domotic.unit.user.UserConfigType.UserConfig.Builder;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class UserConfigTreeItem extends BuilderTreeItem<Builder> {

    public UserConfigTreeItem(final FieldDescriptor fieldDescriptor, final Builder builder, final Boolean editable) throws InitializationException {
        super(fieldDescriptor, builder, editable);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected GenericTreeItem createChild(final FieldDescriptor field, final Boolean editable) throws CouldNotPerformException {
        switch (field.getNumber()) {
            case UserConfig.LANGUAGE_FIELD_NUMBER:
                final LeafTreeItem<String> languageTreeItem = new LeafTreeItem<>(field, getBuilder().getLanguage(), editable);
                languageTreeItem.setDescriptionGenerator(LanguageComboBox::getDisplayedText);
                languageTreeItem.setEditingGraphicFactory(EditingGraphicFactory.getInstance(LanguageEditingGraphic.class));
                return languageTreeItem;
            default:
                return super.createChild(field, editable);
        }
    }
}
