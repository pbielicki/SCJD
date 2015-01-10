package suncertify.gui.contractor;

import static suncertify.db.dao.ComparisonOperator.EQUAL;
import static suncertify.db.domain.DBMetaInfo.getFieldNo;

import java.awt.Dimension;
import java.awt.Event;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import suncertify.core.ApplicationContext;
import suncertify.db.dao.DaoDuplicateException;
import suncertify.db.dao.DaoException;
import suncertify.db.dao.DaoInitializationException;
import suncertify.db.dao.DaoStaleObjectException;
import suncertify.db.dao.IContractorDao;
import suncertify.db.domain.Contractor;
import suncertify.gui.AbstractWindow;
import suncertify.gui.contractor.ContractorDialog.Mode;
import suncertify.gui.dialog.AbstractDialog;
import suncertify.gui.util.GUIUtil;

/**
 * <code>MainWindow</code> consists of contractors list, buttons that enable
 * data management and appropriate menu bar. Menu bar's menus have the same
 * functionality as buttons and add additional configuration options and help
 * menu.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
public class MainWindow extends AbstractWindow {

    private static final long serialVersionUID = 1L;

    /**
	 * User dialog message: "Contractor Freeing".
	 */
	private static final String MSG_CONTRACTOR_FREEING = "Contractor Freeing";
	
	/**
	 * User dialog message: "Window Initialization".
	 */
	private static final String MSG_DAO_INITIALIZATION = "Window Initialization";
	
	/**
	 * User dialog message: "Database Error".
	 */
	private static final String MSG_DATABASE_ERROR = "Database Error";
	
	/**
	 * User dialog message: "Remote Host Error".
	 */
	private static final String MSG_REMOTE_HOST_ERROR = "Remote Host Error";
	
	/**
	 * User dialog message: "Searching".
	 */
	private static final String MSG_SEARCHING = "Searching";
	
	/*
	 * Window widgets
	 */
	// Buttons
	private JButton addButton = null;
	private JButton allButton = null;
	private JButton bookButton = null;
	private JButton deleteButton = null;
	private JButton editButton = null;
	private JButton exitButton = null;
	private JButton freeButton = null;
	private JButton searchButton = null;

	// Menu widgets
	private JMenuItem addMenuItem = null;
	private JMenuItem allRecordsMenuItem = null;
	private JMenuItem bookMenuItem = null;
	private JMenu contractorMenu = null;
	private JMenuItem deleteMenuItem = null;
	private JMenuItem editMenuItem = null;
	private JMenuItem freeMenuItem = null;
	private JMenuBar mainMenuBar = null;
	private JMenu searchMenu = null;
	private JMenuItem searchMenuItem = null;

	// Panels & Panes
	private JScrollPane contractorScrollPane = null;
	private JSplitPane dataSplitPane = null;
	private JPanel mainPanel = null;

	// Tables & Trees
	private JTable contractorTable = null;
	private JTree filterTree = null;

	/**
	 * Table model for contractors list.
	 */
	private ContractorTableModel contractorTableModel = null;

	/**
	 * Contractors list.
	 */
	private List<Contractor> contractorsList;

	/**
	 * DAO for retrieving data for table.
	 */
	private IContractorDao dao = null;

	/**
	 * Current search filter.
	 */
	private ContractorFilter filterBean;
	
	/**
	 * Last selected path on tree.
	 */
	private TreePath[] lastSelectedPaths = null;

	/**
	 * Indicates if main window is currently retrieving to the previous state - because of database
	 * exception.
	 */
	private boolean reverting = false;

	/**
	 * Actually selected contractor on the contractors table.
	 */
	private Contractor selectedContractor = null;
	
	/**
	 * Constructs and initializes <code>MainWindow</code> instance.
	 */
	public MainWindow() {
		super();
		dao = ApplicationContext.getInstance().getContractorDao();
		try {
			contractorsList = dao.load();
		} catch (RemoteException e) {
			contractorsList = new ArrayList<Contractor>();
		}
		initialize();
	}

	/**
	 * Action invoked when user clicks <i>Add</i> button (or relevant menu
	 * item) in order to add new contractor to database.
	 */
	private void actionAdd() {
		ContractorDialog dialog;
		try {
			dialog = new ContractorDialog(this, null);
		} catch (DaoInitializationException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), MSG_DAO_INITIALIZATION, JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (dialog.open() == AbstractDialog.OK_OPTION) {
			Contractor newRecord = dialog.getDialogBean();
			if (newRecord.isStored()) {
				contractorsList.add(newRecord);
				selectedContractor = newRecord;
				refreshTable();
				refreshTree();
			}
		}
	}

	/**
	 * Sets the selected contractor's bean to the object that is actually selected in the
	 * contractors table.
	 */
	private void setSelectedContractor() {
		if (contractorTable.getSelectedRow() > -1 && contractorTable.getSelectedRow() < contractorsList.size()) {
			selectedContractor = contractorsList.get(contractorTable.getSelectedRow());
		} else {
			selectedContractor = null;
		}
	}
	
	/**
	 * Action invoked when user clicks <i>All Records</i> button (or relevant
	 * menu item) in order to show all records in the table (clear filter at the
	 * same time).
	 */
	private void actionAllRecords() {
		setSelectedContractor();
		ContractorFilter oldFilter = filterBean;
		filterBean = new ContractorFilter();
		try {
			contractorsList.clear();
			contractorsList.addAll(dao.load());
			contractorTableModel.sort();
		} catch (RemoteException e) {
			filterBean = oldFilter;
			JOptionPane.showMessageDialog(this, e.getMessage(), MSG_REMOTE_HOST_ERROR, JOptionPane.ERROR_MESSAGE);
		}
		
		refreshTable();
		refreshTree();
	}

	/**
	 * Action invoked when user clicks <i>Delete</i> button (or relevant menu
	 * item) in order to delete selected in the table contractor from database.
	 */
	private void actionDelete() {
		int selectionIndex = contractorTable.getSelectedRow();
		if (selectionIndex >= 0) {
			if (JOptionPane.showConfirmDialog(this, "Do you really want to delete selected contractor?",
					"Contractor Deletion", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

				try {
					dao.remove(contractorsList.get(selectionIndex));
					contractorsList.remove(selectionIndex);
					refreshTable();
					refreshTree();
				} catch (DaoException e) {
					JOptionPane.showMessageDialog(this, e.getMessage(), MSG_DATABASE_ERROR, JOptionPane.ERROR_MESSAGE);
				} catch (RemoteException e) {
					JOptionPane.showMessageDialog(this, e.getMessage(), MSG_REMOTE_HOST_ERROR, JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	/**
	 * Action invoked when user clicks <i>Edit</i> or <i>Book</i> button (or relevant menu item)
	 * in order to update/edit/book selected contractor's data in the database.
	 * 
	 * @param mode
	 *            ContractorDialog.Mode - see
	 *            {@link ContractorDialog#ContractorDialog(Frame, Contractor, Mode)} and
	 *            {@link ContractorDialog.Mode}.
	 */
	private void actionEdit(Mode mode) {
		int selectionIndex = contractorTable.getSelectedRow();
		if (selectionIndex >= 0) {
			ContractorDialog dialog;
			try {
				dialog = new ContractorDialog(this, contractorsList.get(selectionIndex), mode);
			} catch (DaoInitializationException e) {
				JOptionPane.showMessageDialog(this, "Could not edit selected contractor:\n" + e.getMessage(),
						MSG_DAO_INITIALIZATION, JOptionPane.ERROR_MESSAGE);

				actionAllRecords();
				return;
			}

			if (dialog.open() == AbstractDialog.OK_OPTION) {
				contractorsList.remove(selectionIndex);
				Contractor record = dialog.getDialogBean();
				contractorsList.add(selectionIndex, record);
				selectedContractor = record;
				refreshTable();
				refreshTree();
			}
		}
	}

	/**
	 * Action invoked when user clicks <i>Free</i> button (or relevant menu item) in order to free
	 * book info from selected contractor's data in the database.
	 */
	private void actionFree() {
		int selectionIndex = contractorTable.getSelectedRow();
		if (selectionIndex >= 0) {
			Contractor record = contractorsList.get(selectionIndex);
			// Check if contractor is already booked by anyone
			if (record.isAvailable()) {
				JOptionPane.showMessageDialog(this, "Selected contractor is already available.",
						MSG_CONTRACTOR_FREEING, JOptionPane.INFORMATION_MESSAGE);
				
				return;
			}
			
			// Ask user about confirmation
			if (JOptionPane.showConfirmDialog(this, "Do you really want to free booking info from "
					+ "selected contractor?", MSG_CONTRACTOR_FREEING, JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {

				return;
			}
			
			try {
				record.free();
				dao.update(record);
				selectedContractor = record;
				refreshTable();
				refreshTree();
			} catch (DaoStaleObjectException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), MSG_DATABASE_ERROR, JOptionPane.ERROR_MESSAGE);

			} catch (DaoDuplicateException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), MSG_DATABASE_ERROR, JOptionPane.ERROR_MESSAGE);

			} catch (RemoteException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), MSG_REMOTE_HOST_ERROR, JOptionPane.ERROR_MESSAGE);
				
			} catch (DaoException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), MSG_DATABASE_ERROR, JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Action invoked when user clicks <i>Search</i> button (or relevant menu
	 * item) in order to search required contractors in the database.
	 */
	private void actionSearch() {
		ContractorFilterDialog dialog = new ContractorFilterDialog(this, filterBean);
		if (dialog.open() == AbstractDialog.OK_OPTION && dialog.getFilterBean() != null) {
			ContractorFilter oldFilter = filterBean;
			filterBean = dialog.getFilterBean();
			if (!applyFilter(filterBean)) {
				filterBean = oldFilter;
			}
		}
	}
	
	/**
	 * Reverts tree selection to the previous state.
	 * 
	 * @see #lastSelectedPaths
	 * @see #reverting
	 */
	private void revertTreeSelection() {
		if (lastSelectedPaths == null) {
			return;
		}
		
		reverting = true;
		try {
			filterTree.clearSelection();
			for (int i = 0; i < lastSelectedPaths.length; ++i) {
				filterTree.addSelectionPath(lastSelectedPaths[i]);
			}
		} finally {
			reverting = false;
		}
	}
	
	/**
	 * Action invoked when user changes ones selection on the filter tree (tree
	 * on the left panel).
	 */
	private void actionTreeSelectionChanged() {
		setSelectedContractor();
		// Do not do anything when reverting to the previous state
		if (reverting) {
			return;
		}
		
		TreePath[] selectedPaths = filterTree.getSelectionPaths();
		if (selectedPaths == null || selectedPaths.length == 0) {
			return;
		}

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectedPaths[selectedPaths.length - 1]
				.getLastPathComponent();
		if (node == null) {
			return;
		}

		// Root node selected - show all records
		if (node.getParent() == null) {
			lastSelectedPaths = selectedPaths;
			actionAllRecords();
			return;
		}
		
		// 
		if (node.getPath().length < 3) {
			filterTree.removeSelectionPath(selectedPaths[selectedPaths.length - 1]);
			revertTreeSelection();
			return;
			
		// Leaf node selected - filter records according to selection
		} else {
			String[] filter = new Contractor().toStrings();
			// set filter
			for (int i = 0; i < selectedPaths.length; ++i) {
				String field = selectedPaths[i].getParentPath().getLastPathComponent().toString().toLowerCase();
				filter[getFieldNo(field)] = selectedPaths[i].getLastPathComponent().toString();
			}
			
			ContractorFilter oldFilter = filterBean;
			// create filter bean
			filterBean = new ContractorFilter(new Contractor(filter));
			
			// set comparison operators
			for (int i = 0; i < selectedPaths.length; ++i) {
				String field = selectedPaths[i].getParentPath().getLastPathComponent().toString().toLowerCase();
				filterBean.setOperator(field, EQUAL);
			}
			
			if (applyFilter(filterBean)) {
				lastSelectedPaths = selectedPaths;

				// Leave only last selection in each subtree
				if (selectedPaths.length > 1) {
					String p = ((DefaultMutableTreeNode) selectedPaths[selectedPaths.length - 1]
							.getLastPathComponent()).getParent().toString();

					// user cannot select more than one leaf in one subtree
					for (int i = 0; i < selectedPaths.length - 1; ++i) {
						if (p.equals(selectedPaths[i].getPathComponent(selectedPaths[i].getPathCount() - 2)
								.toString())) {

							filterTree.removeSelectionPath(selectedPaths[i]);
							break;
						}
					}
				}
			} else {
				filterBean = oldFilter;
			}
		}
	}

	/**
	 * Retrieves data from the persistent layer upon given filter and returns <code>true</code> on
	 * success.
	 * 
	 * @param filterBean
	 *            ContractorFilter - data filter.
	 * @return boolean - <code>true</code> when filter was applied successfully and
	 *         <code>false</code> otherwise.
	 */
	private boolean applyFilter(ContractorFilter filterBean) {
		try {
			setSelectedContractor();
			List<Contractor> list = dao.load(filterBean.getFilter(), filterBean.getOperators());
			contractorsList.clear();
			contractorsList.addAll(list);
			refreshTable();
			return true;
		} catch (DaoException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), MSG_SEARCHING, JOptionPane.INFORMATION_MESSAGE);
			revertTreeSelection();
		} catch (RemoteException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), MSG_REMOTE_HOST_ERROR, JOptionPane.ERROR_MESSAGE);
			revertTreeSelection();
		}

		return false;
	}

	/**
	 * @see suncertify.gui.AbstractWindow#exit()
	 */
	protected void exit() {
		GUIUtil.actionExit(this);
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
	 * This method initializes {@link #addMenuItem}.
	 * 
	 * @return JMenuItem - initialized {@link #addMenuItem}.
	 */
	private JMenuItem getAddMenuItem() {
		if (addMenuItem == null) {
			addMenuItem = new JMenuItem();
			addMenuItem.setText("Add New Contractor");
			addMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, Event.SHIFT_MASK));
			addMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					actionAdd();
				}
			});
		}
		return addMenuItem;
	}

	/**
	 * This method initializes {@link #searchButton}.
	 * 
	 * @return JButton - initialized {@link #searchButton}.
	 */
	private JButton getAllButton() {
		if (allButton == null) {
			allButton = new JButton();
			allButton.setText("All Records");
			allButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					actionAllRecords();
				}
			});
		}
		return allButton;
	}

	/**
	 * This method initializes {@link #allRecordsMenuItem}.
	 * 
	 * @return JMenuItem - initialized {@link #allRecordsMenuItem}.
	 */
	private JMenuItem getAllRecordsMenuItem() {
		if (allRecordsMenuItem == null) {
			allRecordsMenuItem = new JMenuItem();
			allRecordsMenuItem.setText("Show All Contractors (Resets Filter)");
			allRecordsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, Event.CTRL_MASK));
			allRecordsMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					actionAllRecords();
				}
			});
		}
		return allRecordsMenuItem;
	}

	/**
	 * This method initializes {@link #bookButton}.
	 * 
	 * @return JButton - initialized {@link #bookButton}.
	 */
	private JButton getBookButton() {
		if (bookButton == null) {
			bookButton = new JButton();
			bookButton.setText("Book");
			bookButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					actionEdit(Mode.BOOK);
				}
			});
		}
		return bookButton;
	}

	/**
	 * This method initializes {@link #bookMenuItem}.
	 * 
	 * @return JMenuItem - initialized {@link #bookMenuItem}.
	 */
	private JMenuItem getBookMenuItem() {
		if (bookMenuItem == null) {
			bookMenuItem = new JMenuItem();
			bookMenuItem.setText("Book Selected Contractor");
			bookMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					actionEdit(Mode.BOOK);
				}
			});
		}
		return bookMenuItem;
	}
	
	/**
	 * This method initializes {@link #contractorMenu}.
	 * 
	 * @return JMenu - initialized {@link #contractorMenu}.
	 */
	private JMenu getContractorMenu() {
		if (contractorMenu == null) {
			contractorMenu = new JMenu();
			contractorMenu.setText("Contractor");
			contractorMenu.add(getAddMenuItem());
			contractorMenu.add(getDeleteMenuItem());
			contractorMenu.add(getEditMenuItem());
			contractorMenu.add(new JSeparator());
			contractorMenu.add(getBookMenuItem());
			contractorMenu.add(getFreeMenuItem());
		}
		return contractorMenu;
	}

	/**
	 * This method initializes {@link #contractorScrollPane}.
	 * 
	 * @return JScrollPane - initialized {@link #contractorScrollPane}.
	 */
	private JScrollPane getContractorScrollPane() {
		if (contractorScrollPane == null) {
			contractorScrollPane = new JScrollPane();
			contractorScrollPane.setViewportView(getContractorTable());
		}
		return contractorScrollPane;
	}

	/**
	 * This method initializes {@link #contractorTable}.
	 * 
	 * @return JTable - initialized {@link #contractorTable}.
	 */
	private JTable getContractorTable() {
		if (contractorTable == null) {
			contractorTableModel = new ContractorTableModel(contractorsList);
			contractorTable = new JTable(contractorTableModel);
			contractorTableModel.addMouseListener(contractorTable);
			contractorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}
		return contractorTable;
	}

	/**
	 * This method initializes {@link #dataSplitPane}.
	 * 
	 * @return JSplitPane - initialized {@link #dataSplitPane}.
	 */
	private JSplitPane getDataSplitPane() {
		if (dataSplitPane == null) {
			dataSplitPane = new JSplitPane();
			dataSplitPane.setLeftComponent(getFilterTree());
			dataSplitPane.setRightComponent(getContractorScrollPane());
			dataSplitPane.setDividerLocation(200);
		}
		return dataSplitPane;
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
	 * This method initializes {@link #deleteMenuItem}.
	 * 
	 * @return JMenuItem - initialized {@link #deleteMenuItem}.
	 */
	private JMenuItem getDeleteMenuItem() {
		if (deleteMenuItem == null) {
			deleteMenuItem = new JMenuItem();
			deleteMenuItem.setText("Delete Selected Contractor");
			deleteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, Event.SHIFT_MASK));
			deleteMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					actionDelete();
				}
			});
		}
		return deleteMenuItem;
	}

	/**
	 * This method initializes {@link #editButton}.
	 * 
	 * @return JButton - initialized {@link #editButton}.
	 */
	private JButton getEditButton() {
		if (editButton == null) {
			editButton = new JButton();
			editButton.setText("Edit");
			editButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					actionEdit(Mode.EDIT);
				}
			});
		}
		return editButton;
	}

	/**
	 * This method initializes {@link #editMenuItem}.
	 * 
	 * @return JMenuItem - initialized {@link #editMenuItem}.
	 */
	private JMenuItem getEditMenuItem() {
		if (editMenuItem == null) {
			editMenuItem = new JMenuItem();
			editMenuItem.setText("Edit Selected Contractor");
			editMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					actionEdit(Mode.EDIT);
				}
			});
		}
		return editMenuItem;
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
	 * This method initializes {@link #filterTree}.
	 * 
	 * @return JTree - initialized {@link #filterTree}.
	 */
	private JTree getFilterTree() {
		if (filterTree == null) {
			filterTree = new JTree(new ContractorTreeModel(contractorsList));
			filterTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
			filterTree.addTreeSelectionListener(new TreeSelectionListener() {
				/**
				 * @see TreeSelectionListener#valueChanged(TreeSelectionEvent)
				 */
				public void valueChanged(TreeSelectionEvent e) {
					actionTreeSelectionChanged();
				}
			});
		}
		return filterTree;
	}

	/**
	 * This method initializes {@link #freeButton}.
	 * 
	 * @return JButton - initialized {@link #freeButton}.
	 */
	private JButton getFreeButton() {
		if (freeButton == null) {
			freeButton = new JButton();
			freeButton.setText("Free");
			freeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					actionFree();
				}
			});
		}
		return freeButton;
	}

	/**
	 * This method initializes {@link #freeMenuItem}.
	 * 
	 * @return JMenuItem - initialized {@link #freeMenuItem}.
	 */
	private JMenuItem getFreeMenuItem() {
		if (freeMenuItem == null) {
			freeMenuItem = new JMenuItem();
			freeMenuItem.setText("Free Selected Contractor");
			freeMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					actionFree();
				}
			});
		}
		return freeMenuItem;
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
			// Contractors table & filter tree
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.fill = GridBagConstraints.BOTH;
				constraints.gridy = 0;
				constraints.weightx = 1.0;
				constraints.weighty = 1.0;
				constraints.gridheight = 10;
				constraints.insets = new Insets(5, 5, 5, 0);
				constraints.gridx = 0;
				mainPanel.add(getDataSplitPane(), constraints);
			}
			// Add button
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.gridx = 1;
				constraints.anchor = GridBagConstraints.NORTH;
				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.insets = new Insets(5, 5, 2, 5);
				constraints.ipady = 0;
				constraints.gridy = 0;
				mainPanel.add(getAddButton(), constraints);
			}
			// Edit button
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.gridx = 1;
				constraints.anchor = GridBagConstraints.NORTH;
				constraints.insets = new Insets(2, 5, 2, 5);
				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.ipady = 0;
				constraints.gridy = 3;
				mainPanel.add(getEditButton(), constraints);
			}
			// Delete button
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.gridx = 1;
				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.insets = new Insets(2, 5, 2, 5);
				constraints.gridy = 4;
				mainPanel.add(getDeleteButton(), constraints);
			}
			// Book button
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.gridx = 1;
				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.insets = new Insets(20, 5, 2, 5);
				constraints.gridy = 5;
				mainPanel.add(getBookButton(), constraints);
			}
			// Free button
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.gridx = 1;
				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.insets = new Insets(2, 5, 2, 5);
				constraints.gridy = 6;
				mainPanel.add(getFreeButton(), constraints);
			}
			// Search button
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.gridx = 1;
				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.insets = new Insets(50, 5, 2, 5);
				constraints.anchor = GridBagConstraints.CENTER;
				constraints.gridheight = 1;
				constraints.ipadx = 0;
				constraints.ipady = 0;
				constraints.gridy = 7;
				mainPanel.add(getSearchButton(), constraints);
			}
			// All records button
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.gridx = 1;
				constraints.anchor = GridBagConstraints.CENTER;
				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.insets = new Insets(2, 5, 2, 5);
				constraints.gridheight = 1;
				constraints.ipadx = 0;
				constraints.ipady = 0;
				constraints.gridy = 8;
				mainPanel.add(getAllButton(), constraints);
			}
			// Exit button
			{
				GridBagConstraints constraints = new GridBagConstraints();
				constraints.gridx = 1;
				constraints.anchor = GridBagConstraints.SOUTH;
				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.insets = new Insets(2, 5, 5, 5);
				constraints.gridy = 9;
				mainPanel.add(getExitButton(), constraints);
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
			mainMenuBar.add(getContractorMenu());
			mainMenuBar.add(getSearchMenu());
			mainMenuBar.add(GUIUtil.getHelpMenu(this));
		}
		return mainMenuBar;
	}

	/**
	 * This method initializes {@link #searchButton}.
	 * 
	 * @return JButton - initialized {@link #searchButton}.
	 */
	private JButton getSearchButton() {
		if (searchButton == null) {
			searchButton = new JButton();
			searchButton.setText("Search");
			searchButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					actionSearch();
				}
			});
		}
		return searchButton;
	}

	/**
	 * This method initializes {@link #searchMenu}.
	 * 
	 * @return JMenu - initialized {@link #searchMenu}.
	 */
	private JMenu getSearchMenu() {
		if (searchMenu == null) {
			searchMenu = new JMenu();
			searchMenu.setText("Search");
			searchMenu.add(getSearchMenuItem());
			searchMenu.add(getAllRecordsMenuItem());
		}
		return searchMenu;
	}

	/**
	 * This method initializes {@link #searchMenuItem}.
	 * 
	 * @return JMenuItem - initialized {@link #searchMenuItem}.
	 */
	private JMenuItem getSearchMenuItem() {
		if (searchMenuItem == null) {
			searchMenuItem = new JMenuItem();
			searchMenuItem.setText("Search Contractors");
			searchMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK));
			searchMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					actionSearch();
				}
			});
		}
		return searchMenuItem;
	}

	/**
	 * @see suncertify.gui.AbstractWindow#initialize()
	 */
	protected void initialize() {
		this.setMinimumSize(new Dimension(800, 600));
		this.setJMenuBar(getMainMenuBar());
		this.setSize(800, 600);
		setDefaultLocation();
		this.setContentPane(getMainPanel());
		this.setTitle(ApplicationContext.getInstance().getMainWindowName());
		contractorTableModel.sort(0, true);
		if (contractorTable.getRowCount() > 0) {
			contractorTable.setRowSelectionInterval(0, 0);
		}
	}

	/**
	 * Refreshes data in the window (tree).
	 * 
	 * @see JTree#setModel(javax.swing.tree.TreeModel)
	 */
	private void refreshTree() {
		try {
			filterTree.setModel(new ContractorTreeModel(dao.load()));
			if (lastSelectedPaths != null && lastSelectedPaths.length > 0) {
				TreePath[] paths = new TreePath[lastSelectedPaths.length];
				for (int  i = 0; i < lastSelectedPaths.length; ++i) {
					// expanding relevant subtrees
					for (int j = 0; j < filterTree.getRowCount(); ++j) {
						TreePath path = filterTree.getPathForRow(j);
						if (lastSelectedPaths[i].getParentPath() != null
								&& path.toString().equals(lastSelectedPaths[i].getParentPath().toString())) {

							filterTree.expandRow(j);
							break;
						}
					}

					// retrieving paths to select
					for (int j = 0; j < filterTree.getRowCount(); ++j) {
						TreePath path = filterTree.getPathForRow(j);
						if (path.toString().equals(lastSelectedPaths[i].toString())) {
							filterTree.scrollPathToVisible(path);
							paths[i] = path;
							break;
						}
					}					
				}
				reverting = true;
				filterTree.setSelectionPaths(paths);
			}
		} catch (RemoteException e) {
			// XXX: ignore it
		} finally {
			reverting = false;
		}
	}

	/**
	 * Refreshes data in the window (table).
	 * 
	 * @see javax.swing.table.AbstractTableModel#fireTableDataChanged()
	 */
	private void refreshTable() {
		contractorTableModel.fireTableDataChanged();
		contractorTableModel.sort();
		int idx = contractorsList.indexOf(selectedContractor);
		if (idx > -1 && contractorTable.getRowCount() > idx) {
			contractorTable.setRowSelectionInterval(idx, idx);
		} else if (contractorTable.getRowCount() > 0) {
			contractorTable.setRowSelectionInterval(0, 0);			
		}
	}
}