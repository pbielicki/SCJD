package suncertify.db.domain;

import static suncertify.db.domain.DBMetaInfo.FIELDS_COUNT;
import static suncertify.db.domain.DBMetaInfo.FIELD_LENGTHS;
import static suncertify.db.domain.DBMetaInfo.FIELD_LENGTHS_MAP;
import static suncertify.db.domain.DBMetaInfo.FIELD_LOCATION;
import static suncertify.db.domain.DBMetaInfo.FIELD_NAME;
import static suncertify.db.domain.DBMetaInfo.FIELD_SPECIALTIES;
import static suncertify.db.domain.DBMetaInfo.UNSAVED_RECORD_NO;

import java.util.Arrays;

import suncertify.core.StringHelper;

/**
 * <code>Contractor</code> domain object represents contractor's data. This class is an example of
 * simple <i>JavaBean</i> with <code>setXXX</code> and <code>getXXX</code> methods.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 * @see suncertify.db.domain.Persistent
 */
public class Contractor extends Persistent {

    private static final long serialVersionUID = 1L;

    /**
	 * Subcontrator's location (city).
	 */
	private String location;

	/**
	 * Subcontrator's name.
	 */
	private String name;

	/**
	 * Id value (an 8 digit number) of the customer who has booked this contractor.
	 */
	private int owner = UNSAVED_RECORD_NO;

	/**
	 * Subcontrator's rate per hour.
	 */
	private Money rate;

	/**
	 * Subcontrator's company size (number of workers available).
	 */
	private int size;

	/**
	 * Subcontrator's specialties (comma separated strings).
	 */
	private String specialties;

	/**
	 * Constructs new <code>Contractor</code> object using {@link #Contractor(String, String)}
	 * constructor with <code>""</code>, <code>""</code> parameters.
	 * 
	 * @see #Contractor(String, String)
	 */
	public Contractor() {
		this("", "");
	}

	/**
	 * Constructs new <code>Contractor</code> object.
	 * 
	 * @param name
	 *            String - contractor's name;
	 * @param location
	 *            String - contractor's location.
	 */
	public Contractor(final String name, final String location) {
		setName(name);
		setLocation(location);
		setSpecialties("");
		setRate(new Money(0, " "));
	}

	/**
	 * Constructs new <code>Contractor</code> from given string array.
	 * 
	 * @param strings
	 *            String[] - string array consists of data of <code>Contractor</code> to be
	 *            created.
	 */
	public Contractor(final String[] strings) {
		if (strings.length != FIELDS_COUNT) {
			throw new IllegalArgumentException("Invalid array length.");
		}

		setName(strings[0]);
		setLocation(strings[1]);
		setSpecialties(strings[2]);

		setSize(StringHelper.intValue(strings[3]));
		setRate(Money.valueOf(strings[4]));
		setOwner(StringHelper.intValue(strings[5], UNSAVED_RECORD_NO));
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}

		if (!(obj instanceof Contractor)) {
			return false;
		}

		Contractor tmp = (Contractor) obj;
		if (tmp.getName().equals(this.getName()) && tmp.getLocation().equals(this.getLocation())
				&& tmp.getFlag() == this.getFlag()) {
			return true;
		}
		return false;
	}

	/**
	 * Makes current contractor available for booking.
	 */
	public void free() {
		owner = UNSAVED_RECORD_NO;
	}

	/**
	 * Returns the location (city) of the contractor.
	 * 
	 * @return String - the location (city) of the contractor.
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Returns the name of the contractor.
	 * 
	 * @return String - the name of the contractor.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the id of the of the customer who has booked this contractor.
	 * 
	 * @return int - the id of the customer who has booked this contractor.
	 */
	public int getOwner() {
		return owner;
	}

	/**
	 * Returns the rate per hour of the contractor.
	 * 
	 * @return Money - the rate per hour of the contractor.
	 */
	public Money getRate() {
		return rate;
	}

	/**
	 * Returns the company size (number of workers available).
	 * 
	 * @return int - the company size (number of workers available).
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Returns the contrator's specialties (comma separated strings).
	 * 
	 * @return String - the contrator's specialties (comma separated strings).
	 */
	public String getSpecialties() {
		return specialties;
	}

	/**
	 * hashCode() method based on "Effective Java: Programming Language Guide" by Joshua Bloch
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int hash = 17;

		hash = 37 * hash + this.getName().hashCode();
		hash = 37 * hash + this.getLocation().hashCode();
		return hash;
	}

	/**
	 * Checks if current contractor is booked by any owner.
	 * 
	 * @return boolean - <code>true</code> if current contractor is available and
	 *         <code>false</code> otherwise.
	 */
	public boolean isAvailable() {
		return owner == UNSAVED_RECORD_NO;
	}

	/**
	 * Sets the location (city) of the contractor.
	 * 
	 * @param location
	 *            String - the location (city) of the contractor to set.
	 */
	private void setLocation(final String location) {
		this.location = StringHelper.setStringLength(location, FIELD_LENGTHS_MAP.get(FIELD_LOCATION));
	}

	/**
	 * Sets the name of the contractor.
	 * 
	 * @param name
	 *            String - the name of the contractor to set.
	 */
	private void setName(final String name) {
		this.name = StringHelper.setStringLength(name, FIELD_LENGTHS_MAP.get(FIELD_NAME));
	}

	/**
	 * Sets the id of the the customer who has booked this contractor.
	 * 
	 * @param owner
	 *            int - the id of the the customer who has booked this contractor to set.
	 */
	public void setOwner(final int owner) {
		this.owner = owner;
	}

	/**
	 * Sets the rate per hour of the contractor.
	 * 
	 * @param rate
	 *            Money - the rate per hour of the contractor. to set.
	 */
	public void setRate(final Money rate) {
		this.rate = rate;
	}

	/**
	 * Sets the company size (number of workers available).
	 * 
	 * @param size
	 *            int - the company size (number of workers available) to set.
	 */
	public void setSize(final int size) {
		this.size = size;
	}

	/**
	 * Sets the subcontrator's specialties (comma separated strings).
	 * 
	 * @param specialties
	 *            String - the subcontrator's specialties (comma separated strings) to set.
	 */
	public void setSpecialties(final String specialties) {
		this.specialties = StringHelper.setStringLength(specialties, FIELD_LENGTHS_MAP.get(FIELD_SPECIALTIES));
	}

	/**
	 * Returns the string representation of this sub contractor.
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return Arrays.toString(toStrings());
	}

	/**
	 * @see suncertify.db.domain.IDomain#toStrings()
	 */
	public String[] toStrings() {
		String[] strings = new String[FIELDS_COUNT];

		strings[0] = getName();
		strings[1] = getLocation();
		strings[2] = getSpecialties();
		strings[3] = StringHelper.setStringLength("" + getSize(), FIELD_LENGTHS[3]);

		strings[4] = StringHelper.setStringLength("" + getRate(), FIELD_LENGTHS[4]);

		strings[5] = StringHelper.setStringLength(StringHelper.toString(getOwner(), UNSAVED_RECORD_NO),
				FIELD_LENGTHS[5]);

		return strings;
	}
}