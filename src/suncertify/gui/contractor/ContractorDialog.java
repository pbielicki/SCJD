package suncertify.gui.contractor;

import static suncertify.db.domain.DBMetaInfo.FIELD_LENGTHS_MAP;
import static suncertify.db.domain.DBMetaInfo.FIELD_LOCATION;
import static suncertify.db.domain.DBMetaInfo.FIELD_NAME;
import static suncertify.db.domain.DBMetaInfo.FIELD_OWNER;
import static suncertify.db.domain.DBMetaInfo.FIELD_RATE;
import static suncertify.db.domain.DBMetaInfo.FIELD_SIZE;
import static suncertify.db.domain.DBMetaInfo.UNSAVED_RECORD_NO;
import static suncertify.gui.contractor.ContractorDialog.Mode.*;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.Arrays;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import suncertify.core.ApplicationContext;
import suncertify.core.StringHelper;
import suncertify.db.dao.DaoDuplicateException;
import suncertify.db.dao.DaoException;
import suncertify.db.dao.DaoInitializationException;
import suncertify.db.dao.DaoStaleObjectException;
import suncertify.db.dao.IContractorDao;
import suncertify.db.domain.Contractor;
import suncertify.db.domain.Money;
import suncertify.gui.dialog.AbstractDialog;
import suncertify.gui.util.GUIHelper;
import suncertify.gui.util.GUIUtil;

