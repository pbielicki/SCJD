package suncertify.command;

import java.io.IOException;

import suncertify.gui.server.ServerWindow;

/**
 * <code>ServerCommand</code> is responsible for setting up and starting <i>Contractors</i>
 * application in the server mode - application window that enables managing of the RMI service.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
class ServerCommand implements ICommand {
	/**
	 * @see suncertify.command.ICommand#execute()
	 */
	public void execute() {
		try {
			final Process p = Runtime.getRuntime().exec(
					System.getProperty("sun.boot.library.path", "") + "/rmiregistry");
			
			Runtime.getRuntime().addShutdownHook(new Thread() {
				/**
				 * @see Thread#run()
				 */
				public void run() {
					if (p != null) {
						p.destroy();
					}
				};
			});
		} catch (IOException e) {
			// XXX: ignore it
		}
		ServerWindow window = new ServerWindow();
		window.open();
	}
}
