/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.gov.turkiye.esign.core;

import iaik.pkcs.pkcs11.TokenException;
import iaik.pkcs.pkcs11.wrapper.CK_ATTRIBUTE;
import iaik.pkcs.pkcs11.wrapper.CK_INFO;
import iaik.pkcs.pkcs11.wrapper.CK_MECHANISM;
import iaik.pkcs.pkcs11.wrapper.CK_SLOT_INFO;
import iaik.pkcs.pkcs11.wrapper.PKCS11Connector;
import iaik.pkcs.pkcs11.wrapper.PKCS11Constants;
import iaik.pkcs.pkcs11.wrapper.PKCS11Exception;
import iaik.pkcs.pkcs11.wrapper.PKCS11Implementation;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAKey;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.asn1.DERUTCTime;
import tr.gov.turkiye.esign.cms.Attribute;
import tr.gov.turkiye.esign.cms.MessageDigest;
import tr.gov.turkiye.esign.cms.ContentInfo;
import tr.gov.turkiye.esign.cms.ObjectID;
import tr.gov.turkiye.esign.cms.SignedData;
import tr.gov.turkiye.esign.cms.SignerInfo;
import tr.gov.turkiye.esign.cms.SigningCertificate;
import tr.gov.turkiye.esign.exception.SmartCardException;

import tr.gov.turkiye.esign.model.KeyAndCertificate;
import tr.gov.turkiye.esign.support.CertificatePolicySupport;
import tr.gov.turkiye.esign.support.ExceptionSupport;

/**
 *
 * @author sercan
 */
public class SmartCard {

    /**
     * Module instance
     */
    private PKCS11Implementation pkcs11Module;

