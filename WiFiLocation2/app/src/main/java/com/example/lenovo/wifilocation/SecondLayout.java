package com.example.lenovo.wifilocation;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import net.lee.wifilocation.adapter.MyAdapter;
import net.lee.wifilocation.config.Config;
import net.lee.wifilocation.net.GetAreaMapUrl;
import net.lee.wifilocation.net.GetAreaName;
import net.lee.wifilocation.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2015/4/18.
 */
public class SecondLayout extends LinearLayout implements View.OnClickListener, AdapterView.OnItemClickListener {

    public SecondLayout(Context context) {
        super(context);
    }

    public SecondLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private Button measureBtn;
    private Button createBtn;
    private ListView locationListView;
    private List<String> locationList;
    private MyAdapter adapter;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        measureBtn = (Button) findViewById(R.id.measureBtn);
        createBtn = (Button) findViewById(R.id.createLocationBtn);
        locationListView = (ListView) findViewById(R.id.locationLV);

        locationList = new ArrayList<String>();
        adapter = new MyAdapter(getContext(), locationList);
        locationListView.setAdapter(adapter);

        measureBtn.setOnClickListener(this);
        createBtn.setOnClickListener(this);
        locationListView.setOnItemClickListener(this);

        new GetAreaName(getContext(), new GetAreaName.SuccessCallback() {

            @Override
            public void onSuccess(String allAreaName) {

                Config.valueAllAreaName = allAreaName;
                refreshListView();

                progressDialog = new ProgressDialog(getContext());
                progressDialog.show();

                Thread downloadThread = new Thread(new Runnable() {

                    boolean downloadOver = false;
                    boolean downloaFail = false;

                    @Override
                    public void run() {
                        //After getting the areaName from server, the next job is to download the map of every area.
                        for (final String areaName : Config.valueAllAreaName.split(",")) {

                            downloadOver = false;

                            new GetAreaMapUrl(getContext(), areaName, new GetAreaMapUrl.SuccessCallback() {
                                @Override
                                public void onSuccess(final String mapUrl) {

                                    //Remenber open a new thread to download file
                                    Runnable runnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            FileUtils fileUtils = new FileUtils();
                                            System.out.println(fileUtils.downFile(mapUrl, "Wifilocation", areaName + ".jpg"));

                                            downloadOver = true;

                                            return;

                                        }
                                    };
                                    new Thread(runnable).start();
                                }
                            }, new GetAreaMapUrl.FailCallback() {
                                @Override
                                public void onFail(String failResult) {
                                    Toast.makeText(getContext(), failResult, Toast.LENGTH_SHORT).show();
                                    downloadOver = true;
                                    downloaFail = true;
                                }
                            });

                            System.out.println("SecondLayout==============This is before while");
                            while (!downloadOver) ;
                            System.out.println("SecondLayout==============This is after while");

                            if (downloaFail) {
                                break;
                            }

                        }
                        dialogHandler.sendEmptyMessage(DIALOG_CANCEL);
                    }
                });

                downloadThread.start();

            }
        }, new GetAreaName.FailCallback() {
            @Override
            public void onFail(String failResult) {
                Toast.makeText(getContext(), failResult, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private final int DIALOG_CANCEL = 1;
    private ProgressDialog progressDialog;
    private Handler dialogHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == DIALOG_CANCEL) {
                progressDialog.dismiss();
            }
        }
    };

    private void refreshListView() {
        String str = Config.valueAllAreaName;
        if (str != null) {
            String[] areaNameArray = str.split(",");
            for (String s : areaNameArray) {
                locationList.add(s);
            }
            adapter.notifyDataSetChanged();

        }
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == View.VISIBLE) {
            if (Config.valueAreaChanged) {
                locationList.clear();
                refreshListView();
                Config.valueAreaChanged = false;
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.measureBtn) {
            if (Config.valueManageSelectedAreaName == null) {
                Toast.makeText(getContext(), "请先选择当前区域", Toast.LENGTH_SHORT).show();
            } else {
                if (Config.getCacheManagePassword(getContext()) == null) {

                    final View view = LayoutInflater.from(getContext()).inflate(R.layout.alerdialog, null);
                    final EditText passwordEt = (EditText) view.findViewById(R.id.alertEt);
                    passwordEt.setTransformationMethod(PasswordTransformationMethod.getInstance());

                    //Create a AlertDialog
                    final AlertDialog ad = new AlertDialog.Builder(getContext()).create();
//            ad.setCanceledOnTouchOutside(false);
                    ad.setTitle("请输入密码");
                    ad.setView(view);
                    ad.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (TextUtils.isEmpty(passwordEt.getText().toString())) {
                                Toast.makeText(getContext(), "请输入密码", Toast.LENGTH_SHORT).show();
                            } else {
                                final String managePassword = passwordEt.getText().toString();
                                if (managePassword.equals(Config.VALUE_MANAGE_PASSWORD)) {
                                    Config.cacheManagePassword(getContext(), managePassword);
                                    Intent intent = new Intent(getContext(), MeasureActivity.class);
                                    intent.putExtra(Config.KEY_AREA_NAME, Config.valueManageSelectedAreaName);
                                    getContext().startActivity(intent);
                                }

                                ad.dismiss();

                            }
                        }
                    });
                    ad.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ad.dismiss();
                        }
                    });
                    ad.show();

                } else {
                    Intent intent = new Intent(getContext(), MeasureActivity.class);
                    intent.putExtra(Config.KEY_AREA_NAME, Config.valueManageSelectedAreaName);
                    getContext().startActivity(intent);
                }

            }
        } else if (v.getId() == R.id.createLocationBtn) {
            if (Config.getCacheManagePassword(getContext()) == null) {

                final View view = LayoutInflater.from(getContext()).inflate(R.layout.alerdialog, null);
                final EditText passwordEt = (EditText) view.findViewById(R.id.alertEt);
                passwordEt.setTransformationMethod(PasswordTransformationMethod.getInstance());

                //Create a AlertDialog
                final AlertDialog ad = new AlertDialog.Builder(getContext()).create();
//            ad.setCanceledOnTouchOutside(false);
                ad.setTitle("请输入密码");
                ad.setView(view);
                ad.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (TextUtils.isEmpty(passwordEt.getText().toString())) {
                            Toast.makeText(getContext(), "请输入密码", Toast.LENGTH_SHORT).show();
                        } else {
                            final String managePassword = passwordEt.getText().toString();
                            if (managePassword.equals(Config.VALUE_MANAGE_PASSWORD)) {
                                Config.cacheManagePassword(getContext(), managePassword);
                                ad.dismiss();
                                openCreateAreaAty();
                            } else {
                                ad.dismiss();
                            }

                        }
                    }
                });
                ad.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ad.dismiss();
                    }
                });
                ad.show();

            } else {
                openCreateAreaAty();
            }
        }
    }

    private void openCreateAreaAty() {
        Intent intent = new Intent(getContext(), CreateAreaAty.class);
        getContext().startActivity(intent);

//        final View view = LayoutInflater.from(getContext()).inflate(R.layout.alerdialog, null);
//        final EditText alertEt = (EditText) view.findViewById(R.id.alertEt);
//        //Create a AlertDialog
//        final AlertDialog ad = new AlertDialog.Builder(getContext()).create();
////            ad.setCanceledOnTouchOutside(false);
//        ad.setTitle("添加新区域");
//        ad.setView(view);
//        ad.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (TextUtils.isEmpty(alertEt.getText().toString())) {
//                    Toast.makeText(getContext(), "请输入区域名称", Toast.LENGTH_SHORT).show();
//                } else {
//                    final String name = alertEt.getText().toString();
//
//                    //Send the new area name to the server
//                    AreaInfo areaInfo = new AreaInfo(name);
//                    areaInfo.save(getContext(), new SaveListener() {
//                        @Override
//                        public void onSuccess() {
//
//                            locationList.add(name);
//                            //Tell the FirstLayout's spinner that area have change
//                            Config.valueAreaChanged = true;
//                            adapter.notifyDataSetChanged();
//                        }
//
//                        @Override
//                        public void onFailure(int i, String s) {
//
//                            Toast.makeText(getContext(), "创建区域失败：" + s, Toast.LENGTH_SHORT).show();
//                        }
//                    });
//
//                    ad.dismiss();
//                }
//            }
//        });
//        ad.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                ad.dismiss();
//            }
//        });
//        ad.show();
    }

    /**
     * This is short click on items
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Config.valueManageSelectedAreaName = locationList.get(position);

        Log.i("onItemClick", "Config.valueManageSelectedAreaName:" + Config.valueManageSelectedAreaName);
        //Tell the ListView I have changed! Refresh the list quickly.
        adapter.notifyDataSetChanged();
    }

}
