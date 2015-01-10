package suncertify.db.dao.comparator;

/**
 * <code>IComparator</code> provides method that allows to compare database fields without
 * worrying about conversion from string to specific database field type. In order to obtain
 * concrete comparator you should use appropriate factory instead of creating it using
 * <code>new</code> operator.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 * @see suncertify.db.dao.comparator.ComparatorFactory
 */
public interface IComparator {
	/**
	 * Compares given string objects as specific database types.
	 * 
	 * @param string1
	 *            String - first argument of comparison operation.
	 * @param string2
	 *            String - second argument of comparison operation.
	 * @return int a negative integer, zero, or a positive integer as the first argument is less
	 *         than, equal to, or greater than the second.
	 * 
	 * @see java.util.Comparator
	 */
	public int compare(String string1, String string2);
}