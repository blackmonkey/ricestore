
package framework.task;

import java.util.Hashtable;
import java.util.Vector;

import main.config.Config;
import main.config.StringResource;
import framework.json.me.JSONArray;
import framework.json.me.JSONException;
import framework.json.me.JSONObject;
import framework.net.HttpListener;
import framework.net.HttpTask;
import framework.util.TextUtil;

/**
 * Get applications list of specified type.
 *
 * @author Oscar Cai
 */

public class GetAppListTask extends StoreTask {

    /**
     * Means the request for the application list cannot be handled by the
     * server.
     */
    private static final int FAIL = 0;

    /**
     * Means the request for the application list is handled by the server
     * successfully.
     */
    private static final int SUCCESS = 1;

    private Vector apps;

    private int page;

    private int totalPages;

    public GetAppListTask(int page, TaskListener listener) {
        super(listener);
        this.page = page;
        totalPages = 0;
        apps = null;
    }

    public void execute() {
        String url = Config.getSoftListUrl();
        Hashtable params;
        if (TextUtil.isEmpty(Config.getClientId())) {
            params = Config.getCompanyBatchParams();
        } else {
            params = new Hashtable();
            params.put("userid", Config.getClientId());
        }
        params.put("page", new Integer(page));
        params.put("order", "down"); // set sort order by total download times

        new HttpTask(url, params, (HttpListener) this).start();
        notifyProgress(false, false, StringResource.START_GET_APP_LIST);
    }

    public void onHttpResult(HttpTask task, boolean success, Object response, int responseType, String errMsg) {
        if (success) {
            boolean allAppsParsed = false;
            if (responseType == HttpListener.TEXT && !TextUtil.isEmpty((String)response)) {
                try {
                    JSONObject jsonObj = new JSONObject((String)response);
                    int result = jsonObj.getInt("result");
                    if (result == SUCCESS) {
                        String downloadBase = jsonObj.getString("downloadBase");
                        totalPages = jsonObj.getInt("pages");

                        JSONArray appAry = null;
                        try {
                            appAry = jsonObj.getJSONArray("apps");
                        } catch (JSONException e) {
                            /* In this case, the value of 'apps' is a JSONArray expressed string. */
                            String appAryStr = jsonObj.getString("apps");
                            appAry = new JSONArray(appAryStr);
                        }

                        apps = new Vector();
                        for (int i = 0; i < appAry.length(); i++) {
                            apps.addElement(new AppInfo(downloadBase, appAry.getJSONObject(i)));
                        }

                        allAppsParsed = true;
                    } else if (result == FAIL) {
                        // the application list is not updated, do nothing.
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (allAppsParsed) {
                notifyProgress(true, true, StringResource.GET_TYPE_APP_SUCCESS);
            } else {
                notifyProgress(true, false, StringResource.GET_TYPE_APP_FAIL);
            }
        } else {
            notifyProgress(true, false, StringResource.GET_TYPE_APP_FAIL);
        }
    }

    public int getTotalPages() {
        return totalPages;
    }

    public Vector getApps() {
        return apps;
    }
}
