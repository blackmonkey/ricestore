
package framework.ui.activity;

/**
 * <code>PointerDragBuffer</code> records certain pointerDragged events.
 * <p>
 * Only when the buffer is fulfilled, <code>PointerDragBuffer</code> notifies
 * {@link PointerDragListener} via invoking
 * {@link PointerDragListener#onPointerDragged()} method.
 * <p>
 * The depth of the buffer should be appropriate. Big depth will ignore slim
 * dragging, while small depth will make the buffer meaningless.
 * <p>
 * Component can get drag direction and distance from <code>PointerDragBuffer</code>.
 *
 * @author Oscar Cai
 */

public class PointerDragBuffer {
    private static final int TRACK_POS_BUF_DEPTH = 5;
    private int[][] trackPosBuffer;
    private int trackBufPos;
    private PointerDragListener listener;

    public PointerDragBuffer(PointerDragListener listener) {
        this.listener = listener;
        trackPosBuffer = new int[TRACK_POS_BUF_DEPTH][2];
        resetTrackPosBuffer();
    }

    /**
     * Reset track position buffer.
     */
    synchronized public void resetTrackPosBuffer() {
        for (int i = 0; i < trackPosBuffer.length; i++) {
            trackPosBuffer[i][0] = trackPosBuffer[i][1] = 0;
        }
        trackBufPos = 0;
    }

    /**
     * Get the horizontal dragging distance, ignoring dragging details.
     * @return the distance in pixels.
     */
    private int getHorizontalDistance() {
        if (trackBufPos > 0) {
            int i = trackBufPos;
            if (trackBufPos >= TRACK_POS_BUF_DEPTH) {
                i = TRACK_POS_BUF_DEPTH - 1;
            }
            return trackPosBuffer[i][0] - trackPosBuffer[0][0];
        }
        return 0;
    }

    /**
     * Get the vertical dragging distance, ignoring dragging details.
     * @return the distance in pixels.
     */
    private int getVerticalDistance() {
        if (trackBufPos > 0) {
            int i = trackBufPos;
            if (trackBufPos >= TRACK_POS_BUF_DEPTH) {
                i = TRACK_POS_BUF_DEPTH - 1;
            }
            return trackPosBuffer[i][1] - trackPosBuffer[0][1];
        }
        return 0;
    }

    /**
     * Record drag position.
     *
     * @param x
     * @param y
     * @return true if the buffer is fulfilled, false otherwise.
     */
    synchronized public void bufferTrackPos(int x, int y) {
        if (trackBufPos >= TRACK_POS_BUF_DEPTH) {
            resetTrackPosBuffer();
        }

        boolean fulfilled = false;
        if (trackBufPos == (TRACK_POS_BUF_DEPTH - 1)) {
            fulfilled = true;
        } else {
            fulfilled = false;
        }
        trackPosBuffer[trackBufPos][0] = x;
        trackPosBuffer[trackBufPos][1] = y;
        trackBufPos++;

        if (fulfilled && listener != null) {
            listener.onPointerDragged(getHorizontalDistance(), getVerticalDistance());
            resetTrackPosBuffer();
        }
    }
}
