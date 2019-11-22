/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Description:   
* @author:     fxw@rock-chips.com
* Create at:   2014年5月14日 下午2:25:05  
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2014年5月14日      fxw         1.0         create
*******************************************************************/   

package com.bestom.stresstest.test;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.bestom.stresstest.R;
import com.bestom.stresstest.base.BaseActivity;
import com.bestom.stresstest.test.aging.base.AgingCallback;
import com.bestom.stresstest.test.aging.base.AgingDelegate;
import com.bestom.stresstest.test.aging.CpuTest;
import com.bestom.stresstest.test.aging.GpuTest;
import com.bestom.stresstest.test.aging.MemoryTest;
import com.bestom.stresstest.test.aging.VpuTest;
import com.bestom.stresstest.test.common.AgingType;
import com.bestom.stresstest.util.IniEditor;
import com.bestom.stresstest.util.SystemInfoUtils;


public class AgingTestMain extends BaseActivity implements AgingCallback {

    public static final String AGINGTEST_FOREGROUND_ACTION = "com.rockchip.devicetest.state.foreground";
    public static final String AGINGTEST_BACKGROUND_ACTION = "com.rockchip.devicetest.state.background";
	private AgingDelegate mAgingDelegate;
	private IniEditor mIniConfig;
	private Handler mMainHandler;
	private int mKeyBackCount;


	private Toast mBackToast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_aging);
		mMainHandler = new Handler();

		mAgingDelegate = new AgingDelegate();
		initAgingDelegate();
		mAgingDelegate.onCreate(this);
		
		//version
		TextView softVersionText = (TextView)findViewById(R.id.tv_soft_ver2);
		softVersionText.setText(SystemInfoUtils.getAppVersionName(this));
		
	}
	
	/**
	 * 初始化测试项
	 */
	private void initAgingDelegate(){
		//cpu
		mAgingDelegate.addAgingTest(new CpuTest( this));
		//memory
		mAgingDelegate.addAgingTest(new MemoryTest(this));
		//gpu
		mAgingDelegate.addAgingTest(new GpuTest(this));
		//vpu
		mAgingDelegate.addAgingTest(new VpuTest( this));
	}
	
	
	@Override
	protected void onStart() {
		super.onStart();
		
		mMainHandler.postDelayed(new Runnable() {
			public void run() {
				mAgingDelegate.onStart();
			}
		}, 30);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	protected void onStop() {
		super.onStop();
		mAgingDelegate.onStop();
		//Disable home power
		Intent keyIntent = new Intent(AGINGTEST_BACKGROUND_ACTION);
		sendBroadcast(keyIntent);
	}
	
	protected void onDestroy() {
		super.onDestroy();
		mAgingDelegate.onDestroy();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	/**
	 * 测试失败
	 */
	public void onFailed(AgingType type) {
		mAgingDelegate.onFailed();

	}

	
}
