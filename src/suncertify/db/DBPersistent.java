package suncertify.db;

import java.util.List;

import suncertify.db.domain.Persistent;

/**
 * <code>DBPersistent</code> provides additional methods to the main db
 * interface - {@link suncertify.db.DBMain} - that can operate on persistent
 * domain objects (see {@link suncertify.db.domain.Persistent}) rather than
 * <code>String</code> arrays.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
public interface DBPersistent<T extends Persistent> extends DBMain {
	/**
	 * Reads all records from database.
	 * 
	 * @return List&lt;T&gt; - the list of all records from database.
	 */
	public List<T> readAll();
}
