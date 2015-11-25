
package framework.ui.activity;

/**
 * Interface used to allow the creator of an Activity to run some code when a
 * pointer dragging event occurs.
 *
 * @author Oscar Cai
 */

public interface PointerDragListener {

    /**
     * This method will be invoked when a pointer dragging event occurs.
     *
     * @param horizontalDistance the horizontal dragging distance. Positive value
     * means dragging right, while negative value means dragging left.
     * @param verticalDistance the vertical dragging distance. Positive value
     * means dragging down, while negative value means dragging up.
     */
    public void onPointerDragged(int horizontalDistance, int verticalDistance);
}
