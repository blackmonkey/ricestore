
package framework.ui.activity;

/**
 * Interface used to allow the creator of a dialog to run some code when an
 * item on the dialog is clicked.
 *
 * @author Oscar Cai
 */

public interface OnClickListener {

    /**
     * This method will be invoked when a button in the dialog is clicked.
     *
     * @param obj The dialog that received the click.
     * @param which The button that was clicked (e.g.
     *            {@link MessageDialog#BUTTON_OK}).
     */
    public void onClick(Object obj, int which);
}
