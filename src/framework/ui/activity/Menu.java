
package framework.ui.activity;

import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Image;

import main.config.Config;
import main.config.Theme;

import framework.ui.component.Divider;
import framework.ui.component.IconView;
import framework.ui.component.View;

/**
 * Menu shows and maintains menu items of an RASActivity.
 *
 * @author Oscar Cai
 */

public class Menu extends Activity {

    private Vector menuItems;
    private MenuItem selectedMenuItem;
    private OnMenuItemSelectedListener listener;

    public Menu(Vector items, Image preActivityBg, OnMenuItemSelectedListener listener) {
        menuItems = items;
        this.listener = listener;

        frame.setContentWidth(getWidth());
        frame.setContentHeight(getHeight());
        frame.setBackgroundImage(preActivityBg);

        if (items != null) {
            int rows = (items.size() + Config.MENU_ITEMS_PER_ROW - 1) / Config.MENU_ITEMS_PER_ROW;
            int start, end;
            View alignView = frame;
            for (; rows > 0; rows--) {
                end = rows * Config.MENU_ITEMS_PER_ROW;
                start = end - Config.MENU_ITEMS_PER_ROW;
                if (end > items.size()) {
                    end = items.size();
                }
                if (start < 0) {
                    start = 0;
                }
                alignView = buildMenuItemRow(items, start, end, alignView);
            }
        }

        frame.layout();
    }

    /**
     * Build a row of menu items
     *
     * @param items Vector of menu items
     * @param start index of the first menu item to build
     * @param end the upper limit index (exclusive) of the menu items to build
     * @param alignView the view to align
     * @return the view for next menu item row to align.
     */
    private View buildMenuItemRow(Vector items, int start, int end, View alignView) {
        int hAlign, vAlign;
        int count = end - start;

        /**
         * Due to integer calculating, there maybe a pixel width margin on the
         * left edge of each row. Merge this margin to the most left menu item
         * if it exists.
         */
        int[] menuItemWidth = new int[count];
        int w = (frame.getWidth() - Theme.MENU_ITEM_DIVIDER_SIZE * (count - 1)) / count;
        for (int i = 0; i < count; i++) {
            menuItemWidth[i] = w;
        }
        w = w * count + Theme.MENU_ITEM_DIVIDER_SIZE * (count - 1);
        if (w < frame.getWidth()) {
            menuItemWidth[0] += frame.getWidth() - w;
        }

        for (int i = end - 1; i >= start; i--) {
            MenuItem menuItem = (MenuItem) items.elementAt(i);
            IconView menuItemView = new IconView(menuItem.getIcon(), menuItem.getTitle(), Theme.MENU_TITLE_EFFECT, IconView.LABEL_ALIGN_BOTTOM);
            menuItem.setView(menuItemView);

            hAlign = View.ALIGN_LEFT_OF;
            vAlign = View.ALIGN_TOP_WITH;
            if (i == end - 1) {
                if (alignView == frame) {
                    hAlign = View.ALIGN_PARENT_RIGHT;
                    vAlign = View.ALIGN_PARENT_BOTTOM;
                } else {
                    // To here, the alignView should be an instance of Divider.
                    // Align to latest horizontal Divider.
                    hAlign = View.ALIGN_RIGHT_WITH;
                    vAlign = View.ALIGN_ABOVE;
                }
            }
            menuItemView.setAlignment(alignView, View.EXACT_SIZE, View.WRAP_CONTENT, hAlign, vAlign);
            menuItemView.setWidth(menuItemWidth[i - start]);
            menuItemView.setPaddings(
                    (menuItemWidth[i - start] - menuItemView.getContentWidth()) / 2,
                    Theme.ITEM_GAP_SIZE / 2,
                    (menuItemWidth[i - start] - menuItemView.getContentWidth()) / 2,
                    Theme.ITEM_GAP_SIZE / 2);
            menuItemView.setBackgroundColor(Theme.MENU_ITEM_BGCOLOR);
            frame.addChild(menuItemView);
            alignView = menuItemView;

            if (i > start) {
                Divider vDiv = new Divider(menuItemView,
                        Theme.MENU_ITEM_DIVIDER_SIZE, menuItemView.getHeight(),
                        View.ALIGN_LEFT_OF, View.ALIGN_TOP_WITH,
                        Divider.VERTICAL);
                frame.addChild(vDiv);
                alignView = vDiv;
            }
        }

        // create horizontal Divider of this menu items row
        Divider vDiv = new Divider(alignView,
                frame.getWidth(), Theme.MENU_ITEM_DIVIDER_SIZE,
                View.ALIGN_LEFT_WITH, View.ALIGN_ABOVE,
                Divider.HORIZONTAL);
        frame.addChild(vDiv);

        return vDiv;
    }

