
package framework.net;

/**
 * A listener for HTTP communication, providing a way to handle the
 * communication result.
 *
 * @author Oscar Cai
 */

public interface HttpListener {

    /**
     * Indicate the content type of response from HTTP server is unknown.
     */
    public static final int UNKNOWN = 0;

    /**
     * Indicate the content type of response from HTTP server is plain text, including JSON, HTML, etc.
     */
    public static final int TEXT = 1;

    /**
     * Indicate the content type of response from HTTP server is an image, including PNG, JPG, GIF, etc.
     */
    public static final int IMAGE = 2;

    /**
     * Notify the listener that the communication finish.
     *
     * @param task the HttpTask who invokes this callback.
     * @param success whether communicate successfully, true for success, false for failure.
     * @param response the response content from HTTP server.
     * @param responseType the content type of response.
     * @param errMsg failure description if success is false, null if success is true.
     */
    public abstract void onHttpResult(HttpTask task, boolean success, Object response, int responseType, String errMsg);
}
