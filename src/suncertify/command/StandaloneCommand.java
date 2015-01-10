package suncertify.command;

import javax.swing.JOptionPane;

import suncertify.core.ApplicationContext;
import suncertify.db.dao.IContractorDao;
import suncertify.db.dao.impl.ContractorLocalDao;
import suncertify.gui.configuration.StandaloneConfigurationDialog;
import suncertify.gui.contractor.MainWindow;
import suncertify.gui.dialog.AbstractDialog;

/**
 * <code>StandaloneCommand</code> is responsible for setting up and starting
 * <i>Contractors</i> application in the network mode (data is retrieved from
 * remote host). When exception occurs during initialization application asks
 * user if one wants to change configuration and try to start the whole system
 * again. If user presses 'Cancel' button in the configuration dialog
 * application stops.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
class StandaloneCommand implements ICommand {
	/**
	 * @see suncertify.command.ICommand#execute()
	 */
	public void execute() {
		while (true) {
			IContractorDao dao = null;
			try {
				ApplicationContext appCtx = ApplicationContext.getInstance();

				if (dao == null) {
					dao = new ContractorLocalDao(appCtx.getLocalDBFileName());
					appCtx.setContractorDao(dao);
				}

				MainWindow window = new MainWindow();
				window.open();
				break;
			} catch (Exception e) {
				if (JOptionPane.showConfirmDialog(null,
						"Could not initialize standalone application:\n"
								+ e.getMessage() + "\n\nDo you want to change "
								+ "the configuration and try again?",
						"Application Initialization Error",
						JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE) == JOptionPane.YES_OPTION) {

					// Change configuration dialog
					if (new StandaloneConfigurationDialog(null).open() == AbstractDialog.OK_OPTION) {
						continue;
					}
				}
				System.exit(0);
			}
		}
	}
}
