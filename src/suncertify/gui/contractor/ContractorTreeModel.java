package suncertify.gui.contractor;

import static suncertify.db.domain.DBMetaInfo.FIELDS_COUNT;
import static suncertify.db.domain.DBMetaInfo.FIELD_LOCATION;
import static suncertify.db.domain.DBMetaInfo.FIELD_NAME;
import static suncertify.db.domain.DBMetaInfo.FIELD_NAMES;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import suncertify.db.domain.Contractor;

/**
 * <code>ContractorTreeModel</code> is a tree model for filter tree for contractors list. This
 * tree displays database structure (excluding selected fields) with all disctinct data retrieved
 * from db as a filters for all fields.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 * @see javax.swing.tree.DefaultTreeModel
 */
class ContractorTreeModel extends DefaultTreeModel {

    private static final long serialVersionUID = 1L;

    /**
	 * Field names that will be excluded from filter tree structure - e.g. numeric fields.
	 */
	private static final List<String> VISIBLE_FIELDS = new ArrayList<String>();

	static {
		VISIBLE_FIELDS.add(FIELD_NAME);
		VISIBLE_FIELDS.add(FIELD_LOCATION);
	}

	/**
	 * Constructs <code>ContractorTreeModel</code> and initializes the whole tree structure.
	 * 
	 * @param list
	 *            List&lt;Contractor&gt; - contractors list for which tree structured will be
	 *            generated.
	 */
	public ContractorTreeModel(List<Contractor> list) {
		super(null);
		initialize(list);
	}

	/**
	 * Initializes tree structure with appropriate data.
	 * 
	 * @param list
	 *            List&lt;Contractor&gt; - contractors list for which tree structured will be
	 *            initialized.
	 */
	private void initialize(List<Contractor> list) {
		List<SortedSet<String>> treeStructure = new ArrayList<SortedSet<String>>();
		// Creates tree structure
		for (Contractor contractor : list) {
			boolean first = true;
			String[] strings = contractor.toStrings();
			for (int i = 0; i < FIELDS_COUNT; ++i) {
				if (first) {
					treeStructure.add(new TreeSet<String>());
				}

				// Do not process excluded fields
				if (VISIBLE_FIELDS.contains(FIELD_NAMES[i])) {
					treeStructure.get(i).add(strings[i]);
				}
			}
			first = false;
		}

		DefaultMutableTreeNode root = new DefaultMutableTreeNode("All Contractors");
		// Convert data from the list to the tree structure
		for (int i = 0; i < FIELDS_COUNT; ++i) {
			if (VISIBLE_FIELDS.contains(FIELD_NAMES[i])) {
				DefaultMutableTreeNode parent = new DefaultMutableTreeNode(FIELD_NAMES[i].toUpperCase());

				if (treeStructure.size() > i) {
					for (String string : treeStructure.get(i)) {
						parent.add(new DefaultMutableTreeNode(string));
					}
				}
				root.add(parent);
			}
		}
		this.root = root;
	}
}