package suncertify.core;

import java.io.InputStream;
import java.util.Properties;

/**
 * A simple class for loading {@link java.util.Properties} backed by
 * <code>.properties</code> files deployed as classpath resources.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
public final class PropertyLoader {
	/**
	 * Looks up a resource named <i>name</i> in the classpath.
	 * 
	 * @param name
	 *            String - the properties filename.
	 * @param loader
	 *            ClassLoader - the classloader through which to load the
	 *            resource (<code>null</code> is equivalent to the system
	 *            classloader).
	 * 
	 * @return Properties - resources converted to {@link Properties}.
	 */
	public static Properties loadProperties(String name, ClassLoader loader) {
		Properties result = null;
		InputStream in = null;
		try {
			if (loader == null) {
				loader = ClassLoader.getSystemClassLoader();
			}

			in = loader.getResourceAsStream(name);
			if (in != null) {
				result = new Properties();
				result.load(in);
			}
		} catch (Exception e) {
			result = new Properties();
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (Throwable ignore) {
					// XXX: ignore it
				}
		}

		return result;
	}

	/**
	 * This method simply invokes {@link #loadProperties(String, ClassLoader)}
	 * with <i>loader</i> parameter set to current thread's classloader:
	 * <code>Thread.currentThread().getContextClassLoader()</code>.
	 * 
	 * @see #loadProperties(String, ClassLoader)
	 */
	public static Properties loadProperties(final String name) {
		return loadProperties(name, Thread.currentThread()
				.getContextClassLoader());
	}
}