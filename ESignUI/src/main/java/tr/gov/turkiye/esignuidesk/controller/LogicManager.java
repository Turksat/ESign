/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.gov.turkiye.esignuidesk.controller;

import iaik.pkcs.pkcs11.TokenException;
import iaik.pkcs.pkcs11.wrapper.PKCS11Exception;
import java.util.logging.Level;
import java.util.logging.Logger;
import tr.gov.turkiye.esign.core.SmartCard;
import tr.gov.turkiye.esign.manager.LibraryManager;
import tr.gov.turkiye.esign.statics.Modules;
import tr.gov.turkiye.esignuidesk.config.Config;
import tr.gov.turkiye.esignuidesk.data.UserData;
import tr.gov.turkiye.esignuidesk.view.pane.CardSelectScreen;
import tr.gov.turkiye.esignuidesk.view.pane.CertificateShowScreen;
import tr.gov.turkiye.esignuidesk.view.pane.DonePane;
import tr.gov.turkiye.esignuidesk.view.pane.PinPane;

/**
 *
 * @author iakpolat
 */
public class LogicManager {
    public static SmartCard smartCard;
    
    /**
     * Handles screen termination and transition.
     * 
     * @param screenId 
     */
    public static void done(int screenId) {
        if(screenId==Config.CARD_SELECT_SCREEN_ID) { //goto screen 2
            try {
                LibraryManager.installAndLoadPKCS11Wrapper();
                smartCard = new SmartCard(Modules.getModuleLibraryName(UserData.slcCardType));
                UserData.certs = smartCard.getCertificateList();
                GUIManager.changeScreen(new CertificateShowScreen());
            } catch (final Throwable t) {
                LogManager.handleError(t, true, t.getMessage());
            }
        } else if(screenId==Config.CERT_SHOW_SCREEN_ID) { //replace certifacete show screen right pane with pin pane
            GUIManager.replacePane(Config.certPaneName, new PinPane());
        } else if(screenId==Config.PIN_PANE_ID) { //goto last screen
            try {
                /**
                 * Sign user agreement by selected certificate.
                 */
                UserData.signedUserAgreement = smartCard.signContent(UserData.userAgreement.getBytes(), UserData.pinPass, UserData.slcCertIndex);
                UserData.pinPass = null;
                GUIManager.changeScreen(new DonePane());
            } catch(Throwable err) {
                if(err instanceof PKCS11Exception) {
                    if(err.getMessage().equals("CKR_PIN_INCORRECT")) {
                        GUIManager.showErrMsg("Wrong Password");
                    } else {
                        LogManager.handleError(err, true, null);
                    }
                } else {
                    LogManager.handleError(err, true, null);
                }
            }
        } else if(screenId==Config.DONE_PANE_ID) { //close app
            GUIManager.destroy();
        }
    }
    
    /**
     * Handles cancel operations.
     * 
     * @param screenId 
     */
    public static void cancel(int screenId) {
        if(screenId==Config.CERT_SHOW_SCREEN_ID) { 
            try {
                //goto screen 1
                LogicManager.smartCard.finalizeModule();
                GUIManager.changeScreen(new CardSelectScreen());
            } catch (TokenException ex) {
                Logger.getLogger(LogicManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if(screenId==Config.PIN_PANE_ID) { //goto screen 2
            GUIManager.changeScreen(new CertificateShowScreen());
        } else if(screenId==Config.DONE_PANE_ID) {
//            GUIManager.changeScreen(new PinPane());
        }
        
    }
}
