package suncertify.db.dao.comparator;

import static suncertify.db.domain.DBMetaInfo.UNSAVED_RECORD_NO;

/**
 * <code>IntegerComparator</code> is an implementation of comparator that compares string values
 * as integers - it simply converts given strings to integer objects and invokes
 * {@link java.lang.Integer#compareTo(java.lang.Integer)} method.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 * @see suncertify.db.dao.comparator.IComparator
 */
public class IntegerComparator implements IComparator {
	/**
	 * @see IComparator#compare(String, String)
	 */
	public int compare(String string1, String string2) {
		try {
			Integer int1 = null;
			Integer int2 = null;
			// checks firts param - empty string equals 0
			if (string1.trim().length() == 0) {
				int1 = UNSAVED_RECORD_NO;
			} else {
				int1 = Integer.valueOf(string1.trim());
			}

			// checks second param - empty string equals 0
			if (string2.trim().length() == 0) {
				int2 = UNSAVED_RECORD_NO;
			} else {
				int2 = Integer.valueOf(string2.trim());
			}

			return int1.compareTo(int2);

		} catch (NumberFormatException e) {
			return 0;
		}
	}
}
