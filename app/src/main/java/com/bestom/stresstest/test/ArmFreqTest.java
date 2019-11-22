package com.bestom.stresstest.test;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bestom.stresstest.test.service.ArmFreqTestService;

import java.util.ArrayList;

public class ArmFreqTest extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isWork = isSeviceWorked(this,"com.bestom.stresstest.test.service.ArmFreqTestService");
        if(!isWork){
//        	Intent i = new Intent("com.freqtestservice.start");
			Intent i = new Intent(this, ArmFreqTestService.class);
        	this.startService(i);
        }
        this.finish();
    }
    
    public static boolean isSeviceWorked(Context context, String serviceName) {
		ActivityManager myManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager
				.getRunningServices(30);
		for (int i = 0; i < runningService.size(); i++) {
			if (runningService.get(i).service.getClassName().toString().equals(serviceName)) {
				return true;
			}
		}
		return false;
	}
}