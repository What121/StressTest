package com.bestom.stresstest.util;

import android.app.Instrumentation;
import android.content.Context;
import android.hardware.input.InputManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NativeInputManager {


	//region use outtime API InputManager.getInstance().injectInputEvent
	//this api status is hide, use class invoke method to find it
//	public static void injectKeyEvent(KeyEvent ev, boolean sync) {
//        long downTime = ev.getDownTime();
//        long eventTime = ev.getEventTime();
//
//        int action = ev.getAction();
//        int code = ev.getKeyCode();
//        int repeatCount = ev.getRepeatCount();
//        int metaState = ev.getMetaState();
//        int deviceId = ev.getDeviceId();
//        int scancode = ev.getScanCode();
//        int source = ev.getSource();
//        int flags = ev.getFlags();
//
//        if (source == InputDevice.SOURCE_UNKNOWN) {
//            source = InputDevice.SOURCE_KEYBOARD;
//        }
//
//        if (eventTime == 0) eventTime = SystemClock.uptimeMillis();
//        if (downTime == 0) downTime = eventTime;
//
//        KeyEvent newEvent = new KeyEvent(downTime, eventTime, action, code, repeatCount, metaState,
//                deviceId, scancode, flags | KeyEvent.FLAG_FROM_SYSTEM, source);
//
////        InputManager.getInstance().injectInputEvent(newEvent,
////                sync ? (InputManager.INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH)
////                        : (InputManager.INJECT_INPUT_EVENT_MODE_WAIT_FOR_RESULT));
//    }
//
//	public static void injectPointerEvent(MotionEvent ev) throws NoSuchFieldException {
//        MotionEvent newEvent = MotionEvent.obtain(ev);
//
//        if ((newEvent.getSource() & InputDevice.SOURCE_CLASS_POINTER) == 0) {
//            newEvent.setSource(InputDevice.SOURCE_TOUCHSCREEN);
//        }
//		//通过反射 获取方法
////		InputManager.getInstance().injectInputEvent(newEvent, InputManager.INJECT_INPUT_EVENT_MODE_ASYNC);
//
//		Class cl = InputManager.class;
//		try {
//			Method method = cl.getMethod("getInstance");
//			Field field = cl.getField("InputManager.INJECT_INPUT_EVENT_MODE_ASYNC");
//			int i =Integer.valueOf(field.toString()) ;
//			Object result = method.invoke(cl);
//			InputManager im = (InputManager) result;
//			method = cl.getMethod("injectInputEvent", InputEvent.class, int.class);
//			method.invoke(im, newEvent, 0);
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		}  catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		} catch (NoSuchMethodException e) {
//			e.printStackTrace();
//		}catch (InvocationTargetException e) {
//			e.printStackTrace();
//		}
//
//
//    }

    //endregion

	public static void sendKeyDownUpSync(final int keyCode) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// InputManager.getInstance().injectInputEvent api 已经不提供，hide 了
//	                KeyEvent keDown = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
//	                injectKeyEvent( keDown, true);
//
//	                KeyEvent keUp = new KeyEvent(KeyEvent.ACTION_UP, keyCode);
//	                injectKeyEvent( keUp, true);

				Instrumentation mInst = new Instrumentation();
				//此api提供执行一个点击down&&up
				mInst.sendKeyDownUpSync(keyCode);
			}
		}).start();
	}

	 public static void sendTouchEventSync(final float x, final float y) {
	        new Thread(new Runnable() {
	            @Override
	            public void run() {
	                //ACTION_DOWN
	                long downTime ;
	                long eventTime ;

	                Instrumentation mInst = new Instrumentation();
					//此api提供执行一个motionevent
//	                mInst.sendKeyDownUpSync(KeyEvent.KEYCODE_A);
					// 触摸按下
//	                mInst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMilis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, point.x, point.y, 0);
					// 触摸抬起
//					mInst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMilis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, point.x, point.y, 0);

					try {
	                downTime = SystemClock.uptimeMillis();
	                eventTime = SystemClock.uptimeMillis();
	                //按键事件
	                MotionEvent me_down = MotionEvent.obtain(downTime, eventTime + 100, MotionEvent.ACTION_DOWN,
	                        x, y, 0);

	                // InputManager.getInstance().injectInputEvent api已经隐藏
//	                injectPointerEvent(me);
					// 触摸按下
	                mInst.sendPointerSync(me_down);

					} catch (SecurityException e) {
	                    Log.d("","MotionEvent.ACTION_DOWN");
	                }
	                
	                //ACTION_UP
	                try {
	                downTime = SystemClock.uptimeMillis();
	                eventTime = SystemClock.uptimeMillis();

	                MotionEvent me_up = MotionEvent.obtain(downTime, eventTime + 100, MotionEvent.ACTION_UP,
	                        x, y, 0);
	                // InputManager.getInstance().injectInputEvent api已经隐藏
//	                injectPointerEvent(me_up);
					// 触摸抬起
					mInst.sendPointerSync(me_up);


					} catch (SecurityException e) {
	                	Log.d("","MotionEvent.ACTION_UP");
	                }
	            }
	        }).start(); 
	    }
}
