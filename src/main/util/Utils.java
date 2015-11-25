
package main.util;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Image;


import main.config.Config;
import main.config.Theme;

public class Utils {

    public static void requestInstall(String jadUrl) {
        try {
            Config.getMIDlet().platformRequest(jadUrl);
        } catch (ConnectionNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Image getMIDletDefaultIcon() {
        return Theme.getSoftIcon();
    }
}
