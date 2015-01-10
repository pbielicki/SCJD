package suncertify.db.domain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static suncertify.db.domain.DBType.*;

/**
 * <code>DBMetaInfo</code> contains database meta info (e.g. field names, field sizes, etc.)
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
public final class DBMetaInfo {
	/**
	 * Inaccessible constructor.
	 */
	private DBMetaInfo() {
	}

	/**
	 * Magic cookie value.
	 */
	public static final int MAGIC_COOKIE = 513;

	/**
	 * Total overall length in bytes of each record.
	 */
	public static final int RECORD_LENGTH = 182;

	/**
	 * Length (in bytes) of record flag.
	 */
	public static final int FLAG_LENGTH = 1;

	/**
	 * Number of fields in each record.
	 */
	public static final short FIELDS_COUNT = 6;

	/**
	 * Record field's name - "name".
	 */
	public static final String FIELD_NAME = "name";

	/**
	 * Record field's name - "location".
	 */
	public static final String FIELD_LOCATION = "location";

	/**
	 * Record field's name - "specialties".
	 */
	public static final String FIELD_SPECIALTIES = "specialties";

	/**
	 * Record field's name - "size".
	 */
	public static final String FIELD_SIZE = "size";

	/**
	 * Record field's name - "rate".
	 */
	public static final String FIELD_RATE = "rate";

	/**
	 * Record field's name - "owner".
	 */
	public static final String FIELD_OWNER = "owner";

	/**
	 * Array of field names.
	 */
	public static final String[] FIELD_NAMES = new String[] { FIELD_NAME, FIELD_LOCATION, FIELD_SPECIALTIES,
			FIELD_SIZE, FIELD_RATE, FIELD_OWNER };

	/**
	 * List containing field names.
	 * 
	 * @see #FIELD_NAMES
	 */
	private static List<String> FIELD_NAMES_LIST = Arrays.asList(FIELD_NAMES);

	/**
	 * Returns the field number of given field name.
	 * 
	 * @param fieldName String - field name.
	 * @return int -the field number of given field name.
	 */
	public static int getFieldNo(String fieldName) {
		return FIELD_NAMES_LIST.indexOf(fieldName.toLowerCase());
	}

	/**
	 * Returns the field type of field with given number.
	 * 
	 * @param fieldNo int - field number.
	 * @return DBType - the field type of field with given number.
	 */
	public static DBType getFieldType(int fieldNo) {
		return FIELD_TYPES[fieldNo];
	}

	/**
	 * Array of field lengths.
	 */
	public static final short[] FIELD_LENGTHS = new short[] { 32, 64, 64, 6, 8, 8 };

	/**
	 * Array of field types.
	 */
	private static final DBType[] FIELD_TYPES = new DBType[] { TEXT, TEXT, TEXT, INTEGER, MONEY, INTEGER };

	/**
	 * Map containing lengths of fields - field name is the key. If anybody wants to know what is
	 * the length of "name" field the only thing to do is to get this info from this map:
	 * 
	 * <pre>
	 * short nameLength = DBMetaInfo.FIELD_LENGTHS_MAP.get(DBMetaInfo.FIELD_NAME);
	 * </pre>
	 */
	public static final Map<String, Short> FIELD_LENGTHS_MAP = new HashMap<String, Short>();
	static {
		for (int i = 0; i < FIELDS_COUNT; ++i) {
			FIELD_LENGTHS_MAP.put(FIELD_NAMES[i], FIELD_LENGTHS[i]);
		}
	}

	/**
	 * Indicates from which number the record counting starts.
	 */
	public static final int FIRST_RECORD_NO = 1;

	/**
	 * No of the record indicating that it is not saved into persistent layer.
	 */
	public static final int UNSAVED_RECORD_NO = FIRST_RECORD_NO - 1;
}