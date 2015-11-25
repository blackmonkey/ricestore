
package framework.ui.activity;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import framework.ui.component.Container;
import framework.ui.component.View;

import main.config.Config;

/**
 * Activity provides general GUI, which currently only supports full screen
 * mode.
 *
 * @author Oscar Cai
 */

public abstract class Activity extends Canvas {

    protected Container frame;

    public Activity() {
        setFullScreenMode(true);

        frame = new Container();
        frame.setAlignment(null, View.EXACT_SIZE, View.EXACT_SIZE, 0, 0);
        frame.changeSize(getWidth(), getHeight());
    }

    /**
     * Get the opacity identifier of the Activity
     *
     * @return opacity value in range of 0 ~ 255.
     */
    public abstract int getOpacity();

    /**
     * @see javax.microedition.lcdui.Canvas#paint(Graphics)
     */
    protected void paint(Graphics g) {
        g.setClip(frame.getLeft(), frame.getTop(), frame.getWidth(), frame.getHeight());
        frame.paint(g);
    }

    /**
     * Get a snapshot of the Activity.
     *
     * @return the snapshot Image.
     */
    protected Image getSnapshot() {
        Image snapshot = Image.createImage(frame.getWidth(), frame.getHeight());
        frame.paint(snapshot.getGraphics());
        return snapshot;
    }

    /**
     * Call this when your activity should be shown.
     */
    public void show() {
        Config.getGlobalDisplay().setCurrent(this);
        Config.getActivityManager().onShown(this);
    }

    /**
     * Call this when your activity should be hidden.
     */
    public void hide() {
        hideNotify();
        ActivityManager manager = Config.getActivityManager();
        if (manager.isToppest(this)) {
            Config.getGlobalDisplay().setCurrent(manager.getPreviousActivity(this));
        }
        manager.onHidden(this);
    }

    /**
     * Call this when the activity is done and should be closed.
     */
    public void destroy() {
        hide();
        frame.destroy();
        Config.getActivityManager().remove(this);
        onDestroy();
    }

    /**
     * Perform any final cleanup before an activity is destroyed. This can
     * happen because the activity is destroying (someone called
     * {@link #destroy} on it.
     * <p>
     * This method is usually implemented to free resources like threads that
     * are associated with an activity, so that a destroyed activity does not
     * leave such things around while the rest of its application is still
     * running.
     * <p>
     * <em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em>
     *
     * @see #destroy
     */
    protected void onDestroy() {
    }
}
