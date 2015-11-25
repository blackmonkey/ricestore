
package framework.ui.activity;

import javax.microedition.lcdui.Canvas;

import main.config.Config;
import main.config.StringResource;
import main.config.Theme;
import framework.ui.component.ButtonView;
import framework.ui.component.Divider;
import framework.ui.component.Gap;
import framework.ui.component.TextView;
import framework.ui.component.View;

/**
 * MessageDialog shows a dialog with a message and one or two buttons.
 *
 * @author Oscar Cai
 */

public class MessageDialog extends Dialog {

    /**
     * ID for OK button.
     */
    public static final int BUTTON_OK = 1;

    /**
     * ID for Cancel button.
     */
    public static final int BUTTON_CANCEL = 2;

    private ButtonView buttonOk;
    private ButtonView buttonCancel;
    private int btnMask;
    private OnClickListener clickListener;

    /**
     * Create a message dialog with specified button(s).
     *
     * @param message the message to show
     * @param activity the Activity popping this Dialog.
     * @param btnMask the button(s) to show, maybe one of {@link #BUTTON_OK},
     * {@link #BUTTON_CANCEL} or {@link #BUTTON_OK} | {@link #BUTTON_CANCEL}.
     */
    public MessageDialog(String message, Activity activity, int btnMask, OnClickListener listener) {
        super(message, activity, btnMask);
        clickListener = listener;
    }

    private void initButtons() {
        if (buttonOk == null) {
            buttonOk = new ButtonView(StringResource.BUTTON_OK, Theme.DEFAULT_EFFECT);
            buttonOk.setCanvas(this);
        }
        if (buttonCancel == null) {
            buttonCancel = new ButtonView(StringResource.BUTTON_CANCEL, Theme.DEFAULT_EFFECT);
            buttonCancel.setCanvas(this);
        }
    }

    protected void bindDialogView(View panel, String message, int flag) {
        initButtons();
        btnMask = flag;

        /**
         * FIXME: It is possible that the message is too long to be shown in
         * screen. Need to wrap it.
         */

        TextView msgView = new TextView(message, Theme.DIALOG_DEFAULT_EFFECT);
        msgView.setAlignment(panel, View.WRAP_CONTENT, View.WRAP_CONTENT, 0, View.ALIGN_PARENT_TOP | View.ALIGN_CENTER);
        panel.addChild(msgView);

        int btnsWidth = 0;
        int btnsHeight = 0;

        switch (btnMask) {
        case BUTTON_OK:
            buttonOk.setAlignment(panel, View.WRAP_CONTENT, View.WRAP_CONTENT, 0, View.ALIGN_PARENT_BOTTOM | View.ALIGN_CENTER);
            buttonOk.requestFocus();
            panel.addChild(buttonOk);

            btnsWidth = buttonOk.getWidth();
            btnsHeight = buttonOk.getHeight();
            break;

        case BUTTON_CANCEL:
            buttonCancel.setAlignment(panel, View.WRAP_CONTENT, View.WRAP_CONTENT, 0, View.ALIGN_PARENT_BOTTOM | View.ALIGN_CENTER);
            buttonCancel.requestFocus();
            panel.addChild(buttonCancel);

            btnsWidth = buttonCancel.getWidth();
            btnsHeight = buttonCancel.getHeight();
            break;

        case BUTTON_OK | BUTTON_CANCEL:
            buttonOk.setAlignment(panel, View.WRAP_CONTENT, View.WRAP_CONTENT, 0, View.ALIGN_PARENT_BOTTOM | View.ALIGN_LEFT);
            buttonOk.requestFocus();
            panel.addChild(buttonOk);

            Gap btnGap = new Gap(buttonOk, Theme.ITEM_GAP_SIZE, buttonOk.getHeight(), View.ALIGN_RIGHT_OF | View.ALIGN_CENTER, 0);
            panel.addChild(btnGap);

            buttonCancel.setAlignment(btnGap, View.WRAP_CONTENT, View.WRAP_CONTENT, View.ALIGN_RIGHT_OF | View.ALIGN_CENTER, 0);
            panel.addChild(buttonCancel);

            btnsWidth = buttonOk.getWidth() + btnGap.getWidth() + buttonCancel.getWidth();
            btnsHeight = Math.max(buttonOk.getHeight(), buttonCancel.getHeight());
            break;
        }

        int panelContentWidth = Math.max(msgView.getWidth(), btnsWidth);

        Divider hDivider = new Divider(msgView, panelContentWidth, Theme.ITEM_GAP_SIZE, 0, View.ALIGN_BELOW | View.ALIGN_CENTER, Divider.HORIZONTAL);
        panel.addChild(hDivider);

        panel.setContentWidth(Math.max(msgView.getWidth(), btnsWidth));
        panel.setContentHeight(msgView.getHeight() + hDivider.getHeight() + btnsHeight);
        panel.layout();
    }

