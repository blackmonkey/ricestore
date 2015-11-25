
package framework.task;

import framework.net.HttpListener;

/**
 * StoreTask provides communication with Rice App Store server.
 *
 * @author Oscar Cai
 */

public abstract class StoreTask implements HttpListener {

    protected TaskListener listener;

    public StoreTask(TaskListener listener) {
        this.listener = listener;
    }

    /**
     * Notify the listener of the progress of current task.
     *
     * @param done true if this task finishes, false otherwise.
     * @param success whether the task operates successfully, only available
     * when done is true.
     * @param msg the status description.
     */
    protected void notifyProgress(boolean done, boolean success, String msg) {
        if (listener != null) {
            if (done) {
                listener.onFinished(success, msg);
            } else {
                listener.onUpdated(msg);
            }
        }
    }

    /**
     * Starts executing the task.
     */
    public abstract void execute();
}
