
package main;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import framework.ui.activity.ActivityManager;

import main.config.Config;
import main.config.Theme;

public class Main extends MIDlet {

    private AppListActivity home;

    protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
        Config.getGlobalDisplay().setCurrent(null);
        Config.getActivityManager().clear();
    }

    protected void pauseApp() {
    }

    protected void startApp() throws MIDletStateChangeException {
        Config.setMIDlet(this);
        Config.setActivityManager(new ActivityManager());

        Theme.init();

        home = new AppListActivity();
        home.show();
        home.getNextPageOfApps();
    }
}
