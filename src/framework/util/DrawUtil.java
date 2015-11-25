
package framework.util;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import main.config.Theme;

import framework.ui.component.Rect;
import framework.ui.component.View;

/**
 * Utilities for drawing.
 *
 * @author Oscar Cai
 */

public final class DrawUtil {

    private static final int HORIZONTAL = 1;
    private static final int VERTICAL = 2;

    /**
     * Creates a gradient of colors.
     * This method is highly optimized and only uses bit-shifting and additions
     * (no multiplication nor devision), but it will create a new integer array
     * in each call.
     *
     * @param startColor the first color
     * @param endColor the last color
     * @param steps the number of colors in the gradient, when 2 is given, the
     * first one will be the startColor and the second one will the endColor.
     * @return an int array with the gradient.
     */
    public static int[] getGradient(int startColor, int endColor, int steps) {
        if (steps <= 0) {
            return new int[0];
        } else if (steps == 1) {
            return new int[] {startColor};
        } else if (steps == 2) {
            return new int[] {startColor, endColor};
        }
        int[] gradient = new int[steps];

        int startAlpha = startColor >>> 24;
        int startRed = (startColor >>> 16) & 0x00FF;
        int startGreen = (startColor >>> 8) & 0x0000FF;
        int startBlue = startColor & 0x00000FF;

        int endAlpha = endColor >>> 24;
        int endRed = (endColor >>> 16) & 0x00FF;
        int endGreen = (endColor >>> 8) & 0x0000FF;
        int endBlue = endColor & 0x00000FF;

        int stepAlpha = ((endAlpha - startAlpha) << 8) / (steps - 1);
        int stepRed = ((endRed - startRed) << 8) / (steps - 1);
        int stepGreen = ((endGreen - startGreen) << 8) / (steps - 1);
        int stepBlue = ((endBlue - startBlue) << 8) / (steps - 1);

        startAlpha <<= 8;
        startRed <<= 8;
        startGreen <<= 8;
        startBlue <<= 8;

        gradient[0] = startColor;
        for (int i = 1; i < steps; i++) {
            startAlpha += stepAlpha;
            startRed += stepRed;
            startGreen += stepGreen;
            startBlue += stepBlue;

            gradient[i] = ((startAlpha << 16) & 0xFF000000) | ((startRed << 8) & 0x00FF0000)
                    | (startGreen & 0x0000FF00) | (startBlue >>> 8);
        }

        return gradient;
    }

    /**
     * Retrieves the complementary color to the specified one.
     *
     * @param color the original argb color
     * @return the complementary color with the same alpha value
     */
    public static int getComplementaryColor(int color) {
        return (0xFF000000 & color) | ((255 - ((0x00FF0000 & color) >> 16)) << 16)
                | ((255 - ((0x0000FF00 & color) >> 8)) << 8) | (255 - (0x000000FF & color));
    }

    /**
     * Draws an (A)RGB array and fits it into the clipping area.
     *
     * @param rgb the (A)RGB array
     * @param x the horizontal start position
     * @param y the vertical start position
     * @param width the width of the RGB array
     * @param height the heigt of the RGB array
     * @param processAlpha true when the alpha values should be used so that pixels are blended with the background
     * @param g the graphics context
     */
    public static void drawRgb(int[] rgb, int x, int y, int width, int height, boolean processAlpha, Graphics g) {
        drawRgb(rgb, x, y, width, height, processAlpha, g.getClipX(), g.getClipY(), g.getClipWidth(), g.getClipHeight(), g);
    }

