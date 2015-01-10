package suncertify.command;

import suncertify.core.ApplicationContext;

/**
 * <code>CommandFactory</code> is an implementation of <i>Factory Method</i>
 * design pattern and is responsible for creating relevant {@link ICommand}
 * implementation dependent on the mode flag passed by user while running
 * application. Mapping between relevant flags and command class names is stored
 * in <code>command.properties</code> file and is retrieved from the file
 * system via application context - see {@link ApplicationContext}. <br>
 * <br>
 * This class utilizes <i>Factory Method</i> design pattern.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 * @see suncertify.command.ICommand
 * @see suncertify.core.ApplicationContext
 */
public class CommandFactory {
	/**
	 * <i>Factory Method</i> that returns {@link ICommand} implementation for
	 * given mode flag.
	 * 
	 * @param mode
	 *            String - application mode flag.
	 * @return ICommand - {@link ICommand} implementation for given mode flag.
	 * @throws CommandException
	 *             If mode flag is not supported.
	 */
	public static ICommand getCommand(String mode) throws CommandException {
		try {
			ApplicationContext appCtx = ApplicationContext.getInstance();
			return (ICommand) appCtx.getCommandClass(mode).newInstance();
		} catch (Exception e) {
			throw new CommandException("This mode '" + mode
					+ "' is not supported:\n" + e.getMessage(), e);
		}
	}
}
