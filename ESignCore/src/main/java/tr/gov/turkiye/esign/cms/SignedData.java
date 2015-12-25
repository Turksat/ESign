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
import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERTaggedObject;
import tr.gov.turkiye.esign.exception.SmartCardException;
import tr.gov.turkiye.esign.support.ExceptionSupport;

/**
 * The class identifies the signed-data content.<br>
 * <br>
 * SignedData ::= DERSequence {<br>
 * version CMSVersion,<br>
 * digestAlgorithms DigestAlgorithmIdentifiers,<br>
 * encapContentInfo EncapsulatedContentInfo,<br>
 * certificates [0] IMPLICIT CertificateSet OPTIONAL,<br>
 * signerInfos SignerInfos }<br>
 * <br>
 * DigestAlgorithmIdentifiers ::= DERSet OF DigestAlgorithmIdentifier<br>
 * <br>
 * EncapsulatedContentInfo ::= DERSequence {<br>
        eContentType ContentType,<br>
        eContent [0] EXPLICIT DEROctetString OPTIONAL }<br>
 * <br>
      ContentType ::= ASN1ObjectIdentifier
 * <br>
 * CertificateSet ::= DERSet OF CertificateChoices<br>
 * <br>
 * CertificateChoices ::= CHOICE {<br>
 * certificate Certificate<br>
 * }<br>
 * <br>
 * <br>
 * SignerInfos ::= DERSet OF SignerInfo<br>
 *
 * @author sercan
 */
public class SignedData implements CMSObject {

    /**
     * the bytes of content to be signed.
     */
    private byte[] content; 
    
    /**
     * list of signer certificates
     */
    private List<Certificate> certs;
    
    /**
     * list of signer infos
     */
    private List<SignerInfo> signerInfos;
    
    /**
     * the hash algorithms used by the signers for digesting the content data is SHA256 or not
     */
    private boolean isSHA256;

    /**
     * Creates instance of SignerData
     * @param content the bytes of content to be signed.
     * @param isSHA256 the hash algorithms used by the signers for digesting the content data is SHA256 or not
     */
    public SignedData(byte[] content, boolean isSHA256) {
        signerInfos = new ArrayList<>();
        certs = new ArrayList<>();
        this.content = content;
        this.isSHA256 = isSHA256;
    }

    @Override
    public ASN1Object toASN1Object() throws SmartCardException {

        ASN1EncodableVector bodyVec = new ASN1EncodableVector();
        bodyVec.add(getCMSVersion());
        bodyVec.add(getDigestAlgorithms());
        bodyVec.add(getContentInfo(content));
        bodyVec.add(new DERTaggedObject(false, 0, getCertificates()));
        bodyVec.add(getSignerInfosEncodable());
        DERSequence bodySeq = new DERSequence(bodyVec);
        return bodySeq;

    }
    
    /**
     * version is the syntax version number.  The appropriate value
       depends on certificates, eContentType, and SignerInfo
     * @return ASN1Integer instance
     */
    private ASN1Integer getCMSVersion(){
        ASN1Integer cmsVersion;
        if ((certs != null && certs.size() > 0)
                && anyCertificatesWithTypeOfOtherPresent()) {
            cmsVersion = new ASN1Integer(5);
        } else {
            if ((certs != null && certs.size() > 0)
                    && anyVersionAttributeCertificatesPresent(2)) {
                cmsVersion = new ASN1Integer(4);
            } else if ((certs != null && certs.size() > 0) && (anyVersionAttributeCertificatesPresent(1)) 
                || (checkSignerInfoVersion(3))){
                cmsVersion = new ASN1Integer(3);
            }else{
                cmsVersion = new ASN1Integer(1);
            }
        }
        return cmsVersion;
    }
    
