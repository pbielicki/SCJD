package suncertify.db.dao;

/**
 * <code>DaoInitializationException</code> is thrown during concrete DAO
 * instantiation process when any kind of failure occurs.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
public class DaoInitializationException extends DaoException {

    private static final long serialVersionUID = 1L;

    /**
	 * @see DaoException#DaoException()
	 */
	public DaoInitializationException() {
		super();
	}

	/**
	 * @see DaoException#DaoException(String)
	 */
	public DaoInitializationException(String message) {
		super(message);
	}

	/**
	 * @see DaoException#DaoException(String, Throwable)
	 */
	public DaoInitializationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @see DaoException#DaoException(Throwable)
	 */
	public DaoInitializationException(Throwable cause) {
		super(cause);
	}
}
