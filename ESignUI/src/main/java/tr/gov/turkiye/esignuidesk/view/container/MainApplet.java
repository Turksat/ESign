/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.gov.turkiye.esignuidesk.view.container;

import javax.swing.JApplet;
import javax.swing.JPanel;
import tr.gov.turkiye.esignuidesk.controller.GUIManager;

/**
 *
 * @author iakpolat
 */
public class MainApplet extends JApplet {

    private JPanel curPane;
    
    /**
     * Initialization method that will be called after the applet is loaded into
     * the browser.
     */
    @Override
    public void init() {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                setLayout(null);
                setBounds(0,0,530,250);
                GUIManager.init(MainApplet.this);
                setVisible(true);
                
            }
        });
        // TODO start asynchronous download of heavy resources
        
    }

    // TODO overwrite start(), stop() and destroy() methods

    public JPanel getCurPane() {
        return curPane;
    }

    public void setCurPane(JPanel curPane) {
        this.curPane = curPane;
    }
    
    public void dispose() {
        //do nothing
    }
}
