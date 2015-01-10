package suncertify.db.dao;

/**
 * <code>ComparisonOperator</code> is a simple enum class containing comparison operators.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
public enum ComparisonOperator {
	/**
	 * <i>Equal</i> operator - operator: <code>=</code>, name: <code>equal</code>.
	 */
	EQUAL("=", "equal"),

	/**
	 * <i>Greater</i> operator - operator: <code>&gt;</code>, name: <code>greater</code>.
	 */
	GREATER(">", "greater"),

	/**
	 * <i>Greater or equal</i> operator - operator: <code>&gt;=</code>, name:
	 * <code>greater or equal</code>.
	 */
	GREATER_OR_EQUAL(">=", "greater or equal"),

	/**
	 * <i>Less</i> operator - operator: <code>&lt;</code>, name: <code>equal</code>.
	 */
	LESS("<", "less"),

	/**
	 * <i>Less or equal</i> operator - operator: <code>&lt;=</code>, name: <code>less or equal</code>.
	 */
	LESS_OR_EQUAL("<=", "less or equal"),

	/**
	 * <i>Contains</i> operator - operator: <code>%%</code>, name: <code>contains</code>.
	 */
	CONTAINS("%%", "contains"),

	/**
	 * <i>Starts with</i> operator - operator: <code>ST</code>, name: <code>starts with</code>.
	 */
	STARTS_WITH("ST", "starts with"),

	/**
	 * <i>Not equal</i> operator - operator: <code>!=</code>, name: <code>not equal</code>.
	 */
	NOT_EQUAL("!=", "not equal");

	/**
	 * Name of concrete operator - not used at this moment.
	 */
	private String name;

	/**
	 * String representation of concrete operator.
	 */
	private String operator;

	/**
	 * Constructs <code>ComparisonOperator</code> enum object with given operator and name.
	 * 
	 * @param operator
	 *            String - string representation of concrete operator.
	 * @param name
	 *            String - name of concrete operator.
	 */
	private ComparisonOperator(String operator, String name) {
		this.operator = operator;
		this.name = name;
	}

	/**
	 * Returns the name of concrete operator.
	 * 
	 * @return String - name of concrete operator.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the string represenation of concrete operator.
	 * 
	 * @return String - the string represenation of concrete operator.
	 */
	public String getOperator() {
		return operator;
	}

	/**
	 * @see java.lang.Enum#toString()
	 */
	public String toString() {
		return operator;
	}
}