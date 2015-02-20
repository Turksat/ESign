/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.gov.turkiye.esign.statics;

/**
 *
 * Includes library wrapper paths for different operating systems.
 * 
 * @author sercan
 */
public class LibConfig {
    public static final String WINDOWS32_LIB_NAME = "PKCS11Wrapper.dll";
    public static final String WINDOWS64_LIB_NAME = "PKCS11Wrapper.dll";
    public static final String LINUX_LIB_NAME = "libpkcs11wrapper.so";
    public static final String MACOS_LIB_NAME = "libpkcs11wrapper.jnilib";
    public static final String SOLARIS_LIB_NAME = "libpkcs11wrapper.so";
    public static final String OTHER_LIB_NAME = "libpkcs11wrapper.so";
    
    public static final String WINDOWS32_LIB_PATH = "libs/windows/32/"+WINDOWS32_LIB_NAME;
    public static final String WINDOWS64_LIB_PATH = "libs/windows/64/"+WINDOWS64_LIB_NAME;
    public static final String LINUX32_LIB_PATH = "libs/linux/32/"+LINUX_LIB_NAME;
    public static final String LINUX64_LIB_PATH = "libs/linux/64/"+LINUX_LIB_NAME;
    public static final String MACOS_LIB_PATH = "libs/macos/"+MACOS_LIB_NAME;
    public static final String SOLARIS_LIB_PATH = "libs/solaris/"+SOLARIS_LIB_NAME;
    public static final String SOLARIS_V9_LIB_PATH = "libs/solaris/v9/"+SOLARIS_LIB_NAME;
    //Other operating systems are assumed as linux.
    public static final String OTHER32_LIB_PATH = "libs/linux/32/"+LINUX_LIB_NAME;
    public static final String OTHER64_LIB_PATH = "libs/linux/64/"+LINUX_LIB_NAME;
    
    
}
