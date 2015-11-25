package main;

import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Image;

import main.config.Config;
import main.config.StringResource;
import main.config.Theme;
import main.util.Log;
import main.util.Utils;
import framework.task.AppInfo;
import framework.task.SendInstallNotifyTask;
import framework.task.TaskListener;
import framework.ui.activity.Dialog;
import framework.ui.activity.MenuItem;
import framework.ui.activity.MessageDialog;
import framework.ui.activity.ProgressDialog;
import framework.ui.activity.RASActivity;
import framework.ui.component.ButtonView;
import framework.ui.component.Container;
import framework.ui.component.DataUpdateListener;
import framework.ui.component.ImageView;
import framework.ui.component.ScrollView;
import framework.ui.component.TextView;
import framework.ui.component.View;
import framework.util.DrawUtil;

public class AppInfoActivity extends RASActivity implements DataUpdateListener, TaskListener {
    private static int BTN_INSTALL_TOP = 0;
    private static int BTN_INSTALL_BOTTOM = 1;
    private static int BTN_BACK = 2;
    private static int BTN_COUNT = 3;

    private AppInfo appInfo;
    private ScrollView scrollView;
    private Image defaultIcon;
    private ImageView appSnapshotView;
    private ButtonView[] buttons = new ButtonView[BTN_COUNT];

    private Dialog progressDlg;

    public AppInfoActivity(AppInfo info, Image defaultIcon) {
        super(info.getName());
        appInfo = info;
        if (appInfo.getLogo() == null) {
            appInfo.fetchLogo(this);
        } else {
            updateIcon(appInfo.getLogo());
        }
        if (appInfo.getSnapshot() == null) {
            appInfo.fetchSnapshot(this);
        }
        this.defaultIcon = defaultIcon;
        bindContentView();
    }

