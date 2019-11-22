/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Description:   
* @author:     fxw@rock-chips.com
* Create at:   2014年5月14日 下午3:43:56  
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2014年5月14日      fxw         1.0         create
*******************************************************************/   

package com.bestom.stresstest.test.aging;

import java.io.File;

import com.bestom.stresstest.R;
import com.bestom.stresstest.test.aging.base.AgingCallback;
import com.bestom.stresstest.test.aging.base.BaseAgingTest;
import com.bestom.stresstest.test.common.AgingType;
import com.bestom.stresstest.test.common.ConfigFinder;
import com.bestom.stresstest.util.FileUtils;
import com.bestom.stresstest.util.LogUtil;
import com.bestom.stresstest.view.VideoView;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class VpuTest extends BaseAgingTest {
	private static final String TAG = "VpuTest";
	public static final int DETECT_VIDEO_TIME = 60000;//60s
	private Activity mActivity;
	private VideoView mVideoView;
	private TextView mResultTextView;
	private boolean isTestVideoExisted;
	private boolean isRunning;
	private Handler mMainHandler;
	private String mVideoPath;
	private int mDuration;
	private long mLastStartTime;
	private int mTimeOutStartCnt;

//	private final String mediaPath="/system/usr/Aging_Test_Video.mp4";
	private final String videoname="Aging_Test_Video.mp4";
	private String mediaPath=null;

	public VpuTest( AgingCallback agingCallback){
		super( agingCallback);
		mMainHandler = new Handler();
	}
	
	public void onCreate(Activity activity) {
		mActivity = activity;
		mVideoView = (VideoView)mActivity.findViewById(R.id.vv_vpu);
		mResultTextView = (TextView)mActivity.findViewById(R.id.tv_vpu_result);

		//初始化测试视频文件路径
		mediaPath=mActivity.getFilesDir().getAbsolutePath()+File.separator+videoname;
		initvideo();

		File videoFile = getTestVideoFile();
		if(videoFile==null||!videoFile.exists()){
			isTestVideoExisted = false;
			mResultTextView.setText(R.string.vpu_err_video);
			mResultTextView.setVisibility(View.VISIBLE);
			return;
		}
		isTestVideoExisted = true;
		mTimeOutStartCnt = 0;
		mLastStartTime = System.currentTimeMillis();
		mVideoPath = videoFile.getAbsolutePath();
		//final MediaController mediaController = new MediaController(mActivity);
		mVideoView.setVideoPath(mVideoPath);
        //mVideoView.setMediaController(mediaController);
        mVideoView.setOnPreparedListener(new OnPreparedListener() {
			public void onPrepared(MediaPlayer mp) {
				Log.d("VpuTest", "VideoPlayer is onPrepared. ");
				mp.start();
				mLastStartTime = System.currentTimeMillis();
				//mp.setLooping(true);
			}
		});
        mVideoView.setOnCompletionListener(new OnCompletionListener() {
			public void onCompletion(MediaPlayer mp) {
				Log.d("VpuTest", "VideoPlayer is onCompletion. ");
				if(isRunning){
					mVideoView.pause();
					mVideoView.stopPlayback();
					mMainHandler.removeCallbacks(mRepeatAction);
					mMainHandler.postDelayed(mRepeatAction, 300);
					//mVideoView.start();
				}
			}
		});
        mVideoView.setOnErrorListener(new OnErrorListener() {
			public boolean onError(MediaPlayer mp, int what, int extra) {
				if(what==-1010){
					mVideoView.setVideoPath(mVideoPath);
					LogUtil.e(VpuTest.this, "Rock On prepared error. ");
				}else{
					mResultTextView.setText(R.string.vpu_err_play);
					mResultTextView.setVisibility(View.VISIBLE);
					mAgingCallback.onFailed(AgingType.VPU);
				}
				return true;
			}
		});
        mVideoView.requestFocus();
	}

	private void initvideo(){
		boolean copyResult = FileUtils.copyFromAsset(mActivity, videoname, true);
		if(copyResult){
			FileUtils.chmodDataFile(mActivity, videoname);
		}else{
			//ui暂时不更新，在下面检查视频存在再做更新结果
//			isTestVideoExisted = false;
//			mResultTextView.setText(R.string.vpu_err_video);
//			mResultTextView.setVisibility(View.VISIBLE);
			Log.e(TAG, "initvideo: 视频初始化copyFromAsset()失败!!" );
		}
	}
	
	//循环播放
	Runnable mRepeatAction = new Runnable() {
		public void run() {
			if(isTestVideoExisted){
				mVideoView.setVideoPath(mVideoPath);
			}
		}
	};
	
	//检测播放状态
	Runnable mDetectVideoAction = new Runnable() {
		public void run() {
			if(isRunning){
				if((System.currentTimeMillis()-mLastStartTime)>DETECT_VIDEO_TIME){
					mTimeOutStartCnt++;
					if(mTimeOutStartCnt<=1){//first time, try to repeat again
						mMainHandler.removeCallbacks(mRepeatAction);
						mMainHandler.postDelayed(mRepeatAction, 300);
						Log.d("VpuTest", "Detect video isn't playing. try again. ");
					}else{//Error
						Log.d("VpuTest", "Detect video occour error. stop test.");
						mAgingCallback.onFailed(AgingType.VPU);
						return;
					}
				}else{
					mTimeOutStartCnt = 0;
				}
				mMainHandler.postDelayed(mDetectVideoAction, DETECT_VIDEO_TIME);//60s
			}
		}
	};
	
	/**
	 * 获取片源时长
	 */
	public int getVideoDuration(){
		if(mDuration<=0){
			mDuration = mVideoView.getDuration();
		}
		return mDuration;
	}

	public void onStart() {
		if(isTestVideoExisted){
			mVideoView.start();
			isRunning = true;
		}
	}
	
	/**
	 * 获取测试片源路径
	 * @return
	 */
	public File getTestVideoFile(){
//		String mediaFile = mAgingConfig.get("testvideo");
		String mediaFile = mediaPath;
	    return ConfigFinder.findConfigFile(mediaFile,mActivity.getApplicationContext());
	}
	

	public void onStop() {
		mMainHandler.removeCallbacks(mDetectVideoAction);
		mMainHandler.removeCallbacks(mRepeatAction);
		if(isTestVideoExisted){
			mVideoView.pause();
		}
		isRunning = false;
	}

	public void onDestroy() {
		if(isTestVideoExisted){
			mVideoView.stopPlayback();
		}
	}

	@Override
	public void onFailed() {
		isRunning = false;
		mVideoView.stopPlayback();
	}
	
}

class MyVideoView extends VideoView{

	public MyVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
}
