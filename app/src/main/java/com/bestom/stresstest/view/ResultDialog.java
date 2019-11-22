package com.bestom.stresstest.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bestom.stresstest.R;
import com.bestom.stresstest.base.App;
import com.bestom.stresstest.base.RequestBean;
import com.bestom.stresstest.base.ResponseBean;
import com.bestom.stresstest.service.clientmsg;
import com.bestom.stresstest.util.AppUtil;
import com.bestom.stresstest.util.TimeUtil;
import com.bestom.stresstest.util.UUIDUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;


public class ResultDialog extends AlertDialog {

    private static ResultDialog resultDialog;
    private Context mContext;
    private Activity mActivity;
//    private AVLoadingIndicatorView avi;

    private String title="";
    private int maxtimes;
    private int curtimes;


    Button close_bt;
    TextView tv_title,tv_content,tv_result;

    private static final String TAG = "ResultDialog";

    public static ResultDialog getInstance(Context context, Activity activity) {
        if (null == resultDialog) {
            resultDialog = new ResultDialog(context, R.style.ResultDialog,activity); //设置AlertDialog背景透明
            resultDialog.setCancelable(false);
            resultDialog.setCanceledOnTouchOutside(false);
        }
        return resultDialog;
    }

    public ResultDialog setTitle(String title){
        this.title=title;
        return resultDialog;
    }

    public ResultDialog setMaxtimes(int maxtimes){
        this.maxtimes=maxtimes;
        return resultDialog;
    }

    public ResultDialog setCurtimes(int curtimes){
        this.curtimes=curtimes;
        return resultDialog;
    }

