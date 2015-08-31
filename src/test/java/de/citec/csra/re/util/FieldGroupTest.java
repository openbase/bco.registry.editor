/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.util;

import com.google.protobuf.Message;
import de.citec.jul.exception.InstantiationException;
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
public class FieldGroupTest {

    private final FieldDescriptorGroup group;

    private final String homeID = "home";
    private final String kitchenID = "kitchen";

    private final PlacementConfig home;
    private final PlacementConfig kitchen;

    private final DeviceConfig.Builder device1;
    private final DeviceConfig.Builder device2;
    private final DeviceConfig.Builder device3;

    public FieldGroupTest() throws InstantiationException {
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
        List<Object> result = group.getFieldValues(builderList);
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

}
