
package main.config;

import java.util.Hashtable;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

import framework.ui.activity.ActivityManager;

/**
 * Provides access to MIDlet configurations.
 *
 * @author Oscar Cai
 */

public class Config {
    /** keyCode for up arrow */
    public static final int KEYCODE_UP     = -1;

    /** keyCode for down arrow */
    public static final int KEYCODE_DOWN   = -2;

    /** keyCode for left arrow */
    public static final int KEYCODE_LEFT   = -3;

    /** keyCode for right arrow */
    public static final int KEYCODE_RIGHT  = -4;

    /** keyCode for select */
    public static final int KEYCODE_SELECT = -5;

    /** keyCode for soft1 */
    public static final int KEYCODE_SOFT1  = -6;

    /** keyCode for soft2 */
    public static final int KEYCODE_SOFT2  = -7;

    /** keyCode for clear */
    public static final int KEYCODE_CLEAR  = -8;

    /** keyCode for back */
    public static final int KEYCODE_BACK = -13;

    public static final int MENU_ITEMS_PER_ROW = 3;

    public static final int MENU_REFRESH_ID = 1000;
    public static final int MENU_INSTALL_ID = 1001;
    public static final int MENU_BACK_ID    = 1002;
    public static final int MENU_EXIT_ID    = 1003;

    private static Display globalDisplay;

    private static MIDlet midlet;

    private static ActivityManager activityManager;

    public static void setMIDlet(MIDlet m) {
        midlet = m;
        globalDisplay = Display.getDisplay(m);
    }

    public static MIDlet getMIDlet() {
        return midlet;
    }

    public static Display getGlobalDisplay() {
        return globalDisplay;
    }

    public static void setActivityManager(ActivityManager manager) {
        activityManager = manager;
    }

    public static ActivityManager getActivityManager() {
        return activityManager;
    }

    public static String getClientId() {
        // TODO: return concrete client id.
        return null;
    }

    public static Hashtable getCompanyBatchParams() {
        Hashtable params = new Hashtable();
        params.put("company", "9000");
        params.put("batch", "9000");
        return params;
    }

    private static String getServerletUrl(String serverletName) {
        return "http://127.0.0.1/servlet/" + serverletName;
    }

    public static String getSoftListUrl() {
        return getServerletUrl("soft.php");
    }

    public static String getNotifyUrl() {
        return getServerletUrl("notify.php");
    }
}
