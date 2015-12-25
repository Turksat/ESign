/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.gov.turkiye.esign.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DLSequence;
import tr.gov.turkiye.esign.exception.SmartCardException;
import tr.gov.turkiye.esign.support.ExceptionSupport;
import tr.gov.turkiye.esign.util.Util;

/**
 *The signing certificate attribute is designed to prevent the simple <br>
   substitution and re-issue attacks, and to allow for a restricted set <br>
   of authorization certificates to be used in verifying a signature <br>
 * <br>
 * <br>
 * SigningCertificate ::=  DERSequence {<br>
     certs        DERSequence OF ESSCertID,<br>
  }<br>
  * <br>
  * <br>
 ESSCertID ::=  DERSequence { <br>       
   certHash     Hash,<br>
   issuerSerial IssuerSerial OPTIONAL   }<br>
   * <br>
   * <br>
 Hash ::= DEROctetString<br>
 * <br>
 * <br>
 IssuerSerial ::= DERSequence { <br>       
   issuer       GeneralNames,<br>
   serialNumber CertificateSerialNumber   }<br>
 * <br>
 * @author sercan
 */
public class SigningCertificate implements CMSObject {

    /**
     * list of signing certificates
     */
    private final List<Certificate> certs;
    
    /**
     * the hash algorithms used for signing is SHA256 or not
     */
    private final boolean isSHA256;

    /**
     * Creates SigningCertificate instance
     * @param certs list of signing certificates
     * @param isSHA256 the hash algorithms used for signing is SHA256 or not
     */
    public SigningCertificate(List<Certificate> certs, boolean isSHA256) {
        this.certs = certs;
        this.isSHA256 = isSHA256;
    }

    @Override
    public ASN1Object toASN1Object() throws SmartCardException {
        ASN1EncodableVector certList = new ASN1EncodableVector();
        for (Certificate cert : certs) {
            ASN1EncodableVector essCertID1 = new ASN1EncodableVector();
            essCertID1.add(getCertificateHash(cert));
            essCertID1.add(getIssuerAndSerialForESSCertId(cert));
            DERSequence essCertID = new DERSequence(essCertID1);
            certList.add(essCertID);

        }

        DERSequence certListSeq = new DERSequence(certList);
        DERSequence signSeq = new DERSequence(certListSeq);
        return signSeq;
    }

    /**
     * the certHash is computed over the entire DER encoded certificate including the signature
     * @param cert signing certificate
     * @return DEROctetString instance
     * @throws SmartCardException 
     */
    private ASN1Encodable getCertificateHash(Certificate cert) throws SmartCardException {
        try {
            byte[] certHash;
            if(isSHA256){
                certHash = Util.digestSHA256(cert.getEncoded());
            }else{
                certHash = Util.digestSHA1(cert.getEncoded());
            }
            DEROctetString hash = new DEROctetString(certHash);
            return hash;
        } catch (CertificateEncodingException ex) {
            throw new SmartCardException(ExceptionSupport.getValue("CertificateEncodingException"), ex);
        }
    }

    /**
     * Gets issuer and serial part from signing certificate
     * @param cert signing certificate
     * @return DERSequence instance
     * @throws SmartCardException 
     */
    private ASN1Encodable getIssuerAndSerialForESSCertId(Certificate cert) throws SmartCardException {
        try {
            final ASN1EncodableVector issuerSerialPart = new ASN1EncodableVector();
            issuerSerialPart.add(new DERSequence(new DERTaggedObject(true, 4, getIssuer(cert))));
            issuerSerialPart.add(new ASN1Integer(((X509Certificate) cert).getSerialNumber()));
            final DERSequence issuerSerial = new DERSequence(issuerSerialPart);
            return issuerSerial;
        } catch (CertificateEncodingException ex) {
            throw new SmartCardException(ExceptionSupport.getValue("CertificateEncodingException"), ex);
        } catch (IOException ex) {
            throw new SmartCardException(ExceptionSupport.getValue("IOException"), ex);
        }
    }

    /**
     * Gets issuer part from signing certificate
     * @param cert singing certificate
     * @return ASN1Encodable instance
     * @throws CertificateEncodingException
     * @throws IOException 
     */
    private ASN1Encodable getIssuer(Certificate cert) throws CertificateEncodingException, IOException {
        final ASN1InputStream in = new ASN1InputStream(new ByteArrayInputStream(((X509Certificate) cert).getTBSCertificate()));
        final DLSequence seq = (DLSequence) in.readObject();
        final ASN1Encodable issuer = seq.getObjectAt(seq.getObjectAt(0) instanceof DERTaggedObject ? 3 : 2)
                .toASN1Primitive();
        return issuer;
    }

}
