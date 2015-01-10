package suncertify.gui.server;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;
import java.rmi.NotBoundException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import suncertify.core.ApplicationContext;
import suncertify.db.dao.IContractorDao;
import suncertify.db.dao.impl.ContractorRemoteDao;
import suncertify.gui.AbstractWindow;
import suncertify.gui.util.GUIUtil;

/**
 * <code>ServerWindow</code> consists of buttons that enable RMI server
 * management and appropriate menu bar. Menu bar's menus have the same
 * functionality as buttons and add additional configuration options and help
 * menu.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
public class ServerWindow extends AbstractWindow {

    private static final long serialVersionUID = 1L;

    /**
	 * User dialog message: "Service Error".
	 */
	private static final String MSG_SERVICE_ERROR = "Service Error";

	/**
	 * <code>Status</code> enum is used to indicate the current status of the
	 * RMI server.
	 */
	private enum Status {
		RUNNING, STARTING, STOPPED, STOPPING
	}

	/**
	 * Context for this application window.
	 */
	private static final ApplicationContext APP_CTX = ApplicationContext.getInstance();

	/**
	 * Remote object to be set as a RMI service.
	 */
	private IContractorDao dao = null;

	/*
	 * Window widgets
	 */
	// Buttons
	private JButton exitButton = null;
	private JButton startButton = null;
	private JButton stopButton = null;

	// Labels
	private JLabel statusLabel = null;
	
	// Menu elements
	private JMenuBar mainMenuBar = null;
	private JMenu serverMenu = null;
	private JMenuItem startMenuItem = null;
	private JMenuItem stopMenuItem = null;

	// Panels & Panes
	private JPanel buttonPanel = null;
	private JPanel mainPanel = null;

	/**
	 * Used in resuming server status to the previous one in case of failure.
	 */
	private Status oldStatus = null;

	/**
	 * Field indicating current status of the RMI server.
	 */
	private Status status = Status.STOPPED;

	/**
	 * Constructs and initializes <code>ServerWindow</code> instance.
	 */
	public ServerWindow() {
		super();
		initialize();
	}

	/**
	 * @see suncertify.gui.AbstractWindow#exit()
	 */
	protected void exit() {
		GUIUtil.actionExit(this);
	}

	/**
	 * This method initializes {@link #buttonPanel}.
	 * 
	 * @return JPanel - initialized {@link #buttonPanel}.
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(1);
			gridLayout.setHgap(10);
			buttonPanel = new JPanel();
			buttonPanel.setLayout(gridLayout);
			buttonPanel.add(getStartButton(), null);
			buttonPanel.add(getStopButton(), null);
			buttonPanel.add(getExitButton(), null);
		}
		return buttonPanel;
	}

	/**
	 * This method initializes {@link #exitButton}.
	 * 
	 * @return JButton - initialized {@link #exitButton}.
	 */
	private JButton getExitButton() {
		if (exitButton == null) {
			exitButton = new JButton();
			exitButton.setText("Exit");
			exitButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					exit();
				}
			});
		}
		return exitButton;
	}

	/**
	 * This method initializes {@link #mainPanel}.
	 * 
	 * @return JPanel - initialized {@link #mainPanel}.
	 */
	private JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.gridx = 0;
				constraints.fill = GridBagConstraints.VERTICAL;
				constraints.weightx = 1.0;
				constraints.weighty = 1.0;
				constraints.insets = new Insets(5, 5, 5, 5);
				constraints.anchor = GridBagConstraints.CENTER;
				constraints.gridy = 0;
				statusLabel = new JLabel();
				statusLabel.setText(getStatusString());
				mainPanel.add(statusLabel, constraints);
			}
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.gridx = 0;
				constraints.fill = GridBagConstraints.NONE;
				constraints.insets = new Insets(5, 5, 5, 5);
				constraints.gridy = 1;
				mainPanel.add(getButtonPanel(), constraints);
			}
		}
		return mainPanel;
	}

	/**
	 * This method initializes {@link #mainMenuBar}.
	 * 
	 * @return JMenuBar - initialized {@link #mainMenuBar}.
	 */
	private JMenuBar getMainMenuBar() {
		if (mainMenuBar == null) {
			mainMenuBar = new JMenuBar();
			mainMenuBar.add(GUIUtil.getFileMenu(this));
			mainMenuBar.add(GUIUtil.getConfigurationMenu(this));
			mainMenuBar.add(getServerMenu());
			mainMenuBar.add(GUIUtil.getHelpMenu(this));
		}
		return mainMenuBar;
	}

	/**
	 * This method initializes {@link #serverMenu}.
	 * 
	 * @return JMenu - initialized {@link #serverMenu}.
	 */
	private JMenu getServerMenu() {
		if (serverMenu == null) {
			serverMenu = new JMenu();
			serverMenu.setText("Server");
			serverMenu.add(getStartMenuItem());
			serverMenu.add(getStopMenuItem());
		}
		return serverMenu;
	}

	/**
	 * Returns the full service name as <code>String</code> - i.e. server db
	 * filename; service host; service name.
	 * 
	 * @return String - the full service name as <code>String</code> - i.e.
	 *         server db filename; service host; service name.
	 */
	private String getServiceName() {
		return APP_CTX.getServerDBFileName() + "; " + APP_CTX.getServiceHost() + "; " + APP_CTX.getServiceName();
	}

	/**
	 * This method initializes {@link #startButton}.
	 * 
	 * @return JButton - initializes {@link #startButton}.
	 */
	private JButton getStartButton() {
		if (startButton == null) {
			startButton = new JButton();
			startButton.setText("Start");
			startButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					startService();
				}
			});
		}
		return startButton;
	}

	/**
	 * This method initializes {@link #startMenuItem}.
	 * 
	 * @return JMenuItem - initialized {@link #startMenuItem}.
	 */
	private JMenuItem getStartMenuItem() {
		if (startMenuItem == null) {
			startMenuItem = new JMenuItem();
			startMenuItem.setText("Start Server");
			startMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					startService();
				}
			});
		}
		return startMenuItem;
	}

	/**
	 * Returns the string that is to be displayed on the main server window as a
	 * current status of the RMI service.
	 * 
	 * @return String - the string that is to be displayed on the main server
	 *         window as a current status of the RMI service.
	 */
	private String getStatusString() {
		switch (status) {
			// Stopping service
			case STOPPING:
				return "Please wait while unregistering RMI service...";

			// Service stopped
			case STOPPED:
				return "RMI service is stopped.";

			// Starting service
			case STARTING:
				return "Please wait while registering RMI service...";

			// Service up and running
			case RUNNING:
				return "RMI service (" + getServiceName() + ") is up and running.";

			// Error
			default:
				return "Error...";
		}
	}

	/**
	 * This method initializes {@link #stopButton}.
	 * 
	 * @return JButton - initialized {@link #stopButton}.
	 */
	private JButton getStopButton() {
		if (stopButton == null) {
			stopButton = new JButton();
			stopButton.setText("Stop");
			stopButton.setEnabled(false);
			stopButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					stopService();
				}
			});
		}
		return stopButton;
	}

	/**
	 * This method initializes {@link #stopMenuItem}.
	 * 
	 * @return JMenuItem - initialized {@link #stopMenuItem}.
	 */
	private JMenuItem getStopMenuItem() {
		if (stopMenuItem == null) {
			stopMenuItem = new JMenuItem();
			stopMenuItem.setText("Stop Server");
			stopMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					stopService();
				}
			});
		}
		return stopMenuItem;
	}

	/**
	 * @see suncertify.gui.AbstractWindow#initialize()
	 */
	protected void initialize() {
		this.setJMenuBar(getMainMenuBar());
		this.setSize(400, 150);
		this.setMaximumSize(new Dimension(800, 150));
		this.setMinimumSize(new Dimension(400, 150));
		this.setContentPane(getMainPanel());
		this.setTitle(APP_CTX.getMainWindowName());
		setDefaultLocation();
	}

	/**
	 * Rollback changing status operation.
	 */
	private void rollback() {
		if (oldStatus != null) {
			setStatus(oldStatus);
			oldStatus = null;
		}
	}

	/**
	 * Rollback starting service operation to the previous status.
	 */
	private void rollbackStarting() {
		rollback();
		setStartEnabled(true);
		setStopEnabled(false);
	}

	/**
	 * Rollback stopping service operation to the previous status.
	 */
	private void rollbackStopping() {
		rollback();
		setStartEnabled(false);
		setStopEnabled(true);
	}

	/**
	 * Sets all widgets that enable statring of the RMI service to the given
	 * enable status.
	 * 
	 * @param enabled boolean -
	 *            <code>true</code> to enable the widgets, otherwise
	 *            <code>false</code>.
	 * @see javax.swing.AbstractButton#setEnabled(boolean)
	 * @see JMenuItem#setEnabled(boolean)
	 */
	private void setStartEnabled(boolean enabled) {
		startButton.setEnabled(enabled);
		startMenuItem.setEnabled(enabled);
	}

	/**
	 * Sets current status to the new value and automatically changes the status
	 * label ({@link #statusLabel}) to the appropriate value.
	 * 
	 * @param status Status -
	 *            the new status value.
	 */
	private void setStatus(Status status) {
		this.status = status;
		statusLabel.setText(getStatusString());
		statusLabel.setToolTipText(getStatusString());
	}

	/**
	 * Sets all widgets that enable stopping of the RMI service to the given
	 * enable status.
	 * 
	 * @param enabled boolean -
	 *            <code>true</code> to enable the widgets, otherwise
	 *            <code>false</code>.
	 * @see javax.swing.AbstractButton#setEnabled(boolean)
	 * @see JMenuItem#setEnabled(boolean)
	 */
	private void setStopEnabled(boolean enabled) {
		stopButton.setEnabled(enabled);
		stopMenuItem.setEnabled(enabled);
	}

	/**
	 * Starts the RMI service.
	 */
	private void startService() {
		startTransaction(Status.STARTING);
		final Frame thisFrame = this;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					if (dao == null) {
						dao = new ContractorRemoteDao(APP_CTX.getServerDBFileName());
					}

					Naming.rebind(APP_CTX.getServiceURL(), dao);
					setStartEnabled(false);
					setStopEnabled(true);
					setStatus(Status.RUNNING);
				} catch (Exception e) {
					rollbackStarting();
					JOptionPane.showMessageDialog(thisFrame, "Could not start service:\n" + e.getMessage()
							+ "\n\nCheck your configuration " + "and try again.", MSG_SERVICE_ERROR,
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}

	/**
	 * Starts transaction of changing the RMI service status.
	 * 
	 * @param newStatus
	 *            Status - status to which the RMI service will be changed.
	 */
	private void startTransaction(Status newStatus) {
		oldStatus = status;
		setStatus(newStatus);
		setStartEnabled(false);
		setStopEnabled(false);
	}

	/**
	 * Stops RMI service.
	 */
	private void stopService() {
		startTransaction(Status.STOPPING);
		final Frame thisFrame = this;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					Naming.unbind(APP_CTX.getServiceURL());
					setStartEnabled(true);
					setStopEnabled(false);
					setStatus(Status.STOPPED);
				} catch (Exception e) {
					rollbackStopping();
					if (e instanceof NotBoundException) {
						setStartEnabled(true);
						setStopEnabled(false);
						setStatus(Status.STOPPED);
					}
					JOptionPane.showMessageDialog(thisFrame, "Could not stop service:\n" + e.getMessage(),
							MSG_SERVICE_ERROR, JOptionPane.ERROR_MESSAGE);
				}
			};
		});
	}
}
