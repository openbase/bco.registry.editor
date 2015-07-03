/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.citec.csra.re.struct.node;

/**
 *
 * @author thuxohl
 */
public interface Node {

    public String getDescriptor();

    /**
     * Used by the tree table column to fill its cells with values.
     *
     * @return itself
     */
    public Node getContext();
}