    /**
     * Draws an (A)RGB array and fits it into the clipping area.
     *
     * @param rgb the (A)RGB array
     * @param x the horizontal start position
     * @param y the vertical start position
     * @param width the width of the RGB array
     * @param height the heigt of the RGB array
     * @param processAlpha true when the alpha values should be used so that pixels are blended with the background
     * @param clipX the horizontal start of the clipping area
     * @param clipY the vertical start of the clipping area
     * @param clipWidth the width of the clipping area
     * @param clipHeight the height of the clipping area
     * @param g the graphics context
     */
    public static void drawRgb(int[] rgb, int x, int y, int width, int height,
            boolean processAlpha, int clipX, int clipY, int clipWidth,
            int clipHeight, Graphics g)
    {
        if (x + width < clipX || x > clipX + clipWidth || y + height < clipY || y > clipY + clipHeight) {
            // this is not within the visible bounds:
            return;
        }
        // adjust x / y / width / height to draw RGB within visible bounds:
        int offset = 0;
        if (x < clipX) {
            offset = clipX - x;
            x = clipX;
        }
        int scanlength = width;
        width -= offset;
        if (x + width > clipX + clipWidth) {
            width = (clipX + clipWidth) - x;
        }
        if (width <= 0) {
            return;
        }
        if (y < clipY) {
            offset += (clipY - y) * scanlength;
            height -= (clipY - y);
            y = clipY;
        }
        if (y + height > clipY + clipHeight) {
            height = (clipY + clipHeight) - y;
        }
        if (height <= 0) {
            return;
        }

        g.drawRGB(rgb, offset, scanlength, x, y, width, height, processAlpha);
    }

    public static int[] getVerticalGradientRgb(int width, int height, int startColor,
            int endColor, int borderColor) {
        return getVerticalGradientRgb(width, height, startColor, endColor, borderColor, 0);
    }

    public static int[] getVerticalGradientRgb(int width, int height, int startColor,
            int endColor, int borderColor, int arcWidth) {
        return getGradientRoundRectRgb(width, height, startColor, endColor, borderColor, arcWidth, VERTICAL);
    }

    public static int[] getHorizontalGradientRgb(int width, int height, int startColor,
            int endColor, int borderColor) {
        return getHorizontalGradientRgb(width, height, startColor, endColor, borderColor, 0);
    }

    public static int[] getHorizontalGradientRgb(int width, int height, int startColor,
            int endColor, int borderColor, int arcWidth) {
        return getGradientRoundRectRgb(width, height, startColor, endColor, borderColor, arcWidth, HORIZONTAL);
    }

    private static int[] getGradientRoundRectRgb(int width, int height, int startColor,
            int endColor, int borderColor, int arcR, int direct) {
        int[] rgb = new int[width * height];
        int gradientLen = direct == HORIZONTAL ? width : height;
        int[] gradient = getGradient(startColor, endColor, gradientLen);

        int x, y, left, right, top, bottom, offset;
        boolean transparentBorder = ((borderColor & 0x0FF000000) == 0);

        // fill gradient into whole rectangle according to direct
        if (direct == HORIZONTAL) {
            offset = 0;
            for (y = 0; y < height; y++) {
                System.arraycopy(gradient, 0, rgb, offset, gradientLen);
                offset += width;
            }
        } else /* if (direct == VERTICAL) */ {
            offset = 0;
            for (y = 0; y < height; y++) {
                for (x = 0; x < width; x++) {
                    rgb[offset + x] = gradient[y];
                }
                offset += width;
            }
        }

        if (!transparentBorder) {
            // fill whole top and bottom border
            offset = rgb.length - width;
            for (x = 0; x < width; x++) {
                rgb[x] = borderColor;
                rgb[offset + x] = borderColor;
            }

            // fill whole left and right border
            offset = width;
            bottom = height - 1;
            for (y = 1; y < bottom; y++) {
                rgb[offset] = borderColor;
                offset += width;
                rgb[offset - 1] = borderColor;
            }
        }

        // clear top transparent areas and draw top arcs
        offset = 0;
        for (y = 0; y < arcR; y++) {
            // clear left-top transparent area
            right = arcR - y - 1;
            for (x = 0; x < right; x++) {
                rgb[offset + x] = Theme.TRANSPARENT;
            }

            if (!transparentBorder) {
                // draw left-top arc
                rgb[offset + x] = borderColor;
            }

            // draw right-top arc
            left = width - arcR + y;
            if (!transparentBorder) {
                rgb[offset + left] = borderColor;
            }

            // clear right-top transparent area
            for (x = left + 1; x < width; x++) {
                rgb[offset + x] = Theme.TRANSPARENT;
            }

            offset += width;
        }

        // clear bottom transparent areas and draw bottom arcs
        offset = rgb.length - width;
        top = height - arcR;
        for (y = height - 1; y >= top; y--) {
            // clear left-bottom transparent area
            right = arcR - (height - y - 1) - 1;
            for (x = 0; x < right; x++) {
                rgb[offset + x] = Theme.TRANSPARENT;
            }

            if (!transparentBorder) {
                // draw left-bottom arc
                rgb[offset + x] = borderColor;
            }

            // draw right-bottom arc
            left = width - arcR + (height - y - 1);
            if (!transparentBorder) {
                rgb[offset + left] = borderColor;
            }

            // clear right-bottom transparent area
            for (x = left + 1; x < width; x++) {
                rgb[offset + x] = Theme.TRANSPARENT;
            }

            offset -= width;
        }

        return rgb;
    }

