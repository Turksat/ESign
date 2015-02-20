package tr.gov.turkiye.esign.exception;

/**
 * 
 * This exception is thrown when an error occurred in any of the SmartCard class method.
 * 
 * @author iakpolat
 */
public class SmartCardException extends Exception {

    public SmartCardException(String s, Throwable t) {
        super(s, t);
    }
}
