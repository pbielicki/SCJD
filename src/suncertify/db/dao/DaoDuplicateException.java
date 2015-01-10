package suncertify.db.dao;

/**
 * <code>DaoDuplicateException</code> is thrown when user tries to
 * store/update record(s) with not unique data (i.e. record with the same data
 * already exists in database).
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
public class DaoDuplicateException extends DaoException {

    private static final long serialVersionUID = 1L;

    /**
	 * @see DaoException#DaoException()
	 */
	public DaoDuplicateException() {
		super();
	}

	/**
	 * @see DaoException#DaoException(String)
	 */
	public DaoDuplicateException(String message) {
		super(message);
	}

	/**
	 * @see DaoException#DaoException(String, Throwable)
	 */
	public DaoDuplicateException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @see DaoException#DaoException(Throwable)
	 */
	public DaoDuplicateException(Throwable cause) {
		super(cause);
	}
}
