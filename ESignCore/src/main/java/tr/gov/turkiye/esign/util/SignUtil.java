/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.gov.turkiye.esign.util;

import iaik.asn1.ASN;
import iaik.asn1.ASN1Object;
import iaik.asn1.CON_SPEC;
import iaik.asn1.CodingException;
import iaik.asn1.DerCoder;
import iaik.asn1.OCTET_STRING;
import iaik.asn1.ObjectID;
import iaik.asn1.structures.AlgorithmID;
import iaik.asn1.structures.Attribute;
import iaik.asn1.structures.ChoiceOfTime;
import iaik.pkcs.PKCSException;
import iaik.pkcs.PKCSParsingException;
import iaik.pkcs.pkcs11.Mechanism;
import iaik.pkcs.pkcs11.MechanismInfo;
import iaik.pkcs.pkcs11.Session;
import iaik.pkcs.pkcs11.Slot;
import iaik.pkcs.pkcs11.Token;
import iaik.pkcs.pkcs11.TokenException;
import iaik.pkcs.pkcs11.TokenInfo;
import iaik.pkcs.pkcs11.objects.Key;
import iaik.pkcs.pkcs11.objects.PrivateKey;
import iaik.pkcs.pkcs11.objects.RSAPrivateKey;
import iaik.pkcs.pkcs11.objects.X509PublicKeyCertificate;
import iaik.pkcs.pkcs11.wrapper.PKCS11Constants;
import iaik.pkcs.pkcs7.ContentInfo;
import iaik.pkcs.pkcs7.DigestInfo;
import iaik.pkcs.pkcs7.IssuerAndSerialNumber;
import iaik.pkcs.pkcs7.SignedData;
import iaik.pkcs.pkcs7.SignerInfo;
import iaik.smime.ess.SigningCertificateV2;
import iaik.utils.CryptoUtils;
import iaik.x509.X509Certificate;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import tr.gov.turkiye.esign.model.KeyAndCertificate;

/**
 *
 * @author sercan
 */
public class SignUtil {

    /**
     * Gets token in the slot
     *
     * @param slot
     * @return token instance
     * @throws TokenException
     */
    public static Token getToken(Slot slot) throws TokenException {
        return slot.getToken();
    }

    /**
     * Get the list of mechanisms that this token supports
     *
     * @param token
     * @return list of Mechanisms
     * @throws TokenException
     */
    public static Mechanism[] getMechanismList(Token token) throws TokenException {
        return token.getMechanismList();
    }

    /**
     * checks whether RSA signing supported or not.
     *
     * @param token
     * @return RSA signing supported or not.
     * @throws TokenException
     */
    public static boolean isRSASigningSupported(Token token) throws TokenException {
        List supportedMechanisms = Arrays.asList(token.getMechanismList());
        if (!supportedMechanisms.contains(Mechanism.get(PKCS11Constants.CKM_RSA_PKCS))) {
            return false;
        } else {
            MechanismInfo rsaMechanismInfo = token.getMechanismInfo(Mechanism
                    .get(PKCS11Constants.CKM_RSA_PKCS));
            if (!rsaMechanismInfo.isSign()) {
                return false;
            }
        }
        return true;
    }

    /**
     * opens authorized session with given token.
     *
     * @param token
     * @param rwSession Must be either SessionReadWriteBehavior.RO_SESSION for
     * read-only sessions or SessionReadWriteBehavior.RW_SESSION for read-write
     * sessions.
     * @param pin pin code of smartcard.
     * @return session instance
     * @throws TokenException
     * @throws IOException
     */
    public static Session openAuthorizedSession(Token token, boolean rwSession, char[] pin) throws TokenException, IOException {
        if (token == null) {
            throw new NullPointerException("Argument \"token\" must not be null.");
        }

        Session session = token.openSession(Token.SessionType.SERIAL_SESSION, rwSession, null, null);

        TokenInfo tokenInfo = token.getTokenInfo();
        if (tokenInfo.isLoginRequired()) {
            if (tokenInfo.isProtectedAuthenticationPath()) {
                session.login(Session.UserType.USER, null);
            } else {
                session.login(Session.UserType.USER, pin);
            }
        }
        return session;
    }

