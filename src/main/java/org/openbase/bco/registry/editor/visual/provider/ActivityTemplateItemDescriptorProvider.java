package org.openbase.bco.registry.editor.visual.provider;

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

import com.google.protobuf.Message;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.extension.rst.processing.LabelProcessor;
import rst.domotic.activity.ActivityConfigType.ActivityConfig;
import rst.domotic.activity.ActivityTemplateType.ActivityTemplate;

/**
 * @author <a href="mailto:pLeminoq@openbase.org">Tamino Huxohl</a>
 */
public class ActivityTemplateItemDescriptorProvider extends AbstractTreeItemDescriptorProvider {

    FieldDescriptorGroup fieldGroup;

    public ActivityTemplateItemDescriptorProvider() {
        fieldGroup = new FieldDescriptorGroup(ActivityConfig.newBuilder(), ActivityConfig.ACTIVITY_TEMPLATE_ID_FIELD_NUMBER);
    }

    @Override
    public String getDescriptor(Message.Builder builder) throws CouldNotPerformException {
        ActivityTemplate activityTemplate = Registries.getTemplateRegistry().getActivityTemplateById((String) fieldGroup.getValue(builder));
        return LabelProcessor.getBestMatch(activityTemplate.getLabel());
    }

    @Override
    public Object getValue(Message.Builder msg) throws CouldNotPerformException {
        return fieldGroup.getValue(msg);
    }

    @Override
    public void setValue(Message.Builder msg, Object value) throws CouldNotPerformException, InterruptedException {
        fieldGroup.setValue(msg, value);
    }

    @Override
    public boolean hasEqualValue(Message.Builder msg, Object value) throws CouldNotPerformException {
        return value.equals(getValue(msg));
    }

}
