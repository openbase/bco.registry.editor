/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import rst.configuration.EntryType.Entry;

/**
 *
 * @author thuxohl
 */
public class EntryContainer extends NodeContainer<Entry.Builder>{

    public EntryContainer(Entry.Builder builder) {
        super("entry", builder);
        super.add(builder.getKey(), "key");
        super.add(builder.getValue(), "value");
    }

    public String getDescription() {
        return getBuilder().getKey() + " = " + getBuilder().getValue();
    }
}
