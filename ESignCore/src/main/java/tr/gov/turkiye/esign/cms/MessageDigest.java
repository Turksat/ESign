/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.gov.turkiye.esign.cms;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.DEROctetString;
import tr.gov.turkiye.esign.exception.SmartCardException;
import tr.gov.turkiye.esign.util.Util;

/**
 * The message-digest attribute type specifies the message digest of the
   encapContentInfo eContent OCTET STRING being signed in signed-data
    or authenticated in authenticated-data.
   * <br><br>
 * MessageDigest ::= DEROctetString content
 * @author sercan
 */
public class MessageDigest implements CMSObject{
    /**
     * DEROctetString instance of MessageDigest
     */
    DEROctetString contentDigest;
    
    /**
     * Creates MessageDigest instance
     * @param content the input for digest
     * @param isSHA256 the algorithm requested is SHA256 or not
     * @throws SmartCardException 
     */
    public MessageDigest(byte[] content, boolean isSHA256) throws SmartCardException {
        byte[] contentDigestBytes;
        if(isSHA256){
            contentDigestBytes = Util.digestSHA256(content);
        }else{
            contentDigestBytes = Util.digestSHA1(content);
        }        
        contentDigest = new DEROctetString(contentDigestBytes);
    }

    
    
    @Override
    public ASN1Object toASN1Object() {
        return contentDigest;
    }
    
}
