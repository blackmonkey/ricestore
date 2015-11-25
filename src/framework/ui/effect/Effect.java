
package framework.ui.effect;

import java.util.Vector;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import framework.util.DrawUtil;
import framework.util.TextUtil;

/**
 * Effect is used to render image and text in individual style.
 *
 * @author Oscar Cai
 */

public abstract class Effect {
    public static final int DROP_SHADOW = 1;
    public static final int PLAIN = 2;

    public static final int WIDTH_UNKNOWN = -1;

    protected Font font;
    protected int fontColor;

    private String lastText;
    private Image lastFilteredText;
    private int lastDstTextWidth;

    protected Image lastImage;
    protected Image lastFilteredImage;

    /**
     * The paddings between the bounds of the original image to those of the
     * effect-applied image.
     */
    protected int stringPaddingLeft;
    protected int stringPaddingRight;
    protected int stringPaddingTop;
    protected int stringPaddingBottom;

    protected int imagePaddingLeft;
    protected int imagePaddingRight;
    protected int imagePaddingTop;
    protected int imagePaddingBottom;

    public Effect(Font font, int color) {
        this.font = font;
        fontColor = color;
    }

    /**
     * Get the maximum horizontal padding, i.e. the maximum sum of left padding
     * and right padding.
     *
     * @return size in pixels
     */
    public abstract int getMaxHorizontalPadding();

    /**
     * Get the maximum vertical padding, i.e. the maximum sum of top padding and
     * bottom padding.
     *
     * @return size in pixels
     */
    public abstract int getMaxVerticalPadding();

    /**
     * Get the padding of left bounds from original image to the effect-applied
     * image.
     *
     * @return size in pixels
     */
    public int getStringPaddingLeft() {
        return stringPaddingLeft;
    }

    /**
     * Get the padding of right bounds from original image to the effect-applied
     * image.
     *
     * @return size in pixels
     */
    public int getStringPaddingRight() {
        return stringPaddingRight;
    }

    /**
     * Get the padding of top bounds from original image to the effect-applied
     * image.
     *
     * @return size in pixels
     */
    public int getStringPaddingTop() {
        return stringPaddingTop;
    }

    /**
     * Get the padding of bottom bounds from original image to the effect-applied
     * image.
     *
     * @return size in pixels
     */
    public int getStringPaddingBottom() {
        return stringPaddingBottom;
    }

    /**
     * Get the padding of left bounds from original image to the effect-applied
     * image.
     *
     * @return size in pixels
     */
    public int getImagePaddingLeft() {
        return imagePaddingLeft;
    }

    /**
     * Get the padding of right bounds from original image to the effect-applied
     * image.
     *
     * @return size in pixels
     */
    public int getImagePaddingRight() {
        return imagePaddingRight;
    }

    /**
     * Get the padding of top bounds from original image to the effect-applied
     * image.
     *
     * @return size in pixels
     */
    public int getImagePaddingTop() {
        return imagePaddingTop;
    }

    /**
     * Get the padding of bottom bounds from original image to the effect-applied
     * image.
     *
     * @return size in pixels
     */
    public int getImagePaddingBottom() {
        return imagePaddingBottom;
    }

