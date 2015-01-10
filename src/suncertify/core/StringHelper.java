package suncertify.core;

/**
 * <code>StringHelper</code> is an utility class providing simple methods that
 * operate on strings and string representation of different number types.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
public final class StringHelper {
	/**
	 * Returns double value of given string - if given string cannot be parsed
	 * (i.e. it throws <code>NumberFormatException</code>) this method
	 * returns <code>0d</code> (zero).
	 * 
	 * @param string
	 *            String - string representation of double number.
	 * @return double - double representation of given string or <code>0d</code>
	 *         (zero) if it cannot be parsed.
	 * @see java.lang.Double#parseDouble(java.lang.String)
	 */
	public static double doubleValue(final String string) {
		try {
			return Double.parseDouble(string.trim());
		} catch (NumberFormatException e) {
			return 0d;
		}
	}

	/**
	 * Returns int value of given string - if given string cannot be parsed
	 * (i.e. it throws <code>NumberFormatException</code>) this method
	 * returns <code>0</code> (zero).
	 * 
	 * @param string
	 *            String - string representation of int number.
	 * @return int - int representation of given string or <code>0</code>
	 *         (zero) if it cannot be parsed.
	 * @see Integer#parseInt(java.lang.String)
	 */
	public static int intValue(final String string) {
		return intValue(string, 0);
	}

	/**
	 * Returns int value of given string - if given string cannot be parsed
	 * (i.e. it throws <code>NumberFormatException</code>) this method
	 * returns <code>defaultValue</code> (zero).
	 * 
	 * @param string
	 *            String - string representation of int number.
	 * 
	 * @param defaultValue
	 *            int - default value that is returned in case given string is
	 *            invalid.
	 * @return int - int representation of given string or
	 *         <code>defaultValue</code> (zero) if it cannot be parsed.
	 * @see Integer#parseInt(java.lang.String)
	 */
	public static int intValue(final String string, final int defaultValue) {
		try {
			return Integer.parseInt(string.trim());
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * Returns given string with given length - if string is longer than
	 * required length the old string is cut to the new length; if string is
	 * shorter than required length its length is extended and filled with space (" ")
	 * sign.
	 * 
	 * @param string
	 *            String - string to be tailored to the new length.
	 * @param length
	 *            int - new length of given string.
	 * @return String - tailored string to the new length.
	 * 
	 * @see AbstractStringBuilder#setLength(int)
	 */
	public static String setStringLength(final String string, final int length) {
		StringBuilder buf = new StringBuilder(string);
		buf.setLength(length);
		return buf.toString().replace('\0', ' ');
	}

	/**
	 * Returns string representation of given int or empty string ("") if given
	 * int value equals given <i>emptyValue</i> parameter.
	 * 
	 * @param value
	 *            int - int value to be converted into String.
	 * @param emptyValue -
	 *            if given <i>value</i> equals this value this method will
	 *            return empty string ("") instead of string representation of
	 *            <i>value</i>.
	 * @return String - string representation of given int or empty string ("").
	 * @see Integer#toString(int)
	 */
	public static String toString(final int value, final int emptyValue) {
		if (value == emptyValue) {
			return "";
		}
		return Integer.toString(value);
	}

	/**
	 * Inaccessible constructor.
	 */
	private StringHelper() {
	}
}
