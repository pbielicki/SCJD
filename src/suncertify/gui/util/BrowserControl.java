package suncertify.gui.util;

import java.io.IOException;

/**
 * A simple util class to display a URL in the system browser. <br>
 * Under Unix, the system browser is hard-coded to be 'netscape'. Netscape must
 * be in your PATH for this to work. <br>
 * Under Windows, this will bring up the default browser under windows, usually
 * either Netscape or Microsoft IE. The default browser is determined by the OS.
 * <br>
 * Examples:
 * <ul>
 * <li><code>BrowserControl.displayURL("file://c:\\docs\\index.html");</code></li>
 * <li><code>BrowserContorl.displayURL("file:///user/joe/index.html");</code></li>
 * </ul>
 * 
 * @author <a href="mailto:pbielicki@gmail.com">Przemyslaw Bielicki</a>
 */
public final class BrowserControl {
	/**
	 * The flag to display a url.
	 */
	private static final String UNIX_FLAG = "-remote openURL";

	/**
	 * The default browser under unix.
	 */
	private static final String UNIX_PATH = "netscape";

	/**
	 * The flag to display a url.
	 */
	private static final String WIN_FLAG = "url.dll,FileProtocolHandler";

	/**
	 * Used to identify the windows platform.
	 */
	private static final String WIN_ID = "Windows";

	/**
	 * The default system browser under windows.
	 */
	private static final String WIN_PATH = "rundll32";

	/**
	 * Display given url in the system browser.
	 * 
	 * @param url
	 *            String - the file's url (the url should start with either
	 *            "http://" or "file://").
	 *            
	 * @throws IOException - see {@link Runtime#exec(java.lang.String)}.
	 * @throws InterruptedException see (@link Process#waitFor()}.
	 */
	public static void displayURL(String url) throws IOException, InterruptedException {
		boolean windows = isWindowsPlatform();
		String cmd = null;
		if (windows) {
			cmd = WIN_PATH + " " + WIN_FLAG + " " + url;
			Runtime.getRuntime().exec(cmd);
		} else {
			cmd = UNIX_PATH + " " + UNIX_FLAG + "(" + url + ")";
			Process p = Runtime.getRuntime().exec(cmd);
			// wait for exit code -- if it's 0, command worked,
			// otherwise we need to start the browser up.
			int exitCode = p.waitFor();
			if (exitCode != 0) {
				cmd = UNIX_PATH + " " + url;
				p = Runtime.getRuntime().exec(cmd);
			}
		}
	}

	/**
	 * Determines whether this application is running under Windows or some
	 * other platform by examing the "os.name" property.
	 * 
	 * @return boolean - <code>true</code> if this application is running
	 *         under a Windows OS.
	 */
	public static boolean isWindowsPlatform() {
		String os = System.getProperty("os.name");
		if (os != null && os.startsWith(WIN_ID))
			return true;
		else
			return false;
	}

	/**
	 * Inaccessible constructor.
	 */
	private BrowserControl() {
	}
}