package net.lee.wifilocation.net;

import android.content.Context;

import com.example.lenovo.wifilocation.R;

import net.lee.wifilocation.config.Config;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lenovo on 2015/4/30.
 * The commit params :
 * token
 *
 * The results return by server:
 * status : status of token
 *
 */
public class VerifyToken {

    public VerifyToken(final Context context,String token, final SuccessCallback successCallback, final FailCallback failCallback)
    {
        new NetConnection(context, Config.KEY_CLOUD_CODE_NAME, new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(String successResult) {

                try {
                    JSONObject jsonObject = new JSONObject(successResult.toString());
                    int status = jsonObject.getInt(Config.KEY_STATUS);
                    switch (status)
                    {
                        case Config.RESULT_STATUS_SUCCESS :
                            if(successCallback != null)
                            {
                                successCallback.onSuccess();
                            }
                            break;
                        case Config.RESULT_INVALID_TOKEN :
                            if(failCallback != null)
                            {
                                failCallback.onFail(context.getResources().getString(R.string.out_of_date_token));
                            }
                    }


                } catch (JSONException e) {
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
                if(failCallback != null)
                {
                    failCallback.onFail(context.getResources().getString(R.string.fail_to_get_information) + " : " + failResult);
                }
            }
        },
                Config.KEY_REQUEST_BODY_NAME,Config.ACTION_VERIFY_TOKEN,
                Config.KEY_REQUEST_BODY_TOKEN, token);
    }

    public static interface SuccessCallback
    {
        void onSuccess();
    }
    public static interface FailCallback
    {
        void onFail(String failResult);
    }
}
