/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessage;
import java.util.List;
import javafx.scene.control.TreeItem;

/**
 *
 * @author thuxohl
 */
public class GenericGroupContainer <MB extends GeneratedMessage.Builder> extends TreeItem<Node> implements Node{

    private final Descriptors.FieldDescriptor field;

    public GenericGroupContainer(int fieldNumber, List<MB> builderList, Object group) {
        field = builderList.get(0).getDescriptorForType().findFieldByNumber(fieldNumber);
    }
    
    public GenericGroupContainer(int fieldNumber, List<MB> builderList, Object... group) {
        field = builderList.get(0).getDescriptorForType().findFieldByNumber(fieldNumber);
    }

    @Override
    public String getDescriptor() {
        return field.getName();
    }

    @Override
    public Node getContext() {
        return this;
    }
}
