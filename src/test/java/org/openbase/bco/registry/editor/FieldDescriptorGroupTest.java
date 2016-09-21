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
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openbase.bco.registry.editor.visual.provider.FieldDescriptorGroup;
import org.openbase.jul.exception.InstantiationException;
import org.openbase.jul.extension.rsb.com.RSBFactoryImpl;
import org.openbase.jul.extension.rsb.iface.RSBInformer;
import org.openbase.jul.extension.rsb.iface.RSBListener;
import rsb.Event;
import rsb.Handler;
import rsb.Scope;
import rsb.converter.DefaultConverterRepository;
import rsb.converter.ProtocolBufferConverter;
import rst.geometry.AxisAlignedBoundingBox3DFloatType.AxisAlignedBoundingBox3DFloat;
import rst.homeautomation.device.DeviceConfigType.DeviceConfig;
import rst.math.Vec3DDoubleType;
import rst.spatial.PlacementConfigType.PlacementConfig;
import rst.spatial.ShapeType.Shape;

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

        DeviceConfig.Builder builder = DeviceConfig.newBuilder();
        group.setValue(builder, homeID);
        assertEquals(homeID, builder.getPlacementConfig().getLocationId());
    }

    boolean dataReceived = false;

    @Test
    public void testRequired() throws Exception {
        DefaultConverterRepository.getDefaultConverterRepository().addConverter(new ProtocolBufferConverter<>(Shape.getDefaultInstance()));

        Shape.Builder shapeBuilder = Shape.newBuilder();
        shapeBuilder.setBoundingBox(AxisAlignedBoundingBox3DFloat.getDefaultInstance());
        shapeBuilder.addFloor(Vec3DDoubleType.Vec3DDouble.newBuilder().setX(1).setY(2).setZ(3));
//        shapeBuilder.clearField(FieldDescriptorUtil.getFieldDescriptor("bounding_box", shapeBuilder));
        shapeBuilder.getBoundingBoxBuilder().clearLeftFrontBottom();
        String missingFields = "";
        for (String error : shapeBuilder.findInitializationErrors()) {
            missingFields += "[" + error + "]";
        }
        for (Descriptors.FieldDescriptor a : shapeBuilder.getDescriptorForType().getFields()) {
            System.out.println("FieldName: " + a.getName());
            System.out.println("is required" + a.isRequired());
        }
        System.out.println("Errors:\n" + missingFields);
        Shape shape = shapeBuilder.build();
        System.out.println("Shape:\n" + shape);

        Scope scope = new Scope("/test/required/fields");

        RSBInformer<Shape> informer = RSBFactoryImpl.getInstance().createSynchronizedInformer(scope, Shape.class);
        informer.activate();

        RSBListener listener = RSBFactoryImpl.getInstance().createSynchronizedListener(scope);
        listener.activate();

        listener.addHandler(new Handler() {

            @Override
            public void internalNotify(Event event) {
                dataReceived = true;
                assertEquals(shape, event.getData());
                System.out.println(event.getData().toString());
            }
        }, true);

        informer.publish(shape);

        assertTrue(dataReceived);
    }
}
