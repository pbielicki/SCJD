package suncertify.db.dao;

/**
 * <code>DaoStaleObjectException</code> is thrown when user wants to perform
 * DAO operation on the stale data (e.g. user wants to update the record no
 * longer exists).
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
public class DaoStaleObjectException extends DaoException {

    private static final long serialVersionUID = 1L;

    /**
	 * @see DaoException#DaoException()
	 */
	public DaoStaleObjectException() {
		super();
	}

	/**
	 * @see DaoException#DaoException(String)
	 */
	public DaoStaleObjectException(String message) {
		super(message);
	}

	/**
	 * @see DaoException#DaoException(String, Throwable)
	 */
	public DaoStaleObjectException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @see DaoException#DaoException(Throwable)
	 */
	public DaoStaleObjectException(Throwable cause) {
		super(cause);
	}
}
