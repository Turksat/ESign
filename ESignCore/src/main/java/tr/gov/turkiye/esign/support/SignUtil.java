///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package tr.gov.turkiye.esign.util;
//
//import iaik.pkcs.pkcs11.Session;
//import iaik.pkcs.pkcs11.TokenException;
//import iaik.pkcs.pkcs11.objects.PrivateKey;
//import java.security.NoSuchAlgorithmException;
//import java.security.SignatureException;
//import java.security.cert.CertificateEncodingException;
//import java.security.cert.CertificateException;
//import java.security.cert.CertificateExpiredException;
//import java.security.cert.CertificateNotYetValidException;
//import java.security.cert.X509Certificate;
//import tr.gov.turkiye.esign.model.KeyAndCertificate;
//
///**
// *
// * @author sercan
// */
//public class SignUtil {
//
//    
//    
//
//    /**
//     * Signs with BES(Basic Electronic Signature) the given content by using
//     * given session,key and certificate.
//     *
//     * @param content Content to be signed
//     * @param session must support RSA signing
//     * @param keyAndCertificate key and certificate for signing.
//     * @return signed content
//     * @throws NoSuchAlgorithmException
//     * @throws CertificateEncodingException
//     * @throws CodingException
//     * @throws TokenException
//     * @throws PKCSException
//     * @throws CertificateException
//     */
//    public static byte[] BESSign(byte[] content, Session session, KeyAndCertificate keyAndCertificate) throws NoSuchAlgorithmException, CertificateEncodingException, CodingException, TokenException, PKCSException, CertificateException {
//        X509Certificate signerCertificate = keyAndCertificate.getCertificate();
//        PrivateKey selectedSignatureKey = (PrivateKey) keyAndCertificate.getKey();
//        byte[] contentHash = Util.digestSHA256(content);
//        // create the SignedData
//        SignedData signedData = new SignedData(content,
//                SignedData.IMPLICIT);
//        // set the certificates
//        signedData.setCertificates(new X509Certificate[]{signerCertificate});
//
//        // create a new SignerInfo
//        SignerInfo signerInfo = new SignerInfo(new IssuerAndSerialNumber(signerCertificate),
//                AlgorithmID.sha256, null);
//
//        SigningCertificateV2 signingCertificate = new SigningCertificateV2(new X509Certificate[]{signerCertificate});
//
//        // define the authenticated attributes
//        iaik.asn1.structures.Attribute[] authenticatedAttributes = {
//            new Attribute(ObjectID.contentType, new ASN1Object[]{ObjectID.pkcs7_data}),
//            new Attribute(ObjectID.signingTime,
//            new ASN1Object[]{new ChoiceOfTime().toASN1Object()}),
//            new Attribute(ObjectID.messageDigest, new ASN1Object[]{new OCTET_STRING(
//                contentHash)}), new Attribute(ObjectID.signingCertificateV2, new ASN1Object[]{signingCertificate.toASN1Object()})};
//        // set the authenticated attributes
//        signerInfo.setAuthenticatedAttributes(authenticatedAttributes);
//
//        // encode the authenticated attributes, which is the data that we must sign
//        byte[] toBeSigned = DerCoder.encode(ASN.createSetOf(authenticatedAttributes, true));
//
//        // we do digesting outside the card, because some cards do not support on-card hashing
//        // we can use the digest engine from above
//        byte[] hashToBeSigned = Util.digestSHA256(toBeSigned);
//
//        // according to PKCS#11 building the DigestInfo structure must be done off-card
//        DigestInfo digestInfoEngine = new DigestInfo(AlgorithmID.sha256, hashToBeSigned);
//
//        byte[] toBeEncrypted = digestInfoEngine.toByteArray();
//
//        // initialize for signing
//        session.signInit(Mechanism.get(PKCS11Constants.CKM_RSA_PKCS), selectedSignatureKey);
//
//        // sign the data to be signed
//        byte[] signatureValue = session.sign(toBeEncrypted);
//
//        // set the signature value in the signer info
//        signerInfo.setEncryptedDigest(signatureValue);
//
//        // and add the signer info object to the PKCS#7 signed data object
//        signedData.addSignerInfo(signerInfo);
//
//        ContentInfo info = new ContentInfo(ObjectID.pkcs7_signedData);
//        info.setContent(signedData);
//        return DerCoder.encode(info.toASN1Object());
//    }
//
//    /**
//     * verifies the signed content
//     *
//     * @param signedContent signed content
//     * @return SignedData
//     * @throws CodingException
//     * @throws PKCSParsingException
//     * @throws PKCSException
//     * @throws CertificateExpiredException
//     * @throws CertificateNotYetValidException
//     * @throws Exception
//     */
//    public static SignedData BESVerify(byte[] signedContent) throws CodingException, PKCSParsingException, PKCSException, CertificateExpiredException, CertificateNotYetValidException, Exception {
//        ASN1Object decodedObject = DerCoder.decode(signedContent);
//
//        ObjectID pkcs7_signedData = (ObjectID) decodedObject.getComponentAt(0);
//        if (pkcs7_signedData.equals(ObjectID.pkcs7_signedData)) {
//            CON_SPEC signedBody = (CON_SPEC) decodedObject.getComponentAt(1);
//            SignedData signedData = new SignedData(signedBody.getComponentAt(0));
//            SignerInfo[] signerInfos = signedData.getSignerInfos();
//            // verify the signatures
//            Boolean[] res = new Boolean[signerInfos.length];
//            for (int i = 0; i < signerInfos.length; i++) {
//                try {
//                    // verify the signature for SignerInfo at index i
//                    X509Certificate signerCertificate = signedData.verify(i);
//                    //System.out.println(signerCertificate.getPublicKey());
//                    res[i] = true;
//                    // if the signature is OK the certificate of the signer is returned
//
//                } catch (SignatureException ex) {
//                    // if the signature is not OK a SignatureException is thrown
//                    res[i] = false;
//                    ex.printStackTrace();
//                }
//            }
//
//            boolean result = true;
//            for (boolean b : res) {
//                result = result && b;
//            }
//            if (result) {
//                return signedData;
//            }
//        }
//        return null;
//    }
//}
