/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.gov.turkiye.esignuidesk.support;

import java.awt.Component;

/**
 *
 * @author iakpolat
 */
public class GUIHelper {
    
    /**
     * Request focus to component with FocusOwner annotation in given class.
     * @param c
     */
    public static void requestFocus(Component c) {
        Component comp = AnnotationHelper.getFocusableComponent(c);
        //If a component with @FocusOwner exists.
        if(comp!=null) {
            comp.requestFocusInWindow();
        } else {
            System.out.println("no component found");
        }
    }
}
