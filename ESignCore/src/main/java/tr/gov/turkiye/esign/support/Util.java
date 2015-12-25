/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.gov.turkiye.esign.support;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;

/**
 *
 * @author sercan
 */
    public class Util {

        /**
         * Gets bytes of file
         * @param fileName
         * @return bytes of file.
         * @throws FileNotFoundException
         * @throws NoSuchAlgorithmException
         * @throws IOException 
         */
    //    public static byte[] getFileContent(String fileName) throws FileNotFoundException, NoSuchAlgorithmException, IOException {
    //        InputStream dataInputStream = new FileInputStream(fileName);
    //
    //        // we buffer the content to have it after hashing for the PKCS#7 content
    //        ByteArrayOutputStream contentBuffer = new ByteArrayOutputStream();
    //        byte[] dataBuffer = new byte[1024];
    //        int bytesRead;
    //
    //        // feed all data from the input stream to the message digest
    //        while ((bytesRead = dataInputStream.read(dataBuffer)) >= 0) {
    //            // and buffer the data
    //            contentBuffer.write(dataBuffer, 0, bytesRead);
    //        }
    //        contentBuffer.close();
    //        return contentBuffer.toByteArray();
    //    }

        /**
         * writes bytes to file.
         * @param fileName
         * @param content
         * @throws FileNotFoundException
         * @throws IOException 
         */
    //    public static void writeFileContent(String fileName, byte[] content) throws FileNotFoundException, IOException {
    //        OutputStream signatureOutput = new FileOutputStream(fileName);
    //        signatureOutput.write(content);
    //        signatureOutput.flush();
    //        signatureOutput.close();
    //    }

        /**
         * Performs a final update on the digest using the specified array of bytes, 
         * then completes the digest computation.
         * @param input
         * @return
         * @throws NoSuchAlgorithmException 
         */
        public static byte[] digestSHA256(byte[] input) throws NoSuchAlgorithmException {
            // we do digesting outside the card, because some cards do not support on-card hashing
            MessageDigest digestEngine = MessageDigest.getInstance("SHA-256");

            // we buffer the content to have it after hashing for the PKCS#7 content
            return digestEngine.digest(input);
        }

        /**
         * Gets CN part from principal
         * @param principal
         * @return CN
         */
        public static String getCN(Principal principal) {
            String name = principal.getName();
            int startIndex = name.indexOf("CN=");
            int lastIndex = name.indexOf(",", startIndex);
            return name.substring(startIndex + 3, lastIndex);
        }

        /**
         * Gets serialNumber from principal.
         * @param principal
         * @return serialNumber
         */
        public static String getSerialNumber(Principal principal) {
            String name = principal.getName().toUpperCase();
            int startIndex = name.indexOf("SERIALNUMBER=");
            int lastIndex = name.indexOf(",", startIndex);
            return name.substring(startIndex + 13, lastIndex);
        }

        final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    }
