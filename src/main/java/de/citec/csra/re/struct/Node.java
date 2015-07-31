/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct;

import com.google.protobuf.Descriptors;

/**
 *
 * @author thuxohl
 */
public interface Node {

    /**
     * Get a description for this node displayed in the descriptor column.
     *
     * @return a description
     */
    String getDescriptor();

    /**
     * Used by the tree table column to fill its cells with values.
     *
     * @return itself
     */
    Node getContext();

    /**
     * Get the field descriptor for this value.
     *
     * @return the field descriptor
     */
    Descriptors.FieldDescriptor getFieldDescriptor();
}
