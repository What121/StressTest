package com.bestom.stresstest.base;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.bestom.stresstest.util.ProperTiesUtils;

import static com.bestom.stresstest.test.CameraTest.TAG;

public class TestItems {

	private static String[] testItems = {
		"RebootTest",
		"SleepTest",
		"CameraTest",
		"VideoTest",
		//"RunInTest",
		"WifiOpenTest",
		"BluetoothOpenTest",
		"FlyModeOpenTest",
		"AgingTestMain",
		"MicTest",
	//	"SmsSendTest",
	//	"CallNumTest",
//		"RecoveryTest",
	//	"BoardidSwitchTest",
//        "ArmFreqTest",
	};

	public static String[] getTestItems(Activity activity){
		String str = ProperTiesUtils.getProperties(activity,"MainTest_config","testItems");
		if (!TextUtils.isEmpty(str)){
			testItems=null;
			testItems=str.split(",");
			Log.i(TAG, "str:"+str+"\n"+"testItems.length"+testItems.length);
		}
		return  testItems;
	}


}
