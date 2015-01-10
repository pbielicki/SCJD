package suncertify.db;

/**
 * <code>DuplicateKeyException</code> is thrown while attempting to add or
 * modify database record whoose data are not unique.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
public class DuplicateKeyException extends DBException {

    private static final long serialVersionUID = 1L;

    /**
	 * @see DBException#DBException(String, Throwable)
	 */
	public DuplicateKeyException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @see DBException#DBException(Throwable)
	 */
	public DuplicateKeyException(Throwable cause) {
		super(cause);
	}

	/**
	 * @see DBException#DBException()
	 */
	public DuplicateKeyException() {
		super();
	}

	/**
	 * @see DBException#DBException(java.lang.String)
	 */
	public DuplicateKeyException(String message) {
		super(message);
	}
}
