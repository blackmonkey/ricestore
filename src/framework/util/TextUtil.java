
package framework.util;

import java.util.Vector;

import javax.microedition.lcdui.Font;

import framework.ui.component.TextView;

/**
 * Utilities for handling text.
 *
 * @author Oscar Cai
 */

public final class TextUtil {
    public static boolean isEmpty(String text) {
        return text == null || text.length() == 0;
    }

    public static boolean isEmpty(TextView text) {
        return text == null || text.length() == 0;
    }

    public static boolean isEqual(String text1, String text2) {
        if (text1 == text2) { // including both null
            return true;
        }
        if (text1 != null) {
            return text1.equals(text2);
        }
        // to here, text2 must not be null
        return text2.equals(text1);
    }

    /**
     * Fit a string into the specified width. The string will be wrapped at
     * spaces within the text if possible, or ellipsized.
     *
     * @param text The String to fit.
     * @param font The font which will be used.
     * @param areaWidth the width to fit this string into.
     * @param ellipsize true to ellipsize the text content in dstWidth ending
     * with "...", false to wrap text content into multiple lines.
     *
     * @return A {@link Vector} of splited text lines.
     */
    public static Vector fit(String text, Font font, int areaWidth, boolean ellipsize) {
        if (ellipsize) {
            return ellipsizeText(text, font, areaWidth);
        }
        return wrapText(text, font, areaWidth);
    }

    private static Vector wrapText(String text, Font font, int areaWidth) {
        Vector lines = new Vector();
        char c;
        int i;
        int cWidth;
        int xpos;
        int nlpos = -1;
        int nlxpos = 0;
        int nlwidth = 0;
        int startline = 0;
        int width = 0;
        boolean isR = false;

        xpos = 0;

        for (i = 0; i < text.length(); i++) {
            c = text.charAt(i);

            if (xpos == 0 && c == ' ') {
                startline = i + 1;
                continue;
            }
            if (isR && c == '\n') {
                isR = false;
                startline = i + 1;
                continue;
            }
            isR = (c == '\r');
            cWidth = font.charWidth(c);

            xpos += cWidth;
            if (c == ' ' || c == '\r' || c == '\n') {
                nlpos = i;
                nlxpos = xpos;
                nlwidth = cWidth;
            }

            if (c == '\r' || c == '\n' || xpos > areaWidth) {
                if (nlpos == -1) {
                    if (startline == i) {
                        if (cWidth > width)
                            width = cWidth;
                        lines.addElement(text.substring(i, i + 1));
                        startline = i + 1;
                        xpos = 0;
                    } else {
                        if (xpos - cWidth > width)
                            width = xpos - cWidth;
                        lines.addElement(text.substring(startline, i));
                        startline = i;
                        xpos = cWidth;
                    }
                } else {
                    if (nlxpos - nlwidth > width)
                        width = nlxpos - nlwidth;
                    lines.addElement(text.substring(startline, nlpos));
                    startline = nlpos + 1;
                    xpos -= nlxpos;
                }
                nlpos = -1;
            }
        }
        if (startline < i) {
            if (xpos > width)
                width = xpos;
            lines.addElement(text.substring(startline, i));
        }
        return lines;
    }

    private static Vector ellipsizeText(String text, Font font, int areaWidth) {
        int i = 0;
        int lineWidth = 0;
        char c = 0;
        Vector lines = new Vector();

        for (i = 0; i < text.length(); i++) {
            c = text.charAt(i);
            lineWidth += font.charWidth(c);
            if (lineWidth > areaWidth) {
                text = text.substring(0, i - 3) + "...";
                break;
            }
            if (c == '\r' || c == '\n') {
                text = text.substring(0, i);
                break;
            }
        }

        lines.addElement(text);
        return lines;
    }
}
