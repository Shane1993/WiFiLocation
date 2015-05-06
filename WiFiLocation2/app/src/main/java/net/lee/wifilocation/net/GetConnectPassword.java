package net.lee.wifilocation.net;

import android.content.Context;

import com.example.lenovo.wifilocation.R;

import net.lee.wifilocation.config.Config;

import org.json.JSONObject;

/**
 * Created by lenovo on 2015/5/4.
 */
public class GetConnectPassword {

    public GetConnectPassword(final Context context, String userName, final SuccessCallback successCallback, final FailCallback failCallback) {
        new NetConnection(context, Config.KEY_CLOUD_CODE_NAME, new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(String successResult) {

                try
                {
                    JSONObject jsonObject = new JSONObject(successResult);
                    int status = jsonObject.getInt(Config.KEY_STATUS);

                    switch (status)
                    {
                        case Config.RESULT_STATUS_SUCCESS:

                            String connectPassword = jsonObject.getString(Config.KEY_REQUEST_BODY_CONNECTPASSWORD);
                            if(successCallback != null)
                            {
                                successCallback.onSuccess(connectPassword);
                            }
                            break;
                        case Config.RESULT_STATUS_FAIL:
                            if(successCallback != null)
                            {
                                successCallback.onSuccess("нч");
                            }
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                    if(failCallback != null)
                    {
                        failCallback.onFail(context.getResources().getString(R.string.fail_to_get_information));
                    }
                }

            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(String failResult) {
                if (failCallback != null) {
                    failCallback.onFail(context.getResources().getString(R.string.fail_to_get_information) + " : " + failResult);
                }
            }
        },
                Config.KEY_REQUEST_BODY_NAME, Config.ACTION_GET_CONNECT_PASSWORD,
                Config.KEY_REQUEST_BODY_USERNAME, userName);
    }

    public static interface SuccessCallback {
        void onSuccess(String connectPassword);
    }

    public static interface FailCallback {
        void onFail(String failResult);
    }
}
