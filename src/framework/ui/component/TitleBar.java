
package framework.ui.component;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import framework.ui.effect.Effect;

import main.config.Theme;

/**
 * Currently, TitleBar only contains an IconView, while other items, such as
 * the search button, could be added in future.
 *
 * @author Oscar Cai
 */

public class TitleBar extends View {
    private IconView iconView;

    public TitleBar(View parent, Image icon, String label) {
        this(parent, icon, label, Effect.WIDTH_UNKNOWN, false);
    }

    public TitleBar(View parent, Image icon, String label, int maxDstWidth, boolean ellipsize) {
        iconView = new IconView(icon, label, Theme.TITLE_EFFECT, IconView.LABEL_ALIGN_RIGHT, Theme.ITEM_GAP_SIZE / 2, maxDstWidth, ellipsize);
        iconView.setAlignment(this, WRAP_CONTENT, WRAP_CONTENT, ALIGN_PARENT_LEFT | ALIGN_CENTER, 0);
        addChild(iconView);

        /**
         * HARDCODE: preset widthType/heightType to WRAP_CONTENT here to ensure
         * the width/height is updated as following.
         */
        widthType = WRAP_CONTENT;
        heightType = WRAP_CONTENT;

        setContentWidth(iconView.getWidth());
        setContentHeight(iconView.getHeight());
    }

    public void updateIcon(Image icon) {
        iconView.updateIcon(icon);
    }

    protected void onDestroy() {
        super.onDestroy();
        iconView = null;
    }

    protected void paintContent(Graphics g, int left, int top, int width, int height) {
    }
}
