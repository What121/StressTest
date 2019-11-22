package com.bestom.stresstest.test;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bestom.stresstest.R;
import com.bestom.stresstest.base.App;
import com.bestom.stresstest.view.ResultDialog;


public class SleepTest extends Activity  implements OnClickListener{
	private final String LOG_TAG = "SleepTestActivity";

	private Context mContext;
	private Activity mActivity;
	
	private long mAwakeTime = 5000L;
	private long mSleepTime = 40000L;
	private int mTestCount = 0;
	private int mLimitCount = 0;
	private boolean mIsRunning = false;
	
	private Button mStartBtn;
	private Button mStopBtn;
	private Button mExitBtn;
	private TextView mWakeTV;
	private Button mWakeBtn;
	private TextView mIntervalTV;
	private Button mIntervalBtn;
	private TextView mMaxTV;
	private Button mMaxBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sleep_test);

		mContext=this;
		mActivity=this;

		initRes();
		
		registerReceiver(SleepTestReceiver, new IntentFilter(
				"com.rockchip.sleep.ACTION_TEST_CASE_SLEEP"));
	}
	
	private void initRes() {
		mStartBtn = (Button) findViewById(R.id.start_btn);
		mStartBtn.setOnClickListener(this);
		mStopBtn = (Button) findViewById(R.id.stop_btn);
		mStopBtn.setOnClickListener(this);
		mExitBtn = (Button) findViewById(R.id.exit_btn);
		mExitBtn.setOnClickListener(this);
		
		mWakeBtn = (Button) findViewById(R.id.waketime_btn);
		mWakeBtn.setOnClickListener(this);
		mIntervalBtn = (Button) findViewById(R.id.intervaltime_btn);
		mIntervalBtn.setOnClickListener(this);
		mMaxBtn = (Button) findViewById(R.id.max_count_btn);
		mMaxBtn.setOnClickListener(this);
		
		mWakeTV = (TextView) findViewById(R.id.waketime_tv);
		mIntervalTV = (TextView) findViewById(R.id.intervaltime_tv);
		mMaxTV = (TextView) findViewById(R.id.max_count_tv);
		updateView();
	}
	
	private void updateView() {
		mWakeTV.setText(getString(R.string.wake_string)+mAwakeTime/1000);
		mIntervalTV.setText(getString(R.string.interval_string)+ mSleepTime/1000);
		mMaxTV.setText(getString(R.string.maxcount_string) + mLimitCount + "  "
				+ getString(R.string.nowcount_string) + mTestCount);
	}
	
	private void startTest(Context context) {
		stopAlarm(context);
		mIsRunning = true;
		if (mStartBtn != null&&mStopBtn != null) {
			mStartBtn.setEnabled(!mIsRunning);
			mStopBtn.setEnabled(mIsRunning);
		}
		try {
			int screen_off_timeout =  Settings.System.getInt(context.getContentResolver(), "screen_off_timeout");
			Log.d(LOG_TAG, "screen_off_timeout: "+screen_off_timeout);

			Settings.System.putInt(context.getContentResolver(), "screen_off_timeout", 15000);
			setAlarm(context, mSleepTime, true);
			return;
		} catch (NumberFormatException localNumberFormatException) {
			while (true)
				Log.e(LOG_TAG, "could not persist screen timeout setting");
		} catch (Settings.SettingNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void stopTest(Context context) {
		Log.d(LOG_TAG, "stopTest ...");
	    stopAlarm(context);
	    mIsRunning = false;
		if (mStartBtn != null&&mStopBtn != null){
			mStartBtn.setEnabled(!mIsRunning);
			mStopBtn.setEnabled(mIsRunning);
		}
	}
	

	@SuppressLint("WrongConstant")
	private void setAlarm(Context paramContext, long paramLong, boolean repeat) {
		@SuppressLint("WrongConstant") AlarmManager localAlarmManager = (AlarmManager) paramContext.getSystemService("alarm");
		PendingIntent localPendingIntent = PendingIntent.getBroadcast(paramContext, 0,
				new Intent("com.rockchip.sleep.ACTION_TEST_CASE_SLEEP"), 0);
		//一次性闹钟
		localAlarmManager.set(AlarmManager.RTC_WAKEUP, paramLong + System.currentTimeMillis(), localPendingIntent);

		if (repeat)
			//重复闹钟
			localAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,paramLong + System.currentTimeMillis(), paramLong, localPendingIntent);
	}

	@SuppressLint("WrongConstant")
	private void stopAlarm(Context paramContext) {
		PendingIntent localPendingIntent = PendingIntent.getBroadcast(
				paramContext, 0, new Intent("com.rockchip.sleep.ACTION_TEST_CASE_SLEEP"), 0);
		((AlarmManager) paramContext.getSystemService("alarm")).cancel(localPendingIntent);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
//		((KeyguardManager)getSystemService("keyguard")).newKeyguardLock("TestCaseSleep").reenableKeyguard();
	}
	
	protected void onDestroy() {
		super.onDestroy();
		stopTest(this);
		Log.e(LOG_TAG, "unregisterReceiver(SleepTestReceiver)");
		unregisterReceiver(SleepTestReceiver);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.start_btn:
			startTest(this);
			break;
		case R.id.stop_btn:
			stopTest(this);
			break;
		case R.id.exit_btn:
			finish();
			break;
		case R.id.waketime_btn:
			onSetClick(R.id.waketime_btn);
			break;
		case R.id.intervaltime_btn:
			onSetClick(R.id.intervaltime_btn);
			break;
		case R.id.max_count_btn:
			onSetClick(R.id.max_count_btn);
			break;
		default:
			break;
		}
	}
	
	private void onSetClick(final int id) {
		final EditText editText = new EditText(this);
		editText.setInputType(InputType.TYPE_CLASS_NUMBER);
		new AlertDialog.Builder(this)
			.setTitle(R.string.dialog_title)
			.setView(editText)
			.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(!editText.getText().toString().trim().equals("")) {
						if (id == R.id.waketime_btn) {
							mAwakeTime = Integer.valueOf(editText.getText().toString()) * 1000L;
							updateView();
						} else if (id == R.id.intervaltime_btn) {
							mSleepTime = Integer.valueOf(editText.getText().toString()) * 1000L;
							updateView();
						} else if (id == R.id.max_count_btn) {
							mLimitCount = Integer.valueOf(editText.getText().toString());
							updateView();
						}
					}
				}
			})
			.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
				
			}).show();
	}
	
	
	
	private BroadcastReceiver SleepTestReceiver = new BroadcastReceiver() {
		@SuppressLint({"InvalidWakeLockTag", "WrongConstant"})
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.e(LOG_TAG, "SleepTestReceiver onReceive...");
			 mTestCount = mTestCount+1;
			 updateView();
			 if (mLimitCount != 0 && mTestCount >= mLimitCount) {
				 mIsRunning = false;
				 //2147483647 是永不休眠
				 Settings.System.putInt(context.getContentResolver(), "screen_off_timeout", 2147483647);
				 stopTest(context);
				 //测试达到最大次数，上传数据
				 App.mStressBean.setProject(1);
				 App.mStressBean.setProjectname("SleepTest");
				 App.mStressBean.setMaxtimes(mLimitCount);
				 App.mStressBean.setCurtimes(mTestCount);
				 App.mStressBean.setUnit("次");
				 ResultDialog.getInstance(mContext,mActivity).setMaxtimes(mLimitCount).setCurtimes(mTestCount).setTitle("SleepTest").show();
			 }
			 //				((PowerManager) context.getSystemService("power")).newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK, "ScreenOnTimer").acquire(mAwakeTime);
			((PowerManager)context.getSystemService("power")).newWakeLock(268435482, "ScreenOnTimer").acquire(mAwakeTime);
			((KeyguardManager)context.getSystemService("keyguard")).newKeyguardLock("TestCaseSleep").disableKeyguard();
		}
	};

}

