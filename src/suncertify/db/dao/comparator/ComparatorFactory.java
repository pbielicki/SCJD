package suncertify.db.dao.comparator;

import java.util.HashMap;
import java.util.Map;

import suncertify.db.domain.DBType;

/**
 * <code>ComparatorFactory</code> is a factory class for database field type comparators. It
 * provides method that returns {@link IComparator} instance for given database field type ({@link DBType}).
 * <br>
 * This interface utilizes <i>Factory Method</i> design pattern.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 * @see suncertify.db.dao.comparator.IComparator
 * @see suncertify.db.domain.DBType
 */
public class ComparatorFactory {
	/**
	 * Comparators cache.
	 */
	private static final Map<DBType, IComparator> cache = new HashMap<DBType, IComparator>();

	/**
	 * Returns {@link IComparator} instance for given database field type ({@link DBType}).
	 * 
	 * @param type
	 *            DBType - database field type for which comparator is to be created.
	 * @return IComparator - comparator for given database field type.
	 */
	public static IComparator getComparator(DBType type) {
		if (!cache.containsKey(type)) {
			IComparator comparator = null;

			switch (type) {
				// TEXT field
				case TEXT:
					comparator = new TextComparator();
					break;

				// INTEGER field
				case INTEGER:
					comparator = new IntegerComparator();
					break;

				// MONEY field
				case MONEY:
					comparator = new MoneyComparator();
					break;

				default:
					throw new IllegalArgumentException("Not supported field type: " + type.name());
			}
			cache.put(type, comparator);
		}

		return cache.get(type);
	}
}
