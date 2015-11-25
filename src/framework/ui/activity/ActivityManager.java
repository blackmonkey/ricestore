
package framework.ui.activity;

import java.util.Vector;

/**
 * ActivityManager maintains all {@link Activity}s.
 *
 * @author Oscar Cai
 */
public class ActivityManager {

    private static Vector activities = new Vector();

    /**
     * If the manager hasn't recorded the specified activity, add it to the end
     * of the managed queue.
     * <p>
     * If the manager has recorded the specified activity, move it to the end
     * of the managed queue.
     *
     * @param activity the shown activity.
     */
    public void onShown(Activity activity) {
        // remove potential component recorded in the vector.
        activities.removeElement(activity);
        // add the activity to the end of the vector.
        activities.addElement(activity);
    }

    /**
     * Try switching the specified activity with the one at the end of the
     * managed queue. Otherwise, just record it at the end of the managed
     * queue.
     *
     * @param activity the hidden activity.
     */
    public void onHidden(Activity activity) {
        if (activity != activities.lastElement()) {
            if (!activities.contains(activity)) {
                if (activities.isEmpty()) {
                    activities.addElement(activity);
                } else {
                    /**
                     * In this case the hidden activity has not been recorded,
                     * insert it to activities.size() - 1, i.e. below the current
                     * shown activity.
                     */
                    activities.insertElementAt(activity, activities.size() - 1);
                }
            }
        } else {
            if (activities.size() > 1) {
                /**
                 * The activity at the end of the vector is hidden now, switch its
                 * position with the one below.
                 */
                activities.setElementAt(activities.elementAt(activities.size() - 2), activities.size() - 1);
                activities.setElementAt(activity, activities.size() - 2);
            }
        }
    }

    /**
     * Get the previous activity of the specified one if it exists.
     *
     * @param activity the activity to check.
     * @return the previous activity instance if it exists, null otherwise.
     */
    public Activity getPreviousActivity(Activity activity) {
        int index = activities.indexOf(activity);
        if (index < 0) {
            /**
             * The activity hasn't been recorded, just return the one at the
             * end of the vector.
             */
            return (Activity) (activities.isEmpty() ? null : activities.lastElement());
        } else if (index > 0) {
            return (Activity) activities.elementAt(index - 1);
        }
        return null;
    }

    /**
     * Remove all occurrences of the specified activity from the managed queue.
     *
     * @param activity the activity to remove.
     */
    public void remove(Activity activity) {
        /**
         * Since {@link #onHidden(Activity)} ensures every component in the
         * managed queue is unique, just invoke removeElement() once here.
         */
        activities.removeElement(activity);
    }

    /**
     * Removes all components from the managed queue and sets its size to zero.
     */
    public void clear() {
        while (activities.size() > 0) {
            ((Activity)activities.elementAt(0)).destroy();
            /**
             * Activity.destroy() will invokes remove(Activity) to remove itself
             * from this manager.
             */
        }
    }

    public boolean isToppest(Activity activity) {
        int index = activities.indexOf(activity);
        return index < 0 || index == (activities.size() - 1);
    }
}
