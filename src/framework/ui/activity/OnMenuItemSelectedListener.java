
package framework.ui.activity;

/**
 * Interface used to allow the creator of an Activity to run some code when an
 * menu item of the Activity is clicked/selected.
 *
 * @author Oscar Cai
 */

public interface OnMenuItemSelectedListener {

    /**
     * This method will be invoked when a menu item of the Activity is clicked/selected.
     *
     * @param menuItemId The id of the menu item that was clicked/selected.
     */
    public void onMenuItemSelected(int menuItemId);
}
