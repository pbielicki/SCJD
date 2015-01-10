package suncertify.core.provider;

/**
 * <code>INameProvider</code> interface is a simple interface providing only
 * one method <code>getName()</code>. It can be used in many different places
 * where different names are required depending on different configuration and
 * non-configuration conditions.<br>
 * <br>
 * This interface utilizes very simple <i>Strategy</i> design pattern.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
public interface INameProvider {
	/**
	 * Returns the name constructed upon implemented algorithm.
	 * 
	 * @return String - the name constructed upon implemented algorithm.
	 */
	public String getName();
}
