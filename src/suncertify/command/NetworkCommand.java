package suncertify.command;

import java.rmi.Naming;

import javax.swing.JOptionPane;

import suncertify.core.ApplicationContext;
import suncertify.db.dao.IContractorDao;
import suncertify.gui.configuration.NetworkConfigurationDialog;
import suncertify.gui.contractor.MainWindow;
import suncertify.gui.dialog.AbstractDialog;

/**
 * <code>NetworkCommand</code> is responsible for setting up and starting
 * <i>Contractors</i> application in the network mode (data is retrieved from
 * remote host). When exception occurs during initialization application asks
 * user if one wants to change configuration and try to start the whole system
 * again. If user presses 'Cancel' button in the configuration dialog
 * application stops.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
class NetworkCommand implements ICommand {
	/**
	 * @see suncertify.command.ICommand#execute()
	 */
	public void execute() {
		while (true) {
			IContractorDao dao = null;
			try {
				ApplicationContext appCtx = ApplicationContext.getInstance();

				if (dao == null) {
					dao = (IContractorDao) Naming
							.lookup(appCtx.getServiceURL());
					appCtx.setContractorDao(dao);
				}

				new MainWindow().open();
				break;
			} catch (Exception e) {
				if (JOptionPane.showConfirmDialog(null,
						"Could not initialize network application:\n"
								+ e.getMessage() + "\n\nDo you want to change "
								+ "the configuration and try again?",
						"Application Initialization Error",
						JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE) == JOptionPane.YES_OPTION) {

					// Change configuration dialog
					if (new NetworkConfigurationDialog(null).open() == AbstractDialog.OK_OPTION) {
						continue;
					}
				}
				System.exit(0);
			}
		}
	}
}
