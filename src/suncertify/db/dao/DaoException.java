package suncertify.db.dao;

/**
 * <code>DaoException</code> is thrown when general failure connected with DAO
 * operations occurs and is also base class for all specific exceptions that can
 * occur during DAO proceedings.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
public class DaoException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
	 * @see Exception#Exception()
	 */
	public DaoException() {
		super();
	}

	/**
	 * @see Exception#Exception(java.lang.String)
	 */
	public DaoException(String message) {
		super(message);
	}

	/**
	 * @see Exception#Exception(java.lang.String, java.lang.Throwable)
	 */
	public DaoException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @see Exception#Exception(java.lang.Throwable)
	 */
	public DaoException(Throwable cause) {
		super(cause);
	}
}
