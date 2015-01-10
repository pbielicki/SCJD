package suncertify.gui;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import suncertify.gui.util.GUIUtil;

/**
 * <code>AbstractWindow</code> is a base application window class - it adds
 * some necessary functions to the standard {@link javax.swing.JFrame} class.
 * These functions are:
 * <ul>
 * <li>enable developer to control window closing event - window do not close
 * automatically after pressing Alt+F4</li>
 * <li>control minimum and maximum size of the window</li>
 * <li>add convenient <code>open()</code>, <code>close()</code> methods
 * instead of <code>setVisible(...)</code></li>
 * </ul>
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 * @see javax.swing.JFrame
 */
public abstract class AbstractWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    /**
	 * Constructs window using default constructor from superclass and adds
	 * extra features.
	 * 
	 */
	public AbstractWindow() {
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			/**
			 * @see WindowListener#windowClosing(WindowEvent)
			 */
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});

		this.addComponentListener(new ComponentAdapter() {
			/**
			 * @see ComponentListener#componentResized(ComponentEvent)
			 */
			public void componentResized(ComponentEvent e) {
				if (getSize().width > getMaximumSize().width) {
					setSize(getMaximumSize().width, getSize().height);
				} else if (getSize().width < getMinimumSize().width) {
					setSize(getMinimumSize().width, getSize().height);
				}

				if (getSize().height > getMaximumSize().height) {
					setSize(getSize().width, getMaximumSize().height);
				} else if (getSize().height < getMinimumSize().height) {
					setSize(getSize().width, getMinimumSize().height);
				}
			}
		});
	}

	/**
	 * Closes current window.
	 * 
	 * @see javax.swing.JComponent#setVisible(boolean)
	 */
	public void close() {
		setVisible(false);
	}

	/**
	 * This method sets window closing behaviour. The most simple implementation
	 * of this class (causing window to close) would be:
	 * 
	 * <pre>
	 * protected void exit() {
	 * 	close();
	 * }
	 * </pre>
	 */
	protected abstract void exit();

	/**
	 * This method initializes window data - especially provides it with
	 * appropriate content. The most simple implementation of this class
	 * (without any content; causing that window will be centered) would be:
	 * 
	 * <pre>
	 * protected void initialize() {
	 * 	setDefaultLocation();
	 * }
	 * </pre> 
	 */
	protected abstract void initialize();

	/**
	 * Opens current window.
	 * 
	 * @see javax.swing.JComponent#setVisible(boolean)
	 */
	public void open() {
		setVisible(true);
	}

	/**
	 * Moves current window to the center position relatively to the owner of
	 * this window or the whole screen if the owner is <code>null</code>.
	 * 
	 * @see GUIUtil#getCenterLocation(Window, Dimension)
	 */
	protected void setDefaultLocation() {
		this.setLocation(GUIUtil.getCenterLocation(getOwner(), getSize()));
	}
}
