
package framework.ui.activity;

import java.util.Vector;

import javax.microedition.lcdui.Image;

import framework.ui.component.Container;
import framework.ui.component.ShadowBottomBorder;
import framework.ui.component.TitleBar;
import framework.ui.component.View;

import main.config.Config;
import main.config.Theme;

/**
 * RASActivity stands for Rice App Store Activity, which is composed of three
 * area: title bar, header view, content view Header view and content view can
 * be composed of sub items.
 *
 * @author Oscar Cai
 */

public abstract class RASActivity extends Activity implements OnMenuItemSelectedListener, PointerDragListener {

    protected Container contentView;
    private TitleBar titleBar;
    private Vector menuItems;
    private MenuItem backMenuItem;
    private PointerDragBuffer dragBuffer;
    private boolean justDragged;

    public RASActivity(String title) {
        this(null, title);
    }

    public RASActivity(Image icon, String title) {
        dragBuffer = new PointerDragBuffer(this);
        justDragged = false;

        if (icon == null) {
            icon = Theme.getAppIcon();
        }

        Theme.initTitlebarBackground(frame.getWidth());
        Theme.initRepeatedBg(frame.getWidth(), frame.getHeight());

        titleBar = new TitleBar(frame, icon, title, frame.getWidth(), true);
        titleBar.setAlignment(frame, View.EXACT_SIZE, View.EXACT_SIZE, View.ALIGN_PARENT_LEFT, View.ALIGN_PARENT_TOP);
        titleBar.changeSize(frame.getWidth(), Theme.getTitlebarHeight());
        titleBar.setContentHeight(Theme.getTitlebarHeight());
        titleBar.setBackgroundImage(Theme.getTitlebarBackground());
        frame.addChild(titleBar);

        contentView = new Container();
        contentView.setAlignment(titleBar, View.EXACT_SIZE, View.EXACT_SIZE, View.ALIGN_LEFT_WITH, View.ALIGN_BELOW);
        contentView.changeSize(frame.getWidth(), frame.getHeight() - titleBar.getHeight());
        contentView.setContentWidth(contentView.getWidth());
        contentView.setContentHeight(contentView.getHeight());
        contentView.setBackgroundImage(Theme.getRepeatedBg());
        bindContentView();
        frame.addChild(contentView);

        // Add ShadowBottomBorder here to ensure it is drawn at last.
        ShadowBottomBorder bBorder = new ShadowBottomBorder(frame.getWidth());
        bBorder.setAlignment(titleBar, View.EXACT_SIZE, View.EXACT_SIZE, View.ALIGN_LEFT_WITH, View.ALIGN_BELOW);
        frame.addChild(bBorder);

        frame.layout();

        menuItems = new Vector();
        createMenu(menuItems);
    }

    protected void updateIcon(Image icon) {
        titleBar.updateIcon(icon);
    }

    public int getOpacity() {
        return 255; // Full opacity
    }

    public void showMenu() {
        Menu menu = new Menu(menuItems, getSnapshot(), this);
        menu.show();
    }

    /**
     * Set the menu item responding to right soft button.
     *
     * @param item the menu item to bind.
     */
    protected void setBackMenuItem(MenuItem item) {
        backMenuItem = item;
    }

    protected abstract void bindContentView();
    protected abstract void createMenu(Vector menuItems);

    /**
     * Handle key released events.
     * <p>
     * <em>Derived classes must call through to the super class's
     * implementation of this method.</em>
     */
    protected void keyReleased(int keyCode) {
        switch (keyCode) {
        case Config.KEYCODE_SOFT1:
            showMenu();
            break;

        case Config.KEYCODE_BACK:
        case Config.KEYCODE_CLEAR:
        case Config.KEYCODE_SOFT2:
            if (backMenuItem != null) {
                onMenuItemSelected(backMenuItem.getId());
            }
            break;
        }
    }

    /**
     * Called when the pointer is released.
     * <p>
     * <em>Derived classes must call through to the super class's
     * implementation of this method.</em>
     */
    protected void pointerReleased(int x, int y) {
        dragBuffer.resetTrackPosBuffer();
        if (justDragged) {
            justDragged = false;
        } else {
            onPointerReleased(x, y);
        }
    }

    /**
     * Handle pointer released events.
     * <p>
     * <em>Derived classes must call through to the super class's
     * implementation of this method.</em>
     */
    protected void onPointerReleased(int x, int y) {
    }

    /**
     * Called when the pointer is dragged.
     * <p>
     * <em>Derived classes must call through to the super class's
     * implementation of this method.</em>
     */
    protected void pointerDragged(int x, int y) {
        dragBuffer.bufferTrackPos(x, y);
    }

    /**
     * Called when get pointer dragged event.
     * <p>
     * Class <code>RASActivity</code> has an empty implementation of this method,
     * and the subclass has to redefine it if it wants to listen this method.
     * <p>
     * <em>Derived classes must call through to the super class's
     * implementation of this method.</em>
     *
     * @param horizontalDistance the horizontal dragging distance. Positive value
     * means dragging right, while negative value means dragging left.
     * @param verticalDistance the vertical dragging distance. Positive value
     * means dragging down, while negative value means dragging up.
     */
    public void onPointerDragged(int horizontalDistance, int verticalDistance) {
        justDragged = true;
    }
}
