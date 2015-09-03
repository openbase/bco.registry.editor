/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import de.citec.csra.regedit.struct.Node;
import de.citec.csra.regedit.struct.GenericNodeContainer;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.FieldDescriptor.Type;
import com.google.protobuf.GeneratedMessage;
import de.citec.csra.regedit.struct.GenericGroupContainer;
import de.citec.csra.regedit.struct.GenericListContainer;
import de.citec.csra.regedit.struct.Leaf;
import de.citec.csra.regedit.struct.consistency.Configuration;
import de.citec.csra.regedit.util.FieldDescriptorGroup;
import de.citec.csra.regedit.util.FieldDescriptorUtil;
import de.citec.jul.exception.InstantiationException;
import javafx.scene.control.TreeItem;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import rst.homeautomation.device.DeviceClassType;
import rst.homeautomation.device.DeviceConfigType.DeviceConfig;
import rst.homeautomation.device.DeviceRegistryType.DeviceRegistry;
import rst.homeautomation.unit.UnitTemplateConfigType;
import rst.rsb.ScopeType;
import rst.spatial.LocationConfigType;
import rst.spatial.LocationRegistryType;
import rst.spatial.PlacementConfigType.PlacementConfig;

/**
 *
 * @author thuxohl
 */
public class GenericNodeContainerTest {

