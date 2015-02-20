/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.gov.turkiye.esign.statics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Includes supported module names.
 * 
 * @author sercan
 */
public class Modules {
    
    private static final HashMap<String, String> libraryMap;
    
    final public static String module_AKIS = "Tubitak AKIS";
    final public static String module_GEMSAFE = "GemSafe";
    final public static String module_STARCOS = "StarCOS";
    final public static String module_SIEMENSCARDOS = "Siemens CardOS";
    final public static String module_NETID = "Net iD";
    final public static String module_NCIPHER = "NCipher";
    final public static String module_KOBIL = "Kobil";
    final public static String module_ALADDIN = "Aladdin";
    
    static {
        libraryMap = new HashMap<>();
        libraryMap.put(module_AKIS, System.mapLibraryName("akisp11"));
        libraryMap.put(module_GEMSAFE, System.mapLibraryName("gclib"));
        libraryMap.put(module_STARCOS, System.mapLibraryName("aetpkss1"));
        libraryMap.put(module_SIEMENSCARDOS, System.mapLibraryName("siecap11"));
        libraryMap.put(module_NETID, System.mapLibraryName("iidp11"));
        libraryMap.put(module_NCIPHER, System.mapLibraryName("cknfast"));
        libraryMap.put(module_KOBIL, System.mapLibraryName("kpkcs11hash"));
        libraryMap.put(module_ALADDIN, System.mapLibraryName("etpkcs11"));
    }
    
    /**
     * Retrieves list of module identifiers(keys).
     * 
     * @return if no module exists returns empty list, otherwise returns list of module identifiers.
     */
    public static ArrayList<String> getModuleIdentifiers(){
        ArrayList<String> moduleNameList = new ArrayList<>();
        final Iterator<String> it = libraryMap.keySet().iterator();
        while (it.hasNext()) {
            final String name = it.next();
            moduleNameList.add(name);
        }
        return moduleNameList;
    }

    /**
     * Gets library name of module from identifier.
     * 
     * @param selectedItem
     * @return null if no matching module is found otherwise return library name of module.
     */
    public static String getModuleLibraryName(String selectedItem) {
        return libraryMap.get(selectedItem);
    }
    
}
