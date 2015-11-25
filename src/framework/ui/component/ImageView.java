
package framework.ui.component;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import framework.ui.effect.Effect;

/**
 * ImageView is used to show an image with Effect.
 *
 * @author Oscar Cai
 */

public class ImageView extends View {

    protected Image image;
    private Effect effect;

    private int effectPaddingLeft;
    private int effectPaddingTop;
    private int effectPaddingRight;
    private int effectPaddingBottom;

    public ImageView(Image img, Effect effect) {
        updateImage(img, effect);
    }

    public void updateImage(Image img) {
        updateImage(img, effect);
    }

    private void updateImage(Image img, Effect effect) {
        this.effect = effect;
        if (effect != null) {
            image = effect.filterImage(img);

            effectPaddingLeft = effect.getImagePaddingLeft();
            effectPaddingTop = effect.getImagePaddingTop();
            effectPaddingRight = effect.getImagePaddingRight();
            effectPaddingBottom = effect.getImagePaddingBottom();
        } else {
            image = img;
        }
        updateSize();
    }

    protected void updateSize() {
        if (image != null) {
            changeSize(image.getWidth(), image.getHeight());
            setContentWidth(image.getWidth() - effectPaddingLeft - effectPaddingRight);
            setContentHeight(image.getHeight() - effectPaddingTop - effectPaddingBottom);

            /**
             * FIXME: Maybe use the following invoking?
             *      setPaddings(getPaddingLeft() - effectPaddingLeft,
             *                  getPaddingTop() - effectPaddingTop,
             *                  getPaddingRight() - effectPaddingRight,
             *                  getPaddingBottom() - effectPaddingBottom);
             */
            setPaddings(effectPaddingLeft, effectPaddingTop, effectPaddingRight, effectPaddingBottom);
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        image = null;
        effect = null;
    }

    public void setPaddingLeft(int padding) {
        // effectPaddingLeft is the minimum left padding
        super.setPaddingLeft(padding < effectPaddingLeft ? effectPaddingLeft : padding);
    }

    public void setPaddingTop(int padding) {
        // effectPaddingLeft is the minimum top padding
        super.setPaddingTop(padding < effectPaddingTop ? effectPaddingTop : padding);
    }

    public void setPaddingRight(int padding) {
        // effectPaddingLeft is the minimum right padding
        super.setPaddingRight(padding < effectPaddingRight ? effectPaddingRight : padding);
    }

    public void setPaddingBottom(int padding) {
        // effectPaddingLeft is the minimum bottom padding
        super.setPaddingBottom(padding < effectPaddingBottom ? effectPaddingBottom : padding);
    }

    protected void paintContent(Graphics g, int left, int top, int width, int height) {
        if (image != null) {
            g.drawImage(image,
                        left + getPaddingLeft() - effectPaddingLeft,
                        top + getPaddingTop() - effectPaddingTop,
                        Graphics.LEFT | Graphics.TOP);
        }
    }
}
