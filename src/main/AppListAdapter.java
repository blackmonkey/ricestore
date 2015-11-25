
package main;

import java.util.Vector;

import javax.microedition.lcdui.Image;

import main.config.StringResource;
import main.config.Theme;

import framework.task.AppInfo;
import framework.ui.component.Container;
import framework.ui.component.DataUpdateListener;
import framework.ui.component.ImageView;
import framework.ui.component.ListAdapter;
import framework.ui.component.ListView;
import framework.ui.component.TextView;
import framework.ui.component.View;
import framework.util.DrawUtil;
import framework.util.TextUtil;

/**
 * A AppListAdapter object acts as a bridge between an {@link ListView} and the
 * Rice Store applets list for that view. The AppListAdapter provides access to
 * the applets list. The AppListAdapter is also responsible for making a
 * overviewing {@link View} for each applet.
 *
 * @author Oscar Cai
 */

public class AppListAdapter extends ListAdapter {

    private Image appDefaultIcon;
    private DataUpdateListener listener;

    /**
     * Create an AppListAdapter.
     *
     * @param dataSet the initial applets
     * @param icon the applet default icon
     * @param listener the listener to notify applet icon updating.
     */
    public AppListAdapter(Vector dataSet, Image icon, DataUpdateListener listener) {
        super(dataSet);
        appDefaultIcon = icon;
        this.listener = listener;
    }

    public Image getDefaultIcon() {
        return appDefaultIcon;
    }

    protected int bindView(int position, Container itemView) {
        AppInfo appInfo = (AppInfo) elementAt(position);

        itemView.setPaddingRight(Theme.SCROLL_INDICATOR_WIDTH * 3 / 2);

        Image icon = appInfo.getLogo();
        if (icon == null) {
            appInfo.fetchLogo(listener);
        } else if (appDefaultIcon != null) {
            icon = DrawUtil.scaleImage(icon, appDefaultIcon.getWidth(), appDefaultIcon.getHeight());
        }

        ImageView iconView = new ImageView(icon == null ? appDefaultIcon : icon, Theme.DEFAULT_EFFECT);
        iconView.setAlignment(itemView, View.WRAP_CONTENT, View.WRAP_CONTENT, View.ALIGN_PARENT_LEFT, View.ALIGN_PARENT_CENTER);
        if (icon != null) {
            iconView.setPaddingLeft((appDefaultIcon.getWidth() - icon.getWidth()) / 2);
        }
        iconView.setPaddingRight(iconView.getPaddingLeft() + Theme.ITEM_GAP_SIZE);
        itemView.addChild(iconView);

        Container infoContainer = new Container();
        infoContainer.setAlignment(iconView, View.WRAP_CONTENT, View.WRAP_CONTENT, View.ALIGN_RIGHT_OF | View.ALIGN_CENTER, 0);

        int maxWidth = itemView.getContentWidth() - iconView.getWidth();

        TextView nameView = new TextView(appInfo.getName(), Theme.LIST_TITLE_EFFECT, maxWidth, true);
        nameView.setAlignment(infoContainer, View.WRAP_CONTENT, View.WRAP_CONTENT, View.ALIGN_PARENT_LEFT, View.ALIGN_PARENT_TOP);
        infoContainer.addChild(nameView);

        String versionStr = StringResource.APP_VERSION + appInfo.getVersion();
        TextView versionView = new TextView(versionStr, Theme.LIST_SUMMARY_EFFECT);
        versionView.setAlignment(nameView, View.WRAP_CONTENT, View.WRAP_CONTENT, View.ALIGN_LEFT_WITH, View.ALIGN_BELOW);
        infoContainer.addChild(versionView);

        String vendorStr = StringResource.APP_VENDOR + appInfo.getVendor();
        TextView vendorView = new TextView(vendorStr, Theme.LIST_SUMMARY_EFFECT, maxWidth, true);
        vendorView.setAlignment(versionView, View.WRAP_CONTENT, View.WRAP_CONTENT, View.ALIGN_LEFT_WITH, View.ALIGN_BELOW);
        infoContainer.addChild(vendorView);

        itemView.addChild(infoContainer);

        TextView downCountView = new TextView(String.valueOf(appInfo.getTotalDownloadCount()), Theme.NUMBER_EFFECT);
        downCountView.setAlignment(itemView, View.WRAP_CONTENT, View.WRAP_CONTENT, View.ALIGN_PARENT_RIGHT, View.ALIGN_PARENT_CENTER);
        itemView.addChild(downCountView);

        TextView downCountLabelView = new TextView(StringResource.APP_TOTAL_DOWN_LABEL, Theme.NUMBER_LABEL_EFFECT);
        downCountLabelView.setAlignment(downCountView, View.WRAP_CONTENT, View.WRAP_CONTENT, View.ALIGN_LEFT_OF, View.ALIGN_TOP_WITH);
        itemView.addChild(downCountLabelView);

        return Math.max(iconView.getHeight(), nameView.getHeight() + versionView.getHeight() + vendorView.getHeight());
    }

    protected int updateView(int position, Container itemView) {
        AppInfo appInfo = (AppInfo) elementAt(position);

        ImageView iconView = (ImageView) itemView.getChild(0);
        Image icon = appInfo.getLogo();
        if (icon != null && appDefaultIcon != null) {
            icon = DrawUtil.scaleImage(icon, appDefaultIcon.getWidth(), appDefaultIcon.getHeight());
            iconView.updateImage(icon);
            iconView.setPaddingLeft((appDefaultIcon.getWidth() - icon.getWidth()) / 2);
            iconView.setPaddingRight(iconView.getPaddingLeft() + Theme.ITEM_GAP_SIZE);
        }

        Container infoContainer = (Container) itemView.getChild(1);

        TextView nameView = (TextView) infoContainer.getChild(0);
        if (!TextUtil.isEqual(nameView.getText(), appInfo.getName())) {
            nameView.updateText(appInfo.getName());
        }

        TextView versionView = (TextView) infoContainer.getChild(1);
        String newVerStr = StringResource.APP_VERSION + appInfo.getVersion();
        if (!TextUtil.isEqual(versionView.getText(), newVerStr)) {
            versionView.updateText(newVerStr);
        }

        TextView vendorView = (TextView) infoContainer.getChild(2);
        String newVendorStr = StringResource.APP_VENDOR + appInfo.getVendor();
        if (!TextUtil.isEqual(vendorView.getText(), newVendorStr)) {
            vendorView.updateText(newVendorStr);
        }

        TextView downCountView = (TextView) itemView.getChild(2);
        String countStr = String.valueOf(appInfo.getTotalDownloadCount());
        if (!TextUtil.isEqual(downCountView.getText(), countStr)) {
            downCountView.updateText(countStr);
        }

        return Math.max(iconView.getHeight(), nameView.getHeight() + versionView.getHeight() + vendorView.getHeight());
    }

    public void addApps(Vector apps) {
        if (apps != null) {
            for (int i = 0; i < apps.size(); i++) {
                addElement(apps.elementAt(i));
            }
        }
        notifyDataSetChanged();
    }
}
