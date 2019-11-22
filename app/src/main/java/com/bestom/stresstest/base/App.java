package com.bestom.stresstest.base;

import android.annotation.TargetApi;
import android.app.Application;
import android.os.Build;
import android.util.Log;

import com.bestom.stresstest.service.WSClient;
import com.bestom.stresstest.util.FileUtils;

import java.io.File;

public class App extends Application {
    private static final String TAG = "App";

    public static StressBean mStressBean;
    public static WSClient wsClient;

    private String ConfigPath;
    private String configfilename = "MainTest_config";

//    static
//    {
//        System.loadLibrary("GPIO");
//        System.loadLibrary("stlport");
//    }


    @Override
    public void onCreate() {

        Config();

        //初始化测试结果数据类
        mStressBean=new StressBean();

        //初始化client，上传数据到服务器
        wsClient =  WSClient.getInstance();
        //建立连接
        wsClient.connect();
//        wsClient.send();

        super.onCreate();
    }

    @TargetApi(Build.VERSION_CODES.N)
    private void Config(){
        ConfigPath=this.getDataDir().getAbsolutePath()+ File.separator+configfilename;
        File configFile = new File(ConfigPath);
        if(!configFile.exists()){
            //初始化测试视频文件路径
            initConfig();
        }else {
            Log.d(TAG, "Config: file exit !!!");
        }
    }

    private void initConfig(){
        boolean copyResult = FileUtils.copyFromAssetToData(this, configfilename, true);
        if(copyResult){
            // chmod 777 configfile
            FileUtils.chmodDataFilePath(this, ConfigPath);
//            initViewdefault();
        }else{
            Log.e(TAG, "initConfig: 初始化copyFromAsset()失败!! 完成默认初始化赋值" );
        }
    }


}
