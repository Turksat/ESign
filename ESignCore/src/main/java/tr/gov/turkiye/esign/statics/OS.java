/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.gov.turkiye.esign.statics;

import tr.gov.turkiye.esign.enums.OSVersion;
import tr.gov.turkiye.esign.enums.OSNames;
import tr.gov.turkiye.esign.enums.OSArch;
import java.util.Locale;

/**
 *
 * Includes supported operating systems.
 * 
 * @author sercan
 */
public class OS {
    final private static String WINDOWS = "windows";
    final private static String LINUX = "linux";
    final private static String MACOS = "mac";
    final private static String SOLARIS = "solaris";

    /**
     * Gets user operating system.
     * 
     * @return Return corresponding OS name. If user operation system is not supported returns OSNames.OTHER
     */
    public static OSNames getSystemOS() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
        if (osName.contains(WINDOWS)) {
            return OSNames.WINDOWS;
        } else if (osName.contains(LINUX)) {
            return OSNames.LINUX;
        } else if (osName.contains(MACOS)) {
            return OSNames.MACOS;
        } else if(osName.contains(SOLARIS)){
            return OSNames.SOLARIS;
        }else{
            return OSNames.OTHER;
        }
    }
    
    /**
     * Gets bits of operating system.
     * 
     * @return 
     */
    public static OSArch getSystemOSArch() {
        String osAcrh = System.getProperty("os.arch").toLowerCase(Locale.ENGLISH);
        if (osAcrh.contains("64")) {
            return OSArch._64BIT;
        } else {
            return OSArch._32BIT;
        }
    }
    
    /**
     * Gets system version from Solaris operation system.
     * 
     * @return 
     */
    public static OSVersion getSystemOSVersionForSolaris() {
        String osVersion = System.getProperty("os.version").toLowerCase(Locale.ENGLISH);
        if (osVersion.contains("V9")) {
            return OSVersion.SPARC_V9;
        } else{
            return OSVersion.SPARC;
        }
    }
    
    

}
