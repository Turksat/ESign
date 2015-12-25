/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.gov.turkiye.esign.support;

import java.security.cert.X509Certificate;
import java.util.ResourceBundle;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;

/**
 * This extension lists certificate policies, recognized by the issuing CA, 
 * that apply to the certificate, together with optional qualifier 
 * information pertaining to these certificate policies
 * @author sercan
 */
public class CertificatePolicySupport {

    private static ResourceBundle bundle = null;

    /**
     * 
     * @param key the oid for certificate policy
     * @return name of certificate policy name
     */
    public static String getValue(final String key) {
        if (bundle == null) {
            bundle = ResourceBundle.getBundle("certificate/certificatePolicyIds");
        }
        return bundle.getString(key);
    }

    /**
     * Checks whether given certificate has one of the predefined certificate policies.
     * @param cert certificate
     * @return true or false
     */
    public static boolean checkCertificatePolicyIdentifier(final X509Certificate cert) {
        try {
            //Logger logger = Logger.getLogger(CertificatePolicySupport.class.toString());
            //logger.info("CHECKING CERTIFICATE POLICY IDENTIFIER");
            byte[] extensionValue = cert.getExtensionValue("2.5.29.32");
            ASN1OctetString octs = (ASN1OctetString) ASN1Primitive.fromByteArray(extensionValue);
            ASN1Sequence sequence = (ASN1Sequence) ASN1Primitive.fromByteArray(octs.getOctets());
            for (int i = 0; i < sequence.size(); i++) {
                ASN1Sequence subSequence = (ASN1Sequence) sequence.getObjectAt(i);
                ASN1ObjectIdentifier identifier = (ASN1ObjectIdentifier) subSequence.getObjectAt(0);
                String oidValue = getValue(identifier.getId());
                if (oidValue != null) {
                    //logger.info("CHECKING CERTIFICATE POLICY IDENTIFIER IS VALID : " + identifier.getId() + " - " + oidValue);
                    return true;
                } else {
                    //logger.info("CHECKING CERTIFICATE POLICY IDENTIFIER IS NOT VALID : " + identifier.getId());
                }
            }
            return false;
        } catch (final Throwable e) {
        }
        return false;
    }

}