    /**
     * opens unauthorized session with given token.
     *
     * @param token
     * @param rwSession Must be either SessionReadWriteBehavior.RO_SESSION for
     * read-only sessions or SessionReadWriteBehavior.RW_SESSION for read-write
     * sessions.
     * @return Session instance
     * @throws TokenException
     * @throws IOException
     */
    public static Session openUnauthorizedSession(Token token, boolean rwSession) throws TokenException, IOException {
        if (token == null) {
            throw new NullPointerException("Argument \"token\" must not be null.");
        }

        Session session = token.openSession(true, rwSession, null, null);
        return session;
    }

    /**
     * closes session.
     *
     * @param session
     * @throws TokenException
     */
    public static void closeSession(Session session) throws TokenException {
        if (session != null) {
            session.closeSession();
        }
    }

    /**
     * Gets list of key and certificates in the session
     *
     * @param session
     * @return list of key and certificates
     * @throws TokenException
     * @throws IOException
     * @throws CertificateException
     */
    public static List<KeyAndCertificate> getSigningKeyAndCertificates(Session session)
            throws TokenException, IOException, CertificateException {
        RSAPrivateKey keyTemplate = new RSAPrivateKey();
        keyTemplate.getSign().setBooleanValue(Boolean.TRUE);
        if (session == null) {
            throw new NullPointerException("Argument \"session\" must not be null.");
        }
        if (keyTemplate == null) {
            throw new NullPointerException("Argument \"keyTemplate\" must not be null.");
        }

        List<KeyAndCertificate> keyAndCertificates = new ArrayList<>();

        // holds the first suitable object handle if pickFirstSuitable is set true
        Vector keyList = new Vector(4);

        session.findObjectsInit(keyTemplate);
        iaik.pkcs.pkcs11.objects.Object[] matchingKeys;

        while ((matchingKeys = session.findObjects(1)).length > 0) {
            keyList.addElement(matchingKeys[0]);
        }
        session.findObjectsFinal();

        // try to find the corresponding certificates for the signature keys
        Hashtable keyToCertificateTable = new Hashtable(4);
        Enumeration keyListEnumeration = keyList.elements();
        while (keyListEnumeration.hasMoreElements()) {
            PrivateKey signatureKey = (PrivateKey) keyListEnumeration.nextElement();
            byte[] keyID = signatureKey.getId().getByteArrayValue();
            X509PublicKeyCertificate certificateTemplate = new X509PublicKeyCertificate();
            if (!session.getModule().getInfo().getManufacturerID().contains("AEP")) //AEP HSM can't find certificate IDs with findObjects
            {
                certificateTemplate.getId().setByteArrayValue(keyID);
            }

            session.findObjectsInit(certificateTemplate);
            iaik.pkcs.pkcs11.objects.Object[] correspondingCertificates = session.findObjects(1);

            if (correspondingCertificates.length > 0) {
                if (session.getModule().getInfo().getManufacturerID().contains("AEP")) {   //check ID manually for AEP HSM
                    while (correspondingCertificates.length > 0) {
                        X509PublicKeyCertificate certObject = (X509PublicKeyCertificate) correspondingCertificates[0];
                        if (CryptoUtils.equalsBlock(certObject.getId().getByteArrayValue(), keyID)) {
                            keyToCertificateTable.put(signatureKey, certObject);
                            break;
                        }
                        correspondingCertificates = session.findObjects(1);
                    }
                } else {
                    keyToCertificateTable.put(signatureKey, correspondingCertificates[0]);
                }
            }
            session.findObjectsFinal();
        }

        Key selectedKey = null;
        X509PublicKeyCertificate correspondingCertificate = null;
        if (keyList.isEmpty()) {
            //output.println("Found NO matching key that can be used.");
        } else if (keyList.size() == 1) {
            // there is no choice, take this key
            selectedKey = (Key) keyList.elementAt(0);
            // create a IAIK JCE certificate from the PKCS11 certificate
            correspondingCertificate = (X509PublicKeyCertificate) keyToCertificateTable
                    .get(selectedKey);

            X509Certificate x509Certificate = new X509Certificate(correspondingCertificate
                    .getValue().getByteArrayValue());
            boolean[] keyUsage = x509Certificate.getKeyUsage();
            boolean signingSignature = false;
            if (keyUsage != null) {
                signingSignature = keyUsage[0] && keyUsage[1];
            }

            if (signingSignature) {
                keyAndCertificates.add(new KeyAndCertificate(selectedKey, x509Certificate));
            }

        } else {
            // give the user the choice
            //output.println("found these private RSA signing keys:");
            Hashtable objectHandleToObjectMap = new Hashtable(keyList.size());
            Enumeration keyListEnumeration2 = keyList.elements();
            while (keyListEnumeration2.hasMoreElements()) {
                iaik.pkcs.pkcs11.objects.Object signatureKey = (iaik.pkcs.pkcs11.objects.Object) keyListEnumeration2.nextElement();
                long objectHandle = signatureKey.getObjectHandle();
                objectHandleToObjectMap.put(objectHandle, signatureKey);
                correspondingCertificate = (X509PublicKeyCertificate) keyToCertificateTable
                        .get(signatureKey);

                X509Certificate x509Certificate = new X509Certificate(correspondingCertificate
                        .getValue().getByteArrayValue());
                boolean[] keyUsage = x509Certificate.getKeyUsage();
                boolean signingSignature = false;
                if (keyUsage != null) {
                    signingSignature = keyUsage[0] && keyUsage[1];
                }
                if (signingSignature && (null != correspondingCertificate || !keyListEnumeration2.hasMoreElements())) {
                    selectedKey = (RSAPrivateKey) objectHandleToObjectMap.get(objectHandle);
                    if (selectedKey != null) {
                        keyAndCertificates.add(new KeyAndCertificate(selectedKey, x509Certificate));
                    }
                }
            }
        }
        return keyAndCertificates;
    }

