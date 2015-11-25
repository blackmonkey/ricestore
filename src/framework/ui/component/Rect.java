
package framework.ui.component;

/**
 * Rect stores and provides rectangle information.
 *
 * @author Oscar Cai
 */

public class Rect {

    /**
     * The left, top of rectangle.
     */
    protected int left;
    protected int top;

    /**
     * The dimension of rectangle
     */
    protected int width;
    protected int height;

    public Rect() {
        this(0, 0, 0, 0);
    }

    public Rect(int width, int height) {
        this(0, 0, width, height);
    }

    public Rect(int left, int top, int width, int height) {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
    }

    public synchronized void moveTo(int left, int top) {
        this.left = left;
        this.top = top;
    }

    public synchronized void changeSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private boolean isEmpty() {
        return width == 0 && height == 0;
    }

    /**
     * Combine the specified Rect's area
     * @param v the Rect to combine.
     */
    public void add(Rect v) {
        if (v == null || v.isEmpty()) {
            return;
        }

        if (isEmpty()) {
            moveTo(v.getLeft(), v.getTop());
            changeSize(v.getWidth(), v.getHeight());
            return;
        }

        int right, bottom;

        if (v.getLeft() < left) {
            right = left + width;
            moveTo(v.getLeft(), top);
            changeSize(right - v.getLeft(), height);
        }

        if (v.getTop() < top) {
            bottom = top + height;
            moveTo(left, v.getTop());
            changeSize(width, bottom - v.getTop());
        }

        right = v.getLeft() + v.getWidth();
        if (right > left + width) {
            changeSize(right - left, height);
        }

        bottom = v.getTop() + v.getHeight();
        if (bottom > top + height) {
            changeSize(width, bottom - top);
        }
    }
}