    protected void bindContentView() {
        if (appInfo == null) {
            /**
             * Need appInfo to initialize content view, therefore, just return
             * when this method is invoked by super constructor.
             */
            return;
        }

        scrollView = new ScrollView();
        scrollView.setAlignment(contentView, View.MATCH_PARENT, View.MATCH_PARENT, View.ALIGN_PARENT_LEFT, View.ALIGN_PARENT_TOP);
        contentView.addChild(scrollView);

        Container scrollContentView = scrollView.getContentView();
        scrollContentView.setContentWidth(contentView.getWidth() - Theme.ITEM_GAP_SIZE * 2);
        scrollContentView.setPaddings(Theme.ITEM_GAP_SIZE, Theme.ITEM_GAP_SIZE,
                Theme.ITEM_GAP_SIZE, Theme.ITEM_GAP_SIZE);
        int h = 0;

        Container iconInfo = new Container();
        iconInfo.setAlignment(scrollContentView, View.MATCH_PARENT, View.WRAP_CONTENT, View.ALIGN_PARENT_LEFT, View.ALIGN_PARENT_TOP);
        iconInfo.setPaddingBottom(Theme.ITEM_GAP_SIZE);

        int appSnapshotSideSize = frame.getWidth() / 3;
        Image scaledSnapshot = defaultIcon;
        if (appInfo.getSnapshot() != null) {
            scaledSnapshot = appInfo.getSnapshot();
        } else if (appInfo.getLogo() != null) {
            scaledSnapshot = appInfo.getLogo();
        }

        scaledSnapshot = DrawUtil.scaleImage(scaledSnapshot, appSnapshotSideSize, appSnapshotSideSize);
        appSnapshotView = new ImageView(scaledSnapshot, Theme.DIALOG_DEFAULT_EFFECT);
        appSnapshotView.setAlignment(iconInfo, View.EXACT_SIZE, View.EXACT_SIZE, View.ALIGN_PARENT_LEFT, View.ALIGN_PARENT_TOP);
        appSnapshotView.setPaddingRight(Theme.ITEM_GAP_SIZE);
        appSnapshotView.setContentWidth(appSnapshotSideSize);
        appSnapshotView.setContentHeight(appSnapshotSideSize);
        appSnapshotView.changeSize(appSnapshotView.getContentWidth() + Theme.ITEM_GAP_SIZE, appSnapshotView.getContentHeight());
        iconInfo.addChild(appSnapshotView);

        buttons[BTN_INSTALL_TOP] = new ButtonView(StringResource.INSTALL, Theme.APPINFO_BUTTON_EFFECT);
        buttons[BTN_INSTALL_TOP].setCanvas(this);
        buttons[BTN_INSTALL_TOP].setAlignment(appSnapshotView, View.WRAP_CONTENT, View.WRAP_CONTENT, View.ALIGN_RIGHT_OF, View.ALIGN_TOP_WITH);
        buttons[BTN_INSTALL_TOP].requestFocus();
        iconInfo.addChild(buttons[BTN_INSTALL_TOP]);

        TextView downloadTitle = new TextView(StringResource.DOWNLOAD_TITLE, Theme.SUB_TITLE_EFFECT);
        downloadTitle.setAlignment(buttons[BTN_INSTALL_TOP], View.WRAP_CONTENT, View.WRAP_CONTENT, View.ALIGN_LEFT_WITH, View.ALIGN_BELOW);
        iconInfo.addChild(downloadTitle);

        TextView totalDownloadLabel = new TextView(StringResource.APP_TOTAL_DOWN_LABEL2, Theme.DEFAULT_EFFECT);
        totalDownloadLabel.setAlignment(downloadTitle, View.WRAP_CONTENT, View.WRAP_CONTENT, View.ALIGN_LEFT_WITH, View.ALIGN_BELOW);
        iconInfo.addChild(totalDownloadLabel);

        TextView totalDownCountView = new TextView(String.valueOf(appInfo.getTotalDownloadCount()), Theme.NUMBER_EFFECT);
        totalDownCountView.setAlignment(totalDownloadLabel, View.WRAP_CONTENT, View.WRAP_CONTENT, View.ALIGN_RIGHT_OF | View.ALIGN_CENTER, 0);
        iconInfo.addChild(totalDownCountView);

        TextView monthDownloadLabel = new TextView(StringResource.APP_MONTH_DOWN_LABEL, Theme.DEFAULT_EFFECT);
        monthDownloadLabel.setAlignment(totalDownloadLabel, View.WRAP_CONTENT, View.WRAP_CONTENT, View.ALIGN_LEFT_WITH, View.ALIGN_BELOW);
        iconInfo.addChild(monthDownloadLabel);

        TextView monthDownCountView = new TextView(String.valueOf(appInfo.getMonthDownloadCount()), Theme.NUMBER_EFFECT);
        monthDownCountView.setAlignment(monthDownloadLabel, View.WRAP_CONTENT, View.WRAP_CONTENT, View.ALIGN_RIGHT_OF | View.ALIGN_CENTER, 0);
        iconInfo.addChild(monthDownCountView);

        TextView weekDownloadLabel = new TextView(StringResource.APP_WEEK_DOWN_LABEL, Theme.DEFAULT_EFFECT);
        weekDownloadLabel.setAlignment(monthDownloadLabel, View.WRAP_CONTENT, View.WRAP_CONTENT, View.ALIGN_LEFT_WITH, View.ALIGN_BELOW);
        iconInfo.addChild(weekDownloadLabel);

        TextView weekDownCountView = new TextView(String.valueOf(appInfo.getWeekDownloadCount()), Theme.NUMBER_EFFECT);
        weekDownCountView.setAlignment(weekDownloadLabel, View.WRAP_CONTENT, View.WRAP_CONTENT, View.ALIGN_RIGHT_OF | View.ALIGN_CENTER, 0);
        iconInfo.addChild(weekDownCountView);

        iconInfo.setContentHeight(Math.max(appSnapshotView.getHeight(),
                buttons[BTN_INSTALL_TOP].getHeight() + downloadTitle.getHeight() + totalDownloadLabel.getHeight()
                + monthDownCountView.getHeight() + weekDownloadLabel.getHeight()));

        scrollContentView.addChild(iconInfo);
        h += iconInfo.getHeight();

        TextView nameView = new TextView(appInfo.getName(), Theme.LIST_TITLE_EFFECT, scrollContentView.getContentWidth(), false);
        nameView.setAlignment(iconInfo, View.MATCH_PARENT, View.WRAP_CONTENT, View.ALIGN_LEFT_WITH, View.ALIGN_BELOW);
        scrollContentView.addChild(nameView);
        h += nameView.getHeight();

        String versionStr = StringResource.APP_VERSION + appInfo.getVersion();
        TextView versionView = new TextView(versionStr, Theme.DEFAULT_EFFECT);
        versionView.setAlignment(nameView, View.MATCH_PARENT, View.WRAP_CONTENT, View.ALIGN_LEFT_WITH, View.ALIGN_BELOW);
        scrollContentView.addChild(versionView);
        h += versionView.getHeight();

        String vendorStr = StringResource.APP_VENDOR + appInfo.getVendor();
        TextView vendorView = new TextView(vendorStr, Theme.DEFAULT_EFFECT, scrollContentView.getContentWidth(), false);
        vendorView.setAlignment(versionView, View.MATCH_PARENT, View.WRAP_CONTENT, View.ALIGN_LEFT_WITH, View.ALIGN_BELOW);
        scrollContentView.addChild(vendorView);
        h += vendorView.getHeight();

        String introStr = StringResource.APP_INTRO + appInfo.getIntroduction();
        TextView introView = new TextView(introStr, Theme.DEFAULT_EFFECT, scrollContentView.getContentWidth(), false);
        introView.setContentWidth(scrollContentView.getContentWidth());
        introView.setAlignment(vendorView, View.MATCH_PARENT, View.WRAP_CONTENT, View.ALIGN_LEFT_WITH, View.ALIGN_BELOW);
        scrollContentView.addChild(introView);
        h += introView.getHeight();

        buttons[BTN_INSTALL_BOTTOM] = new ButtonView(StringResource.INSTALL, Theme.APPINFO_BUTTON_EFFECT);
        buttons[BTN_INSTALL_BOTTOM].setCanvas(this);
        buttons[BTN_BACK] = new ButtonView(StringResource.MENU_BACK_TITLE, Theme.APPINFO_BUTTON_EFFECT);
        buttons[BTN_BACK].setCanvas(this);

        Container bottomBtns = new Container();
        bottomBtns.setAlignment(introView, View.EXACT_SIZE, View.WRAP_CONTENT, 0, View.ALIGN_BELOW | View.ALIGN_CENTER);
        bottomBtns.setWidth(Math.max(frame.getWidth() * 2 / 3, buttons[BTN_INSTALL_BOTTOM].getWidth() + buttons[BTN_BACK].getWidth()));
        bottomBtns.setContentWidth(bottomBtns.getWidth());
        bottomBtns.setPaddingTop(Theme.ITEM_GAP_SIZE / 3);
        bottomBtns.setPaddingBottom(Theme.ITEM_GAP_SIZE / 3);

        buttons[BTN_INSTALL_BOTTOM].setAlignment(bottomBtns, View.WRAP_CONTENT, View.WRAP_CONTENT, View.ALIGN_PARENT_LEFT, View.ALIGN_PARENT_TOP);
        bottomBtns.addChild(buttons[BTN_INSTALL_BOTTOM]);

        buttons[BTN_BACK].setAlignment(bottomBtns, View.WRAP_CONTENT, View.WRAP_CONTENT, View.ALIGN_PARENT_RIGHT, View.ALIGN_PARENT_TOP);
        bottomBtns.addChild(buttons[BTN_BACK]);

        bottomBtns.setContentHeight(Math.max(buttons[BTN_INSTALL_BOTTOM].getHeight(), buttons[BTN_BACK].getHeight()));

        scrollContentView.addChild(bottomBtns);
        h += bottomBtns.getHeight();

        scrollContentView.setContentHeight(h);

        contentView.layout();
        repaint();
    }

