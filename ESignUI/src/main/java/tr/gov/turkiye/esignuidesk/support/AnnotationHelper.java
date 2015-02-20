/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.gov.turkiye.esignuidesk.support;

import java.awt.Component;
import java.lang.reflect.Method;
import tr.gov.turkiye.esignuidesk.annotation.FocusOwner;
import tr.gov.turkiye.esignuidesk.controller.LogManager;

/**
 *
 * @author iakpolat
 */
public class AnnotationHelper {
    
    /**
     * FocusOwner component must return component otherwise its accepted that given class has no focus owner.
     * @param comp
     * @return 
     */
    public static Component getFocusableComponent(Component comp) {
        Method[] methods = comp.getClass().getMethods();
        for(Method method: methods) {
            if(method.getAnnotation(FocusOwner.class)!=null) {
                try {
                    return (Component) method.invoke(comp);
                } catch (Throwable ex) {
                    LogManager.handleError(ex, false, null);
                    break;
                }
            }
        }
        return null;
    }
            
}
