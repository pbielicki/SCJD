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

import suncertify.core.ApplicationContext;
import suncertify.gui.dialog.AbstractDialog;
import suncertify.gui.util.GUIUtil;

/**
 * <code>StandaloneConfigurationDialog</code> is the dialog window for
 * changing standalone mode application configuration. It enables user to change
 * the following settings:
 * <ul>
 * <li>database filename on the local machine</li>
 * </ul>
 * This dialog consists of default <i>OK</i> and <i>Cancel</i> buttons.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
public class StandaloneConfigurationDialog extends AbstractDialog {

    private static final long serialVersionUID = 1L;

    /**
	 * Application context for this dialog.
	 * 
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

	// Panels & Panes
	private JScrollPane filenameScrollPane = null;
	private JPanel mainPanel = null;

	// Text widgets
	private JTextArea filenameText = null;

	/**
	 * Constructs <code>StandaloneConfigurationDialog</code> instance as a
	 * <i>modal</i> dialog window.
	 * 
	 * @param owner
	 *            Frame - owner window of this dialog.
	 */
	public StandaloneConfigurationDialog(Frame owner) {
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
	 * @return JScrollPane - initialized {@link #filenameScrollPane}.
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
				constraints.insets = new Insets(5, 5, 0, 0);
				constraints.anchor = GridBagConstraints.NORTHWEST;
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
				constraints.insets = new Insets(5, 5, 0, 5);
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
				constraints.gridx = 2;
				constraints.gridwidth = 2;
				constraints.fill = GridBagConstraints.NONE;
				constraints.insets = new Insets(10, 5, 5, 5);
				constraints.anchor = GridBagConstraints.EAST;
				constraints.gridy = 1;
				mainPanel.add(getButtonPanel(), constraints);
			}
		}
		return mainPanel;
	}

	/**
	 * @see suncertify.gui.dialog.AbstractDialog#initialize()
	 */
	protected void initialize() {
		this.setSize(500, 110);
		this.setTitle("Standalone Configuration");
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
		return true;
	}

	/**
	 * @see suncertify.gui.dialog.AbstractDialog#loadDialogBean()
	 */
	protected void loadDialogBean() {
		filenameText.setText(APP_CTX.getLocalDBFileName());
	}

	/**
	 * @see suncertify.gui.dialog.AbstractDialog#saveDialogBean()
	 */
	protected void saveDialogBean() {
		String oldFile = APP_CTX.getLocalDBFileName();
		APP_CTX.setLocalDBFileName(filenameText.getText().trim());
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
		APP_CTX.setLocalDBFileName(oldFile);
	}
}