    /**
     * Initialize the module with module name.
     *
     * @param moduleName name of module library.
     * @throws tr.gov.turkiye.esign.exception.SmartCardException
     */
    public SmartCard(String moduleName) throws SmartCardException {
        try {
            pkcs11Module = (PKCS11Implementation) PKCS11Connector.connectToPKCS11Module(moduleName);
            System.out.println(pkcs11Module.C_GetInfo());
        } catch (final IOException e) {
            MessageFormat fm = new MessageFormat(ExceptionSupport.getValue("LibraryInitializationError"));
            throw new SmartCardException(fm.format(new Object[]{moduleName}), e);
        } catch (PKCS11Exception ex) {
            Logger.getLogger(SmartCard.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            pkcs11Module.C_Initialize(null, true);
        } catch (final TokenException e) {
            if (!e.getMessage().contains("0x00000191")
                    && !e.getMessage().contains("CKR_CRYPTOKI_ALREADY_INITIALIZED")) {
                MessageFormat fm = new MessageFormat(ExceptionSupport.getValue("LibraryInitializationError"));
                throw new SmartCardException(fm.format(new Object[]{moduleName}), e);
            }
        }
    }

    /**
     * Initialize the module with module name and pkcswrapper.
     *
     * @param moduleName name of module library.
     * @param pkcsWrapperPath path of pkcs wrapper.
     * @throws tr.gov.turkiye.esign.exception.SmartCardException
     */
    public SmartCard(String moduleName, String pkcsWrapperPath) throws SmartCardException {
        try {
            pkcs11Module = (PKCS11Implementation) PKCS11Connector.connectToPKCS11Module(moduleName, pkcsWrapperPath);
        } catch (final IOException e) {
            MessageFormat fm = new MessageFormat(ExceptionSupport.getValue("LibraryInitializationError"));
            throw new SmartCardException(fm.format(new Object[]{moduleName}), e);
        }
        try {
            pkcs11Module.C_Initialize(null, true);
        } catch (final TokenException e) {
            if (!e.getMessage().contains("0x00000191")
                    && !e.getMessage().contains("CKR_CRYPTOKI_ALREADY_INITIALIZED")) {
                MessageFormat fm = new MessageFormat(ExceptionSupport.getValue("LibraryInitializationError"));
                throw new SmartCardException(fm.format(new Object[]{moduleName}), e);
            }
        }
    }

    /**
     * Gets detailed information about module.
     *
     * @return module info
     * @throws tr.gov.turkiye.esign.exception.SmartCardException
     */
    public CK_INFO getInfo() throws SmartCardException {
        try {
            return pkcs11Module.C_GetInfo();
        } catch (PKCS11Exception ex) {
            throw new SmartCardException("GetInfoException", ex);
        }
    }

    /**
     * Gets slot list in the module with token present or all.
     *
     * @param tokenPresent true for TOKEN_PRESENT or false for ALL_SLOTS
     * @return array of slot list.
     * @throws tr.gov.turkiye.esign.exception.SmartCardException
     */
    public long[] getSlotList(boolean tokenPresent) throws SmartCardException {
        try {
            return pkcs11Module.C_GetSlotList(tokenPresent);
        } catch (PKCS11Exception ex) {
            throw new SmartCardException(ExceptionSupport.getValue("GetSlotListFailure"), ex);
        }
    }

    /**
     * Obtains information about a particular slot in the module.
     *
     * @param slotHandle the ID of the slot
     * @return CK_SLOT_INFO instance
     * @throws tr.gov.turkiye.esign.exception.SmartCardException
     */
    public CK_SLOT_INFO getSLotInfo(long slotHandle) throws SmartCardException {
        try {
            return pkcs11Module.C_GetSlotInfo(slotHandle);
        } catch (PKCS11Exception ex) {
            throw new SmartCardException(ExceptionSupport.getValue("GetSLotInfoExcepiton"), ex);
        }
    }

    /**
     * opens unauthorized session with given token.
     *
     * @param slotId the slot's ID
     * @param rwSession Must be false for read-only sessions or true for
     * read-write sessions.
     * @return the session handle
     * @throws tr.gov.turkiye.esign.exception.SmartCardException
     */
    public long openUnauthorizedSession(long slotId, boolean rwSession) throws SmartCardException {
        long flags = 0L;
        flags |= PKCS11Constants.CKF_SERIAL_SESSION;
        flags |= rwSession ? PKCS11Constants.CKF_RW_SESSION : 0L;
        try {
            return pkcs11Module.C_OpenSession(slotId, flags, null, null);
        } catch (PKCS11Exception ex) {
            throw new SmartCardException(ExceptionSupport.getValue("OpenSessionFailure"), ex);
        }
    }

    /**
     * opens authorized session with given token.
     *
     * @param slotId the slot's ID
     * @param rwSession Must be false for read-only sessions or true for
     * read-write sessions.
     * @param pin pin code of smartcard.
     * @return the session handle
     * @throws tr.gov.turkiye.esign.exception.SmartCardException
     */
    public long openAuthorizedSession(long slotId, boolean rwSession, char[] pin) throws SmartCardException {

        long flags = 0L;
        flags |= PKCS11Constants.CKF_SERIAL_SESSION;
        flags |= rwSession ? PKCS11Constants.CKF_RW_SESSION : 0L;
        long session;
        try {
            session = pkcs11Module.C_OpenSession(slotId, flags, null, null);
        } catch (PKCS11Exception ex) {
            throw new SmartCardException(ExceptionSupport.getValue("OpenSessionFailure"), ex);
        }

        try {
            final long lUserType = PKCS11Constants.CKU_USER;
            pkcs11Module.C_Login(session, lUserType, pin, true);
        } catch (final TokenException e) {
            try {
                pkcs11Module.C_CloseSession(session);
            } catch (final TokenException e1) {
                throw new SmartCardException(ExceptionSupport.getValue("CloseSessionFailure"), e1);
            }
            if (!e.getMessage().contains("0x00000100")
                    && !e.getMessage().contains("CKR_USER_ALREADY_LOGGED_IN")) {
                if (e.getMessage().contains("CKR_PIN_INCORRECT")) {
                    throw new SmartCardException(ExceptionSupport.getValue("PinIncorrect"), e);
                } else if (e.getMessage().contains("CKR_PIN_LOCKE")) {
                    throw new SmartCardException(ExceptionSupport.getValue("PinLocked"), e);
                }

            }
        }
        return session;
    }

    /**
     * Returns the DER encoded certificate identified by the given handle, as
     * read from the token.
     *
     * @param session the session's handle
     * @param certHandle the handleof the certificate on the token.
     * @return the DER encoded certificate, as a byte array.
     * @throws SmartCardException
     */
    private byte[] getDEREncodedCertificate(long session, long certHandle) throws SmartCardException {

        try {
            byte[] certBytes;
            CK_ATTRIBUTE[] template = new CK_ATTRIBUTE[1];
            template[0] = new CK_ATTRIBUTE();
            template[0].type = PKCS11Constants.CKA_VALUE;
            pkcs11Module.C_GetAttributeValue(session, certHandle, template, true);
            certBytes = (byte[]) template[0].pValue;

            return certBytes;
        } catch (PKCS11Exception ex) {
            throw new SmartCardException(ExceptionSupport.getValue("CertificateEncodingException"), ex);
        }
    }

    /**
     * Gets list of signing certificates in the session
     *
     * @param session the session's handle
     * @return list of signing certificates
     * @throws tr.gov.turkiye.esign.exception.SmartCardException
     */
    public List<X509Certificate> getSigningCertificates(long session) throws SmartCardException {

        List<X509Certificate> certificates = new ArrayList<>();

        CK_ATTRIBUTE[] ckAttributes = new CK_ATTRIBUTE[1];

        ckAttributes[0] = new CK_ATTRIBUTE();
        ckAttributes[0].type = PKCS11Constants.CKA_CLASS;
        ckAttributes[0].pValue = PKCS11Constants.CKO_CERTIFICATE;
        try {
            pkcs11Module.C_FindObjectsInit(session, ckAttributes, true);
        } catch (PKCS11Exception ex) {
            throw new SmartCardException(ExceptionSupport.getValue("FindObjectsInitException"), ex);
        }
        long[] correspondingCertificates;

        try {
            while ((correspondingCertificates = pkcs11Module.C_FindObjects(session, 1)).length > 0) {
                long certHandle = correspondingCertificates[0];
                byte[] derEncodedCertificate = getDEREncodedCertificate(session, certHandle);

                CertificateFactory x509CertificateFactory = CertificateFactory.getInstance("X.509");
                java.security.cert.X509Certificate certificate = (java.security.cert.X509Certificate) x509CertificateFactory
                        .generateCertificate(new ByteArrayInputStream(derEncodedCertificate));

                boolean[] keyUsage = certificate.getKeyUsage();
                boolean signingSignature = false;
                if (keyUsage != null) {
                    signingSignature = keyUsage[0] && keyUsage[1];
                }
                if (signingSignature && CertificatePolicySupport.checkCertificatePolicyIdentifier(certificate)) {
                    certificates.add(certificate);
                }
            }
        } catch (PKCS11Exception ex) {
            throw new SmartCardException(ExceptionSupport.getValue("FindObjectsException"), ex);
        } catch (CertificateException ex) {
            throw new SmartCardException(ExceptionSupport.getValue("CertificateException"), ex);
        }
        try {
            pkcs11Module.C_FindObjectsFinal(session);
        } catch (PKCS11Exception ex) {
            throw new SmartCardException(ExceptionSupport.getValue("FindObjectsFinalFailure"), ex);
        }
        return certificates;
    }

    /**
     * Gets list of key and certificates in the session
     *
     * @param session the session's handle
     * @return list of key and certificates
     * @throws tr.gov.turkiye.esign.exception.SmartCardException
     */
    public List<KeyAndCertificate> getSigningKeyAndCertificates(long session) throws SmartCardException {

        List<KeyAndCertificate> keyAndCertificates = new ArrayList<>();

        // holds the first suitable object handle if pickFirstSuitable is set true
        List<Long> keyList = new ArrayList<>();

        CK_ATTRIBUTE[] attributeTemplateList = new CK_ATTRIBUTE[2];

        attributeTemplateList[0] = new CK_ATTRIBUTE();
        attributeTemplateList[0].type = PKCS11Constants.CKA_CLASS;
        attributeTemplateList[0].pValue = PKCS11Constants.CKO_PRIVATE_KEY;
        attributeTemplateList[1] = new CK_ATTRIBUTE();
        attributeTemplateList[1].type = PKCS11Constants.CKA_SIGN;
        attributeTemplateList[1].pValue = PKCS11Constants.TRUE;
        try {
            pkcs11Module.C_FindObjectsInit(session, attributeTemplateList, true);
        } catch (PKCS11Exception ex) {
            throw new SmartCardException(ExceptionSupport.getValue("FindObjectsInitException"), ex);
        }
        long[] matchingKeys;

        try {
            while ((matchingKeys = pkcs11Module.C_FindObjects(session, 1)).length > 0) {
                keyList.add(matchingKeys[0]);
            }
        } catch (PKCS11Exception ex) {
            throw new SmartCardException(ExceptionSupport.getValue("FindObjectsException"), ex);
        }
        try {
            pkcs11Module.C_FindObjectsFinal(session);
        } catch (PKCS11Exception ex) {
            throw new SmartCardException(ExceptionSupport.getValue("FindObjectsFinalFailure"), ex);
        }

        // try to find the corresponding certificates for the signature keys
        for (Long keyHandle : keyList) {
            attributeTemplateList = new CK_ATTRIBUTE[1];
            attributeTemplateList[0] = new CK_ATTRIBUTE();
            attributeTemplateList[0].type = PKCS11Constants.CKA_ID;

            try {
                pkcs11Module.C_GetAttributeValue(session, keyHandle, attributeTemplateList, true);
            } catch (PKCS11Exception ex) {
                throw new SmartCardException(ExceptionSupport.getValue("KeyEncodingException"), ex);
            }
            byte[] keyAndCertificateID = (byte[]) attributeTemplateList[0].pValue;

            // now get the certificate with the same ID as the signature key
            attributeTemplateList = new CK_ATTRIBUTE[2];

            attributeTemplateList[0] = new CK_ATTRIBUTE();
            attributeTemplateList[0].type = PKCS11Constants.CKA_CLASS;
            attributeTemplateList[0].pValue = PKCS11Constants.CKO_CERTIFICATE;
            attributeTemplateList[1] = new CK_ATTRIBUTE();
            attributeTemplateList[1].type = PKCS11Constants.CKA_ID;
            attributeTemplateList[1].pValue = keyAndCertificateID;

            try {
                pkcs11Module.C_FindObjectsInit(session, attributeTemplateList, true);
            } catch (PKCS11Exception ex) {
                throw new SmartCardException(ExceptionSupport.getValue("FindObjectsInitException"), ex);
            }
            long[] certHandles;
            try {
                certHandles = pkcs11Module.C_FindObjects(session, 1);
            } catch (PKCS11Exception ex) {
                throw new SmartCardException(ExceptionSupport.getValue("FindObjectsException"), ex);
            }
            if (certHandles != null && certHandles.length > 0) {
                byte[] derEncodedCertificate = getDEREncodedCertificate(session, certHandles[0]);
                CertificateFactory x509CertificateFactory;
                X509Certificate certificate;
                try {
                    x509CertificateFactory = CertificateFactory.getInstance("X.509");
                    certificate = (X509Certificate) x509CertificateFactory
                            .generateCertificate(new ByteArrayInputStream(derEncodedCertificate));
                } catch (CertificateException ex) {
                    throw new SmartCardException(ExceptionSupport.getValue("CertificateException"), ex);
                }

                boolean[] keyUsage = certificate.getKeyUsage();
                boolean signingSignature = false;
                if (keyUsage != null) {
                    signingSignature = keyUsage[0] && keyUsage[1];
                }
                if (signingSignature && CertificatePolicySupport.checkCertificatePolicyIdentifier(certificate)) {
                    keyAndCertificates.add(new KeyAndCertificate(keyHandle, certificate));
                }
            }

            try {
                pkcs11Module.C_FindObjectsFinal(session);
            } catch (PKCS11Exception ex) {
                throw new SmartCardException(ExceptionSupport.getValue("FindObjectsFinalFailure"), ex);
            }
        }

        return keyAndCertificates;
    }

    /**
     * closes session.
     *
     * @param session the session's handle
     * @throws tr.gov.turkiye.esign.exception.SmartCardException
     */
    public void closeSession(long session) throws SmartCardException {
        try {
            pkcs11Module.C_CloseSession(session);
        } catch (PKCS11Exception ex) {
            throw new SmartCardException(ExceptionSupport.getValue("CloseSessionFailure"), ex);
        }
    }

    /**
     * signs (encrypts with private key) data in a single part, where the
     * signature is (will be) an appendix to the data, and plaintext cannot be
     * recovered from the signature.
     *
     * @param session the session's handle
     * @param key the handle of the signature key
     * @param contentData the data to sign
     * @return signed content
     * @throws tr.gov.turkiye.esign.exception.SmartCardException
     */
    public byte[] signData(long session, long key, byte[] contentData) throws SmartCardException {
        try {
            
            final CK_MECHANISM ckMechanism = new CK_MECHANISM();
            ckMechanism.mechanism = PKCS11Constants.CKM_RSA_PKCS;
            pkcs11Module.C_SignInit(session, ckMechanism, key, true);            
            return pkcs11Module.C_Sign(session, contentData);
        } catch (TokenException e) {
            throw new SmartCardException(ExceptionSupport.getValue("SignUpdateFailure"), e);
        }
    }

    /**
     * Called by the garbage collector on an object when garbage collection
     * determines that there are no more references to the object. A subclass
     * overrides the finalize method to dispose of system resources or to
     * perform other cleanup.
     *
     * @throws tr.gov.turkiye.esign.exception.SmartCardException
     */
    public void finalizeModule() throws SmartCardException {
        try {
            pkcs11Module.C_Finalize(null);
        } catch (PKCS11Exception ex) {
            throw new SmartCardException(ExceptionSupport.getValue("FinalizeException"), ex);
        }
    }

    /**
     * Gets list of signing certificates in the module.
     *
     * @return list of signing certificates.
     * @throws tr.gov.turkiye.esign.exception.SmartCardException
     */
    public List<X509Certificate> getCertificateList() throws SmartCardException {
        List<X509Certificate> certificates = new ArrayList<>();
        long[] slotList = getSlotList(true);
        for (long slot_handle : slotList) {
            long session_handle = openUnauthorizedSession(slot_handle, false);
            List<X509Certificate> signingCertificates = getSigningCertificates(session_handle);
            certificates.addAll(signingCertificates);
            closeSession(session_handle);
        }
        return certificates;
    }

    /**
     *
     * @param session_handle the session's handle
     * @param content content to be signed
     * @param certIndex selected certificate index
     * @return BES signed content
     * @throws SmartCardException
     */
    private byte[] BESSign(long session_handle, byte[] content, int certIndex) throws SmartCardException {
        List<Certificate> certList = new ArrayList<>();
        List<KeyAndCertificate> signingKeyAndCertificates = getSigningKeyAndCertificates(session_handle);
        X509Certificate certificate = signingKeyAndCertificates.get(certIndex).getCertificate();
        certList.add(certificate);
        RSAKey rsaKey = (RSAKey) (certificate.getPublicKey());
        int keySize = rsaKey.getModulus().bitLength();
        boolean isSHA256 = (keySize == 2048);

        SignedData signedData = new SignedData(content, isSHA256);
        signedData.setCerts(certList);

        SignerInfo signerInfo = new SignerInfo(certificate, isSHA256);

        List<Attribute> attr = new ArrayList<>();
        attr.add(new Attribute(ObjectID.content_type, ObjectID.pkcs7_data));
        MessageDigest contentDigest = new MessageDigest(content, isSHA256);
        attr.add(new Attribute(ObjectID.message_digest, contentDigest.toASN1Object()));

        SigningCertificate signerCert = new SigningCertificate(certList, isSHA256);
        if (isSHA256) {
            attr.add(new Attribute(ObjectID.signing_certificate_v2, signerCert.toASN1Object()));
        } else {
            attr.add(new Attribute(ObjectID.signing_certificate, signerCert.toASN1Object()));
        }

        attr.add(new Attribute(ObjectID.signingTime, new DERUTCTime(new Date())));

        signerInfo.setAuthenticatedAttributes(attr);

        byte[] encodedAuthenticatedAttributes = signerInfo.getEncodedAuthenticatedAttributes();

        byte[] signedValue = signData(session_handle, signingKeyAndCertificates.get(certIndex).getKeyHandle(), encodedAuthenticatedAttributes);

        signerInfo.setEncryptedDigest(signedValue);

        signedData.addSignerInfo(signerInfo);

        ContentInfo contentInfo = new ContentInfo(ObjectID.pkcs7_signedData, signedData);
        byte[] encoded = contentInfo.getEncoded();

        return encoded;

    }

    /**
     * Sign given content by certificate in given index location.
     *
     * @param content Content to be signed
     * @param pin Password of certificate
     * @param certIndex Index of certificate
     * @return signed Content
     * @throws tr.gov.turkiye.esign.exception.SmartCardException
     */
    public byte[] signContent(byte[] content, char[] pin, int certIndex) throws SmartCardException {
        long[] slotList = getSlotList(true);
        for (long s : slotList) {
            long session_handle = openAuthorizedSession(s, false, pin);
            byte[] signContent = BESSign(session_handle, content, certIndex);
            closeSession(session_handle);
            return signContent;
        }
        return null;
    }

    public static String readFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                //sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }


}
