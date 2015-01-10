package suncertify.db;

/**
 * <code>DBException</code> is thrown when a general database failure occurs
 * and it is also base class for all specific exceptions that can occur during
 * processing any operation on database.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
public class DBException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
	 * @see Exception#Exception()
	 */
	public DBException() {
		super();
	}

	/**
	 * @see Exception#Exception(java.lang.String, java.lang.Throwable)
	 */
	public DBException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @see Exception#Exception(java.lang.String)
	 */
	public DBException(String message) {
		super(message);
	}

	/**
	 * @see Exception#Exception(java.lang.Throwable)
	 */
	public DBException(Throwable cause) {
		super(cause);
	}
}
