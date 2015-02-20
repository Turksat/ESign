/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.gov.turkiye.esign.core;

import iaik.pkcs.pkcs11.Info;
import iaik.pkcs.pkcs11.Module;
import iaik.pkcs.pkcs11.Session;
import iaik.pkcs.pkcs11.Slot;
import iaik.pkcs.pkcs11.Token;
import iaik.pkcs.pkcs11.TokenException;
import iaik.x509.X509Certificate;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import tr.gov.turkiye.esign.model.KeyAndCertificate;
import tr.gov.turkiye.esign.util.SignUtil;

/**
 *
 * @author sercan
 */
public class SmartCard {

    /**
     * Module instance
     */
    private final Module pkcs11Module;

    /**
     * Initialize the module with module name.
     *
     * @param moduleName name of module library.
     * @throws IOException
     * @throws TokenException
     */
    public SmartCard(String moduleName) throws IOException, TokenException {
        pkcs11Module = Module.getInstance(moduleName);
        pkcs11Module.initialize(null);
    }

    /**
     * Gets detailed information about module.
     * @return module info
     * @throws TokenException
     */
    public Info getInfo() throws TokenException {
        return pkcs11Module.getInfo();
    }

    /**
     * Gets slot list in the module with token present or all.
     * @param tokenPresent Module.SlotRequirement.TOKEN_PRESENT or Module.SlotRequirement.ALL_SLOTS
     * @return array of slot list.
     * @throws TokenException
     */
    public Slot[] getSlotList(boolean tokenPresent) throws TokenException {
        return pkcs11Module.getSlotList(tokenPresent);
    }

    /**
     * Called by the garbage collector on an object when garbage collection
     * determines that there are no more references to the object. A subclass
     * overrides the finalize method to dispose of system resources or to
     * perform other cleanup.
     *
     * @throws TokenException
     */
    public void finalizeModule() throws TokenException {
        pkcs11Module.finalize(null);
    }

    /**
     * Gets list of signing certificates in the module.
     * 
     * @return list of signing certificates.
     * @throws TokenException
     * @throws IOException
     * @throws CertificateException
     */
    public List<X509Certificate> getCertificateList() throws TokenException, IOException, CertificateException {
        List<X509Certificate> certificates = new ArrayList<>();
        Slot[] slotList = pkcs11Module.getSlotList(Module.SlotRequirement.TOKEN_PRESENT);
        for (Slot slot : slotList) {
            Token token = SignUtil.getToken(slot);
            Session session = SignUtil.openUnauthorizedSession(token, false);
            List<X509Certificate> signingCertificates = SignUtil.getSigningCertificates(session);
            certificates.addAll(signingCertificates);
            SignUtil.closeSession(session);
        }
        return certificates;
    }

    /**
     * Sign given content by certificate in given index location.
     *
     * @param content Content to be signed
     * @param pin Password of certificate
     * @param certIndex Index of certificate
     * @return
     * @throws Exception
     */
    public byte[] signContent(byte[] content, char[] pin, int certIndex) throws Exception {
        Slot[] slotList = pkcs11Module.getSlotList(Module.SlotRequirement.TOKEN_PRESENT);
        for (Slot s : slotList) {
            Token token = SignUtil.getToken(s);
            Session session = SignUtil.openAuthorizedSession(token, false, pin);
            List<KeyAndCertificate> signingKeyAndCertificates = SignUtil.getSigningKeyAndCertificates(session);
            byte[] signContent = SignUtil.BESSign(content, session, signingKeyAndCertificates.get(certIndex));
            SignUtil.closeSession(session);
            return signContent;
        }
        return null;
    }

}
