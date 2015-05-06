package net.lee.wifilocation.net;

import android.content.Context;

import com.example.lenovo.wifilocation.R;

import net.lee.wifilocation.config.Config;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lenovo on 2015/5/4.
 */
public class ResetConnectPassword {

    public ResetConnectPassword(final Context context, String userName, String password, String newPassword, final SuccessCallback successCallback, final FailCallback failCallback)
    {
        new NetConnection(context, Config.KEY_CLOUD_CODE_NAME, new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(String successResult) {

                try {
                    JSONObject jsonObject = new JSONObject(successResult);
                    int status  = jsonObject.getInt(Config.KEY_STATUS);

                    if(successCallback != null)
                    {
                        successCallback.onSuccess(status);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    if(failCallback != null)
                    {
                        failCallback.onFail(context.getResources().getString(R.string.fail_to_reset_password));
                    }
                }

            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(String failResult) {

                if(failCallback != null)
                {
                    failCallback.onFail(context.getResources().getString(R.string.fail_to_reset_password) + " : " + failResult);
                }
            }
        },
                Config.KEY_REQUEST_BODY_NAME,Config.ACTION_RESET_Connect_PASSWORD,
                Config.KEY_REQUEST_BODY_USERNAME,userName,
                Config.KEY_REQUEST_BODY_PASSWORD,password,
                Config.KEY_REQUEST_BODY_NEWCONNECTPASSWORD,newPassword);

    }


    public static interface SuccessCallback
    {
        void onSuccess(int status);
    }
    public static interface FailCallback
    {
        void onFail(String failResult);
    }
}
