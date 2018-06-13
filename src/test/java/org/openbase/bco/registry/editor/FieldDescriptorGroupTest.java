package org.openbase.bco.registry.editor;

/*
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

import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeItem.TreeModificationEvent;
import org.junit.*;
import org.openbase.bco.registry.editor.visual.provider.FieldDescriptorGroup;
import org.openbase.jps.core.JPService;
import org.openbase.jps.exception.JPServiceException;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.extension.protobuf.processing.ProtoBufFieldProcessor;
import rst.domotic.service.ServiceConfigType.ServiceConfig;
import rst.domotic.service.ServiceTemplateType.ServiceTemplate.ServiceType;
import rst.domotic.unit.UnitConfigType.UnitConfig;
import rst.domotic.unit.UnitConfigType.UnitConfig.Builder;
import rst.domotic.unit.UnitTemplateType.UnitTemplate.UnitType;
import rst.spatial.PlacementConfigType.PlacementConfig;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:pleminoq@openbase.org">Tamino Huxohl</a>
 */
public class FieldDescriptorGroupTest {

    private final FieldDescriptorGroup group;

    private final String homeID = "home";
    private final String kitchenID = "kitchen";

    private final PlacementConfig home;
    private final PlacementConfig kitchen;

    private final UnitConfig.Builder device1;
    private final UnitConfig.Builder device2;
    private final UnitConfig.Builder device3;

    public FieldDescriptorGroupTest() throws InstantiationException {
        group = new FieldDescriptorGroup(UnitConfig.newBuilder(), UnitConfig.PLACEMENT_CONFIG_FIELD_NUMBER, PlacementConfig.LOCATION_ID_FIELD_NUMBER);

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
    @Test(timeout = 5000)
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
     * Test of hasEqualValue method, of class FieldGroup.
     *
     * @throws java.lang.Exception
     */
    @Test(timeout = 5000)
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

        String name = clazz.getName();
        for (String sp : name.split("\\$")) {
            System.out.println(sp);
        }

        TreeItem<String> root = new TreeItem<>("ROOT");
        TreeItem<String> child = new TreeItem<>("CHILD");
        root.getChildren().add(child);

        root.addEventHandler(root.valueChangedEvent(), (EventHandler<TreeModificationEvent<String>>) event -> {
            System.out.println(event.getNewValue());
            System.out.println(event.getSource());
        });

        root.addEventHandler(root.childrenModificationEvent(), event -> {
            System.out.println(event.getNewValue());
            System.out.println(event.getAddedChildren().size());
        });

        child.setValue("ADULT");

        root.getChildren().add(new TreeItem<>("test"));

        child.getChildren().add(new TreeItem<>("hahah"));

        UnitConfig.Builder unitConfig = UnitConfig.newBuilder().setId("asdf").setUnitType(UnitType.LOCATION);
        unitConfig.addServiceConfigBuilder().getServiceDescriptionBuilder().setServiceType(ServiceType.COLOR_STATE_SERVICE);
        unitConfig.addServiceConfigBuilder().getServiceDescriptionBuilder().setServiceType(ServiceType.POWER_STATE_SERVICE);

        System.out.println(unitConfig.build());

        FieldDescriptor fieldDescriptor = ProtoBufFieldProcessor.getFieldDescriptor(unitConfig, UnitConfig.SERVICE_CONFIG_FIELD_NUMBER);

        ServiceConfig.Builder serviceConfig = ServiceConfig.newBuilder();
        serviceConfig.getServiceDescriptionBuilder().setServiceType(ServiceType.TARGET_TEMPERATURE_STATE_SERVICE);

        Builder builder = unitConfig.addRepeatedField(fieldDescriptor, serviceConfig.build());
        System.out.println(builder.build());
    }
}
