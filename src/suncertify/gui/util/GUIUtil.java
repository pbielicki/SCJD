package suncertify.gui.util;

import java.awt.Dimension;
import java.awt.Event;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.JTextComponent;

import suncertify.core.ApplicationContext;

/**
 * <code>GUIUtil</code> is an utility class providing useful and common features for GUI
 * components, dialogs, windows and widgets.
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
public final class GUIUtil {
	/**
	 * <code>DBFilter</code> is an implementation of {@link FileFilter} class.
	 * 
	 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
	 * @see FileFilter
	 */
	private static class DBFilter extends FileFilter {
		/**
		 * Description of the current filter.
		 */
		private String description;

		/**
		 * File extensions to be displayed in the file chooser dialog.
		 */
		private Set<String> extensions = new HashSet<String>();

		/**
		 * Constructs <code>DBFilter</code> and sets acceptable file extensions regarding given
		 * ones.
		 * 
		 * @param strings
		 *            String... - list of acceptable file extensions.
		 */
		public DBFilter(String... strings) {
			extensions = new HashSet<String>(Arrays.asList(strings));
		}

		/**
		 * @see FileFilter#accept(java.io.File)
		 */
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}

			int idx = f.getName().lastIndexOf('.');
			if (idx > -1) {
				String ex = f.getName().substring(idx);
				if (ex != null && extensions.contains(ex)) {
					return true;
				}
			}
			return false;
		}

		/**
		 * @see FileFilter#getDescription()
		 */
		public String getDescription() {
			return description;
		}

		/**
		 * Sets the description of the current file filter.
		 * 
		 * @param description
		 *            String - the description of the current file filter.
		 */
		public void setDescription(String description) {
			this.description = description;
		}
	}

	/**
	 * Context for this utility class.
	 */
	private static final ApplicationContext APP_CTX = ApplicationContext.getInstance();

	/**
	 * Action invoked when user clicks <i>Change</i> option in order to change db filename (either
	 * local or server side).
	 * 
	 * @param owner
	 *            Frame - owner window (needed to display message dialog).
	 * @param oldValue
	 *            String - current filename - returned in case of failure or selecting <i>Cancel</i>
	 *            by the user in the selection dialog.
	 * @return String - selected by the user new filename or the old one if the new one was not
	 *         selected or any failre occurs.
	 */
	public static String actionChangeDBFile(final Window owner, final String oldValue) {
		File file = new File(APP_CTX.getLocalDBFileName());
		JFileChooser fc = new JFileChooser(file);
		DBFilter filter = new DBFilter(".db");
		filter.setDescription("Database files");
		fc.setFileFilter(filter);
		fc.setSelectedFile(file);
		fc.setDialogTitle("Select Database File");

		if (fc.showDialog(owner, "Select") == JFileChooser.APPROVE_OPTION) {
			return fc.getSelectedFile().getAbsolutePath();
		}

		return oldValue;
	}

	/**
	 * Action invoked when user clicks <i>Exit</i> option (either button or any other widget) in
	 * order to exit the application.
	 * 
	 * @param owner
	 *            Frame - owner window (needed to display message dialog) that is to be exited from.
	 */
	public static boolean actionExit(Frame owner) {
		if (JOptionPane.showConfirmDialog(owner, "Do you really want to close the application?", "Application Exit",
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

			owner.setVisible(false);
			System.exit(0);
		}
		return false;
	}

	/**
	 * Returns center position as {@link Point} for the given owner {@link Window} and size of the
	 * component to be centered. If the owner window is <code>null</code> this method returns the
	 * center position relatively to the screen (depending on currently set resolution).
	 * 
	 * @param owner
	 *            Window - owner window (needed to display message dialog) relating to which the
	 *            center position will be calculated.
	 * @param size
	 *            Dimension - size of the component to be centered.
	 * @return Point - center position for the given owner window (needed to display message dialog)
	 *         and size of the component to be centered.
	 */
	public static Point getCenterLocation(Window owner, Dimension size) {
		int x, y;
		if (owner != null) {
			x = owner.getLocation().x + owner.getSize().width / 2;
			y = owner.getLocation().y + owner.getSize().height / 2;
		} else {
			Point point = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();

			x = point.x;
			y = point.y;
		}
		x -= size.width / 2;
		y -= size.height / 2;

		return new Point(x, y);
	}

	/**
	 * Initializes the common <i>Configuration</i> menu with all available configuration options
	 * (these options are stored in the <code>command.properties</code> config file).
	 * 
	 * @param owner
	 *            Frame - owner window (needed to display message dialog).
	 * @return JMenu - the common <i>Configuration</i> menu with all available configuration
	 *         options.
	 */
	public static JMenu getConfigurationMenu(final Frame owner) {
		JMenu configurationMenu = new JMenu();
		configurationMenu.setText("Configuration");

		// for (final String mode : APP_CTX.getAvailableModes()) {
		final String mode = APP_CTX.getMode();
		final JMenuItem item = new JMenuItem();
		item.setText(APP_CTX.getModeName(mode));

		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					APP_CTX.getConfigurationDialog(mode, owner).open();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(owner,
							"Cannot find configuration dialog for " + item.getText() + ".", "Configuration",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		configurationMenu.add(item);

		return configurationMenu;
	}

	/**
	 * Initializes the common <i>Help Contents</i> menu item.
	 * 
	 * @param owner
	 *            Frame - owner window (needed to display message dialog).
	 * @return JMenuItem - the common <i>Help Contents</i> menu item.
	 */
	private static JMenuItem getContentsMenuItem(final Frame owner) {
		JMenuItem contentsMenuItem = new JMenuItem();
		contentsMenuItem.setText("Help Contents");
		contentsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, Event.SHIFT_MASK));
		contentsMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					BrowserControl.displayURL(APP_CTX.getHelpURL());
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(owner, "Error occured while bringing up browser."
							+ "Could not display help.", "Help", JOptionPane.ERROR_MESSAGE);

				} catch (InterruptedException ex) {
					JOptionPane.showMessageDialog(owner, "Could not invoke web browser." + "Could not display help.",
							"Help", JOptionPane.ERROR_MESSAGE);

				}
			}
		});
		return contentsMenuItem;
	}

	/**
	 * Initializes the common <i>Exit</i> menu item.
	 * 
	 * @param owner
	 *            Frame - owner window (needed to display message dialog).
	 * @return JMenuItem - the common <i>Exit</i> menu item.
	 */
	private static JMenuItem getExitMenuItem(final Frame owner) {
		JMenuItem exitMenuItem = new JMenuItem();
		exitMenuItem.setText("Exit");
		exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK, true));
		exitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionExit(owner);
			}
		});
		return exitMenuItem;
	}

	/**
	 * Initializes the common <i>File</i> menu with all available options.
	 * 
	 * @param owner
	 *            Frame - owner window (needed to display message dialog).
	 * @return JMenu - the common <i>File</i> menu with all available options.
	 */
	public static JMenu getFileMenu(Frame owner) {
		JMenu fileMenu = new JMenu();
		fileMenu.setText("File");
		fileMenu.add(getExitMenuItem(owner));
		return fileMenu;
	}

	/**
	 * Initializes the common <i>Help</i> menu with all available options.
	 * 
	 * @param owner
	 *            Frame - owner window (needed to display message dialog).
	 * @return JMenu - the common <i>Help</i> menu with all available options.
	 */
	public static JMenu getHelpMenu(Frame owner) {
		JMenu helpMenu = new JMenu();
		helpMenu.setText("Help");
		helpMenu.add(getContentsMenuItem(owner));
		return helpMenu;
	}

	/**
	 * Sets given text field in a way that it only accepts digits.
	 * 
	 * @param textField
	 *            JTextComponent - text component to be set as an integer field.
	 */
	public static void setIntField(final JTextComponent textField) {
		textField.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				if (Character.isDigit(e.getKeyChar())) {
					return;
				}
				e.consume();
				try {
					textField.setText(Math.abs(Integer.valueOf(textField.getText())) + "");
				} catch (NumberFormatException ex) {
					textField.setText("");
				}
			};
		});
	}

	/**
	 * Sets the text limit for the given size for given text component.
	 * 
	 * @param textField
	 *            JTextComponent - component to which text limit is to be set to.
	 * @param size
	 *            int - text limit.
	 */
	public static void setTextLimit(final JTextComponent textField, final int size) {

		textField.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				if (textField.getText().length() >= size) {
					textField.setText(textField.getText().substring(0, size));
					e.consume();
				}
			};
		});
	}

	/**
	 * Inaccessible constructor.
	 */
	private GUIUtil() {
	}
}