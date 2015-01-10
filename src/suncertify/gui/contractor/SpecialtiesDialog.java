package suncertify.gui.contractor;

import static suncertify.db.domain.DBMetaInfo.FIELD_LENGTHS_MAP;
import static suncertify.db.domain.DBMetaInfo.FIELD_SPECIALTIES;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import suncertify.gui.dialog.AbstractDialog;
import suncertify.gui.util.GUIHelper;

/**
 * <code>SpecialtiesDialog</code> enables user to change specialties list for
 * the contractor that tis currently being edited.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
class SpecialtiesDialog extends AbstractDialog {

    private static final long serialVersionUID = 1L;

    /**
	 * Delimiter for record's data - comma (,).
	 */
	private static final String DELIM = ",";

	/*
	 * Dialog widgets
	 */
	// Buttons
	private JButton addButton = null;
	private JButton deleteButton = null;

	// Combo boxes
	private JComboBox specialtyCombo = null;

	// Lists
	private JList specialtyList = null;

	// Panels & Panes
	private JPanel changeButtonPanel = null;
	private JPanel mainPanel = null;
	private JScrollPane specialtyScrollPane = null;
	
	/**
	 * Maximum number of specialties user can choose.
	 */
	private int capacity = Integer.MAX_VALUE;

	/**
	 * Specialties string - comma separated specialties that user selected.
	 */
	private String specialties;

	/**
	 * Specialties selected by the user.
	 */
	private List<String> specialtiesList = new ArrayList<String>();

	/**
	 * Constructs <code>SpecialtiesDialog</code> instance as a <i>modal</i>
	 * dialog window.
	 * 
	 * @param owner
	 *            JDialog - owner window of this dialog.
	 * @param specialties
	 *            String - specialties with which dialog will be initialized.
	 */
	public SpecialtiesDialog(JDialog owner, String specialties) {
		super(owner, true);
		this.specialties = specialties;
		initialize();
		loadDialogBean();
	}

	/**
	 * Action invoked when user clicks <i>Add</i> button in order to change
	 * selected specialties list.
	 */
	private void actionAdd() {
		if (!specialtiesList.contains(specialtyCombo.getSelectedItem())
				&& specialtiesList.size() < capacity) {

			List<String> list = new ArrayList<String>(specialtiesList);
			list.add((String) specialtyCombo.getSelectedItem());
			if (createSpecialties(list).length() > FIELD_LENGTHS_MAP
					.get(FIELD_SPECIALTIES)) {

				JOptionPane.showMessageDialog(this, "Unable to add this "
						+ "specialty because specialties list is too long.",
						"Specialties", JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			specialtiesList.add((String) specialtyCombo.getSelectedItem());
			specialtyList.setListData(specialtiesList
					.toArray(new String[specialtiesList.size()]));
			specialtyList.setSelectedValue(specialtyCombo.getSelectedItem(),
					true);
		}
	}

	/**
	 * Action invoked when user clicks <i>Delete</i> button in order to change
	 * selected specialties list.
	 */
	private void actionDelete() {
		if (specialtyList.getSelectedValue() != null) {
			String sel = (String) specialtyList.getSelectedValue();
			if (specialtiesList.remove(sel)) {
				specialtyList.setListData(specialtiesList
						.toArray(new String[specialtiesList.size()]));
				specialtyList.setSelectedIndex(0);
			}
		}
	}

	/**
	 * Creates specialties string (comma separated specialties) created from
	 * given specialties list.
	 * 
	 * @param list
	 *            List&lt;String&gt; - specialties list.
	 * @return String - specialties string (comma separated specialties) created
	 *         from given specialties list.
	 */
	private String createSpecialties(List<String> list) {
		boolean first = true;
		StringBuilder sb = new StringBuilder();
		for (String specialty : list) {
			if (first) {
				sb.append(specialty);
				first = false;
			} else {
				sb.append(", ").append(specialty);
			}
		}
		return sb.toString();
	}

	/**
	 * This method initializes {@link #addButton}.
	 * 
	 * @return JButton - initialized {@link #addButton}.
	 */
	private JButton getAddButton() {
		if (addButton == null) {
			addButton = new JButton();
			addButton.setText("Add");
			addButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					actionAdd();
				}
			});
		}
		return addButton;
	}

	/**
	 * This method initializes {@link #changeButtonPanel}.
	 * 
	 * @return JPanel - initialized {@link #changeButtonPanel}.
	 */
	private JPanel getChangeButtonPanel() {
		if (changeButtonPanel == null) {
			GridLayout gridLayout1 = new GridLayout();
			gridLayout1.setRows(1);
			gridLayout1.setVgap(5);
			gridLayout1.setHgap(5);
			changeButtonPanel = new JPanel();
			changeButtonPanel.setLayout(gridLayout1);
			changeButtonPanel.add(getAddButton(), null);
			changeButtonPanel.add(getDeleteButton(), null);
		}
		return changeButtonPanel;
	}

	/**
	 * This method initializes {@link #deleteButton}.
	 * 
	 * @return JButton - initialized {@link #deleteButton}.
	 */
	private JButton getDeleteButton() {
		if (deleteButton == null) {
			deleteButton = new JButton();
			deleteButton.setText("Delete");
			deleteButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					actionDelete();
				}
			});
		}
		return deleteButton;
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
				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.gridy = 0;
				constraints.weightx = 1.0;
				constraints.insets = new Insets(5, 5, 2, 5);
				constraints.gridwidth = 1;
				constraints.gridx = 0;
				mainPanel.add(getSpecialtyCombo(), constraints);
			}
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.gridx = 1;
				constraints.fill = GridBagConstraints.BOTH;
				constraints.insets = new Insets(5, 5, 2, 5);
				constraints.gridy = 0;
				mainPanel.add(getChangeButtonPanel(), constraints);
			}
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.fill = GridBagConstraints.BOTH;
				constraints.gridy = 1;
				constraints.weightx = 1.0;
				constraints.weighty = 1.0;
				constraints.insets = new Insets(2, 5, 5, 5);
				constraints.gridwidth = 1;
				constraints.gridx = 0;
				mainPanel.add(getSpecialtyScrollPane(), constraints);
			}
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.gridx = 1;
				constraints.gridwidth = 1;
				constraints.fill = GridBagConstraints.BOTH;
				constraints.insets = new Insets(5, 5, 5, 5);
				constraints.gridy = 2;
				mainPanel.add(getButtonPanel(), constraints);
			}
		}
		return mainPanel;
	}

	/**
	 * Returns the comma separated string of specialties chosen by the user in this
	 * dialog window.
	 * 
	 * @return String - the comma separated string of specialties chosen by the user in this
	 * dialog window..
	 */
	public String getSpecialties() {
		return specialties;
	}

	/**
	 * This method initializes {@link #specialtyCombo}.
	 * 
	 * @return JComboBox - initialized {@link #specialtyCombo}.
	 */
	private JComboBox getSpecialtyCombo() {
		if (specialtyCombo == null) {
			specialtyCombo = new JComboBox(GUIHelper.getInstance()
					.getSpecialties());
			specialtyCombo.setEditable(true);
		}
		return specialtyCombo;
	}

	/**
	 * This method initializes {@link #specialtyList}.
	 * 
	 * @return JList - initialized {@link #specialtyList}.
	 */
	private JList getSpecialtyList() {
		if (specialtyList == null) {
			specialtyList = new JList();
		}
		return specialtyList;
	}

	/**
	 * This method initializes {@link #specialtyScrollPane}.
	 * 
	 * @return JScrollPane - initialized {@link #specialtyScrollPane}.
	 */
	private JScrollPane getSpecialtyScrollPane() {
		if (specialtyScrollPane == null) {
			specialtyScrollPane = new JScrollPane();
			specialtyScrollPane.setViewportView(getSpecialtyList());
		}
		return specialtyScrollPane;
	}

	/**
	 * @see suncertify.gui.dialog.AbstractDialog#initialize()
	 */
	protected void initialize() {
		this.setSize(450, 220);
		this.setTitle("Contractor's Specialties");
		this.setResizable(false);
		this.setContentPane(getMainContentPanel());
		setDefaultLocation();
	}

	/**
	 * @see suncertify.gui.dialog.AbstractDialog#isDialogFilled()
	 */
	protected boolean isDialogFilled() {
		// Check specialties list
		if (specialtiesList.size() <= 0) {
			JOptionPane.showMessageDialog(this,
					"Please add at least one specialty.", "Dialog Data",
					JOptionPane.INFORMATION_MESSAGE);

			addButton.grabFocus();
			return false;
		}

		return true;
	}

	/**
	 * @see suncertify.gui.dialog.AbstractDialog#loadDialogBean()
	 */
	protected void loadDialogBean() {
		String[] tmp = specialties.split(DELIM);
		for (String string : tmp) {
			if (string.trim().length() > 0) {
				if (!specialtiesList.contains(string)) {
					specialtiesList.add(string.trim());
				}
			}
		}

		specialtyList.setListData(specialtiesList
				.toArray(new String[specialtiesList.size()]));
	}

	/**
	 * @see suncertify.gui.dialog.AbstractDialog#saveDialogBean()
	 */
	protected void saveDialogBean() {
		specialties = createSpecialties(specialtiesList);
		close();
	}
}
