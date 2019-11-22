/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Description:   
* @author:     fxw@rock-chips.com
* Create at:   2014年5月14日 下午5:42:07  
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2014年5月14日      fxw         1.0         create
*******************************************************************/   

package com.bestom.stresstest.test.aging;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.bestom.stresstest.R;
import com.bestom.stresstest.test.aging.base.AgingCallback;
import com.bestom.stresstest.test.aging.base.BaseAgingTest;
import com.bestom.stresstest.test.common.AgingType;
import com.bestom.stresstest.util.FileUtils;
import com.bestom.stresstest.util.LogUtil;
import com.bestom.stresstest.util.ProperTiesUtils;
import com.bestom.stresstest.util.SystemBinUtils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MemoryTest extends BaseAgingTest implements SystemBinUtils.CommandResponseListener {
	private static final String TAG = "MemoryTest";
	public static final String MEM_TEST_BIN = "stressapptest";
	public static final int MSG_UPDATE_DETAIL = 1;
	public static final int MSG_LOOP = 2;
	private Activity mActivity;
	private ViewGroup mParent;
	private TextView mMemDetailText;
	private TextView mMemCountText;
	private boolean isRunning;
	private MemoryHandler mMemoryHandler;
	private StringBuilder mDetailContent;
	private int mTestCount;
	private int MemorySize;
	private String MemoryTime;
	private int ThreadNum;
	private int CPUCores;
	private boolean loopflag;
	private String configfilename="aging_config";
	private String AgingConfigPath;

	public MemoryTest( AgingCallback agingCallback){
		super( agingCallback);
	}
	
	@Override
	public void onCreate(Activity activity) {
		mActivity = activity;
		mParent = (ViewGroup) mActivity.findViewById(R.id.rl_mem_content);
		mMemDetailText = (TextView)mActivity.findViewById(R.id.tv_mem_detail);
		mMemCountText = (TextView)mActivity.findViewById(R.id.tv_mem_count);
		mMemDetailText.setMovementMethod(ScrollingMovementMethod.getInstance()); 
		mMemoryHandler = new MemoryHandler();
		mDetailContent = new StringBuilder();
		boolean copyResult = FileUtils.copyFromAsset(mActivity, MEM_TEST_BIN, true);
		if(copyResult){
			FileUtils.chmodDataFile(mActivity, MEM_TEST_BIN);
		}else{
			mMemDetailText.setText(R.string.mem_err_bin);
		}
	}

	@TargetApi(Build.VERSION_CODES.N)
	@Override
	public void onStart() {
		isRunning = true;
		mMemoryHandler.sendEmptyMessageDelayed(MSG_LOOP, 2000);

		AgingConfigPath=mActivity.getDataDir().getAbsolutePath()+ File.separator+configfilename;
		File configFile = new File(AgingConfigPath);
		if(!configFile.exists()){
			//初始化测试视频文件路径
			initConfig();
		}else {
			initdefault();
		}
		//startTest();
	}

	@TargetApi(Build.VERSION_CODES.N)
	private void initConfig(){
		boolean copyResult = FileUtils.copyFromAssetToData(mActivity, configfilename, true);
		if(copyResult){
			FileUtils.chmodDataFilePath(mActivity, AgingConfigPath);
			initdefault();
		}else{
			MemorySize=getMemorySize();
			MemoryTime=getMemoryTime();
			ThreadNum=getThreadNum();
			CPUCores=getCPUCores();
			loopflag=isLoopTest();
			Log.e(TAG, "initConfig: 初始化copyFromAsset()失败!! 完成默认初始化赋值" );
		}
	}

	private void initdefault(){
		MemorySize = Integer.valueOf(ProperTiesUtils.getProperties(mActivity,configfilename,"MemorySize"));
		MemoryTime = ProperTiesUtils.getProperties(mActivity,configfilename,"MemoryTime");
		ThreadNum  = Integer.valueOf(ProperTiesUtils.getProperties(mActivity,configfilename,"ThreadNum"));
		CPUCores   = Integer.valueOf(ProperTiesUtils.getProperties(mActivity,configfilename,"CPUCores"));
		loopflag   = ProperTiesUtils.getProperties(mActivity,configfilename,"loopflag").equals("1");
	}
	
	@SuppressLint("StringFormatMatches")
	public void startTest(){
		if(!isRunning){
			return;
		}
		mTestCount++;
		mMemCountText.setText(mActivity.getString(R.string.mem_test_count, mTestCount));
		//old stressapptest path
//		String binPath = FileUtils.getDataFileFullPath(mActivity, MEM_TEST_BIN);
//		final String cmd = String.format(".%s -M %d -s %s -m %d", binPath, getMemorySize(), getMemoryTime(), getThreadNum());
		String binPath = MEM_TEST_BIN;
		final String cmd = String.format("%s -M %d -s %s -m %d -C %d", binPath, MemorySize, MemoryTime, ThreadNum, CPUCores);
		LogUtil.d(this, "Execute Memory test cmd: "+cmd);
		new Thread(){
			public void run() {
				SystemBinUtils.execCommand(cmd, MemoryTest.this);
			}
		}.start();
	}
	
	@Override
	public void onStop() {
		isRunning = false;
	}

	@Override
	public void onDestroy() {

	}
	
	/**
	 * 获取测试内存大小
	 */
	public int getMemorySize(){
//		String memstr = mAgingConfig.get("mem_size");
		String memstr = "128";
		return Integer.parseInt(memstr);
	}
	
	/**
	 * 获取测试内存时间
	 */
	public String getMemoryTime(){
//		String memstr = mAgingConfig.get("mem_time");
		String memstr = "120";
//		return Integer.parseInt(memstr);
		return memstr;
	}
	
	/**
	 * 是否循环测试
	 */
	public boolean isLoopTest(){
//		String memstr = mAgingConfig.get("mem_loop");
		String memstr = "0";
		return "1".equals(memstr);
	}
	
	/**
	 * 获取线程数
	 */
	public int getCPUCores(){
//		String threadstr = mAgingConfig.get("threads");
		String threadstr = "4";
		return Integer.parseInt(threadstr);
	}

	/**
	 * 获取CPU核数
	 */
	public int getThreadNum(){
//		String threadstr = mAgingConfig.get("threads");
		String threadstr = "1";
		return Integer.parseInt(threadstr);
	}

	@Override
	public void onResponse(InputStream resIn, InputStream errIn) {
		BufferedReader bufferedReader = null;
		try{
			InputStreamReader isr = new InputStreamReader(resIn);
			bufferedReader = new BufferedReader(isr);
			String line = null;
			int logCnt = 0;
			while(isRunning&&(line=bufferedReader.readLine())!=null){
				logCnt++;
				if(logCnt<=2) continue;
				//System.out.println(line);
				if(logCnt%15==0){
					int start = mDetailContent.indexOf("\n");
					mDetailContent.delete(0, start+1);
				}
				mDetailContent.append(line+"\n");
				mMemoryHandler.sendEmptyMessage(MSG_UPDATE_DETAIL);
				
				if(line.contains("Error")||(line.contains("error")&&!line.contains("errors"))||line.contains("FAIL")){
					isRunning = false;
					mAgingCallback.onFailed(AgingType.MEM);
					return;
				}
				
				if(line.contains("PASS")){
					break;
				}
			}
			if(loopflag&&isRunning){
				mMemoryHandler.sendEmptyMessageDelayed(MSG_LOOP, 5000);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(bufferedReader!=null){
				try{
					bufferedReader.close();
				}catch(IOException e){
				}
			}
		}
	}
	
	@Override
	public void onFailed() {
		isRunning = false;
		mMemoryHandler.removeMessages(MSG_LOOP);
	}
	
	class MemoryHandler extends Handler {
		public void handleMessage(Message msg) {
			switch(msg.what){
			case MSG_UPDATE_DETAIL:
				mMemDetailText.setText(mDetailContent.toString());
				int offset = mMemDetailText.getLineCount()*mMemDetailText.getLineHeight()-mMemDetailText.getMeasuredHeight();
				if(offset>0)
					mMemDetailText.scrollTo(0, offset);
				else
					mMemDetailText.scrollTo(0, 0);
				break;
			case MSG_LOOP:
				mDetailContent = new StringBuilder();
				startTest();
				break;
			}
		}
	}

}
