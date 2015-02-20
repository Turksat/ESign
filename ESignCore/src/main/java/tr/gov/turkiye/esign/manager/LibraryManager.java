/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.gov.turkiye.esign.manager;

import tr.gov.turkiye.esign.statics.LibConfig;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import tr.gov.turkiye.esign.statics.OS;
import tr.gov.turkiye.esign.enums.OSArch;
import tr.gov.turkiye.esign.enums.OSNames;
import tr.gov.turkiye.esign.enums.OSVersion;

/**
 *
 * @author sercan
 */
public class LibraryManager {
    
    /**
     * Loads wrapper library for current operating system.
     * 
     * @return If library is loaded successfully or already loaded return true, otherwise false.
     */
    public static boolean loadPKCS11Wrapper() {
        try {
            System.loadLibrary("pkcs11wrapper");
        } catch (final Throwable t) {
            t.printStackTrace(System.err);
            if (t.getMessage().toLowerCase().contains("already loaded")) {
                return true;
            }
            return t.getLocalizedMessage().contains("already loaded");
        }
        return true;
    }

    /**
     * Gets directory which contains installation packages.
     * 
     * @return 
     */
    private static String getInstallDir() {
        OSNames systemOS = OS.getSystemOS();
        if (systemOS.equals(OSNames.WINDOWS)) {
            OSArch systemOSArch = OS.getSystemOSArch();
            if (systemOSArch.equals(OSArch._64BIT)) {
                return LibConfig.WINDOWS64_LIB_PATH;
            } else {
                return LibConfig.WINDOWS32_LIB_PATH;
            }
        } else if (systemOS.equals(OSNames.LINUX)) {
            OSArch systemOSArch = OS.getSystemOSArch();
            if (systemOSArch.equals(OSArch._64BIT)) {
                return LibConfig.LINUX64_LIB_PATH;
            } else {
                return LibConfig.LINUX32_LIB_PATH;
            }
        } else if (systemOS.equals(OSNames.MACOS)) {
            return LibConfig.MACOS_LIB_PATH;
        } else if (systemOS.equals(OSNames.SOLARIS)) {
            OSVersion systemOSVersion = OS.getSystemOSVersionForSolaris();
            if (systemOSVersion.equals(OSVersion.SPARC)) {
                return LibConfig.SOLARIS_LIB_PATH;
            } else {
                return LibConfig.SOLARIS_V9_LIB_PATH;
            }
        } else {
            OSArch systemOSArch = OS.getSystemOSArch();
            if (systemOSArch.equals(OSArch._64BIT)) {
                return LibConfig.OTHER64_LIB_PATH;
            } else {
                return LibConfig.OTHER32_LIB_PATH;
            }
        }
    }
    
    /**
     * Directory to where dynamic library will be installed during jvm lifetime.
     * 
     * @return 
     */
    private static String getExportDir() {
        OSNames systemOS = OS.getSystemOS();
        String home = System.getProperty("user.home")+System.getProperty("file.separator");
        if (systemOS.equals(OSNames.WINDOWS)) {
            OSArch systemOSArch = OS.getSystemOSArch();
            if (systemOSArch.equals(OSArch._64BIT)) {
                return home+LibConfig.WINDOWS64_LIB_NAME;
            } else {
                return home+LibConfig.WINDOWS32_LIB_NAME;
            }
        } else if (systemOS.equals(OSNames.LINUX)) {
            return home+LibConfig.LINUX_LIB_NAME;
        } else if (systemOS.equals(OSNames.MACOS)) {
            return home+LibConfig.MACOS_LIB_NAME;
        } else if (systemOS.equals(OSNames.SOLARIS)) {
            return home+LibConfig.SOLARIS_LIB_NAME;
        } else {
            return home+LibConfig.OTHER_LIB_NAME;
        }
    }

    /**
     * Gets libraries and install them into user.home directory of user computer temporarily. 
     * After that binds library to jvm until it terminates.
     * @return 
     * @throws java.lang.Exception 
     */
    public static boolean installAndLoadPKCS11Wrapper() throws Exception {
        InputStream fileInputStream;
        //load library as stream
        fileInputStream = LibraryManager.class.getClassLoader().getResourceAsStream(LibraryManager.getInstallDir());
        final File destinationFile;
        final BufferedOutputStream bufferedFileOutputStream;
        //temp file name and extension not important
//            final File destinationFile = File.createTempFile("pkcs11wrapper", "dll"));        
        try (BufferedInputStream bufferdFileInputStream = new BufferedInputStream(fileInputStream)) {
            //temp file name and extension not important
//            final File destinationFile = File.createTempFile("pkcs11wrapper", "dll"));
            destinationFile = new File(getExportDir());
            bufferedFileOutputStream = new BufferedOutputStream(new FileOutputStream(
                    destinationFile));
            final byte[] buffer = new byte[4096];
            int bytesRead;
            //load library into user temp directory
            while ((bytesRead = bufferdFileInputStream.read(buffer, 0, buffer.length)) != -1) {
                bufferedFileOutputStream.write(buffer, 0, bytesRead);
            }
        }
        bufferedFileOutputStream.flush();
        bufferedFileOutputStream.close();
        destinationFile.deleteOnExit();

        //Adds user.home to java.library.path
        addHome2Path();

//            System.load(destinationFile.getAbsolutePath());
        System.loadLibrary("pkcs11wrapper");
        return true;
    }

    /**
     * Adds user.home to java.library.path
     * 
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException 
     */
    private static void addHome2Path() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        add2LibPath(System.getProperty("user.home"));
    }
    
    /**
     * Adds given path to java.library.path. To do this after setting system property,
     * sys_paths set to null to force jvm to fill it again when a new call is made for java.library.path.
     * 
     * @param path
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException 
     */
    public static void add2LibPath(String path) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        System.setProperty("java.library.path", System.getProperty("java.library.path")+File.pathSeparator+path);
//        System.out.println(System.getProperty("java.library.path"));
        Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
        fieldSysPath.setAccessible(true);
        fieldSysPath.set(null, null);
        fieldSysPath.setAccessible(false);
    }    
}
