package net.lee.wifilocation.net;

import android.content.Context;

import com.example.lenovo.wifilocation.R;

import net.lee.wifilocation.config.Config;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lenovo on 2015/4/30.
 * The commit params :
 * name : request.body.name
 * information : request.body.information
 *
 * The results return by server:
 * status : status of communication
 * locationName
 */
public class GetMyLocation {

    public GetMyLocation(final Context context, String locationInfoToJSONString, final SuccessCallback successCallback, final FailCallback failCallback)
    {
        new NetConnection(context, Config.KEY_CLOUD_CODE_NAME, new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(String successResult) {

                try {
                    System.out.println(successResult);
                    JSONObject jsonObject = new JSONObject(successResult);
                    int status  = jsonObject.getInt(Config.KEY_STATUS);
                    switch (status)
                    {
                        case Config.RESULT_STATUS_SUCCESS :

                            int locationStatus = jsonObject.getInt(Config.KEY_LOCATION_STATUS);
                            String areaName = jsonObject.getString(Config.KEY_AREA_NAME);
                            String locationName = jsonObject.getString(Config.KEY_LOCATION_NAME);

                            if (successCallback != null)
                            {
                                successCallback.onSuccess(locationStatus,areaName,locationName);
                            }
                            break;
                        case Config.RESULT_STATUS_FAIL:
                            if(failCallback != null)
                            {
                                failCallback.onFail(context.getResources().getString(R.string.fail_to_get_information));
                            }
                            break;
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
                Config.KEY_REQUEST_BODY_NAME,Config.ACTION_GET_MY_LOCATION,
                Config.KEY_REQUEST_BODY_INFORMATION,locationInfoToJSONString);
    }

    public static interface SuccessCallback
    {
        void onSuccess(int locationStatus,String areaName, String locationName);
    }
    public static interface FailCallback
    {
        void onFail(String failResult);
    }
}
