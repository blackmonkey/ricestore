
package framework.ui.activity;

import framework.ui.component.GaugeView;
import framework.ui.component.View;

/**
 * ProgressDialog shows a message with an animating icon.
 * <p>
 * ProgressDialog contains a {@link GaugeView} who invokes Dialog.repaint()
 * when it updates animator icon.
 *
 * @author Oscar Cai
 */

public class ProgressDialog extends Dialog {

    private GaugeView gauge;

    public ProgressDialog(String message, Activity activity) {
        super(message, activity);
    }

    protected void bindDialogView(View panel, String message, int flag) {
        if (gauge != null) {
            panel.removeChild(gauge);
            gauge.destroy();
            gauge = null;
        }

        /**
         * FIXME: It is possible that the message is too long to be shown in
         * screen. Need to wrap it.
         */

        gauge = new GaugeView(message, this);
        gauge.setAlignment(panel, View.WRAP_CONTENT, View.WRAP_CONTENT, View.ALIGN_PARENT_CENTER, View.ALIGN_PARENT_CENTER);

        panel.setContentWidth(gauge.getWidth());
        panel.setContentHeight(gauge.getHeight());
        panel.addChild(gauge);
        panel.layout();

        if (canPaint()) {
            gauge.start();
        }
    }

    protected void showNotify() {
        super.showNotify();
        if (gauge != null) {
            gauge.start();
        }
    }

    protected void hideNotify() {
        super.hideNotify();
        if (gauge != null) {
            gauge.stop();
        }
    }
}
