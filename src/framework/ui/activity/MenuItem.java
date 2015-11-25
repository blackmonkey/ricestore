
package framework.ui.activity;

import javax.microedition.lcdui.Image;

import framework.ui.component.View;

/**
 * MenuItem records icon, title and id information.
 *
 * @author Oscar Cai
 */

public class MenuItem {

    private int id;
    private Image icon;
    private String title;
    private View view;

    public MenuItem(int id, Image icon, String title) {
        this.id = id;
        this.icon = icon;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public Image getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    /**
     * Set the view corresponding to this menu item.
     *
     * @param v the View to bind.
     */
    public void setView(View v) {
        view = v;
    }

    public View getView() {
        return view;
    }
}
