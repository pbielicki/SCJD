package suncertify.core.provider;

import suncertify.core.ApplicationContext;

/**
 * <code>ServerApplicationName</code> provides name for the main window for
 * the server application mode.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
public class ServerApplicationName implements INameProvider {
	/**
	 * Context for current class.
	 */
	private ApplicationContext APP_CTX = ApplicationContext.getInstance();

	/**
	 * @see suncertify.core.provider.INameProvider#getName()
	 */
	public String getName() {
		return APP_CTX.getModeName(APP_CTX.getMode());
	}
}
