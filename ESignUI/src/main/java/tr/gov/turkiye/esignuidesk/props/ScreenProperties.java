/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.gov.turkiye.esignuidesk.props;

import java.util.ResourceBundle;
import tr.gov.turkiye.esignuidesk.config.Config;

/**
 *
 * @author iakpolat
 */
public class ScreenProperties {
    private static ResourceBundle bundle;
    
    public static String getValue(final String key) {
        if (bundle == null) {
            bundle = ResourceBundle.getBundle("screen_values", Config.locale);
        }
        return bundle.getString(key);
    }
    
}