    public int getOpacity() {
        return 255;
    }

    protected void pointerPressed(int x, int y) {
        selectedMenuItem = null;
        for (int i = 0; i < menuItems.size(); i++) {
            MenuItem item = (MenuItem) menuItems.elementAt(i);
            View v = item.getView();
            if (v != null) {
                if (v.contains(x, y)) {
                    v.setBackgroundColor(Theme.SELECTED_BGCOLOR);
                    selectedMenuItem = item;
                } else {
                    v.setBackgroundColor(Theme.MENU_ITEM_BGCOLOR);
                }
            }
        }
        if (selectedMenuItem == null) {
            // not clicked on any menu item, destroy menu panel.
            destroy();
        } else {
            repaint();
        }
    }

    protected void pointerReleased(int x, int y) {
        if (selectedMenuItem != null) {
            View v = selectedMenuItem.getView();
            if (v != null && v.contains(x, y)) {
                if (listener != null) {
                    listener.onMenuItemSelected(selectedMenuItem.getId());
                }
            }
            destroy();
        } else {
            // has handle this case in pointerPressed()
        }
    }

    protected void keyPressed(int keyCode) {
        int step = 0;
        switch (keyCode) {
        case Config.KEYCODE_UP:
        case Config.KEYCODE_LEFT:
        case Canvas.UP:
        case Canvas.LEFT:
        case Canvas.KEY_NUM2:
        case Canvas.KEY_NUM4:
            step = -1;
            break;

        case Config.KEYCODE_DOWN:
        case Config.KEYCODE_RIGHT:
        case Canvas.DOWN:
        case Canvas.RIGHT:
        case Canvas.KEY_NUM8:
        case Canvas.KEY_NUM6:
            step = 1;
            break;
        }

        if (step != 0) {
            View v;

            if (selectedMenuItem == null) {
                selectedMenuItem = (MenuItem) menuItems.elementAt(0);
            } else {
                for (int i = 0; i < menuItems.size(); i++) {
                    if (menuItems.elementAt(i) == selectedMenuItem) {
                        v = selectedMenuItem.getView();
                        if (v != null) {
                            v.setBackgroundColor(Theme.MENU_ITEM_BGCOLOR);
                        }

                        i += step;
                        if (i < 0) {
                            i = 0;
                        } else if (i >= menuItems.size()) {
                            i = menuItems.size() - 1;
                        }
                        selectedMenuItem = (MenuItem) menuItems.elementAt(i);

                        break;
                    }
                }
            }

            v = selectedMenuItem.getView();
            if (v != null) {
                v.setBackgroundColor(Theme.SELECTED_BGCOLOR);
            }
            repaint();
        }
    }

    protected void keyRepeated(int keyCode) {
        this.keyPressed(keyCode);
    }

    protected void keyReleased(int keyCode) {
        switch (keyCode) {
        case Canvas.FIRE:
        case Config.KEYCODE_SOFT1:
        case Config.KEYCODE_SELECT:
            if (selectedMenuItem != null) {
                if (listener != null) {
                    listener.onMenuItemSelected(selectedMenuItem.getId());
                }
            }
            /* fall through */
        case Config.KEYCODE_BACK:
        case Config.KEYCODE_CLEAR:
        case Config.KEYCODE_SOFT2:
            destroy();
            return;
        }
    }
}
