/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.gov.turkiye.esign.cms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DLSequence;
import tr.gov.turkiye.esign.exception.SmartCardException;
import tr.gov.turkiye.esign.support.ExceptionSupport;
import tr.gov.turkiye.esign.util.Util;

/**
 * This class specifies the SignerInfo type for collecting all signer-related information <br>
 * <br>
 * <br>
 * SignerInfo ::= DERSequence { <br>
        version CMSVersion,<br>
        sid SignerIdentifier,<br>
        digestAlgorithm DigestAlgorithmIdentifier,<br>
        signedAttrs [0] IMPLICIT SignedAttributes OPTIONAL,<br>
        signatureAlgorithm SignatureAlgorithmIdentifier,<br>
        signature SignatureValue,<br>
        unsignedAttrs [1] IMPLICIT UnsignedAttributes OPTIONAL }<br>
        * <br>
      SignerIdentifier ::= issuerAndSerialNumber IssuerAndSerialNumber,<br>
      * <br>
      SignedAttributes ::= DERSet SIZE (1..MAX) OF Attribute<br>
      * <br>
      UnsignedAttributes ::= DERSet SIZE (1..MAX) OF Attribute<br>
      * <br>
      SignatureValue ::= DEROctetString<br>
 *  <br>
 * @author sercan
 */
public class SignerInfo implements CMSObject {

    /**
     * signer certificate
     */
    private X509Certificate cert;
    
    /**
     * the hash algorithms used by the signers is SHA256 or not
     */
    private final boolean isSHA256;
    
    /**
     * collection of attributes that are signed
     */
    private List<Attribute> authenticatedAttributes;
    
    /**
     * collection of attributes that are not signed
     */
    private List<Attribute> unAuthenticatedAttributes;
    
    /**
     * the result of digital signature generation, using the
      message digest and the signer's private key
     */
    private byte[] encryptedDigest;

    /**
     * Creates SignerInfo instance
     * @param cert signer certificate
     * @param isSHA256 the hash algorithms used by the signers is SHA256 or not
     */   
    public SignerInfo(X509Certificate cert, boolean isSHA256) {
        this.cert = cert;
        this.isSHA256 = isSHA256;
    }

    @Override
    public ASN1Object toASN1Object() throws SmartCardException {
        try {
            DERSet signedAttributesSet = getSignedAttributesSet();
            DERTaggedObject signedAttributesObject = new DERTaggedObject(false, 0, signedAttributesSet);
            ASN1EncodableVector signerinfo = new ASN1EncodableVector();
            
            signerinfo.add(getSignerVersion());
            signerinfo.add(getIssuerAndSerial());
            signerinfo.add(getSignAlgorithms());
            signerinfo.add(signedAttributesObject);
            signerinfo.add(getDigestAlgorithmIdentifiers());
            signerinfo.add(getSignedDigest());
            final DERSequence ddd = new DERSequence(signerinfo);
            return ddd;
        } catch (CertificateEncodingException ex) {
            throw new SmartCardException(ExceptionSupport.getValue("CertificateEncodingException"), ex);
        } catch (IOException ex) {
            throw new SmartCardException(ExceptionSupport.getValue("IOException"), ex);
        }

    }

    /**
     * Since the SignerIdentifier is the issuerAndSerialNumber, the version MUST be 1.
     * @return ASN1Integer instance
     */
    public ASN1Integer getSignerVersion() {
        return new ASN1Integer(1);
    }


    /**
     * Gets signer certificate
     * @return X509Certificate instance
     */
    public X509Certificate getCert() {
        return cert;
    }

    
    /**
     * Sets signer certificate
     * @param cert signer certificate
     */
    public void setCert(X509Certificate cert) {
        this.cert = cert;
    }

    /**
     * Checks the hash algorithms used by the signers is SHA256 or not
     * @return true or false
     */
    public boolean isIsSHA256() {
        return isSHA256;
    }

    /**
     * Gets list of authenticated attributes
     * @return list of authenticated attributes
     */
    public List<Attribute> getAuthenticatedAttributes() {
        return authenticatedAttributes;
    }

    /**
     * Sets authenticatedAttributes field
     * @param authenticatedAttributes list of authenticated attributes
     */
    public void setAuthenticatedAttributes(List<Attribute> authenticatedAttributes) {
        this.authenticatedAttributes = authenticatedAttributes;
    }

    /**
     * Gets list of unauthenticated attributes
     * @return list of unauthenticated attributes
     */
    public List<Attribute> getUnAuthenticatedAttributes() {
        return unAuthenticatedAttributes;
    }

    /**
     * Sets unAuthenticatedAttributes field
     * @param unAuthenticatedAttributes list of unauthenticated attributes
     */
    public void setUnAuthenticatedAttributes(List<Attribute> unAuthenticatedAttributes) {
        this.unAuthenticatedAttributes = unAuthenticatedAttributes;
    }

