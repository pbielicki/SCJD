package suncertify.db.domain;

/**
 * <code>IDomain</code> defines common methods for all domain objects.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
public interface IDomain {
	/**
	 * Returns the flag of this domain object.
	 * 
	 * @return RecordFlagEnum - the flag of this domain object.
	 */
	public RecordFlagEnum getFlag();

	/**
	 * Returns the id of this domain object.
	 * 
	 * @return int - the id of this domain object.
	 */
	public int getId();

	/**
	 * Sets the flag of this domain object.
	 * 
	 * @param flag
	 *            RecordFlagEnum - the flag of this domain object.
	 */
	public void setFlag(RecordFlagEnum flag);

	/**
	 * Sets the id of this domain object.
	 * 
	 * @param id
	 *            int - the id of this domain object.
	 */
	public void setId(int id);

	/**
	 * Converts domain object to string array.
	 * 
	 * @return String[] - string array representation of domain object.
	 */
	public String[] toStrings();
}
