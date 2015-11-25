
package framework.ui.component;

import javax.microedition.lcdui.Image;

import main.config.Theme;

import framework.util.DrawUtil;

/**
 * ShadowBottomBorder simulates a drop-shadow bottom border.
 *
 * @author Oscar Cai
 */

public class ShadowBottomBorder extends ImageView {

    public ShadowBottomBorder(int width) {
        super(null, null);

        int[] gradients = DrawUtil.getGradient(Theme.SHADOW_START_COLOR, Theme.SHADOW_END_COLOR, Theme.BORDER_SHADOW_WIDTH);
        int[] argb = new int[width * Theme.BORDER_SHADOW_WIDTH];
        int yOffset = 0;
        for (int y = 0; y < Theme.BORDER_SHADOW_WIDTH; y++) {
            for (int x = 0; x < width; x++) {
                argb[yOffset + x] = gradients[y];
            }
            yOffset += width;
        }

        image = Image.createRGBImage(argb, width, Theme.BORDER_SHADOW_WIDTH, true);
        updateSize();
    }
}
