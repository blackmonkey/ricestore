
package framework.ui.component;

import javax.microedition.lcdui.Graphics;

/**
 * ScrollView provides a scrollable container whose ScrollIndicator currently
 * aligns to right border.
 *
 * @author Oscar Cai
 */

public class ScrollView extends View {

    private ScrollIndicator indicator;
    protected Container contentView;
    private float vOffsetRadio = 0.0f;

    /**
     * Variables recording scroll position information
     */
    private int contentViewTop;
    private int indicatorTop;

    public Container getContentView() {
        initComponents();
        return contentView;
    }

    protected void initComponents() {
        if (contentView == null) {
            contentView = new Container();
            contentView.setAlignment(this, MATCH_PARENT, WRAP_CONTENT, ALIGN_PARENT_LEFT, ALIGN_PARENT_TOP);
            addChild(contentView);
        }
        if (indicator == null) {
            indicator = new ScrollIndicator();
            indicator.setAlignment(this, ALIGN_PARENT_RIGHT, 0);
            addChild(indicator);
        }
    }

    protected void resetScrollPosition() {
        contentViewTop = 0;
        indicatorTop = 0;
        if (contentView != null) {
            contentView.setTop(0);
        }
        if (indicator != null) {
            indicator.setTop(0);
        }
    }

    private void updateScrollIndicator() {
        initComponents();

        vOffsetRadio = (float) getHeight() / (float) contentView.getHeight();
        if (contentView.getHeight() <= getHeight()) {
            indicator.setVisible(false);
        } else {
            indicator.setVisible(true);
            indicator.setHeight((int) (getHeight() * vOffsetRadio));
        }
    }

    /**
     * Scrolls the view to the given child.
     *
     * @param childRect the screen rectangle to scroll to
     */
    public void scrollToRect(Rect childRect) {
        Rect rect = getScreenRect();
        int childBottom = childRect.getTop() + childRect.getHeight();
        int bottom = rect.getTop() + rect.getHeight();
        int scrollYDelta = 0;

        if (rect.getTop() > childRect.getTop()) {
            scrollYDelta = rect.getTop() - childRect.getTop();
        } else if (bottom < childBottom) {
            scrollYDelta = bottom - childBottom;
        }
        scrollYBy(scrollYDelta);
    }

    public synchronized void scrollYBy(int distance) {
        int minContentViewTop = getHeight() - contentView.getHeight();
        if (distance != 0 && minContentViewTop < 0) {
            contentViewTop = contentView.getTop() + distance;
            if (contentViewTop > 0) {
                contentViewTop = 0;
            } else if (contentViewTop < minContentViewTop) {
                contentViewTop = minContentViewTop;
            }
            contentView.setTop(contentViewTop);
            if (indicator != null) {
                indicatorTop = (int)((getTop() - contentView.getTop()) * vOffsetRadio);
                indicator.setTop(indicatorTop);
            }
        }
    }

    public synchronized void layout() {
        super.layout();
        updateScrollIndicator();
        contentView.setTop(contentViewTop);
        indicator.setTop(indicatorTop);
    }

    protected void onDestroy() {
        super.onDestroy();
        indicator = null;
        contentView = null;
    }

    protected void paintContent(Graphics g, int left, int top, int width, int height) {
        g.setClip(left, top, width, height);
    }
}
