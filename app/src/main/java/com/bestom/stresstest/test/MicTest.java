package com.bestom.stresstest.test;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bestom.stresstest.R;
import com.bestom.stresstest.base.App;
import com.bestom.stresstest.util.FileUtils;
import com.bestom.stresstest.util.SystemBinUtils;
import com.bestom.stresstest.view.ResultDialog;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class MicTest extends Activity implements OnClickListener {
	private static final String TAG = "MicTest";
	private Context mContext;
	private Activity mActivity;

	Button maxtime_bt,start_bt,exit_bt;
	TextView result_tv,maxtimes_tv,msg_tv;

	private int CurCount;
	private int mMaxCount=10;
	private boolean isTesting=false;

	private String AppPath;
	private String ShellPath0,ShellPath1;
	private String Shellname0="tinycap.sh";
    private String Shellname1="mic.sh";
	private String pcmPath;

	private Timer mTimer;

	private final int UPDATE_MSG_TV = 111;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case UPDATE_MSG_TV:
					switch (CurCount%3){
						case 0:
							msg_tv.setText(getString(R.string.recording)+".");
							break;
						case 1:
							msg_tv.setText(getString(R.string.recording)+"..");
							break;
						case 2:
							msg_tv.setText(getString(R.string.recording)+"...");
							break;
						default:
							break;
					}
					break;
				default:
					break;
			}
		}
	};


	@TargetApi(Build.VERSION_CODES.N)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mic_test);
		mContext=this;
		mActivity=this;

		AppPath=this.getDataDir().getAbsolutePath()+ File.separator;
		pcmPath=AppPath+"test.pcm";
		//check shell
		checkShell();

		initview();
		mTimer=new Timer();
		initshell();

	}

	@TargetApi(Build.VERSION_CODES.N)
	private void checkShell(){
		ShellPath0=AppPath+Shellname0;
        ShellPath1=AppPath+Shellname1;
		File shellFile0 = new File(ShellPath0);
        File shellFile1 = new File(ShellPath1);
		if(!shellFile0.exists()){
			//初始化测试视频文件路径
            initShell0();
		}
        if(!shellFile1.exists()){
            //初始化测试视频文件路径
            initShell1();
        }
	}
	private void initShell0(){
		boolean copyResult = FileUtils.copyFromAssetToData(this, Shellname0, true);
		if(copyResult){
			// chmod 777 configfile
			FileUtils.chmodDataFilePath(this, ShellPath0);
		}else{
			Log.e(TAG, "initShell0: 初始化copyFromAsset()失败!! 完成默认初始化赋值" );
		}
	}

    private void initShell1(){
        boolean copyResult = FileUtils.copyFromAssetToData(this, Shellname1, true);
        if(copyResult){
            // chmod 777 configfile
            FileUtils.chmodDataFilePath(this, ShellPath1);
        }else{
            Log.e(TAG, "initShell1: 初始化copyFromAsset()失败!! 完成默认初始化赋值" );
        }
    }

	private void initview(){
		result_tv= (TextView) findViewById(R.id.result);
		maxtimes_tv= (TextView) findViewById(R.id.maxtime_tv);
		maxtimes_tv.setText(getString(R.string.max_test_time_limit)+ mMaxCount);
		maxtime_bt= (Button) findViewById(R.id.maxtime_btn);
		maxtime_bt.setOnClickListener(this);
		msg_tv= (TextView) findViewById(R.id.msg_tv);
		start_bt= (Button) findViewById(R.id.start_btn);
		start_bt.setOnClickListener(this);
		exit_bt= (Button) findViewById(R.id.exit_btn);
		exit_bt.setOnClickListener(this);
	}

	private void initshell(){
        //执行shell
		Log.d(TAG, "ShellPath1: "+ShellPath1);
        String commd="."+ ShellPath1 ;
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemBinUtils.execCommand(commd);
            }
        }).start();
		Log.d(TAG, "startTest commd: "+commd);
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void onSetClick() {
		final EditText editText = new EditText(this);
		editText.setInputType(InputType.TYPE_CLASS_NUMBER);
		new AlertDialog.Builder(this)
				.setTitle(R.string.dialog_title)
				.setView(editText)
				.setPositiveButton(R.string.dialog_ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,int which) {
								if (!editText.getText().toString().trim().equals("")) {
									mMaxCount = Integer.valueOf(editText.getText().toString());
									maxtimes_tv.setText(getString(R.string.max_test_time_limit)+ mMaxCount);
								}
							}
						})
				.setNegativeButton(R.string.dialog_cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,int which) {
								mMaxCount=10;
								dialog.cancel();
							}

						}).show();
	}

	@TargetApi(Build.VERSION_CODES.N)
	private void startTest() {
		//执行shell
//		Log.d(TAG, "ShellPath: "+ShellPath);
//		Log.d(TAG, "pcmPath: "+pcmPath);
		String commd="."+ShellPath0+" "+mMaxCount+" "+pcmPath ;
		new Thread(new Runnable() {
			@Override
			public void run() {
				SystemBinUtils.execCommand(commd);
			}
		}).start();
		Log.d(TAG, "startTest commd: "+commd);

		// 处理UI
		msg_tv.setTextColor(getColor(R.color.blue));
		msg_tv.setVisibility(View.VISIBLE);
		CurCount=0;
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (CurCount<=mMaxCount){
					mHandler.sendEmptyMessage(UPDATE_MSG_TV);
					CurCount++;
				}else {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							msg_tv.setTextColor(getColor(R.color.green));
							msg_tv.setText(getString(R.string.recording)+" completed"+"\n"+"pcmpath:"+pcmPath);
						}
					});
					// chmod 777 configfile
					FileUtils.chmodDataFilePath(mContext, pcmPath);
					isTesting = false;
					this.cancel();
				}
			}
		},500,1000);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.maxtime_btn:
				onSetClick();
				break;
			case R.id.start_btn:
				if (!isTesting){
					isTesting = true;
					startTest();
				}
				break;
			case R.id.exit_btn:
				finish();
				break;
			default:
				break;
		}
	}


}
