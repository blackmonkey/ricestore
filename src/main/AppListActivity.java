
package main;

import java.util.Vector;

import javax.microedition.lcdui.Canvas;

import main.config.Config;
import main.config.StringResource;
import main.config.Theme;
import main.util.Log;
import main.util.Utils;

import framework.task.AppInfo;
import framework.task.GetAppListTask;
import framework.task.TaskListener;
import framework.ui.activity.Dialog;
import framework.ui.activity.MenuItem;
import framework.ui.activity.MessageDialog;
import framework.ui.activity.OnClickListener;
import framework.ui.activity.ProgressDialog;
import framework.ui.activity.RASActivity;
import framework.ui.component.DataUpdateListener;
import framework.ui.component.ListView;
import framework.ui.component.View;

/**
 * AppListActivity lists Rice Store applets of specified category.
 *
 * @author Oscar Cai
 */

public class AppListActivity extends RASActivity implements TaskListener, DataUpdateListener, OnClickListener {

    private ListView appListView;
    private Dialog progressDlg;
    private AppListAdapter appListAdapter;
    private GetAppListTask getAppsTask;
    private int page;
    private int pageCount;

    public AppListActivity() {
        super(Theme.getTransparentAppIcon(), StringResource.APP_TITLE);
        page = 0;
        pageCount = Integer.MAX_VALUE;

        /**
         * To here, appListView must be initialized
         */
        appListAdapter = new AppListAdapter(null, Utils.getMIDletDefaultIcon(), this);
        appListView.setAdapter(appListAdapter);
    }

    protected void bindContentView() {
        if (appListView == null) {
            appListView = new ListView(null, StringResource.NO_LATEST_CONTENT);
            appListView.setAlignment(contentView, View.MATCH_PARENT, View.MATCH_PARENT, View.ALIGN_PARENT_LEFT, View.ALIGN_PARENT_TOP);
            contentView.addChild(appListView);
        }
        contentView.layout();
        repaint();
    }

    public void getNextPageOfApps() {
        if (progressDlg != null) {
            progressDlg.destroy();
            progressDlg = null;
        }

        if (page < pageCount) {
            getAppsTask = new GetAppListTask(page + 1, this);
            progressDlg = new ProgressDialog(StringResource.START_GET_APP_LIST, this);
            progressDlg.show();
            getAppsTask.execute();
        }
    }

    protected void createMenu(Vector menuItems) {
        menuItems.addElement(new MenuItem(Config.MENU_REFRESH_ID, Theme.getMenuRefreshIcon(), StringResource.MENU_REFRESH_TITLE));
        menuItems.addElement(new MenuItem(Config.MENU_EXIT_ID, Theme.getMenuExitIcon(), StringResource.MENU_EXIT_TITLE));
    }

    private void showAppInfo(int index) {
        if (index >= 0 && index < appListAdapter.size()) {
            AppInfo appInfo = (AppInfo) appListAdapter.elementAt(index);
            AppInfoActivity activity = new AppInfoActivity(appInfo, appListAdapter.getDefaultIcon());
            activity.show();
        }
    }

    public void onMenuItemSelected(int menuItemId) {
        switch (menuItemId) {
        case Config.MENU_REFRESH_ID:
            appListView.clear();
            page = 0;
            pageCount = Integer.MAX_VALUE;
            getNextPageOfApps();
            break;

        case Config.MENU_EXIT_ID:
            MessageDialog msgDlg = new MessageDialog(
                    StringResource.CONFIRM_EXIT, this,
                    MessageDialog.BUTTON_OK | MessageDialog.BUTTON_CANCEL, this);
            msgDlg.show();
            break;
        }
    }

    public void onFinished(boolean success, String msg) {
        Log.d("HTTP", (success ? "OKAY " : "FAIL ") + msg);

        if (!success) {
            MessageDialog msgDlg = new MessageDialog(msg, this, MessageDialog.BUTTON_OK, null);
            msgDlg.copyGradientBackground(progressDlg);
            msgDlg.show();
        } else {
            page++;
            pageCount = getAppsTask.getTotalPages();

            // fetched new page of applets list
            Vector nextApps = getAppsTask.getApps();
            if (!nextApps.isEmpty()) {
                // add new applets
                appListAdapter.addApps(nextApps);
                bindContentView();
            }
        }

        getAppsTask = null;

        if (progressDlg != null) {
            progressDlg.destroy();
            progressDlg = null;
        }

        System.gc();
    }

    public void onUpdated(String msg) {
        if (progressDlg != null) {
            progressDlg.setMessage(msg);
        }
        Log.d("HTTP", ".... " + msg);
    }

    protected void pointerPressed(int x, int y) {
        if (appListView != null) {
            appListView.onPointerPressed(x, y);
            repaint();
        }
    }

    protected void onPointerReleased(int x, int y) {
        super.onPointerReleased(x, y);
        if (appListView != null) {
            int index = appListView.getClickedItemIndex(x, y);
            showAppInfo(index);
        }
    }

    public void onPointerDragged(int horizontalDistance, int verticalDistance) {
        super.onPointerDragged(horizontalDistance, verticalDistance);
        if (appListView != null) {
            appListView.scrollYBy(verticalDistance);
            repaint();
        }
    }

    protected void keyPressed(int keyCode) {
        switch (keyCode) {
        case Config.KEYCODE_UP:
        case Canvas.UP:
        case Canvas.KEY_NUM2:
            if (appListView != null) {
                appListView.selectPrevious();
                repaint();
            }
            break;

        case Config.KEYCODE_DOWN:
        case Canvas.DOWN:
        case Canvas.KEY_NUM8:
            if (appListView != null) {
                appListView.selectNext();
                repaint();

                if (page < pageCount) {
                    int index = appListView.getSelectedItemIndex();
                    if (index > appListAdapter.size() - 3) {
                        getNextPageOfApps();
                    }
                }
            }
            break;
        }
    }

    protected void keyRepeated(int keyCode) {
        this.keyPressed(keyCode);
    }

    protected void keyReleased(int keyCode) {
        switch (keyCode) {
        case Canvas.FIRE:
        case Config.KEYCODE_SELECT:
            if (appListView != null) {
                int index = appListView.getSelectedItemIndex();
                showAppInfo(index);
            }
            return;
        }

        super.keyReleased(keyCode);
    }

    public void onDataUpdated(Object data) {
        if (appListAdapter != null) {
            appListAdapter.notifyDataSetChanged();
            repaint();
        }
    }

    public void onClick(Object obj, int which) {
        /* In AppListActivity, only exit confirm dialog uses this callback */
        if (which == MessageDialog.BUTTON_OK) {
            Config.getMIDlet().notifyDestroyed();
        }
    }
}