    protected void pointerPressed(int x, int y) {
        if ((btnMask & BUTTON_OK) != 0 && buttonOk.contains(x, y)) {
            buttonCancel.clearFocus();
            buttonOk.requestFocus();
        }
        if ((btnMask & BUTTON_CANCEL) != 0 && buttonCancel.contains(x, y)) {
            buttonOk.clearFocus();
            buttonCancel.requestFocus();
        }
    }

    protected void pointerReleased(int x, int y) {
        if ((btnMask & BUTTON_OK) != 0 && buttonOk.contains(x, y)) {
            if (clickListener != null) {
                clickListener.onClick(this, BUTTON_OK);
            }
            destroy();
        }
        if ((btnMask & BUTTON_CANCEL) != 0 && buttonCancel.contains(x, y)) {
            if (clickListener != null) {
                clickListener.onClick(this, BUTTON_CANCEL);
            }
            destroy();
        }
    }

    protected void keyPressed(int keyCode) {
        if (btnMask != (BUTTON_OK | BUTTON_CANCEL)) {
            return;
        }

        switch (keyCode) {
        case Config.KEYCODE_SOFT1:
            buttonCancel.clearFocus();
            buttonOk.requestFocus();
            break;

        case Config.KEYCODE_SOFT2:
        case Config.KEYCODE_BACK:
        case Config.KEYCODE_CLEAR:
            buttonOk.clearFocus();
            buttonCancel.requestFocus();
            break;

        case Config.KEYCODE_UP:
        case Config.KEYCODE_DOWN:
        case Config.KEYCODE_LEFT:
        case Config.KEYCODE_RIGHT:
        case Canvas.UP:
        case Canvas.DOWN:
        case Canvas.LEFT:
        case Canvas.RIGHT:
        case Canvas.GAME_A:
        case Canvas.GAME_B:
        case Canvas.GAME_C:
        case Canvas.GAME_D:
        case Canvas.KEY_NUM2:
        case Canvas.KEY_NUM4:
        case Canvas.KEY_NUM6:
        case Canvas.KEY_NUM8:
            if (buttonOk.isFocused()) {
                buttonOk.clearFocus();
                buttonCancel.requestFocus();
            } else if (buttonCancel.isFocused()) {
                buttonCancel.clearFocus();
                buttonOk.requestFocus();
            }
            break;
        }
    }

    protected void keyRepeated(int keyCode) {
        this.keyPressed(keyCode);
    }

    protected void keyReleased(int keyCode) {
        switch (keyCode) {
        case Canvas.FIRE:
        case Config.KEYCODE_SELECT:
        case Config.KEYCODE_SOFT1:
        case Config.KEYCODE_SOFT2:
        case Config.KEYCODE_BACK:
        case Config.KEYCODE_CLEAR:
            if (buttonOk.isFocused()) {
                if (clickListener != null) {
                    clickListener.onClick(this, BUTTON_OK);
                }
                destroy();
            } else if (buttonCancel.isFocused()) {
                if (clickListener != null) {
                    clickListener.onClick(this, BUTTON_CANCEL);
                }
                destroy();
            }
            break;
        }
    }
}
