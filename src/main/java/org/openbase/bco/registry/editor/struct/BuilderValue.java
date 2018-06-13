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

import com.google.protobuf.Message;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class BuilderValue<MB extends Message.Builder> {

    private final String description;
    private final String valueDescription;
    private final MB builder;

    public BuilderValue(final String description, final String valueDescription, final MB builder) {
        this.description = description;
        this.valueDescription = valueDescription;
        this.builder = builder;
    }

    /**
     * Description as displayed in the description column.
     *
     * @return
     */
    public String getDescription() {
        return getDescription();
    }

    /**
     * Description for the value as displayed in the value column.
     */
    public String getValueDescription() {
        return getValueDescription();
    }

    public MB getBuilder() {
        return getBuilder();
    }
}
