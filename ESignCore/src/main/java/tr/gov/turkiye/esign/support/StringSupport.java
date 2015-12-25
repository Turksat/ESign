/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.gov.turkiye.esign.support;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import static tr.gov.turkiye.esign.support.Util.hexArray;

/**
 *
 * @author iakpolat
 */
public class StringSupport {
    /**
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
    
    /**
     * 
     * @param bytes
     * @return 
     */
    public static String bytesToString(final byte[] bytes) {
        try {
            return new String(bytes, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            return new String(bytes);
        }
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
}
