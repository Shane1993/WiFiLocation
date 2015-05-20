package net.lee.wifilocation.net;

import android.content.Context;

import com.example.lenovo.wifilocation.MainActivity;
import com.example.lenovo.wifilocation.R;

import net.lee.wifilocation.config.Config;
import net.lee.wifilocation.model.AreaInfo;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.GetListener;

/**
 * Created by LEE on 2015/5/20.
 */
public class GetAreaMap {

    public GetAreaMap(final Context context, String areaName, final SuccessCallback successCallback, final FailCallback failCallback) {
        new NetConnection(context, Config.KEY_CLOUD_CODE_NAME, new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(String successResult) {

                try {
                    JSONObject jsonObject = new JSONObject(successResult);
                    int status = jsonObject.getInt(Config.KEY_STATUS);
                    switch (status)
                    {
                        case Config.RESULT_STATUS_SUCCESS:

                            String objectId = jsonObject.getString(Config.KEY_OBJECT_ID);

                            //获取ICON
                            BmobQuery<AreaInfo> query = new BmobQuery<AreaInfo>();
                            query.getObject(context, objectId, new GetListener<AreaInfo>() {
                                @Override
                                public void onSuccess(AreaInfo areaInfo) {
                                    //获取到BmobFile
                                    BmobFile map = areaInfo.getMap();
//                                  //获取Url
                                    String mapUrl = map.getFileUrl(context);
                                    System.out.println("GetAreaMap============url------>" + mapUrl);

                                    if(successCallback != null)
                                    {
                                        successCallback.onSuccess(mapUrl);
                                    }

                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    if(failCallback != null)
                                    {
                                        failCallback.onFail(context.getResources().getString(R.string.fail_to_get_information));
                                    }
                                }
                            });

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
                if (failCallback != null) {
                    failCallback.onFail(failResult);
                }
            }
        },
                Config.KEY_REQUEST_BODY_NAME, Config.ACTION_GET_OBJECT_ID,
                Config.KEY_REQUEST_BODU_AREA_NAME, areaName);
    }

    public static interface SuccessCallback {
        void onSuccess(String mapUrl);
    }

    public static interface FailCallback {
        void onFail(String failResult);
    }
}
