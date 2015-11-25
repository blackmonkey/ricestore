
package framework.ui.component;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Image;

import main.config.Theme;
import framework.ui.effect.Effect;
import framework.util.DrawUtil;

/**
 * ButtonView shows a text label with nine-patch background image.
 *
 * @author Oscar Cai
 */

public class ButtonView extends IconView {

    private static final int NORMAL = 0;
    private static final int SELECTED = 1;

    private static Image[] bgImages = Theme.getButtonBackgrounds();

    /**
     * The Canvas contains the ButtonView
     */
    private Canvas canvas;

    public ButtonView(String text, Effect effect) {
        super(bgImages[NORMAL], text, effect, LABEL_ALIGN_CENTER);
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public void requestFocus() {
        if (!focused) {
            focused = true;
            iconView.updateImage(bgImages[SELECTED]);
            DrawUtil.repaintCanvas(canvas, iconView);
        }
    }

    public void clearFocus() {
        if (focused) {
            focused = false;
            iconView.updateImage(bgImages[NORMAL]);
            DrawUtil.repaintCanvas(canvas, iconView);
        }
    }
}
