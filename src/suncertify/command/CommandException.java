package suncertify.command;

/**
 * <code>CommandException</code> is thrown when user passes unsupported mode
 * flag when running application.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 * @see suncertify.core.Contractors
 */
public class CommandException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
	 * @see Exception#Exception()
	 */
	public CommandException() {
		super();
	}

	/**
	 * @see Exception#Exception(java.lang.String)
	 */
	public CommandException(String message) {
		super(message);
	}

	/**
	 * @see Exception#Exception(java.lang.String, java.lang.Throwable)
	 */
	public CommandException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @see Exception#Exception(java.lang.Throwable)
	 */
	public CommandException(Throwable cause) {
		super(cause);
	}
}
