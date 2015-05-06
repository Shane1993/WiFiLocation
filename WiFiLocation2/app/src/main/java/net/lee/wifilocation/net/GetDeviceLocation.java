package net.lee.wifilocation.net;

import android.content.Context;

import com.example.lenovo.wifilocation.R;

import net.lee.wifilocation.config.Config;
import net.lee.wifilocation.model.DeviceLocation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2015/5/5.
 */
public class GetDeviceLocation {

    public GetDeviceLocation(final Context context, String deviceName, int infoNumber, final SuccessCallback successCallback, final FailCallback failCallback) {
        new NetConnection(context, Config.KEY_CLOUD_CODE_NAME, new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(String successResult) {

                try {
                    JSONObject jsonObject = new JSONObject(successResult);
                    int status = jsonObject.getInt(Config.KEY_STATUS);

                    switch (status) {
                        case Config.RESULT_STATUS_SUCCESS:

                            JSONArray jsonArray = jsonObject.getJSONArray(Config.KEY_DEVICE_LOCATION);
                            List<DeviceLocation> list = new ArrayList<DeviceLocation>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                DeviceLocation deviceLocation = new DeviceLocation();
                                deviceLocation.setAreaName(jsonArray.getJSONObject(i).getString(Config.KEY_AREA_NAME));
                                deviceLocation.setLocationName(jsonArray.getJSONObject(i).getString(Config.KEY_LOCATION_NAME));
                                deviceLocation.setTime(jsonArray.getJSONObject(i).getString(Config.KEY_TIME));
                                list.add(deviceLocation);
                            }

                            if (successCallback != null)
                            {
                                successCallback.onSuccess(true,list);
                            }
                            break;
                        case Config.RESULT_STATUS_FAIL:
                            if (successCallback != null) {
                                successCallback.onSuccess(false, null);
                            }
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (failCallback != null) {
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
                Config.KEY_REQUEST_BODY_NAME, Config.ACTION_GET_DEVICE_LOCATION,
                Config.KEY_REQUEST_BODY_USERNAME, deviceName,
                Config.KEY_REQUEST_BODY_INFONUMBER, infoNumber + "");
    }

    public static interface SuccessCallback {
        void onSuccess(boolean haveData, List<DeviceLocation> list);
    }

    public static interface FailCallback {
        void onFail(String failResult);
    }
}
