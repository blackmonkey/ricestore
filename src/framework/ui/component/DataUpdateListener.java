
package framework.ui.component;

/**
 * Interface used to allow the event handler to run some code when an
 * bound data is updated.
 *
 * @author Oscar Cai
 */

public interface DataUpdateListener {

    /**
     * This method will be invoked when the bound data is updated.
     *
     * @param data the bound data
     */
    public void onDataUpdated(Object data);
}
