package net.lee.wifilocation.net;

import android.app.ProgressDialog;
import android.content.Context;

import net.lee.wifilocation.config.Config;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lenovo on 2015/4/30.
 * The commit params :
 * userName
 * password
 *
 * The results return by server:
 * status : status of communication
 * token
 */
public class Login {

    public Login(Context context, String userName, String password, final SuccessCallback successCallback, final FailCallback failCallback)
    {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();

        new NetConnection(context, Config.KEY_CLOUD_CODE_NAME, new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(String successResult) {

                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(successResult);
                    int status  = jsonObject.getInt(Config.KEY_STATUS);
                    switch (status)
                    {
                        case Config.RESULT_STATUS_SUCCESS :

                            String token = jsonObject.getString(Config.KEY_TOKEN);
                            if(successCallback != null)
                            {
                                successCallback.onSuccess(token);
                            }

                            break;
                        case Config.RESULT_STATUS_FAIL:
                            if(failCallback != null)
                            {
                                failCallback.onFail("ÓÃ»§Ãû»òÕßÃÜÂë´íÎó");
                            }
                            break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    if(failCallback != null)
                    {
                        failCallback.onFail("µÇÂ¼Ê§°Ü");
                    }
                }

            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(String failResult) {
                progressDialog.dismiss();

                if(failCallback != null)
                {
                    failCallback.onFail("µÇÂ¼Ê§°Ü : " + failResult);
                }
            }
        },
                Config.KEY_REQUEST_BODY_NAME,Config.ACTION_LOGIN,
                Config.KEY_REQUEST_BODY_USERNAME,userName,
                Config.KEY_REQUEST_BODY_PASSWORD,password);

    }


    public static interface SuccessCallback
    {
        void onSuccess(String token);
    }
    public static interface FailCallback
    {
        void onFail(String failResult);
    }
}
