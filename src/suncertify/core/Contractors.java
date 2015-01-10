package suncertify.core;

import java.util.Arrays;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import suncertify.command.CommandException;
import suncertify.command.CommandFactory;
import suncertify.command.ICommand;

/**
 * <code>Contractors</code> is the main class of this application - its responsibility is to
 * instantiate relevant {@link ICommand} class based on mode flag passed by the user and execute it.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
public class Contractors {
	/**
	 * Starts the application - instatntiates relevant {@link ICommand} class and executes it.
	 * 
	 * @param args
	 *            String[] - command line arguments.
	 */
	public static void main(String[] args) {
		try {
			// Set java.rmi.server.codebase for rmi registry
			if (System.getProperty("java.rmi.server.codebase", null) == null) {
				System.setProperty("java.rmi.server.codebase", "file:/" + System.getProperty("user.dir", "") + "/"
						+ System.getProperty("java.class.path", "runme.jar"));
			}

			ApplicationContext appCtx = ApplicationContext.getInstance();
			if (args.length == 1) {
				appCtx.setMode(args[0]);
			}
			
            try {
                Class.forName("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } catch (Exception e) {
                // ignore
            }
			
			try {
				ICommand command = CommandFactory.getCommand(appCtx.getMode());
				command.execute();
			} catch (CommandException e) {
				JOptionPane.showMessageDialog(null, "Incorrect application mode.\nAvailable modes:\n"
						+ Arrays.toString(appCtx.getAvailableModes()), "Application Mode Error",
						JOptionPane.ERROR_MESSAGE);
			}

		} catch (Throwable e) {
			System.err.println("Could not start application:\n"
					+ "Check if valid 'suncertify.properties' file is in current directory " + "or in your classpath.");

			JOptionPane.showMessageDialog(null, "Could not start application:\n"
					+ "Check if valid 'suncertify.properties' file is in current directory " + "or in your classpath.",
					"Application Startup Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}