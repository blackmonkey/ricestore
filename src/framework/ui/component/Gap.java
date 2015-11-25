
package framework.ui.component;

import javax.microedition.lcdui.Graphics;

/**
 * Gap is used as padding between views.
 *
 * @author Oscar Cai
 */

public class Gap extends View {

    /**
     * Create a Gap.
     *
     * @param v The view to which this view aligns.
     * @param width {@link #MATCH_PARENT} or a value greater than or equal to
     * <code>0</code>.
     * @param height {@link #MATCH_PARENT} or a value greater than or equal to
     * <code>0</code>.
     * @param horizontal horizontal alignment, see {@link #horizontalAlign}.
     * @param vertical vertical alignment, see {@link #verticalAlign}.
     */
    public Gap(View v, int width, int height, int horizontal, int vertical) {
        int wType = EXACT_SIZE;
        int hType = EXACT_SIZE;
        if (width == MATCH_PARENT) {
            wType = MATCH_PARENT;
            width = 0;
        } else if (width < 0) {
            width = 0;
        }
        if (height == MATCH_PARENT) {
            hType = MATCH_PARENT;
            height = 0;
        } else if (height < 0) {
            height = 0;
        }
        setAlignment(v, wType, hType, horizontal, vertical);
        setContentWidth(width);
        setContentHeight(height);
        changeSize(width, height);
    }

    protected void paintContent(Graphics g, int left, int top, int width, int height) {
    }
}
