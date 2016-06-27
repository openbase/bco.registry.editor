package org.openbase.bco.registry.editor;

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

import org.openbase.bco.registry.editor.visual.provider.FieldDescriptorGroup;
import com.google.protobuf.Message;
import org.openbase.jul.exception.InstantiationException;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import rst.homeautomation.device.DeviceConfigType.DeviceConfig;
import rst.spatial.PlacementConfigType.PlacementConfig;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class FieldDescriptorGroupTest {

    private final FieldDescriptorGroup group;

    private final String homeID = "home";
    private final String kitchenID = "kitchen";

    private final PlacementConfig home;
    private final PlacementConfig kitchen;

    private final DeviceConfig.Builder device1;
    private final DeviceConfig.Builder device2;
    private final DeviceConfig.Builder device3;

    public FieldDescriptorGroupTest() throws InstantiationException {
        group = new FieldDescriptorGroup(DeviceConfig.newBuilder(), DeviceConfig.PLACEMENT_CONFIG_FIELD_NUMBER, PlacementConfig.LOCATION_ID_FIELD_NUMBER);

        home = PlacementConfig.newBuilder().setLocationId(homeID).build();
        kitchen = PlacementConfig.newBuilder().setLocationId(kitchenID).build();

        device1 = DeviceConfig.newBuilder().setId("device_1").setPlacementConfig(home);
        device2 = DeviceConfig.newBuilder().setId("device_2").setPlacementConfig(home);
        device3 = DeviceConfig.newBuilder().setId("device_3").setPlacementConfig(kitchen);
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getFieldValues method, of class FieldGroup.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetFieldValues() throws Exception {
        System.out.println("getFieldValues");
        
        List<Message.Builder> builderList = new ArrayList<>();
        builderList.add(device1);
        builderList.add(device2);
        builderList.add(device3);
        List<Object> expResult = new ArrayList<>();
        expResult.add(homeID);
        expResult.add(kitchenID);
        List<Object> result = group.getValueList(builderList);
        assertEquals(expResult, result);
    }

    /**
     * Test of getValue method, of class FieldGroup.
     *
     * @throws java.lang.Exception
     */
    @Test
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
     * Test of hasEqualValue method, of class FieldGroup.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testHasEqualValue() throws Exception {
        System.out.println("hasEqualValue");

        Object value = homeID;
        boolean expResult = false;
        boolean result = group.hasEqualValue(device3, value);
        assertEquals(expResult, result);

        expResult = true;
        result = group.hasEqualValue(device2, value);
        assertEquals(expResult, result);
    }

    /**
     * Test of setValue method, of class FieldGroup.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testsetValue() throws Exception {
        System.out.println("setValue");

        DeviceConfig.Builder builder = DeviceConfig.newBuilder();
        group.setValue(builder, homeID);
        assertEquals(homeID, builder.getPlacementConfig().getLocationId());
    }
}