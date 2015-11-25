
package framework.ui.component;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import main.config.Theme;

import framework.ui.effect.Effect;
import framework.util.DrawUtil;

/**
 * IconView is composed of an ImageView for icon and an TextView for label.
 *
 * The label can be aligned at the top/right/bottom/left of the icon, while
 * currently only implement right and bottom alignment.
 *
 * The layout width and height of IconView currently is wrap content, i.e. the
 * default style. Other style will be implemented in future.
 *
 * @author Oscar Cai
 */

public class IconView extends View {

    public static final int LABEL_ALIGN_RIGHT = 1; // default align
    public static final int LABEL_ALIGN_BOTTOM = 2;
    public static final int LABEL_ALIGN_CENTER = 3;

    protected ImageView iconView;
    protected TextView labelView;
    protected boolean focused;

    private Image selectedBackground;

    public IconView(Image icon, String label, Effect effect, int labelAlign) {
        this(icon, label, effect, labelAlign, 0, Effect.WIDTH_UNKNOWN, false);
    }

    public IconView(Image icon, String label, Effect effect, int labelAlign, int labelLeftPadding) {
        this(icon, label, effect, labelAlign, labelLeftPadding, Effect.WIDTH_UNKNOWN, false);
    }

    public IconView(Image icon, String label, Effect effect, int labelAlign, int maxDstWidth, boolean ellipsize) {
        this(icon, label, effect, labelAlign, 0, maxDstWidth, ellipsize);
    }

    public IconView(Image icon, String label, Effect effect, int labelAlign, int labelLeftPadding, int maxDstWidth, boolean ellipsize) {
        iconView = new ImageView(icon, effect);
        addChild(iconView);

        if (labelAlign != LABEL_ALIGN_CENTER && labelAlign != LABEL_ALIGN_BOTTOM && maxDstWidth > 0) {
            // i.e. labelAlign equals to LABEL_ALIGN_RIGHT or other value.
            maxDstWidth -= iconView.getWidth();
        }

        labelView = new TextView(label, effect, maxDstWidth, ellipsize);
        addChild(labelView);

        /**
         * HARDCODE: preset widthType/heightType to WRAP_CONTENT here to ensure
         * the width/height is updated as following.
         */
        widthType = WRAP_CONTENT;
        heightType = WRAP_CONTENT;

        switch (labelAlign) {
        case LABEL_ALIGN_CENTER:
            int maxWidth = Math.max(iconView.getWidth(), labelView.getWidth());
            int maxHeight = Math.max(iconView.getHeight(), labelView.getHeight());

            iconView.setAlignment(this, WRAP_CONTENT, WRAP_CONTENT, ALIGN_PARENT_CENTER, ALIGN_PARENT_CENTER);
            labelView.setAlignment(this, WRAP_CONTENT, WRAP_CONTENT, ALIGN_PARENT_CENTER, ALIGN_PARENT_CENTER);
            setContentWidth(maxWidth);
            setContentHeight(maxHeight);
            break;

        case LABEL_ALIGN_BOTTOM:
            iconView.setAlignment(this, WRAP_CONTENT, WRAP_CONTENT, 0, ALIGN_PARENT_TOP | ALIGN_CENTER);
            labelView.setAlignment(iconView, WRAP_CONTENT, WRAP_CONTENT, 0, ALIGN_BELOW | ALIGN_CENTER);
            setContentWidth(Math.max(iconView.getWidth(), labelView.getWidth()));
            setContentHeight(iconView.getHeight() + labelView.getHeight());
            break;

        case LABEL_ALIGN_RIGHT:
        default:
            iconView.setAlignment(this, WRAP_CONTENT, WRAP_CONTENT, ALIGN_PARENT_LEFT | ALIGN_TOP, 0);
            labelView.setAlignment(iconView, WRAP_CONTENT, WRAP_CONTENT, ALIGN_RIGHT_OF | ALIGN_CENTER, 0);
            setContentWidth(iconView.getWidth() + labelLeftPadding + labelView.getWidth());
            setContentHeight(Math.max(iconView.getHeight(), labelView.getHeight()));
            break;
        }

        labelView.setPaddingLeft(labelLeftPadding);
        focused = false;
    }

    public void updateIcon(Image icon) {
        icon = DrawUtil.scaleImage(
                icon,
                iconView.getContentWidth(),
                iconView.getContentHeight());
        iconView.updateImage(icon);
    }

    private Image getSelectedBackgroundImage() {
        if (selectedBackground == null) {
            Image img = Theme.getIconBackgroudnSelected();
            selectedBackground = DrawUtil.stretch9PatchImage(img, getWidth(), getHeight(), false);
        }
        return selectedBackground;
    }

    public boolean isFocused() {
        return focused;
    }

    public void requestFocus() {
        if (!focused) {
            focused = true;
            setBackgroundImage(getSelectedBackgroundImage());
        }
    }

    public void clearFocus() {
        if (focused) {
            focused = false;
            setBackgroundColor(Theme.TRANSPARENT);
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        iconView = null;
        labelView = null;
        selectedBackground = null;
    }

    protected void paintContent(Graphics g, int left, int top, int width, int height) {
    }
}
