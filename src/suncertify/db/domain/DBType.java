package suncertify.db.domain;

/**
 * <code>DBType</code> enum that defines data types of fields in database.
 *
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
public enum DBType {
	/**
	 * Text type defines ordinary <code>String</code> class.
	 */
	TEXT,
	
	/**
	 * Integer type defines ordinary <code>Integer</code> class.
	 */
	INTEGER,
	
	/**
	 * Money type defines <code>Money</code> class.
	 * @see suncertify.db.domain.Money
	 */
	MONEY;
}
