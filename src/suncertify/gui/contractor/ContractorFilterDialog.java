package suncertify.gui.contractor;

import static suncertify.db.dao.ComparisonOperator.EQUAL;
import static suncertify.db.dao.ComparisonOperator.GREATER;
import static suncertify.db.dao.ComparisonOperator.GREATER_OR_EQUAL;
import static suncertify.db.dao.ComparisonOperator.LESS;
import static suncertify.db.dao.ComparisonOperator.LESS_OR_EQUAL;
import static suncertify.db.dao.ComparisonOperator.NOT_EQUAL;
import static suncertify.db.dao.ComparisonOperator.STARTS_WITH;
import static suncertify.db.domain.DBMetaInfo.FIELD_LENGTHS_MAP;
import static suncertify.db.domain.DBMetaInfo.FIELD_LOCATION;
import static suncertify.db.domain.DBMetaInfo.FIELD_NAME;
import static suncertify.db.domain.DBMetaInfo.FIELD_OWNER;
import static suncertify.db.domain.DBMetaInfo.FIELD_RATE;
import static suncertify.db.domain.DBMetaInfo.FIELD_SIZE;
import static suncertify.db.domain.DBMetaInfo.UNSAVED_RECORD_NO;
import static suncertify.gui.contractor.ContractorFilter.Availability.ALL;
import static suncertify.gui.contractor.ContractorFilter.Availability.AVAILABLE;
import static suncertify.gui.contractor.ContractorFilter.Availability.BOOKED;
import static suncertify.gui.contractor.ContractorFilter.Availability.BOOKED_FOR;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import suncertify.core.StringHelper;
import suncertify.db.dao.ComparisonOperator;
import suncertify.db.domain.Contractor;
import suncertify.db.domain.Money;
import suncertify.gui.dialog.AbstractDialog;
import suncertify.gui.util.GUIUtil;

