package org.openbase.bco.registry.editor.util.fieldpath;

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

import org.openbase.bco.registry.editor.util.FieldDescriptorPath;
import org.openbase.bco.registry.editor.util.FieldPathDescriptionProvider;
import org.openbase.bco.registry.remote.Registries;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.extension.type.processing.LabelProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openbase.type.domotic.unit.UnitConfigType.UnitConfig;
import org.openbase.type.domotic.unit.agent.AgentClassType.AgentClass;
import org.openbase.type.domotic.unit.agent.AgentConfigType.AgentConfig;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class AgentUnitAgentClassFieldPath extends FieldPathDescriptionProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgentUnitAgentClassFieldPath.class);

    public AgentUnitAgentClassFieldPath() {
        super(new FieldDescriptorPath(UnitConfig.getDefaultInstance(), UnitConfig.AGENT_CONFIG_FIELD_NUMBER, AgentConfig.AGENT_CLASS_ID_FIELD_NUMBER));
    }

    @Override
    public String generateDescription(final Object value) {
        final String agentClassId = (String) value;
        try {
            final AgentClass agentClass = Registries.getClassRegistry().getAgentClassById(agentClassId);
            return LabelProcessor.getBestMatch(agentClass.getLabel());
        } catch (CouldNotPerformException ex) {
            LOGGER.warn("Could not generate description from agent class id[" + value + "]", ex);
            return agentClassId;
        }
    }
}
