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
public interface Leaf extends Node {

    public Object getValue();

    public void setValue(Object value);
}
