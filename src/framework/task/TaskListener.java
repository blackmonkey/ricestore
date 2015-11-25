
package framework.task;

/**
 * A listener for Rice App Store server communication, providing a way to handle
 * the communication result.
 *
 * @author Oscar Cai
 */

public interface TaskListener {

    /**
     * Notify the task progress.
     *
     * @param msg task status description
     */
    public void onUpdated(String msg);

    /**
     * Notify the task has finished.
     *
     * @param success whether the task operates successfully
     * @param msg task success or failure description
     */
    public void onFinished(boolean success, String msg);
}