    /**
     * Gets list of signing certificates in the session
     *
     * @param session
     * @return list of signing certificates
     * @throws TokenException
     * @throws IOException
     * @throws CertificateException
     */
    public static List<X509Certificate> getSigningCertificates(Session session)
            throws TokenException, IOException, CertificateException {
        if (session == null) {
            throw new NullPointerException("Argument \"session\" must not be null.");
        }
        List<X509Certificate> certificates = new ArrayList<>();

        X509PublicKeyCertificate certificateTemplate = new X509PublicKeyCertificate();
        session.findObjectsInit(certificateTemplate);
        iaik.pkcs.pkcs11.objects.Object[] correspondingCertificates;

        while ((correspondingCertificates = session.findObjects(1)).length > 0) {
            X509PublicKeyCertificate x509PublicKeyCertificate = (X509PublicKeyCertificate) correspondingCertificates[0];
            X509Certificate x509Certificate = new X509Certificate(x509PublicKeyCertificate
                    .getValue().getByteArrayValue());
            boolean[] keyUsage = x509Certificate.getKeyUsage();
            boolean signingSignature = false;
            if (keyUsage != null) {
                signingSignature = keyUsage[0] && keyUsage[1];
            }
            if (signingSignature) {
                certificates.add(x509Certificate);
            }
        }
        session.findObjectsFinal();
        return certificates;
    }