    private ResultDialog(Context context, int themeResId, Activity activity) {
        super(context,themeResId);
        mContext=context;
        mActivity=activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void initview(){
        tv_title=resultDialog.findViewById(R.id.tv_title);
        tv_content=resultDialog.findViewById(R.id.tv_content);
        tv_result=resultDialog.findViewById(R.id.tv_result);

        close_bt=resultDialog.findViewById(R.id.close);
        close_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        this.setContentView(R.layout.dialog_result);

//        avi =  this.findViewById(R.id.avi);

        initview();

    }

    @Override
    public void show() {
        if (AppUtil.hasNavBar(mContext)) {//隐藏底部导航栏
            AppUtil.hideBottomUIMenu(mActivity);
        }

        //initview data
        tv_title.setText(title);
        tv_content.setText("maxtimes:"+maxtimes+"       curtimes:"+curtimes);

        if (!TextUtils.isEmpty(title)){
            super.show();
//            Toast.makeText(mContext,"please setrequest to save resultdata",Toast.LENGTH_SHORT).show();
//            this.dismiss();

            tv_title.append(" 结果...");

            //添加本次测试的时间
            App.mStressBean.setTime(TimeUtil.getCurDate());
            Gson gson=new Gson();
            String msg=gson.toJson(App.mStressBean);
            final RequestBean requestBean=new RequestBean(UUIDUtil.getUUID(),msg);

//            Log.i(TAG, "show: requestbody"+new Gson().toJson(requestBean));

            //region net 上传数据
            /*
            HttpUtil.doPostBody("https://www.baidu.com/", "", new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
//                    Toast.makeText(mContext,"update fail "+e.getMessage(),Toast.LENGTH_SHORT).show();
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext,"update onFailure "+e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
//                    tv_result.setText("update fail "+e.getMessage());

                    mActivity.getHandler().sendEmptyMessageDelayed(30,1000);
                    Log.i(TAG, "update onFailure "+e.getMessage());
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
//                    Toast.makeText(mContext,"update success "+response.toString(),Toast.LENGTH_SHORT).show();
//                    tv_result.setText("update success "+response.toString());

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext,"update success "+response.toString(),Toast.LENGTH_LONG).show();
                        }
                    });
                    mActivity.getHandler().sendEmptyMessageDelayed(30,1000);
                    Log.i(TAG, "update success "+response.toString());
                }
            });
            */
            //endregion

            //region websocket client sendmsg
            if (App.wsClient.getReadyState()!= ReadyState.OPEN){
                tv_result.setText("与服务器连接不通，尝试重连...");

                App.wsClient.close();
                App.wsClient.notifaction(new clientmsg() {
                    @Override
                    public void onOpen(ServerHandshake serverHandshake) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_result.setText("与服务器连接成功，正在上传数据...");

                                dorequest(requestBean);
//                        Toast.makeText(mContext,"update success "+msg,Toast.LENGTH_LONG).show();
//                                mActivity.getHandler().sendEmptyMessageDelayed(50,3000);
                            }
                        });
                    }
                    //region onMsg
                    @Override
                    public void onMsg(String s) {

                    }
                    //endregion

                    @Override
                    public void onClose(int i, final String s, boolean b) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_result.setText("与服务器连接失败，请检查网络..."+s);
//                        Toast.makeText(mContext,"update success "+msg,Toast.LENGTH_LONG).show();
//                                mActivity.getHandler().sendEmptyMessageDelayed(30,3000);
                            }
                        });
                    }
                    //region onError
                    @Override
                    public void onError(Exception e) {

                    }
                    //endregion
                }).reconnect();
            }else {
                dorequest(requestBean);
            }

            //endregion

        }
        else {
            super.show();
//        avi.show();
            tv_result.setText("title 为空");
        }

    }

    private void dorequest(final RequestBean requestBean){
        final Gson gson=new Gson();
        String requestbody = gson.toJson(requestBean );
        Log.i(TAG, "show: bodyjson:"+requestbody);

        App.wsClient.notifaction(new clientmsg() {
            //region onOpen
            @Override
            public void onOpen(ServerHandshake serverHandshake) {

            }
            //endregion

            @Override
            public void onMsg(final String s) {
                try {
                    Gson rgson=new Gson();
                    ResponseBean responseBean = rgson.fromJson(s,ResponseBean.class);
                    final String msg = responseBean.getMsg();
                    if (responseBean.getId().equals(requestBean.getId())){
                        //进行id校对，防止消息队列 混乱
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_title.setText("response:"+msg);
                                Toast.makeText(mContext,"response: "+msg, Toast.LENGTH_LONG).show();
                            }
                        });

                        Log.i(TAG, "response: "+msg);
//                        mActivity.getHandler().sendEmptyMessageDelayed(30,2000);

                    }else {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_title.setText("response error:"+msg);
                                Toast.makeText(mContext,"response msg error "+msg, Toast.LENGTH_LONG).show();
                            }
                        });
//
                        Log.i(TAG, "response error "+msg);
//                        mActivity.getHandler().sendEmptyMessageDelayed(50,2000);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    mActivity.runOnUiThread(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            tv_title.setText("response 格式异常:"+s);
                            Toast.makeText(mContext,"response msg 格式异常 ", Toast.LENGTH_LONG).show();
                        }
                    });
//
                    Log.i(TAG, "response 格式异常: ");
//                    mActivity.getHandler().sendEmptyMessageDelayed(50,2000);
                }

            }

            @Override
            public void onClose(int i, final String s, boolean b) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_title.setText("update onclose:"+s);
                        Toast.makeText(mContext,"update onclose:"+s, Toast.LENGTH_LONG).show();
                    }
                });
//
                Log.i(TAG, "update onclose:"+s);
//                mActivity.getHandler().sendEmptyMessageDelayed(50,2000);
            }

            @Override
            public void onError(final Exception e) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_title.setText("upload date error:"+e.getMessage());
                        Toast.makeText(mContext,"upload date error:"+e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
//
                Log.i(TAG, "upload date error:"+e.getMessage());
//                mActivity.getHandler().sendEmptyMessageDelayed(50,2000);

            }
        }).send(requestbody);
    }

    @Override
    public void dismiss() {

        super.dismiss();
//        mActivity.getHandler().sendEmptyMessageDelayed(40,1000);
//        avi.hide();


    }

}
