package suncertify.gui.contractor;

import static suncertify.db.dao.ComparisonOperator.GREATER_OR_EQUAL;
import static suncertify.db.dao.ComparisonOperator.CONTAINS;
import static suncertify.db.dao.ComparisonOperator.STARTS_WITH;
import static suncertify.db.domain.DBMetaInfo.FIELDS_COUNT;
import static suncertify.db.domain.DBMetaInfo.FIELD_RATE;
import static suncertify.db.domain.DBMetaInfo.FIELD_SIZE;
import static suncertify.db.domain.DBMetaInfo.FIELD_SPECIALTIES;
import static suncertify.db.domain.DBMetaInfo.getFieldNo;
import static suncertify.gui.contractor.ContractorFilter.Availability.ALL;

import java.util.Arrays;

import suncertify.db.dao.ComparisonOperator;
import suncertify.db.domain.Contractor;

/**
 * <code>ContractorFilter</code> stores contractor's search filter data. Upon this object search
 * engine can look up for required records in the persistent layer.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
class ContractorFilter {
	/**
	 * <code>Availability</code> enum defines availability values of contractor. This enum is used
	 * in filtering contractors in the contractors table.
	 * 
	 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
	 */
	public enum Availability {
		/**
		 * Searches for all contractors (either booked and available).
		 */
		ALL,

		/**
		 * Searches only for available contractors.
		 */
		AVAILABLE,

		/**
		 * Searches only for booked contractors.
		 */
		BOOKED,

		/**
		 * Searches only for contractors booked for specified owner.
		 */
		BOOKED_FOR;
	}

	/**
	 * Indicates which contractors (booked, available, etc.) user wants to filter and see in the
	 * contractors table.
	 * 
	 * @see Availability
	 */
	private Availability availability = ALL;

	/**
	 * Contractor's filter data.
	 */
	private Contractor contractor;

	/**
	 * Comparison operators array.
	 */
	private final ComparisonOperator[] operators;

	/**
	 * Constructs <code>ContractorFilter</code> and initializes all necessary fields.
	 */
	public ContractorFilter() {
		this.contractor = new Contractor();
		operators = new ComparisonOperator[FIELDS_COUNT];
		Arrays.fill(operators, STARTS_WITH);
		operators[getFieldNo(FIELD_SPECIALTIES)] = CONTAINS;
		operators[getFieldNo(FIELD_SIZE)] = GREATER_OR_EQUAL;
		operators[getFieldNo(FIELD_RATE)] = GREATER_OR_EQUAL;
	}

	/**
	 * Creates new <code>ContractorFilter</code> with given contractor's data.
	 * 
	 * @param contractor
	 *            Contractor - the contractor's data.
	 */
	public ContractorFilter(Contractor contractor) {
		this();
		this.contractor = contractor;
	}

	/**
	 * Returns the availability enum for current filter.
	 * 
	 * @return Availability - the availability enum for current filter.
	 */
	public Availability getAvailability() {
		return availability;
	}

	/**
	 * Returns the contractor's filter data.
	 * 
	 * @return Contractor - the contractor's filter data.
	 */
	public Contractor getContractor() {
		return contractor;
	}

	/**
	 * Returns the filter as strings array.
	 * 
	 * @return String[] - the filter as strings array.
	 */
	public String[] getFilter() {
		return contractor.toStrings();
	}

	/**
	 * Returns the comparison operator for given field name.
	 * 
	 * @param fieldName
	 *            String - field name for which comparison operator is to be returned.
	 * @return ComparisonOperator - the comparison operator for given field name.
	 */
	public ComparisonOperator getOperator(String fieldName) {
		return operators[getFieldNo(fieldName)];
	}

	/**
	 * Returns the comparison operators array.
	 * 
	 * @return ComparisonOperator[] - the comparison operators array.
	 */
	public ComparisonOperator[] getOperators() {
		return operators.clone();
	}

	/**
	 * Sets the availability enum for current filter.
	 * 
	 * @param availability
	 *            Availability - the availability enum for current filter.
	 */
	public void setAvailability(Availability availability) {
		this.availability = availability;
	}

	/**
	 * Sets the comparison operator for given field name.
	 * 
	 * @param fieldName
	 *            String - field name for which operator is to be set.
	 * @param operator
	 *            ComparisonOperator - comparison operator to be set for given field name.
	 */
	public void setOperator(String fieldName, ComparisonOperator operator) {
		operators[getFieldNo(fieldName)] = operator;
	}
}
