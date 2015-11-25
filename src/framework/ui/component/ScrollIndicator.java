
package framework.ui.component;

import javax.microedition.lcdui.Image;

import framework.util.DrawUtil;

import main.config.Theme;

/**
 * The scroll indicator of ScrollView. Currently doesn't support shape changing.
 *
 * @author Oscar Cai
 */

public class ScrollIndicator extends ImageView {

    private static final int MIN_HEIGHT = ((Theme.SCROLL_INDICATOR_WIDTH >> 1) << 1) + 1;

    public ScrollIndicator() {
        super(null, null);
        changeSize(Theme.SCROLL_INDICATOR_WIDTH, MIN_HEIGHT);
        setVisible(false);
        updateImage();
    }

    private void updateImage() {
        if (getWidth() > 0 && getHeight() > 0) {
            int[] argb = DrawUtil.getHorizontalGradientRgb(getWidth(), getHeight(),
                             Theme.SCROLL_INDICATOR_START_COLOR, Theme.SCROLL_INDICATOR_END_COLOR,
                             Theme.SCROLL_INDICATOR_BORDER_COLOR, Theme.SCROLL_INDICATOR_ARC_WIDTH);
            image = Image.createRGBImage(argb, getWidth(), getHeight(), true);
        }
    }

    public void setAlignment(View v, int horizontal, int vertical) {
        super.setAlignment(v, EXACT_SIZE, EXACT_SIZE, horizontal, vertical);
    }

    public void setHeight(int height) {
        height = Math.max(height, MIN_HEIGHT);
        if (height != getHeight()) {
            super.setHeight(height);
            updateImage();
        }
    }
}
