
package framework.ui.effect;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;

/**
 * Render image and text in default plain style.
 *
 * @author Oscar Cai
 */

public class PlainEffect extends Effect {

    public PlainEffect(Font font, int color) {
        super(font, color);
    }

    public int getMaxHorizontalPadding() {
        return 0;
    }

    public int getMaxVerticalPadding() {
        return 0;
    }

    protected ARGBInfo getFilteredTextArgb(ARGBInfo argbInfo) {
        return argbInfo;
    }

    public Image filterImage(Image image) {
        return image;
    }
}
