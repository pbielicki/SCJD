package suncertify.gui.contractor;

import static suncertify.db.domain.DBMetaInfo.FIELDS_COUNT;
import static suncertify.db.domain.DBMetaInfo.FIELD_NAMES;
import static suncertify.db.domain.DBMetaInfo.getFieldType;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import suncertify.db.dao.comparator.IComparator;
import suncertify.db.dao.comparator.ComparatorFactory;
import suncertify.db.domain.Contractor;

/**
 * <code>ContractorTableModel</code> is a table model for contractors that provides content and
 * standard sorting behaviour.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 * @see TableModel
 * @see AbstractTableModel
 */
class ContractorTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    /**
	 * Array of booleans indicating which columns are to be sorted in ascending and which in
	 * descending direction.
	 */
	static boolean[] ascending = new boolean[FIELDS_COUNT];

	static {
		Arrays.fill(ascending, false);
	}

	/**
	 * List of contractors to display in the table.
	 */
	private List<Contractor> contractorList;

	/**
	 * Indicates which column sort the data with.
	 */
	private int sortColumn;

	/**
	 * Constructor - creates table model with given list of contractors to be displayed in the
	 * table.
	 * 
	 * @param list
	 *            List&lt;Contractor&gt; - list of contractors to be displayed in the table.
	 */
	public ContractorTableModel(List<Contractor> list) {
		contractorList = list;
	}

	/**
	 * Adds mouse listeners to the given <code>JTable</code> in order to capture user requests for
	 * sorting columns and mouse motion on table cells (that cause in displaying cell values in the
	 * table's tooltip text).
	 * 
	 * @param table
	 *            JTable - table for which to capture mouse click events on its columns.
	 */
	public void addMouseListener(final JTable table) {
		final int[] point = new int[2];

		// The two following listenres cause that when user points at any cell
		// on the table the value of this selected cell will appear as a tooltip
		// text. This
		table.addMouseListener(new MouseAdapter() {
			/**
			 * @see MouseListener#mouseEntered(MouseEvent)
			 */
			public void mouseEntered(MouseEvent e) {
				point[0] = table.rowAtPoint(e.getPoint());
				point[1] = table.columnAtPoint(e.getPoint());

				table.setToolTipText(getValueAt(point[0], point[1]));
			}

			/**
			 * @see MouseListener#mouseExited(MouseEvent)
			 */
			public void mouseExited(MouseEvent e) {
				Arrays.fill(point, 0);
				table.setToolTipText("");
			}
		});

		table.addMouseMotionListener(new MouseMotionAdapter() {
			/**
			 * @see MouseMotionListener#mouseMoved(MouseEvent)
			 */
			public void mouseMoved(MouseEvent e) {
				if (point[0] != table.rowAtPoint(e.getPoint()) || point[1] != table.columnAtPoint(e.getPoint())) {
					point[0] = table.rowAtPoint(e.getPoint());
					point[1] = table.columnAtPoint(e.getPoint());

					table.setToolTipText(getValueAt(point[0], point[1]));
				}
			}
		});

		// Adds sorting facility to the table
		table.getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event) {
				// find column of click and
				int tableColumn = table.columnAtPoint(event.getPoint());

				// translate to table model index and sort
				int modelColumn = table.convertColumnIndexToModel(tableColumn);
				Contractor record = contractorList.get(table.getSelectedRow());
				sort(modelColumn);
				int idx = contractorList.indexOf(record);
				if (table.getRowCount() > idx) {
					table.setRowSelectionInterval(idx, idx);
				}
			}
		});
	}

	/**
	 * @see TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return FIELDS_COUNT;
	}

	/**
	 * @see TableModel#getColumnName(int)
	 */
	public String getColumnName(int columnIndex) {
		return FIELD_NAMES[columnIndex].toUpperCase();
	}

	/**
	 * @see TableModel#getRowCount()
	 */
	public int getRowCount() {
		return contractorList.size();
	}

	/**
	 * @see TableModel#getValueAt(int, int)
	 */
	public String getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex > -1 && columnIndex > -1) {
			return contractorList.get(rowIndex).toStrings()[columnIndex].trim();
		} else {
			return "";
		}
	}

	/**
	 * Sorts data in current table model against given column number.
	 * 
	 * @param col
	 *            int - column number to sort data against.
	 */
	public void sort(int col) {
		if (col > ascending.length - 1) {
			throw new IllegalArgumentException("Illegal column number. Column number must be number from 0 to "
					+ (ascending.length - 1) + ".");
		}

		sortColumn = col;
		ascending[col] = !ascending[col];
		Collections.sort(contractorList, new Comparator<Contractor>() {
			/**
			 * Field's comparator.
			 */
			private IComparator comparator = ComparatorFactory.getComparator(getFieldType(sortColumn));

			/**
			 * @see java.util.Comparator#compare(T, T)
			 */
			public int compare(Contractor o1, Contractor o2) {
				if (ascending[sortColumn]) {
					return comparator.compare(o1.toStrings()[sortColumn], o2.toStrings()[sortColumn]);
				} else {
					return comparator.compare(o2.toStrings()[sortColumn], o1.toStrings()[sortColumn]);
				}
			}
		});
		fireTableDataChanged();
	}

	/**
	 * Sorts data in current table model against given column number ascending if <i>asc</i> param
	 * equals <code>true</code> and descending otherwise.
	 * 
	 * @param col -
	 *            int - column number to sort data against.
	 * @param asc -
	 *            sorting direction: ascending if <code>true</code> and descending otherwise.
	 * 
	 * @see #sort(int)
	 */
	public void sort(int col, boolean asc) {
		if (col > ascending.length - 1) {
			throw new IllegalArgumentException("Illegal column number. Column number must be number from 0 to "
					+ (ascending.length - 1) + ".");
		}
		ascending[col] = !asc;
		sort(col);
	}

	/**
	 * Sorts data in current table model against column number that was set previously.
	 */
	public void sort() {
		ascending[sortColumn] = !ascending[sortColumn];
		sort(sortColumn);
	}
}
