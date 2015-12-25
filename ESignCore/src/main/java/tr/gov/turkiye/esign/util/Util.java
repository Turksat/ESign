/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.gov.turkiye.esign.util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import tr.gov.turkiye.esign.exception.SmartCardException;
import tr.gov.turkiye.esign.support.ExceptionSupport;

/**
 *
 * @author sercan
 */
public class Util {

    /**
     * Gets bytes of file
     *
     * @param fileName
     * @return bytes of file.
     * @throws FileNotFoundException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public static byte[] getContent(String fileName) throws FileNotFoundException, NoSuchAlgorithmException, IOException {
        InputStream dataInputStream = new FileInputStream(fileName);

        // we buffer the content to have it after hashing for the PKCS#7 content
        ByteArrayOutputStream contentBuffer = new ByteArrayOutputStream();
        byte[] dataBuffer = new byte[1024];
        int bytesRead;

        // feed all data from the input stream to the message digest
        while ((bytesRead = dataInputStream.read(dataBuffer)) >= 0) {
            // and buffer the data
            contentBuffer.write(dataBuffer, 0, bytesRead);
        }
        contentBuffer.close();
        return contentBuffer.toByteArray();

    }

    /**
     * writes bytes to file.
     *
     * @param fileName
     * @param content
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void writeContent(String fileName, byte[] content) throws FileNotFoundException, IOException {
        OutputStream signatureOutput = new FileOutputStream(fileName);
        signatureOutput.write(content);
        signatureOutput.flush();
        signatureOutput.close();
    }

    /**
     * Performs a final update on the digest using the specified array of bytes,
     * then completes the digest computation.
     *
     * @param input the input to be updated before the digest is completed.
     * @return the array of bytes for the resulting hash value
     * @throws tr.gov.turkiye.esign.exception.SmartCardException
     */
    public static byte[] digestSHA256(byte[] input) throws SmartCardException {
        // we do digesting outside the card, because some cards do not support on-card hashing
        MessageDigest digestEngine;
        try {
            digestEngine = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            throw new SmartCardException(ExceptionSupport.getValue("NoSuchAlgorithmException"), ex);
        }

        // we buffer the content to have it after hashing for the PKCS#7 content
        return digestEngine.digest(input);
    }

    /**
     * Performs a final update on the digest using the specified array of bytes,
     * then completes the digest computation.
     *
     * @param input the input to be updated before the digest is completed.
     * @return the array of bytes for the resulting hash value
     * @throws tr.gov.turkiye.esign.exception.SmartCardException
     */
    public static byte[] digestSHA1(byte[] input) throws SmartCardException {
        // we do digesting outside the card, because some cards do not support on-card hashing
        MessageDigest digestEngine;
        try {
            digestEngine = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException ex) {
            throw new SmartCardException(ExceptionSupport.getValue("NoSuchAlgorithmException"), ex);
        }

        // we buffer the content to have it after hashing for the PKCS#7 content
        return digestEngine.digest(input);
    }

    /**
     * Gets CN part from principal
     *
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
     *
     * @param principal
     * @return serialNumber
     */
    public static String getSerialNumber(Principal principal) {
        String name = principal.getName().toUpperCase();
        int startIndex = name.indexOf("SERIALNUMBER=");
        int lastIndex = name.indexOf(",", startIndex);
        return name.substring(startIndex + 13, lastIndex);
    }

    /**
     * Converts a String to UTF-8 bytes.
     *
     * @param inString String to be converted
     * @return Bytes of inString
     */
    public static byte[] stringToBytes(final String inString) {
        final CharsetEncoder enc = Charset.forName("UTF-8").newEncoder();
        enc.onMalformedInput(CodingErrorAction.REPORT);
        enc.onUnmappableCharacter(CodingErrorAction.REPORT);
        final CharBuffer cb = CharBuffer.wrap(inString);
        ByteBuffer bb;
        try {
            bb = enc.encode(cb);
        } catch (final CharacterCodingException e) {
            return inString.getBytes();
        }
        final byte[] ba1 = bb.array();
        final byte[] ba2 = new byte[bb.limit()];
        System.arraycopy(ba1, 0, ba2, 0, ba2.length);
        return ba2;
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    /**
     *
     *
     * @param bytes
     * @return hex representation of bytes
     */
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }


}
