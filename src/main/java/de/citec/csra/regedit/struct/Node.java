/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.regedit.struct;

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
}