    protected void createMenu(Vector menuItems) {
        menuItems.addElement(new MenuItem(Config.MENU_INSTALL_ID, Theme.getMenuInstallIcon(), StringResource.MENU_INSTALL_TITLE));
        menuItems.addElement(new MenuItem(Config.MENU_BACK_ID, Theme.getMenuBackIcon(), StringResource.MENU_BACK_TITLE));
        setBackMenuItem((MenuItem) menuItems.lastElement());
    }

    private void doInstall() {
    	new SendInstallNotifyTask(appInfo.getId(), this).execute();
        Utils.requestInstall(appInfo.getJadUrl());
    }

    public void onMenuItemSelected(int menuItemId) {
        switch (menuItemId) {
        case Config.MENU_INSTALL_ID:
            doInstall();
            break;

        case Config.MENU_BACK_ID:
            destroy();
            break;
        }
    }

    boolean isButtonClicked(int idx, int x, int y) {
        return buttons[idx] != null && buttons[idx].contains(x, y);
    }

    boolean isButtonFocused(int idx) {
        return buttons[idx] != null && buttons[idx].isFocused();
    }

    boolean isButtonNotFocused(int idx) {
        return buttons[idx] != null && !buttons[idx].isFocused();
    }

    protected void pointerPressed(int x, int y) {
        super.pointerPressed(x, y);
        for (int i = 0; i < BTN_COUNT; i++) {
            if (isButtonClicked(i, x, y) && isButtonNotFocused(i)) {
                for (int j = 0; j < BTN_COUNT; j++) {
                    if (i != j) {
                        buttons[j].clearFocus();
                    }
                }
                buttons[i].requestFocus();
                repaint();
                break;
            }
        }
    }

