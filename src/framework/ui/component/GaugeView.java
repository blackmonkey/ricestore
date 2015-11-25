
package framework.ui.component;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Image;

import framework.util.DrawUtil;

import main.config.Theme;

/**
 * Just like javax.microedition.lcdui.Gauge, GaugeView is a circle progress bar
 * indicating the operation progress whose max value is unknown.
 *
 * @author Oscar Cai
 */

public class GaugeView extends IconView implements Runnable {

    private static final Image[] ANIMATE_IMAGES = Theme.getGaugeImages();
    private static final int ANIMATE_DURATION = 1000; // in milliseconds
    private static final int ANIMATE_INTERVAL = ANIMATE_DURATION / ANIMATE_IMAGES.length;

    /**
     * The GaugeView animator icon updating thread
     */
    private Thread animateThread;

    /**
     * The GaugeView animator icon frame index
     */
    private int animateIndex;

    /**
     * Whether continue updating animator icon or not
     */
    private boolean runAnimate;

    /**
     * The Canvas contains the GaugeView
     */
    private Canvas canvas;

    /**
     * Whether need to repaint whole canvas
     */
    private boolean fullRepaint;

    public GaugeView(String label, Canvas canvas) {
        super(ANIMATE_IMAGES[0], label, Theme.GAUGE_EFFECT, LABEL_ALIGN_RIGHT, Theme.ITEM_GAP_SIZE);
        this.canvas = canvas;
        stop();
    }

    public void start() {
        runAnimate = true;
        fullRepaint = true;
        animateThread = new Thread(this);
        animateThread.start();
    }

    public void stop() {
        animateIndex = 0;
        fullRepaint = false;
        runAnimate = false;
        animateThread = null;
    }

    protected void onDestroy() {
        super.onDestroy();
        stop();
        canvas = null;
    }

    public void run() {
        while (runAnimate) {
            try {
                if (fullRepaint) {
                    canvas.repaint();
                    fullRepaint = false;
                } else {
                    DrawUtil.repaintCanvas(canvas, iconView);
                }
                animateIndex++;
                if (animateIndex >= ANIMATE_IMAGES.length) {
                    animateIndex = 0;
                }
                iconView.updateImage(ANIMATE_IMAGES[animateIndex]);
                Thread.sleep(ANIMATE_INTERVAL);
            } catch (Exception e) {
            }
        }
    }
}
