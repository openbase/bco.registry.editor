package org.openbase.bco.registry.editor.visual.column;

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
import java.util.HashMap;

/**
 *
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class FieldPriorityMap extends HashMap<String, Integer> {

    public FieldPriorityMap() {
        this.put("id", -1000);

        this.put("agent_config", -900);
        this.put("app_config", -900);
        this.put("authorization_group_config", -900);
        this.put("connection_config", -900);
        this.put("device_config", -900);
        this.put("location_config", -900);
        this.put("scene_config", -900);
        this.put("unit_group_config", -900);
        this.put("user_config", -900);

        this.put("label", -500);
        this.put("serial_number", -700);
        this.put("product_number", -700);
        this.put("device_class_id", -400);
    }
}
