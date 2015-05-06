package net.lee.wifilocation.net;

import android.content.Context;
import android.widget.Toast;

import net.lee.wifilocation.config.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.listener.CloudCodeListener;

/**
 * Created by lenovo on 2015/4/30.
 * This class is the base used to communication with server
 */
public class NetConnection {

    /**
     * This class is the base used to communication with Bmob server
     * @param context
     * @param cloudfunction : the name of the function in the cloudCode which should be invoke
     * @param successCallback
     * @param failCallback
     * @param kvs
     */
    public NetConnection(final Context context, String cloudfunction ,final SuccessCallback successCallback, final FailCallback failCallback, String... kvs) {

        // cloudCodeName对应你刚刚创建的云端代码名称
        String cloudCodeName = cloudfunction;
        JSONObject params = new JSONObject();
        try {

            for(int i = 0; i< kvs.length; i += 2)
            {
                params.put(kvs[i],kvs[i+1]);
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 创建云端代码对象
        AsyncCustomEndpoints cloudCode = new AsyncCustomEndpoints();
        // 异步调用云端代码
        cloudCode.callEndpoint(context, cloudCodeName, params,
                new CloudCodeListener() {

                    @Override
                    public void onSuccess(Object result) {
                        // TODO Auto-generated method stub

                        System.out.println("NetConnection's onSuccess");

                        if(successCallback != null)
                        {
                            successCallback.onSuccess(result.toString());
                        }
                    }

                    @Override
                    public void onFailure(int i, String s) {

                        if(failCallback != null)
                        {
                            failCallback.onFail(s);
                        }
                    }

                });


    }


    public static interface SuccessCallback {
        void onSuccess(String successResult);
    }

    public static interface FailCallback {
        void onFail(String failResult);
    }

}


