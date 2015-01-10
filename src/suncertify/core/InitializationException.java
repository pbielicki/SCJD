package suncertify.core;

/**
 * <code>InitializationException</code> is thrown during database helper
 * initialization in case of any IO failure (e.g. database file does not exist;
 * cannot read to read-only file, etc.).
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
public class InitializationException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
	 * @see Exception#Exception()
	 */
	public InitializationException() {
		super();
	}

	/**
	 * @see Exception#Exception(java.lang.String)
	 */
	public InitializationException(String message) {
		super(message);
	}

	/**
	 * @see Exception#Exception(java.lang.String, java.lang.Throwable)
	 */
	public InitializationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @see Exception#Exception(java.lang.Throwable)
	 */
	public InitializationException(Throwable cause) {
		super(cause);
	}
}