    /**
     * Signs with BES(Basic Electronic Signature) the given content by using
     * given session,key and certificate.
     *
     * @param content Content to be signed
     * @param session must support RSA signing
     * @param keyAndCertificate key and certificate for signing.
     * @return signed content
     * @throws NoSuchAlgorithmException
     * @throws CertificateEncodingException
     * @throws CodingException
     * @throws TokenException
     * @throws PKCSException
     * @throws CertificateException
     */
    public static byte[] BESSign(byte[] content, Session session, KeyAndCertificate keyAndCertificate) throws NoSuchAlgorithmException, CertificateEncodingException, CodingException, TokenException, PKCSException, CertificateException {
        X509Certificate signerCertificate = keyAndCertificate.getCertificate();
        PrivateKey selectedSignatureKey = (PrivateKey) keyAndCertificate.getKey();
        byte[] contentHash = Util.digestSHA256(content);
        // create the SignedData
        SignedData signedData = new SignedData(content,
                SignedData.IMPLICIT);
        // set the certificates
        signedData.setCertificates(new X509Certificate[]{signerCertificate});

        // create a new SignerInfo
        SignerInfo signerInfo = new SignerInfo(new IssuerAndSerialNumber(signerCertificate),
                AlgorithmID.sha256, null);

        SigningCertificateV2 signingCertificate = new SigningCertificateV2(new X509Certificate[]{signerCertificate});

        // define the authenticated attributes
        iaik.asn1.structures.Attribute[] authenticatedAttributes = {
            new Attribute(ObjectID.contentType, new ASN1Object[]{ObjectID.pkcs7_data}),
            new Attribute(ObjectID.signingTime,
            new ASN1Object[]{new ChoiceOfTime().toASN1Object()}),
            new Attribute(ObjectID.messageDigest, new ASN1Object[]{new OCTET_STRING(
                contentHash)}), new Attribute(ObjectID.signingCertificateV2, new ASN1Object[]{signingCertificate.toASN1Object()})};
        // set the authenticated attributes
        signerInfo.setAuthenticatedAttributes(authenticatedAttributes);

        // encode the authenticated attributes, which is the data that we must sign
        byte[] toBeSigned = DerCoder.encode(ASN.createSetOf(authenticatedAttributes, true));

        // we do digesting outside the card, because some cards do not support on-card hashing
        // we can use the digest engine from above
        byte[] hashToBeSigned = Util.digestSHA256(toBeSigned);

        // according to PKCS#11 building the DigestInfo structure must be done off-card
        DigestInfo digestInfoEngine = new DigestInfo(AlgorithmID.sha256, hashToBeSigned);

        byte[] toBeEncrypted = digestInfoEngine.toByteArray();

        // initialize for signing
        session.signInit(Mechanism.get(PKCS11Constants.CKM_RSA_PKCS), selectedSignatureKey);

        // sign the data to be signed
        byte[] signatureValue = session.sign(toBeEncrypted);

        // set the signature value in the signer info
        signerInfo.setEncryptedDigest(signatureValue);

        // and add the signer info object to the PKCS#7 signed data object
        signedData.addSignerInfo(signerInfo);

        ContentInfo info = new ContentInfo(ObjectID.pkcs7_signedData);
        info.setContent(signedData);
        return DerCoder.encode(info.toASN1Object());
    }

    /**
     * verifies the signed content
     *
     * @param signedContent signed content
     * @return SignedData
     * @throws CodingException
     * @throws PKCSParsingException
     * @throws PKCSException
     * @throws CertificateExpiredException
     * @throws CertificateNotYetValidException
     * @throws Exception
     */
    public static SignedData BESVerify(byte[] signedContent) throws CodingException, PKCSParsingException, PKCSException, CertificateExpiredException, CertificateNotYetValidException, Exception {
        ASN1Object decodedObject = DerCoder.decode(signedContent);

        ObjectID pkcs7_signedData = (ObjectID) decodedObject.getComponentAt(0);
        if (pkcs7_signedData.equals(ObjectID.pkcs7_signedData)) {
            CON_SPEC signedBody = (CON_SPEC) decodedObject.getComponentAt(1);
            SignedData signedData = new SignedData(signedBody.getComponentAt(0));
            SignerInfo[] signerInfos = signedData.getSignerInfos();
            // verify the signatures
            Boolean[] res = new Boolean[signerInfos.length];
            for (int i = 0; i < signerInfos.length; i++) {
                try {
                    // verify the signature for SignerInfo at index i
                    X509Certificate signerCertificate = signedData.verify(i);
                    //System.out.println(signerCertificate.getPublicKey());
                    res[i] = true;
                    // if the signature is OK the certificate of the signer is returned

                } catch (SignatureException ex) {
                    // if the signature is not OK a SignatureException is thrown
                    res[i] = false;
                    ex.printStackTrace();
                }
            }

            boolean result = true;
            for (boolean b : res) {
                result = result && b;
            }
            if (result) {
                return signedData;
            }
        }
        return null;
    }
}