/**
 * <code>ContractorDialog</code> is responsible for adding and editing contractor's data. After
 * pressing <i>OK</i> button it checks if all necessary fields are filled and then tries to store
 * data into persistent layer. If stored data are not unique dialog shows error message and enables
 * user to change it. In case of any other error dialog closes automatically (it doesn't make sense
 * to retry storage operation if database engine fails or record data no longer exists in database -
 * in case of updating data).
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
class ContractorDialog extends AbstractDialog {

    private static final long serialVersionUID = 1L;

    /**
	 * User dialog message: "Database Error".
	 */
	private static final String MSG_DATABASE_ERROR = "Database Error";

	/**
	 * User dialog message: "Dialog Data".
	 */
	private static final String MSG_DIALOG_DATA = "Dialog Data";

	/**
	 * User dialog message: "Remote Host Error".
	 */
	private static final String MSG_REMOTE_HOST_ERROR = "Remote Host Error";

	/**
	 * <code>Mode</code> is an enum class that allows user to open <code>ContractorDialog</code>
	 * in different modes (e.g. editing, booking).
	 * 
	 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
	 */
	public enum Mode {
		/**
		 * Normal mode in which user can normally type data into all dialog fields.
		 */
		EDIT,

		/**
		 * Booking mode in which user has access only to booking radio buttons (i.e. one cannot
		 * change contractor's data but can change booking information - free it or book it for
		 * concrete owner).
		 */
		BOOK;
	}
	
	/**
	 * DAO for data processing.
	 */
	private IContractorDao dao = null;

	/**
	 * Contractor's data to be displayed in this dialog window.
	 */
	private Contractor dialogBean = null;
	
	/**
	 * Mode in which dialog was open.
	 * @see Mode
	 */
	private Mode mode = EDIT;

	/*
	 * Dialog widgets
	 */
	// Buttons & Radio Buttons
	private JRadioButton availableRadio = null;
	private JRadioButton bookedRadio = null;
	private JButton changeSpecialtiesButton = null;

	// Combo boxes
	private JComboBox currencyCombo = null;

	// Labels
	private JLabel availabilityLabel = null;
	private JLabel locationLabel = null;
	private JLabel nameLabel = null;
	private JLabel rateLabel = null;
	private JLabel sizeLabel = null;
	private JLabel specialtiesLabel = null;

	// Panels & Panes
	private JPanel mainPanel = null;
	private JScrollPane specialtiesScrollPane = null;

	// Spinners
	private JSpinner rateSpinner = null;
	private JSpinner sizeSpinner = null;

	// Text widgets
	private JTextField locationText = null;
	private JTextField nameText = null;
	private JTextField ownerText = null;
	private JTextArea specialtiesText = null;

	/**
	 * Constructs <code>ContractorDialog</code> instance as a <i>modal</i> dialog window. This
	 * constructor refreshes given contractor's data (if not <code>null</code>) in order to check
	 * if it still exists in the persistent layer.
	 * 
	 * @param owner
	 *            Frame - owner window of this dialog.
	 * @param bean
	 *            Contractor - contractor's data to be edited or <code>null</code> if the new one
	 *            is to be created.
	 * @throws DaoInitializationException
	 *             If contractor's data to be edited no longer exist in the persistent layer or
	 *             remote DAO is not available.
	 */
	public ContractorDialog(Frame owner, Contractor bean) throws DaoInitializationException {
		this(owner, bean, EDIT);
	}

	/**
	 * Constructs <code>ContractorDialog</code> instance as a <i>modal</i> dialog window. This
	 * constructor refreshes given contractor's data (if not <code>null</code>) in order to check
	 * if it still exists in the persistent layer.
	 * 
	 * @param owner
	 *            Frame - owner window of this dialog.
	 * @param bean
	 *            Contractor - contractor's data to be edited or <code>null</code> if the new one
	 *            is to be created.
	 * @param mode
	 *            ContractorDialog.Mode - mode in which dialog will be open (see {@link Mode} enum
	 *            for available modes).
	 * @throws DaoInitializationException
	 *             If contractor's data to be edited no longer exist in the persistent layer or
	 *             remote DAO is not available.
	 */
	public ContractorDialog(Frame owner, Contractor bean, Mode mode) throws DaoInitializationException {
		super(owner, true);
		this.mode = mode;
		initContractor(bean);
		initialize();
		loadDialogBean();
	}

	/**
	 * Initializes given contractor object i.e. refreshes its data from the persistent layer.
	 * 
	 * @param bean
	 *            Contractor - contractor to be initialized.
	 * @throws DaoInitializationException
	 *             If contractor's data no longer exist in the persistent layer or remote DAO is not
	 *             available.
	 */
	private void initContractor(Contractor bean) throws DaoInitializationException {
		this.dao = ApplicationContext.getInstance().getContractorDao();
		if (bean == null) {
			dialogBean = new Contractor();
		} else {
			try {
				dialogBean = dao.refresh(bean);
			} catch (Exception e) {
				throw new DaoInitializationException(e.getMessage(), e);
			}
		}
	}

	/**
	 * Action invoked when user clicks <i>Change</i> button in order to change
	 * specialties list. 
	 */
	private void actionChangeSpecialties() {
		SpecialtiesDialog dialog = new SpecialtiesDialog(this, specialtiesText.getText());

		if (dialog.open() == AbstractDialog.OK_OPTION) {
			specialtiesText.setText(dialog.getSpecialties());
		}
	}

	/**
	 * This method initializes {@link #availableRadio}.
	 * 
	 * @return JRadioButton - initialized {@link #availableRadio}.
	 */
	private JRadioButton getAvailableRadio() {
		if (availableRadio == null) {
			availableRadio = new JRadioButton();
			availableRadio.setText("Available");
			availableRadio.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ownerText.setEnabled(false);
				}
			});
		}
		return availableRadio;
	}

	/**
	 * This method initializes {@link #bookedRadio}.
	 * 
	 * @return JRadioButton - initialized {@link #bookedRadio}.
	 */
	private JRadioButton getBookedRadio() {
		if (bookedRadio == null) {
			bookedRadio = new JRadioButton();
			bookedRadio.setText("Booked for:");
			bookedRadio.setToolTipText("Give ID of owner who booked this Contractor.");
			bookedRadio.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ownerText.setEnabled(true);
					ownerText.grabFocus();
				}
			});
		}
		return bookedRadio;
	}

	/**
	 * This method initializes {@link #changeSpecialtiesButton}.
	 * 
	 * @return JButton - initialized {@link #changeSpecialtiesButton}.
	 */
	private JButton getChangeSpecialtiesButton() {
		if (changeSpecialtiesButton == null) {
			changeSpecialtiesButton = new JButton();
			changeSpecialtiesButton.setText("Change");
			changeSpecialtiesButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					actionChangeSpecialties();
				}
			});
		}
		return changeSpecialtiesButton;
	}

	/**
	 * This method initializes {@link #currencyCombo}.
	 * 
	 * @return JComboBox - initialized {@link #currencyCombo}.
	 */
	private JComboBox getCurrencyCombo() {
		if (currencyCombo == null) {
			currencyCombo = new JComboBox(GUIHelper.getInstance().getCurrencies());
		}
		return currencyCombo;
	}

	/**
	 * Return new contractor's data instance basing on user input.
	 * 
	 * @return Contractor - new contractor's data instance basing on user input.
	 */
	public Contractor getDialogBean() {
		Contractor record = new Contractor(dialogBean.toStrings());
		record.setId(dialogBean.getId());
		return record;
	}

	/**
	 * This method initializes {@link #locationText}.
	 * 
	 * @return JTextField - initialized {@link #locationText}.
	 */
	private JTextField getLocationText() {
		if (locationText == null) {
			locationText = new JTextField();
			GUIUtil.setTextLimit(locationText, FIELD_LENGTHS_MAP.get(FIELD_LOCATION));
		}
		return locationText;
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
				constraints.gridx = 1;
				constraints.anchor = GridBagConstraints.CENTER;
				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.gridwidth = 1;
				constraints.weightx = 0.0;
				constraints.ipadx = 0;
				constraints.insets = new Insets(0, 5, 0, 0);
				constraints.ipady = 0;
				constraints.gridy = 0;
				nameLabel = new JLabel();
				nameLabel.setText("Name:");
				mainPanel.add(nameLabel, constraints);
			}
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.gridy = 0;
				constraints.weightx = 1.0;
				constraints.anchor = GridBagConstraints.CENTER;
				constraints.insets = new Insets(5, 5, 2, 5);
				constraints.ipady = 0;
				constraints.gridwidth = 4;
				constraints.gridx = 3;
				mainPanel.add(getNameText(), constraints);
			}
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.gridx = 1;
				constraints.anchor = GridBagConstraints.CENTER;
				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.weightx = 0.0;
				constraints.ipadx = 0;
				constraints.insets = new Insets(0, 5, 0, 0);
				constraints.gridy = 1;
				locationLabel = new JLabel();
				locationLabel.setText("Location:");
				mainPanel.add(locationLabel, constraints);
			}
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.gridx = 1;
				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.insets = new Insets(0, 5, 0, 0);
				constraints.anchor = GridBagConstraints.CENTER;
				constraints.weightx = 0.0;
				constraints.gridy = 2;
				specialtiesLabel = new JLabel();
				specialtiesLabel.setText("Specialties:");
				mainPanel.add(specialtiesLabel, constraints);
			}
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.gridy = 1;
				constraints.weightx = 1.0;
				constraints.ipady = 0;
				constraints.insets = new Insets(2, 5, 2, 5);
				constraints.gridwidth = 4;
				constraints.gridx = 3;
				mainPanel.add(getLocationText(), constraints);
			}
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.gridx = 1;
				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.insets = new Insets(2, 5, 5, 0);
				constraints.anchor = GridBagConstraints.SOUTH;
				constraints.gridy = 3;
				mainPanel.add(getChangeSpecialtiesButton(), constraints);
			}
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.fill = GridBagConstraints.BOTH;
				constraints.gridy = 2;
				constraints.weightx = 1.0;
				constraints.weighty = 1.0;
				constraints.gridheight = 2;
				constraints.insets = new Insets(2, 5, 2, 5);
				constraints.gridwidth = 4;
				constraints.gridx = 3;
				mainPanel.add(getSpecialtiesScrollPane(), constraints);
			}
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.gridx = 1;
				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.insets = new Insets(0, 5, 0, 0);
				constraints.gridy = 4;
				sizeLabel = new JLabel();
				sizeLabel.setText("Size:");
				mainPanel.add(sizeLabel, constraints);
			}
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.fill = GridBagConstraints.NONE;
				constraints.gridy = 4;
				constraints.weightx = 0.0;
				constraints.anchor = GridBagConstraints.WEST;
				constraints.ipadx = 20;
				constraints.insets = new Insets(2, 5, 2, 5);
				constraints.gridx = 3;
				mainPanel.add(getSizeSpinner(), constraints);
			}
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.gridx = 4;
				constraints.weightx = 1.0;
				constraints.anchor = GridBagConstraints.EAST;
				constraints.fill = GridBagConstraints.NONE;
				constraints.insets = new Insets(0, 5, 0, 0);
				constraints.gridy = 4;
				rateLabel = new JLabel();
				rateLabel.setText("Rate per hour:");
				mainPanel.add(rateLabel, constraints);
			}
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.fill = GridBagConstraints.NONE;
				constraints.gridy = 4;
				constraints.weightx = 0.0;
				constraints.anchor = GridBagConstraints.WEST;
				constraints.ipadx = 20;
				constraints.insets = new Insets(2, 5, 2, 5);
				constraints.gridx = 6;
				mainPanel.add(getRateSpinner(), constraints);
			}
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.gridy = 4;
				constraints.weightx = 0.0;
				constraints.ipadx = 10;
				constraints.insets = new Insets(2, 5, 2, 5);
				constraints.gridx = 5;
				mainPanel.add(getCurrencyCombo(), constraints);
			}
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.gridx = 1;
				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.insets = new Insets(0, 5, 0, 0);
				constraints.gridy = 5;
				availabilityLabel = new JLabel();
				availabilityLabel.setText("Availability:");
				mainPanel.add(availabilityLabel, constraints);
			}
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.gridy = 5;
				constraints.weightx = 0.0;
				constraints.insets = new Insets(2, 0, 2, 0);
				constraints.gridx = 3;
				mainPanel.add(getAvailableRadio(), constraints);
			}
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.fill = GridBagConstraints.NONE;
				constraints.gridy = 5;
				constraints.weightx = 0.0;
				constraints.insets = new Insets(2, 0, 2, 0);
				constraints.gridx = 4;
				constraints.anchor = GridBagConstraints.EAST;
				mainPanel.add(getBookedRadio(), constraints);
			}
			ButtonGroup group = new ButtonGroup();
			group.add(availableRadio);
			group.add(bookedRadio);
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.gridy = 5;
				constraints.gridwidth = 2;
				constraints.insets = new Insets(2, 5, 2, 5);
				constraints.gridx = 5;
				constraints.anchor = GridBagConstraints.EAST;
				mainPanel.add(getOwnerText(), constraints);
			}
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.gridx = 5;
				constraints.fill = GridBagConstraints.BOTH;
				constraints.gridwidth = 2;
				constraints.insets = new Insets(5, 5, 5, 5);
				constraints.gridy = 6;
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
			GUIUtil.setTextLimit(nameText, FIELD_LENGTHS_MAP.get(FIELD_NAME));
		}
		return nameText;
	}

	/**
	 * This method initializes {@link #ownerText}.
	 * 
	 * @return JTextField - initialized {@link #ownerText}.
	 */
	private JTextField getOwnerText() {
		if (ownerText == null) {
			ownerText = new JTextField();
			GUIUtil.setIntField(ownerText);
			GUIUtil.setTextLimit(ownerText, FIELD_LENGTHS_MAP.get(FIELD_OWNER));
		}
		return ownerText;
	}

	/**
	 * This method initializes {@link #rateSpinner}.
	 * 
	 * @return JSpinner - initialized {@link #rateSpinner}.
	 */
	private JSpinner getRateSpinner() {
		if (rateSpinner == null) {
			double max = Math.pow(10, FIELD_LENGTHS_MAP.get(FIELD_RATE) - 3) - 1;
			rateSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, max, 1.0));
			rateSpinner.setEditor(new JSpinner.NumberEditor(rateSpinner, "##0.00#"));
		}
		return rateSpinner;
	}

	/**
	 * This method initializes {@link #sizeSpinner}.
	 * 
	 * @return JSpinner - initialized {@link #sizeSpinner}.
	 */
	private JSpinner getSizeSpinner() {
		if (sizeSpinner == null) {
			int max = (int) Math.pow(10, FIELD_LENGTHS_MAP.get(FIELD_SIZE)) - 1;
			sizeSpinner = new JSpinner(new SpinnerNumberModel(0, 0, max, 1));
			sizeSpinner.setEditor(new JSpinner.NumberEditor(sizeSpinner, "##"));
		}
		return sizeSpinner;
	}

	/**
	 * This method initializes {@link #specialtiesScrollPane}.
	 * 
	 * @return JScrollPane - initialized {@link #specialtiesScrollPane}.
	 */
	private JScrollPane getSpecialtiesScrollPane() {
		if (specialtiesScrollPane == null) {
			specialtiesScrollPane = new JScrollPane();
			specialtiesScrollPane.setViewportView(getSpecialtiesText());
		}
		return specialtiesScrollPane;
	}

	/**
	 * This method initializes {@link #specialtiesText}.
	 * 
	 * @return JTextArea - initialized {@link #specialtiesText}.
	 */
	private JTextArea getSpecialtiesText() {
		if (specialtiesText == null) {
			specialtiesText = new JTextArea();
			specialtiesText.setLineWrap(true);
			specialtiesText.setFocusable(false);
			specialtiesText.setEditable(false);
		}
		return specialtiesText;
	}

	/**
	 * @see suncertify.gui.dialog.AbstractDialog#initialize()
	 */
	protected void initialize() {
		this.setSize(520, 220);
		this.setResizable(false);
		if (mode == BOOK) {
			this.setTitle("Contractor (Book)");
		} else {
			this.setTitle("Contractor " + (dialogBean.isStored() ? "(Edit)" : "(Add)"));
		}
		this.setContentPane(getMainPanel());
		setDefaultLocation();
	}

	/**
	 * @see suncertify.gui.dialog.AbstractDialog#isDialogFilled()
	 */
	protected boolean isDialogFilled() {
		// Check contractor's name
		if (nameText.getText().trim().length() <= 0) {
			JOptionPane.showMessageDialog(this, "Please enter contractor's name.", MSG_DIALOG_DATA,
					JOptionPane.INFORMATION_MESSAGE);

			nameText.grabFocus();
			return false;
		}

		// Check contractor's location
		if (locationText.getText().trim().length() <= 0) {
			JOptionPane.showMessageDialog(this, "Please enter contractor's location.", MSG_DIALOG_DATA,
					JOptionPane.INFORMATION_MESSAGE);

			locationText.grabFocus();
			return false;
		}

		// Check contractor's specialties
		if (specialtiesText.getText().trim().length() <= 0) {
			JOptionPane.showMessageDialog(this, "Please enter at least one contractor's specialty.", MSG_DIALOG_DATA,
					JOptionPane.INFORMATION_MESSAGE);

			actionChangeSpecialties();
			changeSpecialtiesButton.grabFocus();
			return false;
		}

		// Check contractor's owner
		if (bookedRadio.isSelected() && ownerText.getText().trim().length() <= 0) {
			JOptionPane.showMessageDialog(this, "Please enter owner that is to book this contractor.", MSG_DIALOG_DATA,
					JOptionPane.INFORMATION_MESSAGE);

			ownerText.grabFocus();
			return false;
		}

		return true;
	}

	/**
	 * @see suncertify.gui.dialog.AbstractDialog#loadDialogBean()
	 */
	protected void loadDialogBean() {
		nameText.setText(dialogBean.getName().trim());
		locationText.setText(dialogBean.getLocation().trim());
		specialtiesText.setText(dialogBean.getSpecialties().trim());
		sizeSpinner.setValue(dialogBean.getSize());
		currencyCombo.setSelectedItem(dialogBean.getRate().getCurrency());
		rateSpinner.setValue(dialogBean.getRate().getValue());
		ownerText.setText(StringHelper.toString(dialogBean.getOwner(), UNSAVED_RECORD_NO));

		boolean isBooked = ownerText.getText().length() > 0;
		ownerText.setEnabled(isBooked);
		availableRadio.setSelected(!isBooked);
		bookedRadio.setSelected(isBooked);

		if (mode == BOOK) {
			nameLabel.setEnabled(false);
			nameText.setEnabled(false);
			nameText.setFocusable(false);
			locationLabel.setEnabled(false);
			locationText.setEnabled(false);
			locationText.setFocusable(false);
			specialtiesLabel.setEnabled(false);
			specialtiesText.setEnabled(false);
			changeSpecialtiesButton.setEnabled(false);
			sizeLabel.setEnabled(false);
			sizeSpinner.setEnabled(false);
			rateLabel.setEnabled(false);
			currencyCombo.setEnabled(false);
			rateSpinner.setEnabled(false);
			ownerText.setEnabled(true);
			availableRadio.setEnabled(false);
			bookedRadio.setSelected(true);
			bookedRadio.setFocusable(false);
			ownerText.grabFocus();
		}
	}

	/**
	 * @see suncertify.gui.dialog.AbstractDialog#saveDialogBean()
	 */
	protected void saveDialogBean() {
		Contractor record = new Contractor(nameText.getText(), locationText.getText());
		record.setSpecialties(specialtiesText.getText());
		record.setSize((Integer) sizeSpinner.getValue());
		record.setRate(new Money((Double) rateSpinner.getValue(), (String) currencyCombo.getSelectedItem()));

		if (bookedRadio.isSelected()) {
			record.setOwner(StringHelper.intValue(ownerText.getText(), UNSAVED_RECORD_NO));
		}
		record.setId(dialogBean.getId());

		// check if record's data changed at all
		if (Arrays.equals(record.toStrings(), dialogBean.toStrings())) {
			cancelPressed();
			return;
		}

		try {
			if (record.isStored()) {
				dao.update(record);
			} else {
				record.setId(dao.store(record));
			}
			dialogBean = record;

		} catch (DaoStaleObjectException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), MSG_DATABASE_ERROR, JOptionPane.ERROR_MESSAGE);

		} catch (DaoDuplicateException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), MSG_DATABASE_ERROR, JOptionPane.WARNING_MESSAGE);
			return;

		} catch (DaoException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), MSG_DATABASE_ERROR, JOptionPane.ERROR_MESSAGE);

		} catch (RemoteException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), MSG_REMOTE_HOST_ERROR, JOptionPane.ERROR_MESSAGE);
		}
		close();
	}
}
