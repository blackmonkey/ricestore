
package framework.ui.activity;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import framework.ui.component.Container;
import framework.ui.component.View;
import framework.util.DrawUtil;

import main.config.Theme;

/**
 * Dialog shows a popped up UI of message. Currently, Dialog only
 * supports one style:
 * <ol>
 * <li>The Dialog background color gradients from
 * {@link Theme.DIALOG_BGCOLOR_BEGIN} to
 * {@link Theme.DIALOG_BGCOLOR_END}.</li>
 * <li>Every gradient background color is not full opacity.</li>
 * <li>The Dialog border is antialiasing round rectangle shape with some
 * transparent pixels.</li>
 * <li>Since the background and the border contains transparent pixels, the alpha
 * blending area will reach full opacity eventually. To avoid this problem, it is
 * necessary to repaint the snapshot of the Activity below the Dialog in
 * {@link #paint(Graphics)}.</li>
 * </ol>
 *
 * @author Oscar Cai
 */

public abstract class Dialog extends Activity implements Runnable {

    private static final int BGCOLOR_GRADIENT_STEPS = 10;
    private static final int[] BGCOLOR_GRADIENTS = DrawUtil.getGradient(
            Theme.DIALOG_BGCOLOR_BEGIN, Theme.DIALOG_BGCOLOR_END, BGCOLOR_GRADIENT_STEPS);
    private static final int ANIMATE_DURATION = 1000; // in milliseconds
    private static final int ANIMATE_INTERVAL = ANIMATE_DURATION / BGCOLOR_GRADIENTS.length;

    private int bgGradientIndex;
    private boolean canPaint;
    private View dialogPanel;
    private int bindFlag;
    private Container alphaMaskPanel;

    /**
     * Create a Dialog.
     *
     * @param message the message to show
     * @param activity the Activity popping this Dialog.
     */
    public Dialog(String message, Activity activity) {
        this(message, activity, 0);
    }

    protected Dialog(String message, Activity activity, int flag) {
        bgGradientIndex = 0;
        canPaint = false;
        bindFlag = flag;

        frame.setContentWidth(getWidth());
        frame.setContentHeight(getHeight());
        frame.setBackgroundImage(activity.getSnapshot());

        alphaMaskPanel = new Container();
        alphaMaskPanel.setAlignment(null, View.EXACT_SIZE, View.EXACT_SIZE, 0, 0);
        alphaMaskPanel.changeSize(getWidth(), getHeight());
        alphaMaskPanel.setContentWidth(getWidth());
        alphaMaskPanel.setContentHeight(getHeight());
        alphaMaskPanel.setBackgroundColor(BGCOLOR_GRADIENTS[0]);
        frame.addChild(alphaMaskPanel);

        dialogPanel = new Container();
        dialogPanel.setAlignment(alphaMaskPanel, View.WRAP_CONTENT, View.WRAP_CONTENT, View.ALIGN_PARENT_CENTER, View.ALIGN_PARENT_CENTER);
        bindDialogView(dialogPanel, message, flag);
        dialogPanel.set9PatchBackgroundImage(get9PatchBackgroundImage());
        alphaMaskPanel.addChild(dialogPanel);

        frame.layout();
    }

    public void copyGradientBackground(Dialog dialog) {
        if (dialog == null) {
            return;
        }

        bgGradientIndex = dialog.getBackgroundGradientIndex();
        if (bgGradientIndex >= BGCOLOR_GRADIENTS.length) {
            bgGradientIndex = BGCOLOR_GRADIENTS.length - 1;
        } else if (bgGradientIndex < 0) {
            bgGradientIndex = 0;
        }
        alphaMaskPanel.setBackgroundColor(BGCOLOR_GRADIENTS[bgGradientIndex]);
    }

    protected int getBackgroundGradientIndex() {
        return bgGradientIndex;
    }

    /**
     * Create concrete dialog content view. Should be override by subclass.
     *
     * @param v the dialog panel
     * @param message the dialog message
     * @param flag additional parameter
     */
    protected abstract void bindDialogView(View v, String message, int flag);

    /**
     * Get android style nine-patch background image of this dialog.
     * <p>
     * Could be override by subclass to set different image from this default one.
     *
     * @return background image
     */
    protected Image get9PatchBackgroundImage() {
        return Theme.getDialog9PatchBackground();
    }

    public void setMessage(String message) {
        bindDialogView(dialogPanel, message, bindFlag);
    }

    public int getOpacity() {
        return DrawUtil.getAlpha(Theme.DIALOG_BGCOLOR_BEGIN);
    }

    protected boolean canPaint() {
        return canPaint;
    }

    protected void showNotify() {
        canPaint = true;
        new Thread(this).start();
    }

    protected void hideNotify() {
        canPaint = false;
    }

    /**
     * @see javax.microedition.lcdui.Canvas#paint(Graphics)
     */
    protected void paint(Graphics g) {
        if (!canPaint) {
            return;
        }

        g.setClip(frame.getLeft(), frame.getTop(), frame.getWidth(), frame.getHeight());
        frame.paint(g);
    }

    protected void onDestroy() {
        super.onDestroy();
        bgGradientIndex = 0;
    }

    public void run() {
        /**
         * Update gradient background color until it reaches the end gradient
         * color.
         */
        try {
            for (; bgGradientIndex < BGCOLOR_GRADIENTS.length; bgGradientIndex++) {
                alphaMaskPanel.setBackgroundColor(BGCOLOR_GRADIENTS[bgGradientIndex]);
                repaint();
                Thread.sleep(ANIMATE_INTERVAL);
            }
        } catch (Exception e) {
        }
    }
}
