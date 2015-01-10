package suncertify.gui.configuration;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import suncertify.core.ApplicationContext;
import suncertify.gui.dialog.AbstractDialog;
import suncertify.gui.util.GUIUtil;

/**
 * <code>ServerConfigurationDialog</code> is the dialog window for changing
 * server mode application configuration. It enables user to change the
 * following settings:
 * <ul>
 * <li>database filename on the remote machine</li>
 * <li>RMI service host (IP address or URL)</li>
 * <li>RMI service name</li>
 * </ul>
 * This dialog consists of default <i>OK</i> and <i>Cancel</i> buttons.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
public class ServerConfigurationDialog extends AbstractDialog {

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
	// Buttons
	private JButton changeButton = null;
	
	// Labels
	private JLabel filenameLabel = null;
	private JScrollPane filenameScrollPane = null;
	// Text widgets
	private JTextArea filenameText = null;
	
	// Panels & Panes
	private JPanel mainPanel = null;
	private JLabel serviceHostLabel = null;
	
	private JTextField serviceHostText = null;
	private JLabel serviceNameLabel = null;
	private JTextField serviceNameText = null;

	/**
	 * Constructs <code>ServerConfigurationDialog</code> instance as a
	 * <i>modal</i> dialog window.
	 * 
	 * @param owner
	 *            Frame - owner window of this dialog.
	 */
	public ServerConfigurationDialog(Frame owner) {
		super(owner, true);
		initialize();
		loadDialogBean();
	}

	/**
	 * This method initializes {@link #changeButton}.
	 * 
	 * @return JButton - initialized {@link #changeButton}.
	 */
	private JButton getChangeButton() {
		if (changeButton == null) {
			changeButton = new JButton();
			changeButton.setText("Change");
			final Window thisFrame = this;
			changeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					filenameText.setText(GUIUtil.actionChangeDBFile(thisFrame,
							filenameText.getText()));
				}
			});
		}
		return changeButton;
	}

	/**
	 * This method initializes {@link #filenameScrollPane}.
	 * 
	 * @return JScrollPane - initializes {@link #filenameScrollPane}.
	 */
	private JScrollPane getFilenameScrollPane() {
		if (filenameScrollPane == null) {
			filenameScrollPane = new JScrollPane();
			filenameScrollPane.setViewportView(getFilenameText());
		}
		return filenameScrollPane;
	}

	/**
	 * This method initializes {@link #filenameText}.
	 * 
	 * @return JTextArea - initialized {@link #filenameText}.
	 */
	private JTextArea getFilenameText() {
		if (filenameText == null) {
			filenameText = new JTextArea();
			filenameText.setLineWrap(true);
			filenameText.setEditable(false);
			filenameText.setFocusable(false);
		}
		return filenameText;
	}

	/**
	 * This method initializes {@link #mainPanel}.
	 * 
	 * @return JPanel - initializes {@link #mainPanel}.
	 */
	private JPanel getMainContentPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.gridx = 0;
				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.insets = new Insets(5, 5, 0, 0);
				constraints.anchor = GridBagConstraints.NORTH;
				constraints.gridy = 0;
				filenameLabel = new JLabel();
				filenameLabel.setText("Database Filename:");
				mainPanel.add(filenameLabel, constraints);
			}
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.fill = GridBagConstraints.BOTH;
				constraints.weighty = 1.0;
				constraints.gridwidth = 2;
				constraints.insets = new Insets(5, 5, 2, 5);
				constraints.weightx = 1.0;
				mainPanel.add(getFilenameScrollPane(), constraints);
			}
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.anchor = GridBagConstraints.NORTH;
				constraints.insets = new Insets(5, 0, 0, 5);
				constraints.fill = GridBagConstraints.HORIZONTAL;
				mainPanel.add(getChangeButton(), constraints);
			}
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.gridx = 0;
				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.insets = new Insets(0, 5, 0, 0);
				constraints.gridy = 1;
				serviceHostLabel = new JLabel();
				serviceHostLabel.setText("Service Host:");
				serviceHostLabel.setToolTipText("IP address or valid URL");
				mainPanel.add(serviceHostLabel, constraints);
			}
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.gridy = 1;
				constraints.weightx = 1.0;
				constraints.insets = new Insets(2, 5, 2, 5);
				constraints.gridx = 2;
				mainPanel.add(getServiceHostText(), constraints);
			}
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.gridx = 0;
				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.insets = new Insets(0, 5, 0, 0);
				constraints.gridy = 2;
				serviceNameLabel = new JLabel();
				serviceNameLabel.setText("Service Name:");
				mainPanel.add(serviceNameLabel, constraints);
			}
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.gridy = 2;
				constraints.weightx = 1.0;
				constraints.insets = new Insets(2, 5, 0, 5);
				constraints.gridx = 2;
				mainPanel.add(getServiceNameText(), constraints);
			}
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.gridx = 2;
				constraints.gridwidth = 2;
				constraints.fill = GridBagConstraints.NONE;
				constraints.insets = new Insets(5, 5, 5, 5);
				constraints.anchor = GridBagConstraints.EAST;
				constraints.gridy = 3;
				mainPanel.add(getButtonPanel(), constraints);
			}
		}
		return mainPanel;
	}

	/**
	 * This method initializes {@link #serviceHostText}.
	 * 
	 * @return JTextField - initialized {@link #serviceHostText}.
	 */
	private JTextField getServiceHostText() {
		if (serviceHostText == null) {
			serviceHostText = new JTextField();
		}
		return serviceHostText;
	}

	/**
	 * This method initializes {@link #serviceNameText}.
	 * 
	 * @return JTextField - initialized {@link #serviceNameText}.
	 */
	private JTextField getServiceNameText() {
		if (serviceNameText == null) {
			serviceNameText = new JTextField();
		}
		return serviceNameText;
	}

	/**
	 * @see suncertify.gui.dialog.AbstractDialog#initialize()
	 */
	protected void initialize() {
		this.setSize(500, 150);
		this.setTitle("Server Configuration");
		this.setContentPane(getMainContentPanel());
		setDefaultLocation();
	}

	/**
	 * @see suncertify.gui.dialog.AbstractDialog#isDialogFilled()
	 */
	protected boolean isDialogFilled() {
		// Check filename
		if (filenameText.getText().trim().length() <= 0) {
			JOptionPane.showMessageDialog(this, "Please choose database file.",
					"Dialog Data", JOptionPane.INFORMATION_MESSAGE);

			filenameText.setText(GUIUtil.actionChangeDBFile(this, filenameText
					.getText()));
			changeButton.grabFocus();
			return false;
		}

		// Check filename
		if (serviceHostText.getText().trim().length() <= 0) {
			JOptionPane.showMessageDialog(this, "Please enter service host.",
					"Dialog Data", JOptionPane.INFORMATION_MESSAGE);

			serviceHostText.grabFocus();
			return false;
		}

		// Check filename
		if (serviceNameText.getText().trim().length() <= 0) {
			JOptionPane.showMessageDialog(this, "Please enter service name.",
					"Dialog Data", JOptionPane.INFORMATION_MESSAGE);

			serviceNameText.grabFocus();
			return false;
		}
		return true;
	}

	/**
	 * @see suncertify.gui.dialog.AbstractDialog#loadDialogBean()
	 */
	protected void loadDialogBean() {
		filenameText.setText(APP_CTX.getServerDBFileName());
		serviceHostText.setText(APP_CTX.getServiceHost());
		serviceNameText.setText(APP_CTX.getServiceName());
	}

	/**
	 * @see suncertify.gui.dialog.AbstractDialog#saveDialogBean()
	 */
	protected void saveDialogBean() {
		String oldFilename = APP_CTX.getServerDBFileName();
		String oldHost = APP_CTX.getServiceHost();
		String oldName = APP_CTX.getServiceName();
		APP_CTX.setLocalDBFileName(filenameText.getText().trim());
		APP_CTX.setServiceHost(serviceHostText.getText().trim());
		APP_CTX.setServiceName(serviceNameText.getText().trim());
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

		APP_CTX.setServerDBFileName(oldFilename);
		APP_CTX.setServiceHost(oldHost);
		APP_CTX.setServiceName(oldName);
	}
}
