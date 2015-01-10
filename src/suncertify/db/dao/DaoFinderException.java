package suncertify.db.dao;

/**
 * <code>DaoFinderException</code> is thrown when <i>finder</i>/<i>loader</i>
 * methods could not find required objects in the persistent layer.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
public class DaoFinderException extends DaoException {

    private static final long serialVersionUID = 1L;
    
    /**
	 * @see DaoException#DaoException()
	 */
	public DaoFinderException() {
		super();
	}

	/**
	 * @see DaoException#DaoException(String)
	 */
	public DaoFinderException(String message) {
		super(message);
	}

	/**
	 * @see DaoException#DaoException(String, Throwable)
	 */
	public DaoFinderException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @see DaoException#DaoException(Throwable)
	 */
	public DaoFinderException(Throwable cause) {
		super(cause);
	}
}
