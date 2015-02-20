/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.gov.turkiye.esign.statics;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * Loads smart card exception messages. Currently in Turkish.
 * 
 * @author sercan
 */
public class ExceptionConfig {
    private static ResourceBundle bundle = null;
    public static String getBundleString(final String key) {
        if (bundle == null) {
            Locale locale = new Locale("tr", "TR");
            bundle = ResourceBundle.getBundle("SmartCardExceptions", locale);
        }
        return bundle.getString(key);
    }
}
