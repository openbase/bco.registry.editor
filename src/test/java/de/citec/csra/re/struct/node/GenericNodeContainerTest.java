/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.FieldDescriptor.Type;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import rst.homeautomation.device.DeviceConfigType;

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
     * @throws java.lang.Exception
     */
//    @Test
    public void testGetDescriptor() throws Exception {
        DeviceConfigType.DeviceConfig.Builder builder = DeviceConfigType.DeviceConfig.newBuilder();
        System.out.println("Test");
        for(Descriptors.FieldDescriptor field : builder.getDescriptorForType().getFields()) {
            System.out.println("Field ["+field.getName()+"]");
            if(field.getType().equals(Type.MESSAGE)) {
                System.out.println("is generated message.");
            }
        }
        builder.setDescription("Description");
    }

    /**
     * Test of getContext method, of class GenericNodeContainer.
     */
//    @Test
    public void testGetContext() {
    }
    
}
