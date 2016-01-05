/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dc.bco.registry.editor.visual.cell.editing;

import com.google.protobuf.Message;

/**
 *
 * @author <a href="mailto:thuxohl@techfak.uni-bielefeld.com">Tamino Huxohl</a>
 */
public interface MessageComboBoxConverterInterface {

    public String getText(Message msg);

    public String getValue(Message msg);
}
