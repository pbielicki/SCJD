package suncertify.db.domain;

import java.io.Serializable;

/**
 * <code>Money</code> class represents monetary data - it consists of currency
 * symbol <code>String</code> (one character) and the value (<code>double</code>).
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
public class Money implements Serializable, Comparable<Money> {

    private static final long serialVersionUID = 1L;

    /**
	 * Returns a <code>Money</code> object holding the currency symbol
	 * <code>String</code> (one character) and <code>double</code> value
	 * represented by the argument string <code>s</code>.
	 * 
	 * @param string
	 *            String - string representation of money object.
	 */
	public static Money valueOf(final String string) {
		try {
			// XXX: it should be checked using regex facilities
			if (string.length() < 3) {
				throw new IllegalArgumentException(
						"String must comply with the following format: SX.Y "
								+ "where S is currency symbol (one character) "
								+ "and X.Y is value (floating point).");
			}

			return new Money(Double.parseDouble(string.substring(1)), string
					.substring(0, 1));

		} catch (NumberFormatException e) {
			return new Money(0.0, string.substring(0, 1));
		}
	}

	/**
	 * Currency symbol.
	 */
	private String currency;

	/**
	 * Double value.
	 */
	private double value;

	/**
	 * Constructs new <code>Money</code> object with given currency symbol and
	 * value.
	 * 
	 * @param value
	 *            double - value.
	 * @param symbol
	 *            String - currency symbol.
	 */
	public Money(final double value, final String symbol) {
		this.value = value;
		if (symbol.length() != 1) {
			throw new IllegalArgumentException("Symbol length must equal 1.");
		}
		this.currency = symbol;
	}

	/**
	 * This method compares only values (currency is not taken into account) -
	 * that means that $10 is greater than &#163;9.
	 * 
	 * @see Comparable#compareTo(Object)
	 */
	public int compareTo(Money o) {
		if (o.getValue() < getValue()) {
			return 1;
		} else if (o.getValue() > getValue()) {
			return -1;
		}
		return 0;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}

		if (!(obj instanceof Money)) {
			return false;
		}

		Money tmp = (Money) obj;
		if (tmp.getCurrency().equals(this.getCurrency())
				&& tmp.getValue() == this.getValue()) {
			return true;
		}
		return false;
	}

	/**
	 * Returns the curency symbol.
	 * 
	 * @return String - the currency symbol.
	 */
	public String getCurrency() {
		return currency;
	}

	/**
	 * Returns the value.
	 * 
	 * @return double - the value.
	 */
	public double getValue() {
		return value;
	}

	/**
	 * hashCode() method based on "Effective Java: Programming Language Guide"
	 * by Joshua Bloch
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int hash = 17;

		hash = 37 * hash + this.getCurrency().hashCode();
		long f = Double.doubleToLongBits(this.getValue());
		hash = 37 * hash + (int) (f ^ (f >>> 32));
		return hash;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getCurrency() + getValue();
	}
}
