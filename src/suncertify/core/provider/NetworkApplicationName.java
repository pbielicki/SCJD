package suncertify.core.provider;

import suncertify.core.ApplicationContext;

/**
 * <code>NetworkApplicationName</code> provides name for the main window for
 * the network application mode.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 * @see INameProvider
 */
public class NetworkApplicationName implements INameProvider {
	/**
	 * Context for current class.
	 */
	private ApplicationContext APP_CTX = ApplicationContext.getInstance();

	/**
	 * @see suncertify.core.provider.INameProvider#getName()
	 */
	public String getName() {
		return ApplicationContext.APPLICATION_NAME + " - "
				+ APP_CTX.getModeName(APP_CTX.getMode()) + " ("
				+ APP_CTX.getServiceHost() + "; " + APP_CTX.getServiceName()
				+ ")";
	}
}
