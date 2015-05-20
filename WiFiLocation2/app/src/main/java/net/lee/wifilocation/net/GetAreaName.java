package net.lee.wifilocation.net;

import android.content.Context;

import com.example.lenovo.wifilocation.R;

import net.lee.wifilocation.config.Config;

import org.json.JSONObject;

/**
 * Created by lenovo on 2015/4/30.
 * The commit params :
 * name : request.body.name
 *
 * The results return by server:
 * status : status of communication
 * areaName : all of the areaName
 */
public class GetAreaName {

    public GetAreaName(final Context context,final SuccessCallback successCallback, final FailCallback failCallback) {

        new NetConnection(context, Config.KEY_CLOUD_CODE_NAME, new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(String successResult) {

                //Get the information from the result
                try
                {
                    JSONObject jsonObject = new JSONObject(successResult);
                    int status = jsonObject.getInt(Config.KEY_STATUS);
                    switch (status)
                    {
                        case Config.RESULT_STATUS_SUCCESS:

                            String allAreaName = jsonObject.getString(Config.KEY_AREA_NAME);
                            if(successCallback != null)
                            {
                                successCallback.onSuccess(allAreaName);
                            }
                            break;
                        case Config.RESULT_STATUS_FAIL:
                            if(failCallback != null)
                            {
                                failCallback.onFail(context.getResources().getString(R.string.fail_to_get_information));
                            }
                            break;
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
                if(failCallback != null)
                {
                    failCallback.onFail(context.getResources().getString(R.string.fail_to_get_information) + " : " + failResult);
                }
            }
        },
        Config.KEY_REQUEST_BODY_NAME,Config.ACTION_GET_AREA_NAME
        );


    }

    public static interface SuccessCallback {
        void onSuccess(String allAreaName);
    }

    public static interface FailCallback {
        void onFail(String failResult);
    }

}
