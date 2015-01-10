package suncertify.gui.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import suncertify.core.PropertyLoader;

/**
 * <code>GUIHelper</code> is a helper class for GUI. Its main responsibility
 * is to read and provide specific data from configuration files to GUI widgets,
 * e.g.:
 * <ul>
 * <li>contractor's specialties</li>
 * <li>currency symbols</li>
 * </ul>
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
public final class GUIHelper {
	/**
	 * Currency symbol's property name prefix.
	 */
	private static final String CURRENCY_PROPERTY = "currency.symbol.";

	/**
	 * Dictionaries configuration dialog.
	 */
	private static final String DICTIONARY_FILE = "dictionary.properties";

	/**
	 * Singleton instance of <code>GUIHelper</code> class.
	 */
	private static final GUIHelper GUI_HELPER = new GUIHelper();

	/**
	 * Specialty property name prefix.
	 */
	private static final String SPECIALTY_PROPERTY = "specialty.";

	/**
	 * Returns the <code>GUIHelper</code> singleton instance.
	 * 
	 * @return GUIHelper - the <code>GUIHelper</code> singleton instance.
	 */
	public static GUIHelper getInstance() {
		return GUI_HELPER;
	}

	/**
	 * Currency symbols array.
	 */
	private String[] currencies = new String[0];

	/**
	 * Dictionary properties.
	 */
	private Properties dictionaryProperties;

	/**
	 * Contractor's specialties array.
	 */
	private String[] specialties = new String[0];

	/**
	 * Inaccessible constructor.
	 */
	private GUIHelper() {
		dictionaryProperties = PropertyLoader.loadProperties(DICTIONARY_FILE);
		loadSpecialtyProperties();
		loadCurrencyProperties();
	}

	/**
	 * Returns the currency symbols array.
	 * 
	 * @return String[] - the currency symbols array.
	 */
	public String[] getCurrencies() {
		return (String[]) currencies.clone();
	}

	/**
	 * Returns the contractor's specialties array.
	 * 
	 * @return String[] - the contractor's specialties array.
	 */
	public String[] getSpecialties() {
		return (String[]) specialties.clone();
	}

	/**
	 * Loads currency symbols into appropriate field.
	 */
	private void loadCurrencyProperties() {
		currencies = loadProperties(CURRENCY_PROPERTY);
	}

	/**
	 * Returns properites that start with given prefix with subsequent ordinal
	 * numbers starting from <code>1</code>.
	 * 
	 * @param propertyPrefix
	 *            String - property name prefix.
	 * @return String[] - the array of properties starting with given prefix.
	 */
	private String[] loadProperties(String propertyPrefix) {
		int idx = 0;
		List<String> list = new ArrayList<String>();
		String s = null;
		while ((s = dictionaryProperties.getProperty(propertyPrefix + ++idx)) != null) {
			list.add(s);
		}

		return list.toArray(new String[list.size()]);
	}

	/**
	 * Loads contractor's specialties into appropriate field.
	 */
	private void loadSpecialtyProperties() {
		specialties = loadProperties(SPECIALTY_PROPERTY);
	}
}
