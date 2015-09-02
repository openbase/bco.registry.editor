/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.cellfactory;

import de.citec.agm.remote.AgentRegistryRemote;
import de.citec.apm.remote.AppRegistryRemote;
import de.citec.lm.remote.LocationRegistryRemote;
import de.citec.csra.re.struct.node.Node;
import de.citec.dm.remote.DeviceRegistryRemote;
import de.citec.jps.core.JPService;
import de.citec.jps.preset.JPReadOnly;
import de.citec.jul.exception.CouldNotPerformException;
import de.citec.scm.remote.SceneRegistryRemote;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author thuxohl
 */
public class DescriptionCell extends RowCell {

    private boolean readonly;
    private String type;

    public DescriptionCell(DeviceRegistryRemote deviceRegistryRemote, LocationRegistryRemote locationRegistryRemote, SceneRegistryRemote sceneRegistryRemote, AgentRegistryRemote agentRegistryRemote, AppRegistryRemote appRegistryRemote, String type) {
        super(deviceRegistryRemote, locationRegistryRemote, sceneRegistryRemote, agentRegistryRemote, appRegistryRemote);
        this.type = type;
    }

    @Override
    public void updateItem(Node item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            graphicProperty().setValue(null);
            textProperty().setValue("");
            setContextMenu(null);
        } else if (item instanceof Node) {
            graphicProperty().setValue(null);
            textProperty().setValue(convertDescriptorT(item.getDescriptor()));
        }

        readonly = JPService.getProperty(JPReadOnly.class).getValue();
//        try {
//            if ("agent".equals(type) && agentRegistryRemote.isAgentConfigRegistryReadOnly().get()) {
//                readonly = true;
//            } else if ("agent".equals(type) && agentRegistryRemote.isAgentConfigRegistryReadOnly().get()) {
//                readonly = true;
//            } else if ("device_class".equals(type) && deviceRegistryRemote.isDeviceClassRegistryReadOnly().get()) {
//                readonly = true;
//            } else if ("device_config".equals(type) && deviceRegistryRemote.isDeviceConfigRegistryReadOnly().get()) {
//                readonly = true;
//            } else if ("unit_template".equals(type) && deviceRegistryRemote.isUnitTemplateRegistryReadOnly().get()) {
//                readonly = true;
//            } else if ("location".equals(type) && locationRegistryRemote.isLocationConfigRegistryReadOnly().get()) {
//                readonly = true;
//            } else if ("scene".equals(type) && sceneRegistryRemote.isSceneConfigRegistryReadOnly().get()) {
//                readonly = true;
//            }
//        } catch (CouldNotPerformException | ExecutionException | InterruptedException ex) {
//            logger.warn("Could not determine read only property", ex);
//        }
        if (readonly) {
            setContextMenu(null);
        }
    }

    public static String convertDescriptorT(String descriptor) {
        if (descriptor.equals("")) {
            return descriptor;
        }

        descriptor.toLowerCase();
        String result = "";
        String[] split = descriptor.split("_");
        for (int i = 0; i < split.length; i++) {
            result += Character.toUpperCase(split[i].charAt(0));
            result += split[i].substring(1);
            result += " ";
        }
        return result;
    }
}
