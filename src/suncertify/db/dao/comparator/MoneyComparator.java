package suncertify.db.dao.comparator;

import suncertify.db.domain.Money;

/**
 * <code>MoneyComparator</code> is an implementation of comparator that compares string values
 * as money - it simply converts given strings to money objects and invokes
 * {@link suncertify.db.domain.Money#compareTo(Money)} method. 
 *
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 * @see suncertify.db.dao.comparator.IComparator
 */
public class MoneyComparator implements IComparator {
	/**
	 * @see IComparator#compare(String, String)
	 */
	public int compare(String string1, String string2) {
		return Money.valueOf(string1).compareTo(Money.valueOf(string2));
	}
}
