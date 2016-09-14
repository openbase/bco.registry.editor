/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openbase.bco.registry.editor.visual.cell.editing;

/*-
 * #%L
 * BCO Registry Editor
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

import com.google.protobuf.Message;
import org.openbase.bco.registry.editor.util.FieldDescriptorUtil;
import rst.homeautomation.control.agent.AgentClassType.AgentClass;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class AgentClassComboBoxConverter implements MessageComboBoxConverter {

    @Override
    public String getText(Message msg) {
        return (String) msg.getField(FieldDescriptorUtil.getFieldDescriptor(AgentClass.LABEL_FIELD_NUMBER, AgentClass.getDefaultInstance()));
    }

    @Override
    public String getValue(Message msg) {
        return (String) msg.getField(FieldDescriptorUtil.getFieldDescriptor(AgentClass.ID_FIELD_NUMBER, AgentClass.getDefaultInstance()));
    }
}
