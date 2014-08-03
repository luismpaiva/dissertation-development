package seks.basic.exceptions;

/**
 * Exception thrown when there is one or more parameters missing from a XML 
 * configuration file.
 * 
 * @author Paulo Figueiras
 */
public class MissingParamException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Class constructor.
     * 
     * @param msg   Error message to be logged
     */
    public MissingParamException(String msg) {
        super(msg) ;
    }

}