    public GenericNodeContainerTest() {
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
     * Test of getDescriptor method, of class GenericNodeContainer.
     *
     * @throws java.lang.Exception
     */
//    @Test
    public void testGetDescriptor() throws Exception {
        DeviceConfig.Builder builder = DeviceConfig.getDefaultInstance().toBuilder();
        System.out.println("Test");
        for (Descriptors.FieldDescriptor field : builder.getDescriptorForType().getFields()) {
            System.out.println("Field [" + field.getName() + "]");
            if (field.getType().equals(Type.MESSAGE)) {
                System.out.println("is generated message.");
            }
        }
        builder.setDescription("Description");

        GenericNodeContainer<DeviceConfig.Builder> genericNodeContainer = new GenericNodeContainer<>(DeviceRegistry.getDescriptor().findFieldByNumber(DeviceRegistry.DEVICE_CONFIG_FIELD_NUMBER), builder);
        System.out.println("GenericNodeContainer descriptor [" + genericNodeContainer.getDescriptor() + "]");
        for (TreeItem<Node> item : genericNodeContainer.getChildren()) {
            if (item.getValue() == null) {
                System.out.println("Item is null");
            } else {
                System.out.println("Child [" + item.getValue().getDescriptor() + "]");
                if (item.getValue() instanceof Leaf) {
                    System.out.println("Is leaf with value [" + ((Leaf) item.getValue()).getValue() + "]");
                }
            }
        }

        ScopeType.Scope.Builder scope = ScopeType.Scope.newBuilder().addComponent("test").addComponent("scope");
        for (Descriptors.FieldDescriptor field : scope.getDescriptorForType().getFields()) {
            System.out.println("Field [" + field.getName() + "]");
            System.out.println("Type [" + field.getType() + "]");
        }
    }

    /**
     * Test of getContext method, of class GenericNodeContainer.
     */
    @Test
    public void test() throws InstantiationException {
        String homeId = "home";
        String bathId = "bath";
        String motionSensorId = "motionSensor123";
        String cameraId = "camera1-1456";
        PlacementConfig home = PlacementConfig.newBuilder().setLocationId("home").build();
        PlacementConfig bath = PlacementConfig.newBuilder().setLocationId("bath").build();
        DeviceConfig.Builder config1 = DeviceConfig.newBuilder().setId("device1").setDeviceClassId(motionSensorId).setPlacementConfig(home);
        DeviceConfig.Builder config2 = DeviceConfig.newBuilder().setId("device2").setDeviceClassId(cameraId).setPlacementConfig(home);
        DeviceConfig.Builder config3 = DeviceConfig.newBuilder().setId("device3").setDeviceClassId(motionSensorId).setPlacementConfig(bath);
        DeviceRegistry.Builder registry = DeviceRegistry.newBuilder().addDeviceConfig(config1).addDeviceConfig(config2).addDeviceConfig(config3);
        FieldDescriptorGroup deviceIdGroup = new FieldDescriptorGroup(DeviceConfig.newBuilder(), DeviceConfig.DEVICE_CLASS_ID_FIELD_NUMBER);
        FieldDescriptorGroup locationGroup = new FieldDescriptorGroup(DeviceConfig.newBuilder(), DeviceConfig.PLACEMENT_CONFIG_FIELD_NUMBER, PlacementConfig.LOCATION_ID_FIELD_NUMBER);
        Descriptors.FieldDescriptor field = FieldDescriptorUtil.getField(DeviceRegistry.DEVICE_CONFIG_FIELD_NUMBER, registry);
        GenericGroupContainer test = new GenericGroupContainer(field.getName(), field, registry, registry.getDeviceConfigBuilderList(), deviceIdGroup, locationGroup);

        for (Object child : test.getChildren()) {
            TreeItem<Node> childTree = (TreeItem<Node>) child;
            GenericGroupContainer childList = (GenericGroupContainer) childTree.getValue();
            System.out.println(childList.getDescriptor() + "'s");
            for (Object childChild : childList.getChildren()) {
                TreeItem<Node> childChildTree = (TreeItem<Node>) childChild;
                GenericListContainer childChildNode = (GenericListContainer) childChildTree.getValue();
                System.out.println("\t" + childChildNode.getDescriptor());
                for (Object childChildChild : childChildNode.getChildren()) {
                    TreeItem<Node> childChildChildTree = (TreeItem<Node>) childChildChild;
                    GenericNodeContainer childChildChildNode = (GenericNodeContainer) childChildChildTree.getValue();
                    DeviceConfig.Builder builder = (DeviceConfig.Builder) childChildChildNode.getBuilder();
                    System.out.println("\t\t" + builder.getId());
                }
            }
        }
    }

//    @Test
    public void testSimpleName() {
        DeviceClassType.DeviceClass test = DeviceClassType.DeviceClass.getDefaultInstance();
        assertEquals("DeviceClass", print(test));
        System.out.println(print(test));
    }

//    @Test
    public void testListContainer() throws Exception {
        LocationConfigType.LocationConfig.Builder location = LocationConfigType.LocationConfig.newBuilder();
        System.out.println(location.getClass().getTypeName());
        location.setId("home");
        location.setLabel("home");
        location.addChildId("kitchen");
        location.addChildId("bath");
        GenericNodeContainer<LocationConfigType.LocationConfig.Builder> genericNodeContainer = new GenericNodeContainer<>(LocationRegistryType.LocationRegistry.LOCATION_CONFIG_FIELD_NUMBER, location);
        assertEquals(true, genericNodeContainer.isSendable());
    }

//    @Test
//    public void testLoadRegistry() throws Exception {
//        RemotePool remotePool = RemotePool.getInstance();
//        remotePool.init();
//        remotePool.getDeviceRemote().activate();
//        DeviceRegistry deviceRegistry = remotePool.getDeviceRemote().requestStatus();
//        new GenericListContainer<>(DeviceRegistry.DEVICE_CLASS_FIELD_NUMBER, deviceRegistry.toBuilder());
//        FieldDescriptorGroup deviceClassId = new FieldDescriptorGroup(DeviceConfig.newBuilder(), DeviceConfig.DEVICE_CLASS_ID_FIELD_NUMBER);
//        FieldDescriptorGroup locationId = new FieldDescriptorGroup(DeviceConfig.newBuilder(), DeviceConfig.PLACEMENT_CONFIG_FIELD_NUMBER, PlacementConfig.LOCATION_ID_FIELD_NUMBER);
//        Descriptors.FieldDescriptor field = deviceRegistry.toBuilder().getDescriptorForType().findFieldByNumber(DeviceRegistry.DEVICE_CONFIG_FIELD_NUMBER);
//        new GenericGroupContainer<>(field.getName(), field, deviceRegistry.toBuilder(), deviceRegistry.toBuilder().getDeviceConfigBuilderList(), deviceClassId, locationId);
//
//        remotePool.getLocationRemote().activate();
//        LocationRegistryType.LocationRegistry locationRegistry = remotePool.getLocationRemote().requestStatus();
//        new GenericListContainer<>(LocationRegistryType.LocationRegistry.LOCATION_CONFIG_FIELD_NUMBER, locationRegistry.toBuilder());
//        remotePool.shutdown();
//    }
    private String print(GeneratedMessage msg) {
        return msg.getClass().getSimpleName();
    }

    @Test
    public void testest() throws Exception {
        assertTrue(!Configuration.isSendable(UnitTemplateConfigType.UnitTemplateConfig.getDefaultInstance().toBuilder()));
    }
}
