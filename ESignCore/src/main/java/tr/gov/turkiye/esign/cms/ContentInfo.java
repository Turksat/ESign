/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.gov.turkiye.esign.cms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import tr.gov.turkiye.esign.exception.SmartCardException;
import tr.gov.turkiye.esign.support.ExceptionSupport;

/**
 * ContentInfo ::= DERSequence {<br>
    contentType ContentType,<br>
    content <br>
      [0] EXPLICIT ANY DEFINED BY contentType }<br>
 <br>
 ContentType ::= ASN1ObjectIdentifier<br>
 * <br>
 * @author sercan
 */
public class ContentInfo implements CMSObject{

    /**
     * content type of data
     */
    private ASN1ObjectIdentifier contentType;
    
    /**
     * content (SignedData instance)
     */
    private SignedData signedData;
    
    /**
     * Creates ContentInfo instance
     * @param contentType content type of data
     * @param signedData content (SignedData instance)
     */
    public ContentInfo(ASN1ObjectIdentifier contentType,SignedData signedData ){
        this.signedData = signedData;
        this.contentType = contentType;
    }

    /**
     * Gets encoded bytes of this object
     * @return the bytes of ContentInfo
     * @throws SmartCardException 
     */
    public byte[] getEncoded() throws SmartCardException  {

        try {
            final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            final DEROutputStream dout = new DEROutputStream(bOut);
            dout.writeObject(toASN1Object());
            dout.close();
            return bOut.toByteArray();
        } catch (IOException ex) {
            throw new SmartCardException(ExceptionSupport.getValue("IOException"), ex);
        }
    }

    /**
     * Gets content type
     * @return ASN1ObjectIdentifier instance
     */
    public ASN1ObjectIdentifier getContentType() {
        return contentType;
    }

    /**
     * Sets contentType field
     * @param contentType content type of data
     */
    public void setContentType(ASN1ObjectIdentifier contentType) {
        this.contentType = contentType;
    }

    /**
     * Gets content
     * @return SignedData instance
     */
    public SignedData getSignedData() {
        return signedData;
    }

    /**
     * Sets signedData field
     * @param signedData content
     */
    public void setSignedData(SignedData signedData) {
        this.signedData = signedData;
    }

    @Override
    public ASN1Object toASN1Object() throws SmartCardException {
        ASN1EncodableVector whole = new ASN1EncodableVector();
        whole.add(contentType);
        whole.add(new DERTaggedObject(true, 0, signedData.toASN1Object()));
        final DERSequence whol = new DERSequence(whole);
        return whol;
    }
    
    

}