    /**
     * Apply effect on the specified text.
     *
     * @param text the text to apply effect.
     * @param dstWidth the maximum width of the destination area in which the
     * effect-applied text is drawn. Could be {@link #WIDTH_UNKNOWN} or bigger
     * than 0, while {@link #WIDTH_UNKNOWN} means the destination area with is
     * unknown.
     * @param ellipsize true to ellipsize the text content in dstWidth ending
     * with "...", false to wrap text content into multiple lines.
     * @return the effect-applied image.
     */
    public Image filterString(String text, int dstWidth, boolean ellipsize) {
        // Check whether the text has to be rerendered
        if ((lastText == text ||
            (!TextUtil.isEmpty(lastText) && !TextUtil.isEmpty(text) && lastText.equals(text))) &&
            dstWidth == lastDstTextWidth) {
            return lastFilteredText;
        }

        lastText = text;
        lastFilteredText = null;

        if (TextUtil.isEmpty(text)) {
            return null;
        }

        Vector lines = null;

        if (dstWidth == WIDTH_UNKNOWN) {
            lines = new Vector();
            lines.addElement(text);
        } else {
            lines = TextUtil.fit(text, font, dstWidth - getMaxHorizontalPadding(), ellipsize);
        }

        dstWidth = 0;
        int dstHeight = 0;
        Vector lineArgb = new Vector(lines.size());
        ARGBInfo info = null;
        for (int i = 0; i < lines.size(); i++) {
            info = getTextArgb((String) lines.elementAt(i), fontColor);
            lineArgb.addElement(info);
            dstHeight += info.height;
            dstWidth = Math.max(dstWidth, info.width);
        }

        lastDstTextWidth = dstWidth;

        int[] dstArgb = null;
        if (lines.size() <= 0) {
            return null;
        } else if (lines.size() == 1) {
            info = (ARGBInfo)(lineArgb.elementAt(0));
            dstArgb = info.argb;
        } if (lines.size() > 1) {
            /**
             * Combine all line ARGB pixels into one Image.
             */
            dstArgb = new int[dstWidth * dstHeight];
            int srcYOffset = 0;
            int dstYOffset = 0;
            for (int i = 0; i < lines.size(); i++) {
                info = (ARGBInfo) lineArgb.elementAt(i);
                srcYOffset = 0;
                if (info.argb != null) {
                    for (int j = 0; j < info.height; j++) {
                        System.arraycopy(info.argb, srcYOffset, dstArgb, dstYOffset, info.width);
                        srcYOffset += info.width;
                        dstYOffset += dstWidth;
                    }
                } else {
                    dstYOffset += dstWidth * info.height;
                }
            }
        }

        lastFilteredText = Image.createRGBImage(dstArgb, dstWidth, dstHeight, true);

        return lastFilteredText;
    }

    /**
     * Retrieves an ARGB integer array of the text with concrete effect.
     *
     * @param text the text
     * @param color the font color
     * @return the ARGB data that contains the given text
     */
    private ARGBInfo getTextArgb(String text, int color) {
        int width = font.stringWidth(text);
        int height = font.getHeight();

        if (width == 0) {
            // it is a empty line, return fake ARGB who only provides height
            // information.
            return new ARGBInfo(null, width, height);
        }

        int transparentColor = DrawUtil.getComplementaryColor(color);
        if (transparentColor == color) {
            transparentColor = 0;
        }

        // create Image, Graphics, ARGB-buffer
        Image img = Image.createImage(width, height);
        Graphics g = img.getGraphics();

        // draw pseudo transparent Background
        if (transparentColor != 0) {
            g.setColor(transparentColor);
            g.fillRect(0, 0, width, height);
        }

        // draw String on Graphics
        g.setFont(font);
        g.setColor(color);
        g.drawString(text, 0, 0, Graphics.LEFT | Graphics.TOP);

        // get RGB-Data from Image
        int[] argb = new int[width * height];
        img.getRGB(argb, 0, width, 0, 0, width, height);

        // check clearColor
        int[] clearColorArray = new int[1];
        img.getRGB(clearColorArray, 0, 1, 0, 0, 1, 1);
        transparentColor = clearColorArray[0];

        // transform RGB-Data
        for (int i = 0; i < argb.length; i++) {
            // perform Transparency
            if (argb[i] == transparentColor) {
                argb[i] = 0x00000000;
            }
        }

        return getFilteredTextArgb(new ARGBInfo(argb, width, height));
    }

    protected abstract ARGBInfo getFilteredTextArgb(ARGBInfo argbInfo);

    /**
     * Apply effect on the specified image.
     *
     * @param image the image to apply effect.
     * @return the effect-applied image.
     */
    public abstract Image filterImage(Image image);

    public Font getFont() {
        return font;
    }

    class ARGBInfo {
        public int[] argb;
        public int width;
        public int height;

        public ARGBInfo(int[] data, int w, int h) {
            argb = data;
            width = w;
            height = h;
        }
    }
}
