package com.bestom.stresstest.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class ProperTiesUtils {

    /**
     *
     * @param activity
     * @param filename
     * @param key
     * @return
     */
    @TargetApi(Build.VERSION_CODES.N)
    public static String getProperties(Activity activity, String filename, String key ){
        Properties props = new Properties();
        InputStream in=null;
        String values=null;
        try {
            //方法一：通过activity中的context攻取setting.properties的FileInputStream
            //注意这地方的参数appConfig在eclipse中应该是appConfig.properties才对,但在studio中不用写后缀
            //InputStream in = c.getAssets().open("appConfig.properties");
//            InputStream in = c.getAssets().open("appConfig");
//            InputStream in = c.getAssets().open("appConfig");
            //方法二：通过class获取setting.properties的FileInputStream
//            InputStream in = PropertiesUtill.class.getResourceAsStream("/assets/  setting.properties "));
            //方法三：直接获取文件的properties的FileInputStream
            String filepath = activity.getDataDir().getAbsolutePath()+ File.separator+filename;
            in=new FileInputStream(filepath);

            props.load(in);
            values=props.getProperty(key);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        return values;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static String getProperties(Context context, String filename, String key ){
        Properties props = new Properties();
        InputStream in=null;
        String values=null;
        try {
            //方法一：通过activity中的context攻取setting.properties的FileInputStream
            //注意这地方的参数appConfig在eclipse中应该是appConfig.properties才对,但在studio中不用写后缀
            //InputStream in = c.getAssets().open("appConfig.properties");
//            InputStream in = c.getAssets().open("appConfig");
//            InputStream in = c.getAssets().open("appConfig");
            //方法二：通过class获取setting.properties的FileInputStream
//            InputStream in = PropertiesUtill.class.getResourceAsStream("/assets/  setting.properties "));
            //方法三：直接获取文件的properties的FileInputStream
            String filepath = context.getDataDir().getAbsolutePath()+ File.separator+filename;
            in=new FileInputStream(filepath);

            props.load(in);
            values=props.getProperty(key);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        return values;
    }

}
