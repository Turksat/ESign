/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.gov.turkiye.esign.support;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Used for exception messages
 * @author sercan
 */
public class ExceptionSupport {

    private static ResourceBundle bundle = null;

    public static String getValue(final String key) {
        if (bundle == null) {
            Locale locale = new Locale("tr", "TR");
            bundle = ResourceBundle.getBundle("exception/SmartCardExceptions", locale);
        }
        return bundle.getString(key);
    }
    
}
