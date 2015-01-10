package suncertify.core;

import java.awt.Frame;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import suncertify.core.provider.INameProvider;
import suncertify.db.dao.IContractorDao;
import suncertify.gui.dialog.AbstractDialog;

/**
 * <code>ApplicationContext</code> is a context class that contains
 * configuration details for all system needed in different places along the
 * whole system hierarchy. During initialization it reads configuration from the
 * following files:
 * <ul>
 * <li>suncertify.properties</li>
 * <li>command.properties</li>
 * </ul>
 * All configuration details can be retrieved and/or changed via
 * <code>getXXX</code> and <code>setXXX</code> methods (see methods doc for
 * detailed description). This class enables also to store changed configuration
 * (only <code>suncertify.properties</code>) to the file system (in order to
 * persist configuration).<br>
 * <br>
 * This class is <i>Singleton</i> and its instance can be obtained via
 * <code>getInstance()</code> method.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
public final class ApplicationContext {
	/**
	 * Configuration filename.
	 */
	private static final String APPLICATION_CFG = "suncertify.properties";

	/**
	 * <i>Singleton</i> instance of this class.
	 */
	private static ApplicationContext APPLICATION_CTX = new ApplicationContext();

	/**
	 * Application name that can appear on the window title bar.
	 */
	public static final String APPLICATION_NAME = "Contractors";

	/**
	 * Command (mode flags also) setup filename.
	 */
	private static final String COMMAND_CFG = "command.properties";

	/**
	 * Default mode that application starts in.
	 */
	private static final String DEFAULT_MODE_PROPERTY = "suncertify.default_mode";

	/**
	 * Property name for the help file URL.
	 */
	private static final String HELP_URL = "suncertify.help.url";

	/**
	 * Suffix for configuration dialog class name property.
	 */
	private static final String MODE_CONFIGURATION_DIALOG = "configuration_dialog.";

	/**
	 * Suffix for mode name provider property.
	 */
	private static final String MODE_NAME_PROVIDER = "name_provider.";

	/**
	 * Suffix for mode flag name property.
	 */
	private static final String MODE_NAME = "name.";

	/**
	 * Property name for the database filename on the server side.
	 */
	private static final String REMOTE_DB_FILENAME = "suncertify.rmi.db.filename";

	/**
	 * Property name for the RMI service host.
	 */
	private static final String SERVICE_HOST = "suncertify.rmi.service.host";

	/**
	 * Property name for the RMI service name.
	 */
	private static final String SERVICE_NAME = "suncertify.rmi.service.name";

	/**
	 * Property name for the database filename in the standalone mode.
	 */
	private static final String STANDALONE_DB_FILENAME = "suncertify.alone.db.filename";

	/**
	 * Returns the <i>Singleton</i> instance of this class.
	 * 
	 * @return ApplicationContext - the <i>Singleton</i> instance of this
	 *         class.
	 */
	public static ApplicationContext getInstance() {
		return APPLICATION_CTX;
	}

	/**
	 * Command setup properties.
	 */
	private final Properties commandProperties;

	/**
	 * Configuration properties.
	 */
	private final Properties configProperties;

	/**
	 * DAO for accessing database.
	 */
	private IContractorDao contractorDao;

	/**
	 * Mode flag.
	 */
	private String mode = null;

	/**
	 * Map containing mode methods (method names that retrieves data source
	 * names): <i>key</i> = mode flag; <i>value</i> = method name.
	 */
	private final Map<String, String> nameProviders;

	/**
	 * Map containing mode names: <i>key</i> = mode flag; <i>value</i> = mode
	 * name.
	 */
	private final Map<String, String> modeNames;

	/**
	 * Default (inaccessible) constructor that initializes context.
	 */
	private ApplicationContext() {
		configProperties = PropertyLoader.loadProperties(APPLICATION_CFG);		
		mode = configProperties.getProperty(DEFAULT_MODE_PROPERTY);
		
		commandProperties = PropertyLoader.loadProperties(COMMAND_CFG);
		modeNames = new HashMap<String, String>();
		nameProviders = new HashMap<String, String>();

		for (Object key : commandProperties.keySet()) {
			String sKey = (String) key;
			// mode names
			if (sKey.startsWith(MODE_NAME)) {
				modeNames.put(sKey.substring(MODE_NAME.length()),
						commandProperties.getProperty(sKey));
			}

			// mode data source methods
			if (sKey.startsWith(MODE_NAME_PROVIDER)) {
				nameProviders.put(sKey.substring(MODE_NAME_PROVIDER.length()),
						commandProperties.getProperty(sKey));
			}
		}
	}

	/**
	 * Returns the array of available application modes (e.g. alone, network).
	 * 
	 * @return String[] - the array of available application modes (e.g. alone,
	 *         network).
	 */
	public String[] getAvailableModes() {
		return modeNames.keySet()
				.toArray(new String[modeNames.keySet().size()]);
	}

	/**
	 * Returns the implementation command class for given mode flag.
	 * 
	 * @param mode
	 *            String - application mode flag.
	 * @return Class - the implementation command class for given mode flag.
	 * @throws ClassNotFoundException
	 *             If implementation for given mode flag does not exists.
	 */
	public Class<?> getCommandClass(String mode) throws ClassNotFoundException {
		return Class.forName(commandProperties.getProperty(mode));
	}

	/**
	 * Finds and instantiates configuration dialog based on given application
	 * mode and owner component.
	 * 
	 * @param mode
	 *            String - application mode.
	 * @param owner
	 *            Frame - owner component.
	 * @return AbstractDialog - configuration dialog for given mode and owner
	 *         component.
	 * @throws Exception
	 *             see {@link Class#forName(String)},
	 *             {@link Class#getConstructor(Class[])},
	 *             {@link java.lang.reflect.Constructor#newInstance(Object[])}.
	 */
	public AbstractDialog getConfigurationDialog(String mode, Frame owner)
			throws Exception {

		String className = commandProperties
				.getProperty(MODE_CONFIGURATION_DIALOG + mode);
		Class<?> clazz = Class.forName(className);

		return (AbstractDialog) clazz.getConstructor(
				new Class[] { Frame.class })
				.newInstance(new Object[] { owner });
	}

	/**
	 * Returns the DAO for accessing database.
	 * 
	 * @return IContractorDao - the DAO for accessing database.
	 */
	public IContractorDao getContractorDao() {
		return contractorDao;
	}

	/**
	 * Returns the help file URL.
	 * 
	 * @return String - the help file URL.
	 */
	public String getHelpURL() {
		return configProperties.getProperty(HELP_URL);
	}

	/**
	 * Returns the local database filename.
	 * 
	 * @return String - the local database filename.
	 */
	public String getLocalDBFileName() {
		return configProperties.getProperty(STANDALONE_DB_FILENAME);
	}

	/**
	 * Returns the main window name (main window can be either the window with
	 * contractors list or the server window).
	 * 
	 * @return String - the main window name.
	 */
	public String getMainWindowName() {
		try {
			Class<?> clazz = Class.forName(nameProviders.get(getMode()));
			INameProvider provider = (INameProvider) clazz.newInstance();
			
			return provider.getName();			
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Returns the application mode.
	 * 
	 * @return String - the application mode.
	 */
	public String getMode() {
		return mode;
	}

	/**
	 * Returns the name of the given application mode.
	 * 
	 * @param mode
	 *            String - application mode.
	 * @return String - the name of the given application mode.
	 */
	public String getModeName(String mode) {
		return modeNames.get(mode);
	}

	/**
	 * Returns the server side database filename.
	 * 
	 * @return String - the server side database filename.
	 */
	public String getServerDBFileName() {
		return configProperties.getProperty(REMOTE_DB_FILENAME);
	}

	/**
	 * Returns the RMI service host.
	 * 
	 * @return String - the RMI service host.
	 */
	public String getServiceHost() {
		return configProperties.getProperty(SERVICE_HOST);
	}

	/**
	 * Returns the RMI service name.
	 * 
	 * @return String - the RMI service name.
	 */
	public String getServiceName() {
		return configProperties.getProperty(SERVICE_NAME);
	}

	/**
	 * Returns the RMI service URL.
	 * 
	 * @return String - the RMI service URL.
	 */
	public String getServiceURL() {
		return "//" + getServiceHost() + "/" + getServiceName();
	}

	/**
	 * Sets the DAO for accessing database.
	 * 
	 * @param contractorDao
	 *            IContractorDao - the DAO for accessing database.
	 */
	public void setContractorDao(IContractorDao contractorDao) {
		this.contractorDao = contractorDao;
	}

	/**
	 * Sets the local database filename.
	 * 
	 * @param filename
	 *            String - the local database filename.
	 */
	public void setLocalDBFileName(String filename) {
		setProperty(STANDALONE_DB_FILENAME, filename);
	}

	/**
	 * Sets the application mode.
	 * 
	 * @param mode
	 *            String - the application mode.
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}

	/**
	 * Sets property with given name to the new given value if the new value
	 * differs from the old one.
	 * 
	 * @param key
	 *            String - property name.
	 * @param value
	 *            String - new property value to be set.
	 */
	private void setProperty(String key, String value) {
		if (!configProperties.getProperty(key).equals(value)) {
			configProperties.setProperty(key, value);
		}
	}

	/**
	 * Sets the server side database filename.
	 * 
	 * @param filename
	 *            String - the server side database filename.
	 */
	public void setServerDBFileName(String filename) {
		setProperty(REMOTE_DB_FILENAME, filename);
	}

	/**
	 * Sets the RMI service host.
	 * 
	 * @param host
	 *            String - the RMI service host.
	 */
	public void setServiceHost(String host) {
		setProperty(SERVICE_HOST, host);
	}

	/**
	 * Sets the RMI service name.
	 * 
	 * @param name
	 *            String - the RMI service name.
	 */
	public void setServiceName(String name) {
		setProperty(SERVICE_NAME, name);
	}

	/**
	 * Stores configuration properties into the configuration file -
	 * <code>suncertify.properties</code>.
	 * 
	 * @throws FileNotFoundException
	 *             If configuration file was not found in the classpath.
	 * @throws IOException
	 *             If IO exception occurs during creating FileOutputStream
	 *             object - see
	 *             {@link FileOutputStream#FileOutputStream(java.lang.String)}.
	 */
	public void storeProperties() throws FileNotFoundException, IOException {
		OutputStream os = new FileOutputStream(ClassLoader
				.getSystemClassLoader().getResource(APPLICATION_CFG).getPath());
		configProperties.store(os, null);
	}
}
