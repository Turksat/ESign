/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.gov.turkiye.esignuidesk.run;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import tr.gov.turkiye.esignuidesk.config.Config;
import tr.gov.turkiye.esignuidesk.view.container.MainApplet;

/**
 *
 * This class is used to test whether applet could start successfully or not.
 * 
 * @author iakpolat
 */
public class StartApplet {
    
    private static MainApplet applet;
    
    public static void init(MainApplet app) {
        applet = app;
    }
    
    public static void run() {
        JFrame frame = new JFrame();
        frame.setSize(Config.FRAME_WIDTH, Config.FRAME_HEIGHT);
        frame.setTitle("Applet Test");
        frame.setLayout(null);
        frame.getContentPane().add(applet);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                applet.stop();
                applet.destroy();
                System.exit(0);
            }
        });
        applet.init();
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        init(new MainApplet());
        run();
    }
}
