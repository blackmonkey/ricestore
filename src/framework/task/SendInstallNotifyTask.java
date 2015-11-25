
package framework.task;

import java.util.Hashtable;

import main.config.Config;
import main.config.StringResource;
import framework.json.me.JSONException;
import framework.json.me.JSONObject;
import framework.net.HttpListener;
import framework.net.HttpTask;
import framework.util.TextUtil;

/**
 * Send Rice App Store application installation notification.
 *
 * @author Oscar Cai
 */

public class SendInstallNotifyTask extends StoreTask {

    private String appId;

    public SendInstallNotifyTask(String appId, TaskListener listener) {
        super(listener);
        this.appId = appId;
    }

    public void execute() {
        String url = Config.getNotifyUrl();
        Hashtable params;
        if (TextUtil.isEmpty(Config.getClientId())) {
            params = Config.getCompanyBatchParams();
        } else {
            params = new Hashtable();
            params.put("userid", Config.getClientId());
        }
        params.put("id", appId);

        new HttpTask(url, params, (HttpListener) this).start();
        notifyProgress(false, false, StringResource.START_SEND_INSTALL_NOTIFY);
    }

    public void onHttpResult(HttpTask task, boolean success, Object response, int responseType, String errMsg) {
        if (success) {
            boolean sent = false;
            if (responseType == HttpListener.TEXT && !TextUtil.isEmpty((String)response)) {
                try {
                    JSONObject jsonObj = new JSONObject((String)response);
                    int result = jsonObj.getInt("result");
                    sent = (result == 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            notifyProgress(true, sent, StringResource.SEND_INSTALL_NOTIFY_SUCCESS);
        } else {
            notifyProgress(true, false, StringResource.SEND_INSTALL_NOTIFY_FAIL);
        }
    }

}
