package org.dc.bco.registry.editor.struct.converter;

/*
 * #%L
 * RegistryEditor
 * %%
 * Copyright (C) 2014 - 2016 DivineCooperation
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

import com.google.protobuf.GeneratedMessage;
import rst.geometry.RotationType.Rotation;
import rst.rsb.ScopeType.Scope;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class ConverterSelector {

    public static Converter getConverter(GeneratedMessage.Builder builder) {
        if (builder instanceof Scope.Builder) {
            return new ScopeConverter((Scope.Builder) builder);
        } else if (builder instanceof Rotation.Builder) {
            return new RotationConverter((Rotation.Builder) builder);
        } else {
            return new DefaultConverter(builder);
        }
    }
}
