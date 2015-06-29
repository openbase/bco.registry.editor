/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import de.citec.jul.exception.InstantiationException;
import rst.configuration.EntryType;
import rst.configuration.MetaConfigType;
import rst.configuration.MetaConfigType.MetaConfig;

/**
 *
 * @author divine threepwood
 */
public class MetaConfigContainer extends GenericListContainer<MetaConfigType.MetaConfig.Builder, EntryType.Entry, EntryType.Entry.Builder> {

    public MetaConfigContainer(MetaConfig.Builder builder) throws InstantiationException {
        super("meta_config", MetaConfigType.MetaConfig.ENTRY_FIELD_NUMBER, builder, EntryContainer.class);
    }

    private String getDescription() {
        return "Contains " + builder.getEntryCount() + " Configurations.";
    }
}
