package suncertify.gui.dialog;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import suncertify.gui.util.GUIUtil;

/**
 * <code>AbstractDialog</code> is a base dialog class for all dialogs that are
 * used to input some data with <i>OK</i> and <i>Cancel</i> buttons - it adds
 * some necessary functions to the standard {@link javax.swing.JDialog} class.
 * These functions are:
 * <ul>
 * <li>enable developer to control return value (i.e. information if
 * <i>OK_OPTION</i> or <i>Cancel</i> button was pressed is available)</li>
 * <li>enable control over loading ({@link #loadDialogBean()}) data into
 * dialog widgets and saving ({@link #saveDialogBean()}) data from dialog
 * widgets from and to relevant objects - see </li>
 * <li>enable control over checking if all necessary fields in the current
 * dialog are filled - see {@link #isDialogFilled()}</li>
 * <li>add convenient <code>open()</code>, <code>close()</code> methods
 * instead of <code>setVisible(...)</code></li>
 * </ul>
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 * @see javax.swing.JDialog
 */
public abstract class AbstractDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    /**
	 * <i>Cancel</i> button pressed.
	 */
	public static final int CANCEL_OPTION = 2;

	/**
	 * <i>OK</i> button pressed.
	 */
	public static final int OK_OPTION = 1;

	/**
	 * Undefined option selected (default).
	 */
	public static final int UNDEFINED = 0;

	/*
	 * Common dialog widgets
	 */
	/**
	 * Panel for holding default <i>OK</i> and <i>Cancel</i> buttons.
	 */
	protected JPanel buttonPanel = null;

	/**
	 * Default <i>Cancel</i> button.
	 */
	protected JButton cancelButton = null;

	/**
	 * Default <i>OK</i> button.
	 */
	protected JButton okButton = null;

	/**
	 * Owner component of this dialog window.
	 */
	protected Window owner;

	/**
	 * Return value of this dialog window.
	 */
	protected int returnValue = UNDEFINED;

	/**
	 * @see JDialog#JDialog(java.awt.Dialog, boolean)
	 */
	public AbstractDialog(Dialog owner, boolean modal) throws HeadlessException {
		super(owner, modal);
		this.owner = owner;
	}

	/**
	 * @see JDialog#JDialog(java.awt.Frame, boolean)
	 */
	public AbstractDialog(Frame owner, boolean modal) throws HeadlessException {
		super(owner, modal);
		this.owner = owner;
	}

	/**
	 * This method should be invoked every time <i>Cancel</i> button is pressed
	 * in the current dialog (if such button exists). It simply sets appropriate
	 * return value ({@link #CANCEL_OPTION}) and closes ({@link #close()})
	 * the dialog window.
	 */
	protected void cancelPressed() {
		returnValue = CANCEL_OPTION;
		close();
	}

	/**
	 * Closes current dialog window.
	 * 
	 * @see javax.swing.JComponent#setVisible(boolean)
	 */
	public void close() {
		setVisible(false);
	}

	/**
	 * This method initializes button panel ({@link #buttonPanel}) for <i>OK</i>
	 * and <i>Cancel</i> buttons.
	 * 
	 * @return JPanel - initialized {@link #buttonPanel}.
	 */
	protected JPanel getButtonPanel() {
		if (buttonPanel == null) {
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(1);
			gridLayout.setHgap(5);
			buttonPanel = new JPanel();
			buttonPanel.setLayout(gridLayout);
			buttonPanel.add(getOkButton(), null);
			buttonPanel.add(getCancelButton(), null);
		}
		return buttonPanel;
	}

	/**
	 * This method initializes <i>Cancel</i> button ({@link #cancelButton}).
	 * 
	 * @return JButton - initialized <i>Cancel</i> button ({@link #cancelButton}).
	 */
	protected JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText("Cancel");
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					cancelPressed();
				}
			});
		}
		return cancelButton;
	}

	/**
	 * This method initializes <i>OK</i> button ({@link #okButton}).
	 * 
	 * @return JButton - initialized <i>OK</i> button ({@link #okButton}).
	 */
	protected JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setText("OK");
			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					okPressed();
				}
			});
		}
		return okButton;
	}

	/**
	 * Returns the return value of this dialog - dependent on the option choosen
	 * by the user (OK, Cancel, etc.) in this dialog.
	 * 
	 * @return int - the return value of this dialog - dependent on the option
	 *         choosen by the user (OK, Cancel, etc.) in this dialog.
	 */
	public int getReturnValue() {
		return returnValue;
	}

	/**
	 * This method initializes dialog data - especially provides it with
	 * appropriate content. The most simple implementation of this class
	 * (without any content; causing that dialog will be centered) would be:
	 * 
	 * <pre>
	 * protected void initialize() {
	 * 	setDefaultLocation();
	 * }
	 * </pre>
	 * 
	 */
	protected abstract void initialize();

	/**
	 * Checks if all fields in this dialog are filled properly. The most simple
	 * implementation of this method would be:
	 * 
	 * <pre>
	 * protected boolean isDialogFilled() {
	 * 	return true;
	 * }
	 * </pre>
	 * 
	 * @return boolean - <code>true</code> if all fields in this dialog are
	 *         filled properly and <code>false</code> otherwise.
	 */
	protected abstract boolean isDialogFilled();

	/**
	 * Loads data from dialog beans into relevant widgets.
	 */
	protected abstract void loadDialogBean();

	/**
	 * This method should be invoked every time <i>OK</i> button is pressed in
	 * the current dialog (if such button exists). Firstly it sets appropriate
	 * return value ({@link #OK_OPTION}). It then checks if all fields are
	 * properly filled ({@link #isDialogFilled()}) and if this id true it
	 * saves data (invokes {@link #saveDialogBean()}) from dialog widgets to
	 * relevant objects.
	 */
	protected void okPressed() {
		returnValue = OK_OPTION;
		if (isDialogFilled()) {
			saveDialogBean();
		}
	}

	/**
	 * Opens current dialog window.
	 * 
	 * @see javax.swing.JComponent#setVisible(boolean)
	 */
	public int open() {
		setVisible(true);
		return getReturnValue();
	}

	/**
	 * Saves data from dialog widgets into relevant dialog beans. This method
	 * should close dialog window in case of successfully storage. The most
	 * simple implementation of this method would be:
	 * 
	 * <pre>
	 * protected void saveDialogBean() {
	 * 	close();
	 * }
	 * </pre>
	 */
	protected abstract void saveDialogBean();

	/**
	 * Moves current dialog to the center position relatively to the owner of
	 * this dialog or the whole screen if the owner is <code>null</code>.
	 * 
	 * @see GUIUtil#getCenterLocation(Window, Dimension)
	 */
	protected void setDefaultLocation() {
		this.setLocation(GUIUtil.getCenterLocation(owner, getSize()));
	}
}