    public void onPointerDragged(int horizontalDistance, int verticalDistance) {
        super.onPointerDragged(horizontalDistance, verticalDistance);
        if (scrollView != null) {
            scrollView.scrollYBy(verticalDistance);
            repaint();
        }
    }

    protected void onPointerReleased(int x, int y) {
        super.onPointerReleased(x, y);
        if (isButtonClicked(BTN_INSTALL_TOP, x, y) || isButtonClicked(BTN_INSTALL_BOTTOM, x, y)) {
            doInstall();
        } else if (isButtonClicked(BTN_BACK, x, y)) {
            destroy();
        }
    }

    protected void keyPressed(int keyCode) {
        switch (keyCode) {
        case Config.KEYCODE_LEFT:
        case Config.KEYCODE_UP:
        case Canvas.LEFT:
        case Canvas.UP:
        case Canvas.KEY_NUM2:
        case Canvas.KEY_NUM4:
            if (buttons[BTN_INSTALL_TOP] != null && isButtonFocused(BTN_INSTALL_BOTTOM)) {
                buttons[BTN_INSTALL_BOTTOM].clearFocus();
                buttons[BTN_INSTALL_TOP].requestFocus();
                scrollView.scrollToRect(buttons[BTN_INSTALL_TOP].getScreenRect());
            } else if (buttons[BTN_INSTALL_BOTTOM] != null && isButtonFocused(BTN_BACK)) {
                buttons[BTN_BACK].clearFocus();
                buttons[BTN_INSTALL_BOTTOM].requestFocus();
                scrollView.scrollToRect(buttons[BTN_INSTALL_BOTTOM].getScreenRect());
            }
            repaint();
            break;

        case Config.KEYCODE_RIGHT:
        case Config.KEYCODE_DOWN:
        case Canvas.RIGHT:
        case Canvas.DOWN:
        case Canvas.KEY_NUM6:
        case Canvas.KEY_NUM8:
            if (buttons[BTN_INSTALL_BOTTOM] != null && isButtonFocused(BTN_INSTALL_TOP)) {
                buttons[BTN_INSTALL_TOP].clearFocus();
                buttons[BTN_INSTALL_BOTTOM].requestFocus();
                scrollView.scrollToRect(buttons[BTN_INSTALL_BOTTOM].getScreenRect());
            } else if (buttons[BTN_BACK] != null && isButtonFocused(BTN_INSTALL_BOTTOM)) {
                buttons[BTN_INSTALL_BOTTOM].clearFocus();
                buttons[BTN_BACK].requestFocus();
                scrollView.scrollToRect(buttons[BTN_BACK].getScreenRect());
            }
            repaint();
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
            if (isButtonFocused(BTN_INSTALL_TOP) || isButtonFocused(BTN_INSTALL_BOTTOM)) {
                doInstall();
            } else if (isButtonFocused(BTN_BACK)) {
                destroy();
            }
            return;
        }

        super.keyReleased(keyCode);
    }

    public void onDataUpdated(Object data) {
        if (appInfo == data) {
            if (appInfo.getLogo() != null) {
                updateIcon(appInfo.getLogo());
            }
            if (appInfo.getSnapshot() != null) {
                Image snapshot = DrawUtil.scaleImage(
                        appInfo.getSnapshot(),
                        appSnapshotView.getContentWidth(),
                        appSnapshotView.getContentHeight());
                appSnapshotView.updateImage(snapshot);
            }
            repaint();
        }
    }

    public void onFinished(boolean success, String msg) {
        Log.d("HTTP", (success ? "OKAY " : "FAIL ") + msg);

        MessageDialog msgDlg = new MessageDialog(msg, this, MessageDialog.BUTTON_OK, null);
        msgDlg.copyGradientBackground(progressDlg);
        msgDlg.show();

        if (progressDlg != null) {
            progressDlg.destroy();
            progressDlg = null;
        }

        System.gc();
    }

    public void onUpdated(String msg) {
        if (progressDlg == null) {
            progressDlg = new ProgressDialog(msg, this);
            progressDlg.show();
        } else {
            progressDlg.setMessage(msg);
        }
        Log.d("HTTP", ".... " + msg);
    }
}