/**
 * <code>ContractorFilterDialog</code> is responsible for creating the filter
 * bean ({@link ContractorFilter}) that is to be used to search required
 * records from the persistent layer.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
class ContractorFilterDialog extends AbstractDialog {

    private static final long serialVersionUID = 1L;

    /**
	 * Comparison operators that are to be available in the relevant combo
	 * boxes.
	 */
	private final static ComparisonOperator[] OPERATORS = new ComparisonOperator[] {
			GREATER, GREATER_OR_EQUAL, EQUAL, LESS_OR_EQUAL, LESS };

	/**
	 * Contractor's data to be displayed in this dialog window.
	 */
	private ContractorFilter filterBean = null;

	/*
	 * Dialog widgets
	 */
	// Buttons & Radio Buttons
	private JRadioButton allRadio = null;
	private JRadioButton availableRadio = null;
	private JRadioButton bookedRadio = null;
	private JButton changeSpecialtiesButton = null;

	// Combo boxes
	private JComboBox rateOperatorCombo = null;
	private JComboBox sizeOperatorCombo = null;

	// Labels
	private JLabel locationLabel = null;
	private JLabel nameLabel = null;
	private JLabel availabilityLabel = null;
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
	 * Constructs <code>ContractorFilterDialog</code> instance as a <i>modal</i>
	 * dialog window.
	 * 
	 * @param owner
	 *            Frame - owner window of this dialog.
	 * @param bean
	 *            ContractorFilter - contractor's filter data to be edited or
	 *            <code>null</code> if the new one is to be created.
	 */
	public ContractorFilterDialog(Frame owner, ContractorFilter bean) {
		super(owner, true);
		if (bean == null) {
			filterBean = new ContractorFilter();
		} else {
			filterBean = bean;
		}
		initialize();
		loadDialogBean();
	}

	/**
	 * This method initializes {@link #allRadio}.
	 * 
	 * @return JRadioButton - initialized {@link #allRadio}.
	 */
	private JRadioButton getAllRadio() {
		if (allRadio == null) {
			allRadio = new JRadioButton();
			allRadio.setText("All");
			allRadio.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ownerText.setEnabled(false);
				}
			});
		}
		return allRadio;
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
			final JDialog thisDialog = this;
			changeSpecialtiesButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SpecialtiesDialog dialog = new SpecialtiesDialog(thisDialog, specialtiesText.getText());
					dialog.open();
					specialtiesText.setText(dialog.getSpecialties());
				}
			});
		}
		return changeSpecialtiesButton;
	}

	/**
	 * Returns the contractor's filter dialog.
	 * 
	 * @return ContractorFilter - the contractor's filter dialog.
	 */
	public ContractorFilter getFilterBean() {
		return filterBean;
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
	private JPanel getMainContentPanel() {
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
				constraints.gridwidth = 5;
				constraints.gridx = 2;
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
				constraints.gridwidth = 5;
				constraints.gridx = 2;
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
				constraints.gridwidth = 5;
				constraints.gridx = 2;
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
				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.gridy = 4;
				constraints.weightx = 1.0;
				//constraints.ipadx = 10;
				constraints.insets = new Insets(2, 5, 2, 5);
				constraints.gridx = 2;
				mainPanel.add(getSizeOperatorCombo(), constraints);
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
				//constraints.ipadx = 10;
				constraints.insets = new Insets(2, 5, 2, 5);
				constraints.gridx = 5;
				mainPanel.add(getRateOperatorCombo(), constraints);
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
				constraints.fill = GridBagConstraints.EAST;
				constraints.gridy = 5;
				constraints.weightx = 0.0;
				constraints.insets = new Insets(2, 0, 2, 0);
				constraints.gridx = 2;
				mainPanel.add(getAllRadio(), constraints);
			}
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.fill = GridBagConstraints.CENTER;
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
				constraints.gridwidth = 2;
				constraints.gridx = 4;
				constraints.anchor = GridBagConstraints.EAST;
				mainPanel.add(getBookedRadio(), constraints);
			}
			ButtonGroup group = new ButtonGroup();
			group.add(allRadio);
			group.add(availableRadio);
			group.add(bookedRadio);
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.gridy = 5;
				constraints.insets = new Insets(2, 5, 2, 5);
				constraints.gridx = 6;
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
				okButton.setText("Search");
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
			ownerText.setEnabled(false);
		}
		return ownerText;
	}

	/**
	 * This method initializes {@link #rateOperatorCombo}.
	 * 
	 * @return JComboBox - initialized {@link #rateOperatorCombo}.
	 */
	private JComboBox getRateOperatorCombo() {
		if (rateOperatorCombo == null) {
			rateOperatorCombo = new JComboBox(OPERATORS);
		}
		return rateOperatorCombo;
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
			rateSpinner.setFocusable(false);
		}
		return rateSpinner;
	}

	/**
	 * This method initializes {@link #sizeOperatorCombo}.
	 * 
	 * @return JComboBox - initialized {@link #sizeOperatorCombo}.
	 */
	private JComboBox getSizeOperatorCombo() {
		if (sizeOperatorCombo == null) {
			sizeOperatorCombo = new JComboBox(OPERATORS);
		}
		return sizeOperatorCombo;
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
			sizeSpinner.setFocusable(false);
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
		this.setSize(530, 220);
		this.setName("ContractorDialog");
		this.setResizable(false);
		this.setTitle("Contractor's Search Filter");
		this.setContentPane(getMainContentPanel());
		setDefaultLocation();
	}

	/**
	 * @see suncertify.gui.dialog.AbstractDialog#isDialogFilled()
	 */
	protected boolean isDialogFilled() {
		return true;
	}

	/**
	 * @see suncertify.gui.dialog.AbstractDialog#loadDialogBean()
	 */
	protected void loadDialogBean() {
		Contractor c = filterBean.getContractor();

		nameText.setText(c.getName().trim());
		nameLabel.setEnabled(filterBean.getOperator(FIELD_NAME) != EQUAL);
		nameText.setEditable(filterBean.getOperator(FIELD_NAME) != EQUAL);
		
		locationText.setText(c.getLocation().trim());
		locationLabel.setEnabled(filterBean.getOperator(FIELD_LOCATION) != EQUAL);
		locationText.setEditable(filterBean.getOperator(FIELD_LOCATION) != EQUAL);

		specialtiesText.setText(c.getSpecialties().trim());
		sizeOperatorCombo.setSelectedItem(filterBean.getOperator(FIELD_SIZE));
		sizeSpinner.setValue(c.getSize());
		rateOperatorCombo.setSelectedItem(filterBean.getOperator(FIELD_RATE));
		rateSpinner.setValue(c.getRate().getValue());

		// All records
		if (filterBean.getAvailability() == ALL) {
			allRadio.setSelected(true);

		// Available records
		} else if  (filterBean.getAvailability() == AVAILABLE) {
			availableRadio.setSelected(true);
			
		// Booked records
		} else if (filterBean.getAvailability() == BOOKED) {
			bookedRadio.setSelected(true);
			ownerText.setEnabled(true);
			ownerText.setText(StringHelper.toString(c.getOwner(), UNSAVED_RECORD_NO));
		
		// Records booked for specific owner
		} else {
			bookedRadio.setSelected(true);
			ownerText.setEnabled(true);
			ownerText.setText(StringHelper.toString(c.getOwner(), UNSAVED_RECORD_NO));
		}
	}

	/**
	 * @see suncertify.gui.dialog.AbstractDialog#saveDialogBean()
	 */
	protected void saveDialogBean() {
		Contractor contractor = new Contractor(nameText.getText(), locationText.getText());
		contractor.setSpecialties(specialtiesText.getText());
		contractor.setSize((Integer) sizeSpinner.getValue());
		contractor.setRate(new Money((Double) rateSpinner.getValue(), " "));

		filterBean = new ContractorFilter(contractor);
		// Determine if name operator is "equal" or "starts with"
		if (nameText.isEditable()) {
			filterBean.setOperator(FIELD_NAME, STARTS_WITH);
		} else {
			filterBean.setOperator(FIELD_NAME, EQUAL);			
		}
		
		// Determine if location operator is "equal" or "starts with"
		if (locationText.isEditable()) {
			filterBean.setOperator(FIELD_LOCATION, STARTS_WITH);
		} else {
			filterBean.setOperator(FIELD_LOCATION, EQUAL);
		}
		filterBean.setOperator(FIELD_SIZE, (ComparisonOperator) sizeOperatorCombo.getSelectedItem());
		filterBean.setOperator(FIELD_RATE, (ComparisonOperator) rateOperatorCombo.getSelectedItem());

		// All records
		if (allRadio.isSelected()) {
			contractor.free();
			filterBean.setAvailability(ALL);

		// Available records
		} else if (availableRadio.isSelected()) {
			contractor.free();
			filterBean.setOperator(FIELD_OWNER, EQUAL);
			filterBean.setAvailability(AVAILABLE);

		// Booked...
		} else if (bookedRadio.isSelected()) {
			contractor.setOwner(StringHelper.intValue(ownerText.getText(), UNSAVED_RECORD_NO));
			// Booked records
			if (contractor.getOwner() == UNSAVED_RECORD_NO) {
				filterBean.setOperator(FIELD_OWNER, NOT_EQUAL);
				filterBean.setAvailability(BOOKED);
				
			// Records booked for specific owner
			} else {
				filterBean.setOperator(FIELD_OWNER, EQUAL);
				filterBean.setAvailability(BOOKED_FOR);				
			}
		}
		close();
	}
}
