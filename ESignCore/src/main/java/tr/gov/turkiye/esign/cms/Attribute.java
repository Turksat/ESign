/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.gov.turkiye.esign.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;

/**
 *      This class is used for defining authenticated and unauthenticated attributes. <br> <br>
 * 
 *      Attribute ::= DERSequence {<br>
 *               type    AttributeType,<br>
 *               values  DERSet OF AttributeValue<br>
 *                      -- at least one value is required --<br>
 *              }<br>                 
 *      <br>
 *      AttributeType   ::=   ASN1ObjectIdentifier<br>
 *      AttributeValue  ::=   ANY DEFINED BY type<br>
 * 
 * @author sercan
 */
public class Attribute implements CMSObject{

    /**
     * DERSequence instance of Attribute
     */
    private final DERSequence instance;

    /**
     * Creates attribute instance
     * @param key the type of the attribute
     * @param value the value of the attribute
     */
    public Attribute(ASN1ObjectIdentifier key, ASN1Encodable value) {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(key);
        v.add(new DERSet(value));
        instance = new DERSequence(v);
    }
    
    @Override
    public ASN1Object toASN1Object(){
        return instance;
    }
       
}
