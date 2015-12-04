/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.regedit.view.provider;

import com.google.protobuf.Message;
import de.citec.csra.regedit.util.RemotePool;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.jul.extension.rsb.scope.ScopeGenerator;
import rst.homeautomation.device.DeviceConfigType;
import rst.spatial.LocationConfigType.LocationConfig;
import rst.spatial.PlacementConfigType.PlacementConfig;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public class LocationItemDescriptorProvider extends AbstractTreeItemDescriptorProvider {

    FieldDescriptorGroup fieldGroup;

    public LocationItemDescriptorProvider() {
        fieldGroup = new FieldDescriptorGroup(DeviceConfigType.DeviceConfig.newBuilder(), DeviceConfigType.DeviceConfig.PLACEMENT_CONFIG_FIELD_NUMBER, PlacementConfig.LOCATION_ID_FIELD_NUMBER);
    }

    @Override
    public String getDescriptor(Message.Builder builder) throws CouldNotPerformException {
        LocationConfig location = RemotePool.getInstance().getLocationRemote().getLocationConfigById((String) fieldGroup.getValue(builder));
        return ScopeGenerator.generateStringRep(location.getScope());
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
