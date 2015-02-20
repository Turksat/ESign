/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.gov.turkiye.esignuidesk.data;

import iaik.x509.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import tr.gov.turkiye.esignuidesk.props.ScreenProperties;

/**
 * Holds user data during runtime.
 * 
 * @author iakpolat
 */
public class UserData {
    /**
     * User certificates in card
     */
    public static List<X509Certificate> certs = new ArrayList<>();
    /**
     * Selected card type
     */
    public static String slcCardType = null;
    /**
     * Selected certificate index
     */
    public static int slcCertIndex = 0;
    /**
     * Pin password
     */
    public static char[] pinPass;
    /**
     * User agreement data different to each user since it contains time.
     */
    public static String userAgreement = ScreenProperties.getValue("data_to_be_signed");
    /**
     * Signed user agreement data different to each user since it contains time.
     */
    public static byte[] signedUserAgreement;
}
