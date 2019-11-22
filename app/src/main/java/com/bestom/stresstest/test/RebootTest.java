package com.bestom.stresstest.test;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.ServiceManager;
import android.os.storage.IMountService;
import android.os.storage.StorageManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bestom.stresstest.R;
import com.bestom.stresstest.base.App;
import com.bestom.stresstest.util.AppUtil;
import com.bestom.stresstest.util.StresstestUtil;
import com.bestom.stresstest.view.ResultDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class RebootTest extends Activity implements OnClickListener {
	private final static String LOG_TAG = "RebootTest";

	private Context mContext;
	private Activity mActivity;

	private final static int MSG_REBOOT = 0;
	private final static int MSG_REBOOT_COUNTDOWN = 1;
	private final static int MSG_REBOOT_STARTCOUNT = 2;
	
	private final static String SDCARD_PATH = "/mnt/external_sd";
	
	private final int DELAY_TIME = 5000;// ms
	private final int REBOOT_OFF = 0;
	private final int REBOOT_ON = 1;

	private SharedPreferences mSharedPreferences;

	private TextView mCountTV;
	private TextView mCountdownTV;
	private TextView mMaxTV;
	private Button mStartButton;
	private Button mStopButton;
	private Button mExitBtn;
	private Button mSettingButton;
	private Button mClearButton;
	private CheckBox mSdcardCheckCB;

	// 外置SD卡
	private StorageManager storageManager;
	public static String mInterSD; // 内置sd卡
	public static String mExternalSD; // 外置sd卡 1
	public static String mUSB; // U盘，外置sd卡2
	
	private WakeLock mWakeLock;
	private int mState;
	private int mCount;
	private int mCountDownTime;
	private int mMaxTimes; // max times to reboot
	private boolean mIsCheckSD = false;
	private boolean mFT = false;
	private String mSdState = null;
  private String RebootMode = null;

	@SuppressLint({"InvalidWakeLockTag", "WrongConstant"})
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reboot_test);

		mContext=this;
		mActivity=this;

		storageManager = (StorageManager) getApplicationContext().
				getSystemService(Context.STORAGE_SERVICE);

		// get the reboot flag and count.
		mSharedPreferences = getSharedPreferences("state", 0);
		mState = mSharedPreferences.getInt("reboot_flag", 0);
		mCount = mSharedPreferences.getInt("reboot_count", 0);
		mMaxTimes = mSharedPreferences.getInt("reboot_max", 0);
		mIsCheckSD = mSharedPreferences.getBoolean("check_sd", false);
        ((KeyguardManager)getSystemService("keyguard")).newKeyguardLock("TestReboot").disableKeyguard();
		mWakeLock = ((PowerManager)getSystemService("power")).newWakeLock(PowerManager.FULL_WAKE_LOCK, "RebootTest");
		mWakeLock.acquire();

		// init resource
		initRes();

		if (mState == REBOOT_ON) {
			if (mIsCheckSD) {
				Log.d(LOG_TAG, "start checkSD ...... ");
//				mSdState = getSdCardState();
//				if (!mSdState.equals(Environment.MEDIA_MOUNTED)) {
				getStorageList();
				if (!getTFState()){
					TextView tv =(TextView) findViewById(R.id.sdcard_check_tv); 
					tv.setText(getString(R.string.check_sd_result)+false);
					tv.setVisibility(View.VISIBLE);
					onStopClick();
					return ;
				}
				Log.d(LOG_TAG, "finished checkSD ");
			}

			if (mMaxTimes != 0 && mMaxTimes <= mCount) {
				Log.d(LOG_TAG, "CurrentTimes biger as MaxTimes ...... ");
				mState = REBOOT_OFF;
				saveSharedPreferences(mState, 0);
				//测试达到最大次数，上传数据
				App.mStressBean.setProject(0);
				App.mStressBean.setProjectname("RebootTest");
				App.mStressBean.setMaxtimes(mMaxTimes);
				App.mStressBean.setCurtimes(mCount);
				App.mStressBean.setUnit("次");
				ResultDialog.getInstance(mContext,mActivity).setMaxtimes(mMaxTimes).setCurtimes(mCount).setTitle("RebootTest").show();

				saveMaxTimes(0);
				updateBtnState();
				mCountTV.setText(mCountTV.getText()+" TEST FINISH!");
			}
//			else if(isRebootError()/*false*/){
			else if(false){
				Log.d(LOG_TAG, " is RebootError true ");
					mState = REBOOT_OFF;
					saveSharedPreferences(mState, 0);
					saveMaxTimes(0);
					updateBtnState();
					mCountTV.setText(mCountTV.getText()+" Test fail for error!");
					 
		    }else {
				mCountDownTime = DELAY_TIME / 1000;
				mHandler.sendEmptyMessage(MSG_REBOOT_STARTCOUNT);
				Log.d(LOG_TAG, "sendEmptyMessage MSG_REBOOT_STARTCOUNT: ");
			}
		}
	}

	private void initRes() {
		mCountTV = (TextView) findViewById(R.id.count_tv);
		mCountTV.setText(getString(R.string.reboot_time) + mCount);
		mMaxTV = (TextView) findViewById(R.id.maxtime_tv);
		if (mMaxTimes == 0) {
			mMaxTV.setText(getString(R.string.reboot_maxtime) + getString(R.string.not_setting));
		} else {
			mMaxTV.setText(getString(R.string.reboot_maxtime) + mMaxTimes);
		}

		mStartButton = (Button) findViewById(R.id.start_btn);
		mStartButton.setOnClickListener(this);

		mStopButton = (Button) findViewById(R.id.stop_btn);
		mStopButton.setOnClickListener(this);
		
		mExitBtn = (Button) findViewById(R.id.exit_btn);
		mExitBtn.setOnClickListener(this);

		mSettingButton = (Button) findViewById(R.id.setting_btn);
		mSettingButton.setOnClickListener(this);

		mClearButton = (Button) findViewById(R.id.clear_btn);
		mClearButton.setOnClickListener(this);
		
		mSdcardCheckCB = (CheckBox) findViewById(R.id.sdcard_check_cb);
		mSdcardCheckCB.setChecked(mIsCheckSD);
		mSdcardCheckCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					//old check
//					String state = getSdCardState();
//					if (!state.equals(Environment.MEDIA_MOUNTED)) {
					getStorageList();
					if	(!getTFState()){
						Toast.makeText(RebootTest.this, "Please insert sdcard or check sd gpio control!", Toast.LENGTH_LONG).show();
						buttonView.setChecked(false);
					}
				}
			}
		});
		updateBtnState();

		mCountdownTV = (TextView) findViewById(R.id.countdown_tv);
	}

	private void reboot() {
		// save state
		saveSharedPreferences(mState, mCount + 1);

		// 重启
		/*
		 * String str = "重启"; try { str = runCmd("reboot", "/system/bin"); }
		 * catch (IOException e) { e.printStackTrace(); }
		 */
		/*
		 * Intent reboot = new Intent(Intent.ACTION_REBOOT);
		 * reboot.putExtra("nowait", 1); reboot.putExtra("interval", 1);
		 * reboot.putExtra("window", 0); sendBroadcast(reboot);
		 */
		PowerManager pManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		pManager.reboot("重启");
		System.out.println("execute cmd--> reboot\n" + "重启");
	}

	private void saveSharedPreferences(int flag, int count) {
		SharedPreferences.Editor edit = mSharedPreferences.edit();
		edit.putInt("reboot_flag", flag);
		edit.putInt("reboot_count", count);
		edit.putBoolean("check_sd", mIsCheckSD);
		edit.commit();
	}
	
	private void saveMaxTimes(int max) {
		SharedPreferences.Editor edit = mSharedPreferences.edit();
		edit.putInt("reboot_max", max);
		edit.commit();
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_REBOOT:
				if (mState == 1)
					reboot();
				break;

			case MSG_REBOOT_COUNTDOWN:
				Log.d(LOG_TAG, "MSG_REBOOT_COUNTDOWN");
				Log.d(LOG_TAG, "mState"+mState);
				if (mState == 0)
					return;
				if (mCountDownTime != 0) {
					mCountdownTV.setText(getString(R.string.reboot_countdown)
							+ mCountDownTime);
					mCountdownTV.setVisibility(View.VISIBLE);
					mCountDownTime--;
					sendEmptyMessageDelayed(MSG_REBOOT_COUNTDOWN, 1000);
				} else {
				if(isSystemError()){
					mState = REBOOT_OFF;
					saveSharedPreferences(mState, 0);
				    saveMaxTimes(0);
					updateBtnState();
				    mCountTV.setText(mCountTV.getText()+" Test fail for error!");
							 
				}else{
					mCountdownTV.setText(getString(R.string.reboot_countdown)
							+ mCountDownTime);
					mCountdownTV.setVisibility(View.VISIBLE);
					sendEmptyMessage(MSG_REBOOT);
				   }
				}

				break;
			case MSG_REBOOT_STARTCOUNT:
				sendEmptyMessage(MSG_REBOOT_COUNTDOWN);
				Log.d(LOG_TAG, "sendEmptyMessage MSG_REBOOT_COUNTDOWN");
				break;

			default:
				break;
			}
		};
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.start_btn:
			onStartClick();
			break;
		case R.id.stop_btn:
			onStopClick();
			break;
		case R.id.exit_btn:
			finish();
			break;
		case R.id.setting_btn:
			onSettingClick();
			break;
		case R.id.clear_btn:
			onClearSetting();
			break;
		default:
			break;
		}

	}

       @Override
        protected void onDestroy() {
                super.onDestroy();
         //       stopTest();
                mWakeLock.release();
        }

	private void onStartClick() {
		mFT = true;
		new AlertDialog.Builder(RebootTest.this)
				.setTitle(R.string.reboot_dialog_title)
				.setMessage(R.string.reboot_dialog_msg)
				.setPositiveButton(R.string.dialog_ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								mState = REBOOT_ON;
								mIsCheckSD = mSdcardCheckCB.isChecked();
								mCountDownTime = DELAY_TIME / 1000; // ms->s
								updateBtnState();
								mHandler.sendEmptyMessage(MSG_REBOOT_STARTCOUNT);
							}
						})
				.setNegativeButton(R.string.dialog_cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						}).show();
	}

	private void onStopClick() {
		mHandler.removeMessages(MSG_REBOOT);
		mCountdownTV.setVisibility(View.INVISIBLE);
		mState = REBOOT_OFF;
		updateBtnState();
		mIsCheckSD = false;
		saveSharedPreferences(mState, 0);

	}
	
	private void onSettingClick() {
		final EditText editText = new EditText(this);
		editText.setInputType(InputType.TYPE_CLASS_NUMBER);
		new AlertDialog.Builder(this)
			.setTitle(R.string.btn_setting)
			.setView(editText)
			.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(!editText.getText().toString().trim().equals("")) {
						mMaxTimes = Integer.valueOf(editText.getText().toString());
						saveMaxTimes(mMaxTimes);
						mMaxTV.setText(getString(R.string.reboot_maxtime)+mMaxTimes);
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

	private void onClearSetting() {
		mMaxTimes = 0;
		saveMaxTimes(mMaxTimes);
		mMaxTV.setText(getString(R.string.reboot_maxtime)
				+ getString(R.string.not_setting));
	}
	
	private void SavedRebootMode() {
		Process process = null;
		String filePath = "mnt/internal_sd/boot_mode.txt";
		File file1 = new File(filePath);
		if (file1.isFile() && file1.exists()) {
			file1.delete();
		}
		try {
			StresstestUtil.getBootMode(true);
		} catch (Exception e) {
			Log.e(LOG_TAG, "getBootMode fail!!!");
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			String encoding = "GBK";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { //判断文件是否存在
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding);//考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					Log.d(LOG_TAG, lineTxt);
					int p1 = lineTxt.indexOf("(");
					int p2 = lineTxt.indexOf(")");
					RebootMode = lineTxt.substring(p1 + 1, p2);
					Toast.makeText(this, RebootMode, Toast.LENGTH_LONG).show();

				}
				read.close();
			} else {
				Log.e(LOG_TAG, "not find the mnt/internal_sd/boot_mode.txt");
			}
		} catch (Exception e) {
			Log.e(LOG_TAG, "read error!!");
			e.printStackTrace();
		}
	}

	private boolean isRebootError(){
		SavedRebootMode();
		if(RebootMode!=null){
                  if(Integer.valueOf(RebootMode) == 7) {
					  Dialog dialog = new AlertDialog.Builder(this)
							  .setTitle("重启测试异常")
							  .setMessage("检测到本次为panic重启，详情请看last_log")
							  .setPositiveButton("确定", new DialogInterface.OnClickListener() {
								  public void onClick(DialogInterface dialog, int whichButton) {
									  dialog.cancel();
								  }
							  }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
								  public void onClick(DialogInterface dialog, int whichButton) {
									  dialog.cancel();
								  }
							  }).create();
					  dialog.show();
					  return true;
				  }
                  else if(Integer.valueOf(RebootMode) == 8) {
					  Dialog dialog = new AlertDialog.Builder(
							  this)
							  .setTitle("重启测试异常")
							  .setMessage("检测到本次为watchdog重启，详情请看last_log")
							  .setPositiveButton("确定", new DialogInterface.OnClickListener() {
								  public void onClick(DialogInterface dialog, int whichButton) {
									  dialog.cancel();
								  }
							  }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
								  public void onClick(DialogInterface dialog, int whichButton) {
									  dialog.cancel();
								  }
							  }).create();
					  dialog.show();
					  return true;
				  }
	 	return false;
	 }
		return false;
	}
	
	private boolean isSystemError(){
		InputStreamReader reader = null;
		BufferedReader bufferedReader = null;
		Process process = null;
		String lineText = null;
		if(!mFT){
		try{
		process = Runtime.getRuntime().exec("logcat -d");
		reader = new InputStreamReader(process.getInputStream());
		bufferedReader = new BufferedReader(reader);
				Log.d("--hjc","-------------->>lineTxt:"+lineText);
		while((lineText = bufferedReader.readLine()) != null){
			//	Log.d("--hjc","-------------->>lineTxt:"+lineText);
         if(lineText.indexOf("Force finishing activity")!=-1||lineText.indexOf("backtrace:")!=-1){
				 Log.d("--hjc","------lineTxt:"+lineText);
         			   Dialog dialog = new AlertDialog.Builder(
					   this)
			.setTitle("重启测试异常")
			.setMessage("检测到系统异常，详情请看logcat")
			.setPositiveButton("确定",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int whichButton) {
					 dialog.cancel();
					}
			}).setNegativeButton("取消",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int whichButton) {
					dialog.cancel();
			}}).create();
			dialog.show();
			reader.close();
			bufferedReader.close();
			return true;}
	   }
	   reader.close();
			bufferedReader.close();
			return false;
		} catch (Exception e) {
			Log.e(LOG_TAG,"process Runtime error!!");
		  e.printStackTrace();
		}
		}
	return false;
	
	}
	
	private void updateBtnState() {
		mStartButton.setEnabled(mState == REBOOT_OFF);
		mClearButton.setEnabled(mState == REBOOT_OFF);
		mSettingButton.setEnabled(mState == REBOOT_OFF);
		mStopButton.setEnabled(mState == REBOOT_ON);
	}
	
	public static String getSdCardState() {
        try {
        	IMountService mMntSvc = null;
            if (mMntSvc == null) {
                mMntSvc = IMountService.Stub.asInterface(ServiceManager
                                                         .getService("mount"));
            }
            return mMntSvc.getVolumeState(SDCARD_PATH);
        } catch (Exception rex) {
            return Environment.MEDIA_REMOVED;
        }
    }

	/**********************************************************
	 * check TF卡 USB
	 */
	private void getStorageList() {
		String[] paths = AppUtil.getVolumePaths(storageManager);
		if (paths != null) {
			if (paths.length > 0) {
				mInterSD = paths[0];
			}
			if (paths.length > 1) {
				mExternalSD = paths[1];
			}
			if (paths.length > 2) {
				mUSB = paths[2];
			}
		}
	}

	private boolean getTFState() {
		if (TextUtils.isEmpty(mExternalSD)) {
			return false;
		}
		try {
			return "mounted".equals(Environment.getStorageState(new File(mExternalSD)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean getUSBState() {
		if (TextUtils.isEmpty(mUSB)) {
			return false;
		}
		try {
			return "mounted".equals(Environment.getStorageState(new File(mUSB)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	

}
