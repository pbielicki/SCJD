package suncertify.command;

/**
 * <code>ICommand</code> is an interface for mode flag handler - relevant
 * implementation is executed when user pass relating mode flag. This interface
 * simplifies plugging new mode flags handlers - if you want to add new mode
 * simply add new class that implements this interface and add appropriate
 * entries to <code>command.properties</code> file.<br>
 * <br>
 * This interface utilizes <i>Command</i> design pattern.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
public interface ICommand {
	/**
	 * Command method that must provide appropriate behaviour for related mode
	 * flag. This method should be invoked automatically by command executor -
	 * e.g. application main class.
	 */
	public void execute();
}
