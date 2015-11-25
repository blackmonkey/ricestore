
package framework.task;

import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Image;

import framework.json.me.JSONException;
import framework.json.me.JSONObject;
import framework.net.HttpListener;
import framework.net.HttpTask;
import framework.ui.component.DataUpdateListener;

/**
 * Records Rice App Store applet information.
 *
 * @author Oscar Cai
 */

public class AppInfo implements HttpListener {

/**
 * TODO: Uncomment the following definitions when use them.
 *  private int priority; // MIDlet priority, larger number means higher priority
 *  private boolean forceInstall; // whether force installation
 */

    private String id; // MIDlet id
    private String name; // MIDlet name
    private String logoUrl; // MIDlet logo url
    private Image logoImg; // MIDlet logo
    private boolean logoFetching;
    private Vector listeners;
    private String snapshotUrl; // MIDlet snapshot url
    private Image snapshot;
    private boolean snapshotFetching;
    private String intro; // MIDlet introduction
    private String suiteName; // MIDlet suite name
    private String vendor; // MIDlet vendor name
    private String appVersion; // MIDlet version
    private String jadUrl; // MIDlet jad url
    private int totalDown; // total download count
    private int monthDown; // download count of latest month
    private int weekDown; // download count of latest week

    HttpTask logoFetchTask;
    HttpTask snapshotFetchTask;

    public AppInfo(String downBaseUrl, JSONObject jsonObj) throws JSONException {
/**
 * TODO: Uncomment the following assignment when use them.
 *      priority = jsonObj.getInt("priority");
 *      forceInstall = jsonObj.getBoolean("install");
 */

        id = jsonObj.getString("id");

        if (!downBaseUrl.endsWith("/")) {
            downBaseUrl += "/";
        }

        logoUrl = downBaseUrl + jsonObj.getString("logo");
        logoImg = null;
        logoFetching = false;

        snapshotUrl = downBaseUrl + jsonObj.getString("img");
        snapshot = null;
        snapshotFetching = false;

        name = jsonObj.getString("appname");
        suiteName = jsonObj.getString("suitename");
        vendor = jsonObj.getString("vendor");
        appVersion = jsonObj.getString("appVersion");
        jadUrl = downBaseUrl + jsonObj.getString("jad");
        totalDown = jsonObj.getInt("down");
        monthDown = jsonObj.getInt("mdown");
        weekDown = jsonObj.getInt("wdown");
        intro = jsonObj.getString("intro");

        listeners = new Vector();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSuiteName() {
        return suiteName;
    }

    public String getVendor() {
        return vendor;
    }

    public String getVersion() {
        return appVersion;
    }

    public String getJadUrl() {
        return jadUrl;
    }

    public int getTotalDownloadCount() {
        return totalDown;
    }

    public int getMonthDownloadCount() {
        return monthDown;
    }

    public int getWeekDownloadCount() {
        return weekDown;
    }

    public Image getSnapshot() {
        return snapshot;
    }

    public String getIntroduction() {
        return intro;
    }

    public Image getLogo() {
        return logoImg;
    }

    public void fetchLogo(DataUpdateListener listener) {
        if (!listeners.contains(listener)) {
            listeners.addElement(listener);
        }
        if (!logoFetching) {
            logoFetching = true;
            logoFetchTask = new HttpTask(logoUrl, (Hashtable) null, (HttpListener) this);
            logoFetchTask.start();
        }
    }

    public void fetchSnapshot(DataUpdateListener listener) {
        if (!listeners.contains(listener)) {
            listeners.addElement(listener);
        }
        if (!snapshotFetching) {
            snapshotFetching = true;
            snapshotFetchTask = new HttpTask(snapshotUrl, (Hashtable) null, (HttpListener) this);
            snapshotFetchTask.start();
        }
    }

    public void onHttpResult(HttpTask task, boolean success, Object response, int responseType, String errMsg) {
        if (success && responseType == HttpListener.IMAGE) {
            // FIXME: How about if multiple logo/snapshot fetching is ongoing?
            if (task == logoFetchTask) {
                logoImg = (Image) response;
                logoFetching = false;
            } else if (task == snapshotFetchTask) {
                snapshot = (Image) response;
                snapshotFetching = false;
            }
            for (int i = 0; i < listeners.size(); i++) {
                ((DataUpdateListener)listeners.elementAt(i)).onDataUpdated(this);
            }
        }
    }
}
