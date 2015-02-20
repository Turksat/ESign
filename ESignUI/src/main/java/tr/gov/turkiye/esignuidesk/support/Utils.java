/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.gov.turkiye.esignuidesk.support;

import java.awt.Component;
import java.awt.Point;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JButton;
import tr.gov.turkiye.esignuidesk.config.Config;
import tr.gov.turkiye.esignuidesk.controller.LogManager;

/**
 *
 * @author iakpolat
 */
public class Utils {
    
    /**
     * Takes number from button name.
     * @param btn
     * @return 
     */
    public static int getButtonNumber(JButton btn) {
        int value = -1;
        String svalue = btn.getText();
        try {
            value = Integer.parseInt(svalue);
        } catch(NumberFormatException nfe) {
        }
        
        return value;
    }

    /**
     * Puts component to different positions.
     * 
     * @param list ArrayList(Component)
     * @param colCount Max how many columns there will be on a row.
     * @param x Starting x position
     * @param y Starting y position
     * @param width Width of component
     * @param height Height of component
     */
    public static void randomizePositions(ArrayList list, int colCount, int x, int y, int width, int height) {
        ArrayList<Component> tmpList;
        try {
            tmpList = (ArrayList<Component>) list.clone();
        } catch(Exception e) { //If a cast error is occurred
            LogManager.handleError(e, true, null);
            return;
        }
        
        Point point = new Point(x, y-height);
        for(int i=list.size();i>0;i--) {
            int ind = (int)(Math.random()*i);
            if((i-1)%colCount==0) {
                point.setLocation(x, point.y+height);
            } else {
                point.setLocation(point.x+width, point.y);
            }
            tmpList.get(ind).setLocation(point);
            tmpList.remove(ind);
        }
    }
    
    /**
     * Sets component locations in given list order.
     * @param list
     * @param colCount
     * @param x
     * @param y
     * @param width
     * @param height 
     */
    public static void setComponentPositions(ArrayList list, int colCount, int x, int y, int width, int height) {
        Point point = new Point(x, y-height);
        for(int i=0; i<list.size(); i++) {
            if(list.get(i) instanceof Component) {
                if((i)%colCount==0) {
                    point.setLocation(x, point.y+height);
                } else {
                    point.setLocation(point.x+width, point.y);
                }
                ((Component)list.get(i)).setLocation(point);
            } else {
                System.out.println("Object must extends component.");
                return;
            }
        }
    }
    
    /**
     * Formats date in default format of program.
     * @param date
     * @return 
     */
    public static String formatDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(Config.dateSFormat);
        return format.format(date);
    }
    
    /**
     * Format date in specified time format.
     * @param date
     * @return 
     */
    public static String formatTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(Config.timeSFormat);
        return format.format(date);
    }
    
    /**
     * Return current time in default date format.
     * @return 
     */
    public static String getCurrentDate() {
        return formatDate(new Date(System.currentTimeMillis()));
    }
}
