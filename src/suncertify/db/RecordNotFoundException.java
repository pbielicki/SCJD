package suncertify.db;

/**
 * <code>RecordNotFoundException</code> is thrown while attempting to process
 * (any CRUD operation) record that does not exist in database.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
public class RecordNotFoundException extends DBException {

    private static final long serialVersionUID = 1L;

    /**
	 * @see DBException#DBException(String, Throwable)
	 */
	public RecordNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @see DBException#DBException(Throwable)
	 */
	public RecordNotFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * @see DBException#DBException()
	 */
	public RecordNotFoundException() {
		super();
	}

	/**
	 * @see DBException#DBException(java.lang.String)
	 */
	public RecordNotFoundException(String message) {
		super(message);
	}
}
