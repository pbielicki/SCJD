package suncertify.gui.configuration;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import suncertify.core.ApplicationContext;
import suncertify.gui.dialog.AbstractDialog;

/**
 * <code>NetworkConfigurationDialog</code> is the dialog window for changing
 * network mode application configuration. It enables user to change the
 * following settings:
 * <ul>
 * <li>RMI service host (IP address or URL)</li>
 * <li>RMI service name</li>
 * </ul>
 * This dialog consists of default <i>OK</i> and <i>Cancel</i> buttons.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
public class NetworkConfigurationDialog extends AbstractDialog {

    private static final long serialVersionUID = 1L;

    /**
	 * Application context for this dialog.
	 * @see ApplicationContext
	 */
	private static final ApplicationContext APP_CTX = ApplicationContext
			.getInstance();

	/*
	 * Dialog widgets
	 */
	// Labels
	private JLabel hostLabel = null;
	private JLabel nameLabel = null;

	// Panels & Panes
	private JPanel mainPanel = null;

	// Text widgets
	private JTextField hostText = null;
	private JTextField nameText = null;

	/**
	 * Constructs <code>NetworkConfigurationDialog</code> instance as a
	 * <i>modal</i> dialog window.
	 * 
	 * @param owner
	 *            Frame - owner window of this dialog.
	 */
	public NetworkConfigurationDialog(Frame owner) {
		super(owner, true);
		initialize();
		loadDialogBean();
	}

	/**
	 * This method initializes {@link #hostText}.
	 * 
	 * @return JTextField - initialized {@link #hostText}.
	 */
	private JTextField getHostText() {
		if (hostText == null) {
			hostText = new JTextField();
		}
		return hostText;
	}

	/**
	 * This method initializes {@link #mainPanel}.
	 * 
	 * @return JPanel - initialized {@link #mainPanel}.
	 */
	private JPanel getMainContentPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.gridx = 0;
				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.insets = new Insets(0, 5, 0, 0);
				constraints.gridy = 0;
				hostLabel = new JLabel();
				hostLabel.setText("Service Host:");
				hostLabel.setToolTipText("IP address or valid URL");
				mainPanel.add(hostLabel, constraints);
			}
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.gridy = 0;
				constraints.weightx = 1.0;
				constraints.insets = new Insets(5, 5, 2, 5);
				constraints.gridx = 1;
				mainPanel.add(getHostText(), constraints);
			}
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.gridx = 0;
				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.insets = new Insets(0, 5, 0, 0);
				constraints.gridy = 1;
				nameLabel = new JLabel();
				nameLabel.setText("Service Name:");
				mainPanel.add(nameLabel, constraints);
			}
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.gridy = 1;
				constraints.weightx = 1.0;
				constraints.insets = new Insets(2, 5, 0, 5);
				constraints.gridx = 1;
				mainPanel.add(getNameText(), constraints);
			}
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.gridx = 1;
				constraints.anchor = GridBagConstraints.EAST;
				constraints.insets = new Insets(5, 0, 5, 5);
				constraints.gridy = 2;
				mainPanel.add(getButtonPanel(), constraints);
			}
		}
		return mainPanel;
	}

	/**
	 * This method initializes {@link #nameText}.
	 * 
	 * @return JTextField - initialized {@link #nameText}.
	 */
	private JTextField getNameText() {
		if (nameText == null) {
			nameText = new JTextField();
		}
		return nameText;
	}

	/**
	 * @see suncertify.gui.dialog.AbstractDialog#initialize()
	 */
	protected void initialize() {
		this.setSize(400, 120);
		this.setTitle("Network Configuration");
		this.setContentPane(getMainContentPanel());
		setDefaultLocation();
	}

	/**
	 * @see suncertify.gui.dialog.AbstractDialog#isDialogFilled()
	 */
	protected boolean isDialogFilled() {
		// Check host
		if (hostText.getText().trim().length() <= 0) {
			JOptionPane.showMessageDialog(this, "Please enter service host.",
					"Dialog Data", JOptionPane.INFORMATION_MESSAGE);

			hostText.grabFocus();
			return false;
		}

		// Check name
		if (nameText.getText().trim().length() <= 0) {
			JOptionPane.showMessageDialog(this, "Please enter service name.",
					"Dialog Data", JOptionPane.INFORMATION_MESSAGE);

			nameText.grabFocus();
			return false;
		}
		return true;
	}

	/**
	 * @see suncertify.gui.dialog.AbstractDialog#loadDialogBean()
	 */
	protected void loadDialogBean() {
		hostText.setText(APP_CTX.getServiceHost());
		nameText.setText(APP_CTX.getServiceName());
	}

	/**
	 * @see suncertify.gui.dialog.AbstractDialog#saveDialogBean()
	 */
	protected void saveDialogBean() {
		String oldHost = APP_CTX.getServiceHost();
		String oldName = APP_CTX.getServiceName();
		APP_CTX.setServiceHost(hostText.getText().trim());
		APP_CTX.setServiceName(nameText.getText().trim());
		try {
			APP_CTX.storeProperties();
			close();

			return;
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(this,
					"Configuration file could not be found:\n" + e.getMessage(),
					"Configuration Error", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this,
					"IO error occured during storing configuration:\n"
							+ e.getMessage(), "Configuration Error",
					JOptionPane.ERROR_MESSAGE);
		}

		APP_CTX.setServiceHost(oldHost);
		APP_CTX.setServiceName(oldName);
	}
}
