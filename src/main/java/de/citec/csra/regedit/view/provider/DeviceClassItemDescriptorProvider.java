/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.regedit.view.provider;

import com.google.protobuf.Message;
import de.citec.csra.regedit.util.RemotePool;
import org.dc.jul.exception.CouldNotPerformException;
import rst.homeautomation.device.DeviceClassType.DeviceClass;
import rst.homeautomation.device.DeviceConfigType.DeviceConfig;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class DeviceClassItemDescriptorProvider extends AbstractTreeItemDescriptorProvider {

    FieldDescriptorGroup fieldGroup;

    public DeviceClassItemDescriptorProvider() {
        fieldGroup = new FieldDescriptorGroup(DeviceConfig.newBuilder(), DeviceConfig.DEVICE_CLASS_ID_FIELD_NUMBER);
    }

    @Override
    public String getDescriptor(Message.Builder builder) throws CouldNotPerformException {
        DeviceClass deviceClass = RemotePool.getInstance().getDeviceRemote().getDeviceClassById((String) fieldGroup.getValue(builder));
        return deviceClass.getLabel();
    }

    @Override
    public Object getValue(Message.Builder msg) throws CouldNotPerformException {
        return fieldGroup.getValue(msg);
    }

    @Override
    public void setValue(Message.Builder msg, Object value) throws CouldNotPerformException {
        fieldGroup.setValue(msg, value);
    }

    @Override
    public boolean hasEqualValue(Message.Builder msg, Object value) throws CouldNotPerformException {
        return value.equals(getValue(msg));
    }
}
