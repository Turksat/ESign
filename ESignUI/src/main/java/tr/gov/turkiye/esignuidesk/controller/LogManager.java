/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.gov.turkiye.esignuidesk.controller;

import tr.gov.turkiye.esignuidesk.config.Config;

/**
 *
 * This class should be used to handle log messages.
 * As method of this class could be used as a skeleton structure, it can also be used directly.
 * 
 * @author iakpolat
 */
public class LogManager {
    /**
     * Handles error and shows message pane if desired. 
     * In addition message of message pane can be set if msg parameter is given.
     * If its null then throwable message is used.
     * 
     * @param e
     * @param showMsgPane 
     * @param msg 
     */
    public static void handleError(Throwable e, boolean showMsgPane, String msg) {
        if(e!=null)
            catchErrMsg(e);
        if(showMsgPane) {
            if(msg!=null)
                GUIManager.showErrMsg(msg);
            else
                if(e!=null)
                    GUIManager.showErrMsg(e.getMessage());
        }
    }
    
    /**
     * Catches exception and treats according to developer's wish.
     * @param e 
     */
    private static void catchErrMsg(Throwable e) {
        if(Config.DEBUG_TYPE==1) {
            printConsoleErrMsg(e);
        } else if(Config.DEBUG_TYPE==2){
            printConsoleErrMsg(e.getLocalizedMessage());
        } else if(Config.DEBUG_TYPE==3) {
            //Should be handled if wanna be used
        }
    }
    
    /**
     * Print stack trace to console.
     * @param e 
     */
    private static void printConsoleErrMsg(Throwable e) {
        e.printStackTrace();
    }
    
    /**
     * Prints error message to console.
     * @param msg 
     */
    private static void printConsoleErrMsg(String msg) {
        System.out.println(msg);
    }
}