    /**
     * Gets encryptedDigest field
     * @return encrypted digest
     */
    public byte[] getEncryptedDigest() {
        return encryptedDigest;
    }

    /**
     * Sets encrypted digest
     * @param encryptedDigest 
     */
    public void setEncryptedDigest(byte[] encryptedDigest) {
        this.encryptedDigest = encryptedDigest;
    }

    /**
     * identifies the signer's certificate by the issuer's distinguished name and the certificate serial number
     * 
     * @return DERSequence instance
     * @throws CertificateEncodingException
     * @throws IOException 
     */
    private ASN1Encodable getIssuerAndSerial() throws CertificateEncodingException, IOException {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(getIssuer());
        v.add(getCertificateSerial());
        final DERSequence ddd = new DERSequence(v);
        return ddd;
    }

    /**
     * Gets signer's certificate's issuer
     * @return ASN1Encodable instance
     * @throws CertificateEncodingException
     * @throws IOException 
     */
    private ASN1Encodable getIssuer() throws CertificateEncodingException, IOException {
        ASN1InputStream in = new ASN1InputStream(new ByteArrayInputStream(cert.getTBSCertificate()));
        DLSequence seq = (DLSequence) in.readObject();
        ASN1Encodable issuer = seq.getObjectAt(seq.getObjectAt(0) instanceof DERTaggedObject ? 3 : 2)
                .toASN1Primitive();
        return issuer;
    }

    /**
     * Gets signer's certificate's serial
     * @return ASN1Integer isntance
     */
    private ASN1Encodable getCertificateSerial() {
        return new ASN1Integer(cert.getSerialNumber());
    }

    /**
     * identifies the signature algorithm
     * @return DERSequence instance
     */
    private ASN1Encodable getSignAlgorithms() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        if (isSHA256) {
            v.add(ObjectID.idSHA256);
        } else {
            v.add(ObjectID.idSHA1);
        }
        final DERSequence ddd = new DERSequence(v);
        return ddd;
    }

    /**
     * identifies the message digest algorithm, and any
       associated parameters, used by the signer
     * @return DERSequence instance
     */
    private ASN1Encodable getDigestAlgorithmIdentifiers() {
        ASN1EncodableVector v;
        v = new ASN1EncodableVector();
        v.add(ObjectID.rsaEncryption);
        v.add(DERNull.INSTANCE);
        final DERSequence ddd = new DERSequence(v);
        return ddd;
    }

    /**
     * collection of attributes that are signed
     * @return DERSet instance
     */
    private DERSet getSignedAttributesSet() {
        final ASN1EncodableVector attributes = new ASN1EncodableVector();
        for (int i = 0; i < authenticatedAttributes.size(); i++) {
            attributes.add(authenticatedAttributes.get(i).toASN1Object());
        }
        final DERSet signedAttributes = new DERSet(attributes);
        return signedAttributes;
    }

    /**
     * the result of digital signature generation, using the
      message digest and the signer's private key
     * @return DEROctetString instance
     */
    private ASN1Encodable getSignedDigest() {
        return new DEROctetString(encryptedDigest);
    }

    /**
     * Gets encoded bytes of authenticated attributes
     * @return byte array
     * @throws SmartCardException 
     */
    public byte[] getEncodedAuthenticatedAttributes() throws SmartCardException {
        try {
            final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            final DEROutputStream dOut = new DEROutputStream(bOut);
            dOut.writeObject(getSignedAttributesSet());
            final byte[] bytesToSign = bOut.toByteArray();

            final byte[] bytesToSignHash = getHash(bytesToSign);
            ASN1EncodableVector v = new ASN1EncodableVector();
            if (isSHA256) {
                v.add(ObjectID.idSHA256);
            } else {
                v.add(ObjectID.idSHA1);
            }
            v.add(DERNull.INSTANCE);
            final DERSequence algorithmIdentifier = new DERSequence(v);
            v = new ASN1EncodableVector();
            v.add(algorithmIdentifier);
            v.add(new DEROctetString(bytesToSignHash));
            final DERSequence digestInfo = new DERSequence(v);
            final byte[] bytes = digestInfo.getEncoded();
            return bytes;
        } catch (IOException ex) {
            throw new SmartCardException(ExceptionSupport.getValue("IOException"), ex);
        }
    }

    /**
     * Performs a final update on the digest using the specified array of bytes, then completes the digest computation
     * @param bytesToSign the input to be updated before the digest is completed.
     * @return the array of bytes for the resulting hash value 
     * @throws SmartCardException 
     */
    private byte[] getHash(byte[] bytesToSign) throws SmartCardException {
        if (isSHA256) {
            return Util.digestSHA256(bytesToSign);
        } else {
            return Util.digestSHA1(bytesToSign);
        }
    }
}
