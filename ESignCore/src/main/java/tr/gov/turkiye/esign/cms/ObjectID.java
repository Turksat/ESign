/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.gov.turkiye.esign.cms;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

/**
 * Consist of object identifiers
 * 
 * @author sercan
 */
public class ObjectID {

    /**
     * Creates an ASN1ObjectIdentifier (1.2.840.113549.1.7.2) for the PKCS#7 object PKCS#7 signedData.
     */
    public static final ASN1ObjectIdentifier pkcs7_signedData = new ASN1ObjectIdentifier("1.2.840.113549.1.7.2");
    
    /**
     * Creates an ASN1ObjectIdentifier (1.3.14.3.2.26) for the SHA-1 message digest algorithm. 
     */
    public static final ASN1ObjectIdentifier idSHA1 = new ASN1ObjectIdentifier("1.3.14.3.2.26");
    
    /**
     * Creates an ASN1ObjectIdentifier (2.16.840.1.101.3.4.2.1) for the SHA-256 message digest algorithm. 
     */
    public static final ASN1ObjectIdentifier idSHA256 = new ASN1ObjectIdentifier("2.16.840.1.101.3.4.2.1");
    
    /**
     * Creates an ASN1ObjectIdentifier (1.2.840.113549.1.7.1) for the PKCS#7 object PKCS#7 data.
     */
    public static final ASN1ObjectIdentifier pkcs7_data = new ASN1ObjectIdentifier("1.2.840.113549.1.7.1");
    
    /**
     * Creates an ASN1ObjectIdentifier (1.2.840.113549.1.7.1) for RSA encryption.
     */
    public static final ASN1ObjectIdentifier rsaEncryption = new ASN1ObjectIdentifier("1.2.840.113549.1.1.1");
    
    /**
     * Creates an ASN1ObjectIdentifier (1.2.840.113549.1.9.3) for the attribute contentType.
     */
    public static final ASN1ObjectIdentifier content_type = new ASN1ObjectIdentifier("1.2.840.113549.1.9.3");
    
    /**
     * Creates an ASN1ObjectIdentifier (1.2.840.113549.1.9.4) for the attribute messageDigest.
     */
    public static final ASN1ObjectIdentifier message_digest = new ASN1ObjectIdentifier("1.2.840.113549.1.9.4");
    
    /**
     * Creates an ASN1ObjectIdentifier (1.2.840.113549.1.9.16.2.47) for the SigningCertificateV2 attribute.
     */
    public static final ASN1ObjectIdentifier signing_certificate_v2 = new ASN1ObjectIdentifier("1.2.840.113549.1.9.16.2.47");
    
    /**
     * Creates an ASN1ObjectIdentifier (1.2.840.113549.1.9.16.2.12) for the SigningCertificate attribute.
     */
    public static final ASN1ObjectIdentifier signing_certificate = new ASN1ObjectIdentifier("1.2.840.113549.1.9.16.2.12");
    
    /**
     * Creates an ASN1ObjectIdentifier (1.2.840.113549.1.9.5) for the attribute signingTime.
     */
    public static final ASN1ObjectIdentifier signingTime = new ASN1ObjectIdentifier("1.2.840.113549.1.9.5");
}
