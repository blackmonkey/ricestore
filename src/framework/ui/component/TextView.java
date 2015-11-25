
package framework.ui.component;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import framework.ui.effect.Effect;

/**
 * TextView is used to show a string text with Effect.
 *
 * Currently, TextView only supports WRAP_CONTENT size type
 *
 * @author Oscar Cai
 */

public final class TextView extends View {

    private String text;
    private Image textImg;
    private Effect effect;
    private int maxWidth;
    private boolean ellipsize;

    private int effectPaddingLeft;
    private int effectPaddingTop;
    private int effectPaddingRight;
    private int effectPaddingBottom;

    public TextView(String text, Effect effect) {
        this(text, effect, Effect.WIDTH_UNKNOWN, false);
    }

    public TextView(String text, Effect effect, int maxWidth, boolean ellipsize) {
        this.effect = effect;
        this.maxWidth = maxWidth;
        this.ellipsize = ellipsize;

        /**
         * HARDCODE: Since the label text aligns in one line, we calculate the
         * actual paddings here.
         */
        effectPaddingLeft = 0;
        effectPaddingTop = effect.getFont().getHeight() - effect.getFont().getBaselinePosition();
        effectPaddingRight = effect.getStringPaddingLeft() + effect.getStringPaddingRight();
        effectPaddingBottom = effect.getFont().getHeight() - effect.getFont().getBaselinePosition();

        updateText(text);

        /**
         * FIXME: Maybe use the following invoking?
         *      setPaddings(getPaddingLeft() - effectPaddingLeft,
         *                  getPaddingTop() - effectPaddingTop,
         *                  getPaddingRight() - effectPaddingRight,
         *                  getPaddingBottom() - effectPaddingBottom);
         */
        setPaddings(effectPaddingLeft, effectPaddingTop, effectPaddingRight, effectPaddingBottom);
    }

    public void updateText(String text) {
        this.text = text;
        textImg = effect.filterString(text, maxWidth, ellipsize);
        changeSize(textImg.getWidth(), textImg.getHeight());
        setContentWidth(textImg.getWidth() - effectPaddingLeft - effectPaddingRight);
        setContentHeight(textImg.getHeight() - effectPaddingTop - effectPaddingBottom);
    }

    protected void onDestroy() {
        super.onDestroy();
        effect = null;
        textImg = null;
        text = null;
    }

    public int length() {
        return text != null ? text.length() : 0;
    }

    public String getText() {
        return text;
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
        if (textImg != null) {
            g.drawImage(textImg,
                        left + getPaddingLeft() - effectPaddingLeft,
                        top + getPaddingTop() - effectPaddingTop,
                        Graphics.LEFT | Graphics.TOP);
        }
    }
}
