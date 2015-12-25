/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.gov.turkiye.esign.cms;

import org.bouncycastle.asn1.ASN1Object;
import tr.gov.turkiye.esign.exception.SmartCardException;

/**
 * Base class for defining CMS objects
 * @author sercan
 */
public interface CMSObject {
    /**
     * converts CMS object to ASN1Object.
     * @return ASN1Object instance
     * @throws SmartCardException 
     */
    public abstract ASN1Object toASN1Object() throws SmartCardException;
}