    /**
     * Get alpha value of the specified ARGB color value.
     * @param color the color to analyze
     * @return the alpha value
     */
    public static int getAlpha(int color) {
        return (color >> 24) & 0x0FF;
    }

    /**
     * Check whether the specified ARGB color is opacity.
     * @param color the color to analyze
     * @return true if alpha value is bigger than 0, false otherwise.
     */
    public static boolean isOpacity(int color) {
        return (color & 0x0FF000000) != 0;
    }

    /**
     * Stretch nine-patch image to specified size.
     * <p>
     * The nine-patch image of this project is similar with android nine-patch.
     * However, in this project, the nine-patch image doesn't contains content
     * indicator bounds. Its horizontal or vertical line is always the repeated
     * area.
     * <p>
     * Currently, only support enlarge nine-patch image.
     *
     * @param image the nine-patch image
     * @param contWidth the destination content width
     * @param contHeight the destination content height
     * @param usePadding true to count the padding within the nine-patch image,
     * false not to count.
     * @return stretched image.
     */
    public static Image stretch9PatchImage(Image image, int contWidth, int contHeight, boolean usePadding) {
        if (!usePadding) {
            contWidth = contWidth - image.getWidth() + 1;
            contHeight = contHeight - image.getHeight() + 1;
        }

        int width = image.getWidth() + contWidth - 1;
        int height = image.getHeight() + contHeight - 1;
        int hMiddle = image.getWidth() >> 1;
        int vMiddle = image.getHeight() >> 1;
        int right, bottom;
        int srcYOffset, dstYOffset;

        int[] srcArgb = new int[image.getWidth() * image.getHeight()];
        int[] dstArgb = new int[width * height];

        image.getRGB(srcArgb, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

        // copy left-top and right-top corners
        srcYOffset = dstYOffset = 0;
        for (int y = 0; y < vMiddle; y++) {
            // left-top corner
            System.arraycopy(srcArgb, srcYOffset, dstArgb, dstYOffset, hMiddle);

            // top middle lines
            right = width - hMiddle;
            for (int x = hMiddle; x < right; x++) {
                dstArgb[dstYOffset + x] = srcArgb[srcYOffset + hMiddle];
            }

            // right-top corner
            System.arraycopy(srcArgb, srcYOffset + hMiddle + 1, dstArgb, dstYOffset + width - hMiddle, hMiddle);

            srcYOffset += image.getWidth();
            dstYOffset += width;
        }

        bottom = height - vMiddle;
        for (int y = vMiddle; y < bottom; y++) {
            // left-middle line
            System.arraycopy(srcArgb, srcYOffset, dstArgb, dstYOffset, hMiddle);

            // middle line
            right = width - hMiddle;
            for (int x = hMiddle; x < right; x++) {
                dstArgb[dstYOffset + x] = srcArgb[srcYOffset + hMiddle];
            }

            // right-middle line
            System.arraycopy(srcArgb, srcYOffset + hMiddle + 1, dstArgb, dstYOffset + width - hMiddle, hMiddle);

            dstYOffset += width;
        }

        // copy left-bottom and right-bottom corners
        srcYOffset += image.getWidth();
        for (int y = vMiddle + 1; y < image.getHeight(); y++) {
            // left-bottom corner
            System.arraycopy(srcArgb, srcYOffset, dstArgb, dstYOffset, hMiddle);

            // bottom middle lines
            right = width - hMiddle;
            for (int x = hMiddle; x < right; x++) {
                dstArgb[dstYOffset + x] = srcArgb[srcYOffset + hMiddle];
            }

            // right-bottom corner
            System.arraycopy(srcArgb, srcYOffset + hMiddle + 1, dstArgb, dstYOffset + width - hMiddle, hMiddle);

            srcYOffset += image.getWidth();
            dstYOffset += width;
        }

        return Image.createRGBImage(dstArgb, width, height, true);
    }

    /**
     * Repaint the specified {@link View} on the specified {@link Canvas}.
     * <p>
     * <b>NOTE:</b> the {@link Canvas} must be in full screen mode.
     *
     * @param canvas
     * @param view
     */
    public static void repaintCanvas(Canvas canvas, View view) {
        Rect rct = view.getScreenRect();
        canvas.repaint(rct.getLeft(), rct.getTop(), rct.getWidth(), rct.getHeight());
    }

    /**
     * Scale the specified image to specified size, either bigger or smaller,
     * keeping aspect ratio.
     *
     * @param srcImg the source image to scale
     * @param dstWidth the destination width of scaling
     * @param dstHeight the destination height of scaling
     * @return the scaled image.
     */
    public static Image scaleImage(final Image srcImg, int dstWidth, int dstHeight) {
        if (srcImg == null || dstWidth <= 0 || dstHeight <= 0
                || (dstWidth == srcImg.getWidth() && dstHeight == srcImg.getHeight())) {
            return srcImg;
        }

        int[] srcArgb = new int[srcImg.getWidth() * srcImg.getHeight()];
        srcImg.getRGB(srcArgb, 0, srcImg.getWidth(), 0, 0, srcImg.getWidth(), srcImg.getHeight());

        // keep aspect ratio
        double dstRatio = (double) dstWidth / (double) dstHeight;
        double srcRatio = (double) srcImg.getWidth() / (double) srcImg.getHeight();
        if (dstRatio < srcRatio) {
            dstHeight = (int) (dstWidth / srcRatio);
        } else {
            dstWidth = (int) (dstHeight * srcRatio);
        }

        int[] dstArgb = new int[dstWidth * dstHeight];
        bilinearScale(srcArgb, srcImg.getWidth(), srcImg.getHeight(),
                      dstArgb, dstWidth, dstHeight);
        return Image.createRGBImage(dstArgb, dstWidth, dstHeight, true);
    }

    /**
     * Bilinear image scaling.<br>
     * See <a href='http://blog.csdn.net/hhygcy/article/details/4434870'>
     * http://blog.csdn.net/hhygcy/article/details/4434870</a>
     * <p>
     * This function scales images using <a href='http://en.wikipedia.org/wiki/Bilinear_interpolation'>
     * Bilinear interpolation</a>.
     *
     * @param srcArgb the source image data
     * @param srcWidth the width of source image
     * @param srcHeight the height of source image
     * @param dstArgb the destination image data
     * @param dstWidth the destination width of scaling
     * @param dstHeight the destination height of scaling
     */
    private static void bilinearScale(
            final int[] srcArgb, int srcWidth, int srcHeight,
            final int[] dstArgb, int dstWidth, int dstHeight) {
        int x, y;
        int ox, oy;
        int tmpx,tmpy;
        int ratio = (100 << 8) / (dstWidth * 100 / srcWidth);
        int srcYOffset, dstYOffset = 0;
        int srcLTOffset, srcRTOffset, srcLBOffset, srcRBOffset;
        for (int j = 0; j < dstHeight; j ++) {
            for (int i = 0; i < dstWidth; i ++) {
                tmpx = i * ratio;
                tmpy = j * ratio;
                ox = tmpx >> 8;
                oy = tmpy >> 8;
                x = tmpx & 0xFF;
                y = tmpy & 0xFF;

                srcYOffset = oy * srcWidth;
                srcLTOffset = srcYOffset + ox;
                srcRTOffset = srcYOffset + ox + (ox < srcWidth - 1 ? 1 : 0);
                srcLBOffset = oy < srcHeight - 1 ? srcLTOffset + srcWidth : srcLTOffset;
                srcRBOffset = oy < srcHeight - 1 ? srcRTOffset + srcWidth : srcRTOffset;
                dstArgb[dstYOffset + i] =
                    bilinearCalculateColor(
                            x, y,
                            tryGetColor(srcArgb, srcLTOffset), tryGetColor(srcArgb, srcRTOffset),
                            tryGetColor(srcArgb, srcLBOffset), tryGetColor(srcArgb, srcRBOffset));
            }
            dstYOffset += dstWidth;
        }
    }

    /**
     * Try to get the color of the specified point in the specified image.
     * @param argb The ARGB array of the image.
     * @param offset The linear logical offset of the point.
     * @return if the specified point does inside the ARGB array, return its color. Otherwise,
     * return full transparent, i.e. 0.
     */
    private static int tryGetColor(final int[] argb, int offset) {
        return (offset >= 0 && offset < argb.length) ? argb[offset] : 0;
    }

    /**
     * Calculate the color of bilinear interpolation point.
     * <p>
     * See <a href='http://en.wikipedia.org/wiki/Bilinear_interpolation'>
     * Bilinear interpolation</a>.
     *
     * @return the color value
     */
    private static int bilinearCalculateColor(int x, int y, int color00, int color10, int color01, int color11) {
        int b1 = (0x100 - x) * (0x100 - y);
        int b2 = x * (0x100 - y);
        int b3 = y * (0x100 - x);
        int b4 = x * y;
        int a = b1 * ((color00 >> 24) & 0x0FF) + b2 * ((color10 >> 24) & 0x0FF) + b3 * ((color01 >> 24) & 0x0FF) + b4 * ((color11 >> 24) & 0x0FF);
        int r = b1 * ((color00 >> 16) & 0x0FF) + b2 * ((color10 >> 16) & 0x0FF) + b3 * ((color01 >> 16) & 0x0FF) + b4 * ((color11 >> 16) & 0x0FF);
        int g = b1 * ((color00 >> 8) & 0x0FF) + b2 * ((color10 >> 8) & 0x0FF) + b3 * ((color01 >> 8) & 0x0FF) + b4 * ((color11 >> 8) & 0x0FF);
        int b = b1 * (color00 & 0x0FF) + b2 * (color10 & 0x0FF) + b3 * (color01 & 0x0FF) + b4 * (color11 & 0x0FF);
        a = a >> 16;
        r = r >> 16;
        g = g >> 16;
        b = b >> 16;
        if (a > 255) {
            a = 255;
        } else if (a < 0) {
            a = 0;
        }
        if (r > 255) {
            r = 255;
        } else if (r < 0) {
            r = 0;
        }
        if (g > 255) {
            g = 255;
        } else if (g < 0) {
            g = 0;
        }
        if (b > 255) {
            b = 255;
        } else if (b < 0) {
            b = 0;
        }
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
