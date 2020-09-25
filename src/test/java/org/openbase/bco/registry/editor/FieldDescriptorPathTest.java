package org.openbase.bco.registry.editor;

/*
 * #%L
 * BCO Registry Editor
 * %%
 * Copyright (C) 2014 - 2020 openbase.org
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

import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeItem.TreeModificationEvent;
import org.junit.*;
import org.openbase.bco.registry.editor.util.FieldDescriptorPath;
import org.openbase.jps.core.JPService;
import org.openbase.jps.exception.JPServiceException;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.type.domotic.unit.UnitConfigType.UnitConfig;
import org.openbase.type.spatial.PlacementConfigType.PlacementConfig;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class FieldDescriptorPathTest {

    private final FieldDescriptorPath group;

    private final String homeID = "home";
    private final String kitchenID = "kitchen";

    private final PlacementConfig home;
    private final PlacementConfig kitchen;

    private final UnitConfig.Builder device1;
    private final UnitConfig.Builder device2;
    private final UnitConfig.Builder device3;

    public FieldDescriptorPathTest() throws InstantiationException {
        group = new FieldDescriptorPath(UnitConfig.newBuilder(), UnitConfig.PLACEMENT_CONFIG_FIELD_NUMBER, PlacementConfig.LOCATION_ID_FIELD_NUMBER);

        home = PlacementConfig.newBuilder().setLocationId(homeID).build();
        kitchen = PlacementConfig.newBuilder().setLocationId(kitchenID).build();

        device1 = UnitConfig.newBuilder().setId("device_1").setPlacementConfig(home);
        device2 = UnitConfig.newBuilder().setId("device_2").setPlacementConfig(home);
        device3 = UnitConfig.newBuilder().setId("device_3").setPlacementConfig(kitchen);
    }

    @BeforeClass
    public static void setUpClass() throws JPServiceException {
        JPService.setupJUnitTestMode();
    }

    /**
     * Test of getValue method, of class FieldGroup.
     *
     * @throws java.lang.Exception
     */
    @Test(timeout = 5000)
    public void testGetValue() throws Exception {
        System.out.println("getValue");

        Object expResult = homeID;
        Object result = group.getValue(device1);
        assertEquals(expResult, result);

        expResult = kitchenID;
        result = group.getValue(device3);
        assertEquals(expResult, result);
    }

    /**
     * Test of setValue method, of class FieldGroup.
     *
     * @throws java.lang.Exception
     */
    @Test(timeout = 5000)
    public void testsetValue() throws Exception {
        System.out.println("setValue");

        UnitConfig.Builder builder = UnitConfig.newBuilder();
        group.setValue(builder, homeID);
        assertEquals(homeID, builder.getPlacementConfig().getLocationId());
    }

    @Test
    public void testTest() throws Exception {
        Class clazz = UnitConfig.Builder.class;

        System.out.println(getClass().getName());
        System.out.println(getClass().getPackage().getName());

        String name = clazz.getName();
        for (String sp : name.split("\\$")) {
            System.out.println(sp);
        }

        TreeItem<String> root = new TreeItem<>("ROOT");
        TreeItem<String> child = new TreeItem<>("CHILD");
        root.getChildren().add(child);

        root.addEventHandler(TreeItem.valueChangedEvent(), (EventHandler<TreeModificationEvent<String>>) event -> {
            System.out.println(event.getNewValue());
            System.out.println(event.getSource());
        });

        root.addEventHandler(TreeItem.childrenModificationEvent(), event -> {
            System.out.println(event.getNewValue());
            System.out.println(event.getAddedChildren().size());
        });

        child.setValue("ADULT");

        root.getChildren().add(new TreeItem<>("test"));

        child.getChildren().add(new TreeItem<>("hahah"));
    }
}