    /**
     * Checks any SignerInfo structures are version
     * @param version the syntax version number
     * @return true or false
     */
    private boolean checkSignerInfoVersion(int version){
        for(SignerInfo signerInfo : signerInfos){
            if(signerInfo.getSignerVersion().getValue().intValue() == version){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks any version X attribute certificates are present
     * @param version the syntax version number
     * @return true or false
     */
    private boolean anyVersionAttributeCertificatesPresent(int version) {
        for (Certificate cert : certs) {
            if (cert instanceof X509Certificate) {
                X509Certificate x509Certificate = (X509Certificate) cert;
                if (x509Certificate.getVersion() == version) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks any certificates with a type of other are present
     * @return true or false
     */
    private boolean anyCertificatesWithTypeOfOtherPresent() {
        for (int i = 0; i < certs.size(); i++) {
            for (int j = i + 1; j < certs.size(); j++) {
                if (certs.get(i).getType().equals(certs.get(j).getType())) {
                    return true;
                }
            }

        }
        return false;
    }

    /**
     * Collection of message digest algorithm identifiers.
     * @return DERSet instance
     */
    private ASN1Encodable getDigestAlgorithms() {
        final ASN1EncodableVector algos = new ASN1EncodableVector();
        if (isSHA256) {
            algos.add(ObjectID.idSHA256);
        } else {
            algos.add(ObjectID.idSHA1);
        }
        return new DERSet(new DERSequence(algos));
    }

    /**
     * It is the signed content, consisting of a content
      type identifier and the content itself
     * @param content the bytes of content to be signed.
     * @return DERSequence instance
     */
    private ASN1Encodable getContentInfo(byte[] content) {
        final ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(ObjectID.pkcs7_data);
        DEROctetString ds = new DEROctetString(content);
        DERTaggedObject to = new DERTaggedObject(true, 0, ds);
        v.add(to);
        DERSequence ddd = new DERSequence(v);
        return ddd;
    }

    /**
     * collection of certificates
     * @return DERSet instance
     * @throws SmartCardException 
     */
    private ASN1Encodable getCertificates() throws SmartCardException {
        ASN1EncodableVector v = new ASN1EncodableVector();
        for (Certificate cert : certs) {
            try {
                byte[] encodedCertificate = cert.getEncoded();
                ASN1InputStream tempstream = new ASN1InputStream(new ByteArrayInputStream(encodedCertificate));
                ASN1Primitive dob = tempstream.readObject();
                v.add(dob);
            }catch (CertificateEncodingException ex) {
                throw new SmartCardException(ExceptionSupport.getValue("CertificateEncodingException"), ex);
            }catch (IOException ex) {
                throw new SmartCardException(ExceptionSupport.getValue("IOException"), ex);
            }
        }
        DERSet cert = new DERSet(v);
        return cert;
    }

    /**
     * 
     * @return isSHA256
     */
    public boolean isIsSHA256() {
        return isSHA256;
    }

    /**
     * Sets isSHA256 field
     * @param isSHA256 
     */
    public void setIsSHA256(boolean isSHA256) {
        this.isSHA256 = isSHA256;
    }

    /**
     * 
     * @return the bytes of content
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * Sets content field
     * @param content 
     */
    public void setContent(byte[] content) {
        this.content = content;
    }

    /**
     * 
     * @return list of certificates
     */
    public List<Certificate> getCerts() {
        return certs;
    }

    /**
     * Sets certs field
     * @param certs 
     */
    public void setCerts(List<Certificate> certs) {
        this.certs = certs;
    }

    /**
     * 
     * @return list of signer infos
     */
    public List<SignerInfo> getSignerInfos() {
        return signerInfos;
    }

    /**
     * Sets signerInfos field
     * @param signerInfos 
     */
    public void setSignerInfos(List<SignerInfo> signerInfos) {
        this.signerInfos = signerInfos;
    }

    /**
     * Adds signerInfo
     * @param signerInfo
     * @return 
     */
    public boolean addSignerInfo(SignerInfo signerInfo) {
        return signerInfos.add(signerInfo);
    }

    /**
     * Adds certificate
     * @param cert
     * @return 
     */
    public boolean addCertificate(X509Certificate cert) {
        return certs.add(cert);
    }

    /**
     * collection of signerInfos
     * @return DERSet instance
     * @throws SmartCardException 
     */
    private ASN1Encodable getSignerInfosEncodable() throws SmartCardException {
        for (SignerInfo signerInfo : signerInfos) {
            final DERSet ddd = new DERSet(signerInfo.toASN1Object());
            return ddd;
        }
        return null;
    }
    

}
