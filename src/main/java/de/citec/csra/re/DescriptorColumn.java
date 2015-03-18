/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re;

import de.citec.csra.dm.remote.DeviceRegistryRemote;
import de.citec.csra.re.cellfactory.DescriptionCell;

/**
 *
 * @author thuxohl
 */
public class DescriptorColumn extends Column {

    public DescriptorColumn(DeviceRegistryRemote remote) {
        super("Description", new DescriptionCell(remote));
        this.setPrefWidth(400);
    }
    
}
