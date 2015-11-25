
package framework.ui.component;

import javax.microedition.lcdui.Graphics;

import main.config.Theme;

/**
 * Divider shows a horizontal or vertical divider line.
 *
 * @author Oscar Cai
 */

public class Divider extends Gap {

    /**
     * Indicates a horizontal divider.
     */
    public static final int HORIZONTAL = 1;

    /**
     * Indicates a vertical divider.
     */
    public static final int VERTICAL = 2;

    private int orientation;

    /**
     * Create a Divider.
     *
     * @param v The view to which this view aligns.
     * @param width the width to set.
     * @param height the height to set.
     * @param horizontal horizontal alignment, see {@link #horizontalAlign}.
     * @param vertical vertical alignment, see {@link #verticalAlign}.
     * @param orientation the orientation of the divider, could be
     * {@link #HORIZONTAL} or {@link #VERTICAL}.
     */
    public Divider(View v, int width, int height, int horizontal, int vertical, int orientation) {
        super(v, width, height, horizontal, vertical);
        this.orientation = orientation;
    }

    protected void paintContent(Graphics g, int left, int top, int width, int height) {
        if (orientation == HORIZONTAL) {
            int y = top + height / 2;
            g.setColor(Theme.DIVIDER_DARK);
            g.drawLine(left, y, left + width, y);

            y++;
            if (y < top + height) {
                g.setColor(Theme.DIVIDER_LIGHT);
                g.drawLine(left, y, left + width, y);
            }
        } else if (orientation == VERTICAL) {
            int x = left + width / 2;
            g.setColor(Theme.DIVIDER_DARK);
            g.drawLine(x, top, x, top + height);

            x++;
            if (x < left + width) {
                g.setColor(Theme.DIVIDER_LIGHT);
                g.drawLine(x, top, x, top + height);
            }
        }
    }
}
