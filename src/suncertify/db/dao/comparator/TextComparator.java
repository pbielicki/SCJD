package suncertify.db.dao.comparator;

/**
 * <code>TextComparator</code> is an implementation of comparator that compares string values as
 * text - it simply invokes {@link String#compareToIgnoreCase(String)} method.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 * @see suncertify.db.dao.comparator.IComparator
 */
public class TextComparator implements IComparator {
	/**
	 * @see IComparator#compare(String, String)
	 */
	public int compare(String string1, String string2) {
		return string1.compareToIgnoreCase(string2);
	}
}