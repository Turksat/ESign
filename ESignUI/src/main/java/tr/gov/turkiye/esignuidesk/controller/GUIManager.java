/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.gov.turkiye.esignuidesk.controller;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.RootPaneContainer;
import tr.gov.turkiye.esignuidesk.support.GUIHelper;
import tr.gov.turkiye.esignuidesk.view.container.MainApplet;
import tr.gov.turkiye.esignuidesk.view.container.MainFrame;
import tr.gov.turkiye.esignuidesk.view.pane.CardSelectScreen;


/**
 *
 * @author iakpolat
 */
public class GUIManager {
    public static RootPaneContainer mainContainer;
    public static JPanel curPane;
    
    /**
     * Removes old screen with given pane.
     * 
     * @param pane
     */
    public static void changeScreen(JPanel pane) {
        mainContainer.getContentPane().remove(curPane);
        curPane = pane;
        mainContainer.getContentPane().add(pane);
        mainContainer.getContentPane().repaint();
        GUIHelper.requestFocus(pane);
    }
    
    /**
     * Searches pane with given name inside current panel. 
     * If pane is found, replaces old pane with new pane.
     * 
     * @param oldPaneName
     * @param newPane 
     */
    public static void replacePane(String oldPaneName, JPanel newPane) {
        Component[] components = curPane.getComponents();
        JPanel oldPane = null;
        for(Component comp: components) {
            if(comp.getName()!=null&&comp.getName().equals(oldPaneName)&&comp instanceof JPanel) {
                oldPane = (JPanel) comp;
                break;
            }
        }
        if(oldPane==null) {
            GUIManager.showErrMsg("Component "+oldPaneName+ " not found");
        } else {
            newPane.setLocation(oldPane.getX(),oldPane.getY());
            newPane.setSize(oldPane.getWidth(),oldPane.getHeight());
            curPane.remove(oldPane);
            curPane.add(newPane);
            curPane.repaint();
            GUIHelper.requestFocus(newPane);
        }
    }
    
    /**
     * Initialize main container as RootPaneContainer(such as japplet or jframe)
     * @param container
     */
    public static void init(RootPaneContainer container) {
        mainContainer = container;
        curPane = new CardSelectScreen();
        mainContainer.getContentPane().add(curPane);
        ((Container)mainContainer).setVisible(true);
    }
    
    /**
     * Destroys main container.
     */
    public static void destroy() {
        if(mainContainer instanceof Window) {
            ((Window)mainContainer).dispose();
        } else {
            //do nothing
        }
    }
    
    /**
     * Shows error message to user.
     * @param msg 
     */
    public static void showErrMsg(String msg) {
        JOptionPane.showMessageDialog((Container)mainContainer, msg);
    }
    
}
