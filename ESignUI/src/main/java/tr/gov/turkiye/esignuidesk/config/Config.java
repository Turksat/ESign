/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.gov.turkiye.esignuidesk.config;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Locale;

/**
 *
 * @author iakpolat
 */
public class Config {
    //Main frame configuration
    public static final int FRAME_WIDTH = 530;
    public static final int FRAME_HEIGHT = 275;
    public static final int FRAME_STARTING_X = 30;
    public static final int FRAME_STARTING_Y = 30;
    
    //Main panel configuration
    public static final int DEF_PANEL_WIDTH = FRAME_WIDTH;
    public static final int DEF_PANEL_HEIGHT = FRAME_HEIGHT-25;
    public static final int DEF_PANEL_STARTING_X = 0;
    public static final int DEF_PANEL_STARTING_Y = 0;
    
    public static final Locale locale = new Locale("tr","TR");
//    public static final Locale locale = new Locale("en","EN");    
    
    //Screen ids are used on logic manager to interpret where requests are coming from.
    public static final int CARD_SELECT_SCREEN_ID = 1;
    public static final int CERT_SHOW_SCREEN_ID = 2;
    public static final int PIN_PANE_ID = 3;
    public static final int DONE_PANE_ID = 4;
    
    public static final int DEBUG_TYPE = 1; //0=do nothing,1=print stack trace to console,2=print localized message to console, 3=send to server etc.
    
    public static final String certInfoPaneName = "certInfoPane";
    public static final String certPaneName = "certPane";
    public static final String pinPaneName = "pinPane";
    
    public static final Point pinBtnStartPoint = new Point(20, 60);
    public static final Dimension numberBtnSize = new Dimension(40,40);
    
    public static final String defaultFileName = "signed_document_";
    
    public static final String dateSFormat = "dd.MM.yyyy";
    public static final String timeSFormat = "dd-MM-yyyy_hh_mm_ss";
}
