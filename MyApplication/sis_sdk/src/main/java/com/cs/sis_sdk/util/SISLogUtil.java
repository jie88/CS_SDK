package com.cs.sis_sdk.util;

import android.text.TextUtils;

/**
 * 自定义日志 有开关， 并且可以输入到sd卡中
 */
public class SISLogUtil {

	public static final boolean IS_DEBUG = true;// 日志开关


	public static void v(String msg) {
		v(null,msg);
	}
	public static void v(String tag, String msg) {
		if (!IS_DEBUG)
			return;

		if (msg.length() < LOG_LENGTH) {
			android.util.Log.v(getTag(tag), msg);
		} else {
			// msg过长
			String str1 = msg.substring(0, LOG_LENGTH);
			android.util.Log.v(getTag(tag), str1);
			v(getTag(tag),msg.substring(LOG_LENGTH));
		}
	}

	public static void d(String msg) {
		d(null,msg);
	}

	public static void d(String tag, String msg) {
		if (!IS_DEBUG)
			return;

		if (msg.length() < LOG_LENGTH) {
			android.util.Log.d(getTag(tag), msg);
		} else {
			// msg过长
			String str1 = msg.substring(0, LOG_LENGTH);
			android.util.Log.d(getTag(tag), str1);
			d(getTag(tag),msg.substring(LOG_LENGTH));
		}
	}

	public static void i(String msg) {
		i(null,msg);
	}
	public static void i(String tag, String msg) {
		if (!IS_DEBUG)
			return;

		if (msg.length() < LOG_LENGTH) {
			android.util.Log.i(getTag(tag), msg);
		} else {
			// msg过长
			String str1 = msg.substring(0, LOG_LENGTH);
			android.util.Log.i(getTag(tag), str1);
			i(getTag(tag),msg.substring(LOG_LENGTH));
		}
	}

	public static void w(String msg) {
		if (!IS_DEBUG)
			return;

		if (msg.length() < LOG_LENGTH) {
			android.util.Log.w(TAG, msg);
		} else {
			// msg过长
			String str1 = msg.substring(0, LOG_LENGTH);
			android.util.Log.w(TAG, str1);
			w(msg.substring(LOG_LENGTH));
		}
	}

	public static void e(String msg) {
		d(null,msg);
	}
	public static void e(String tag, String msg) {
		if (!IS_DEBUG)
			return;

		if (msg.length() < LOG_LENGTH) {
			String errorPoint = "error at " + Thread.currentThread().getStackTrace()[2].getMethodName() + " called by "
					+ Thread.currentThread().getStackTrace()[3].getClassName() + "::"
					+ Thread.currentThread().getStackTrace()[3].getMethodName();

			android.util.Log.e(getTag(tag), msg);
			android.util.Log.e(getTag(tag), errorPoint);
		} else {
			// msg过长
			String str1 = msg.substring(0, LOG_LENGTH);
			android.util.Log.e(getTag(tag), str1);
			e(getTag(tag),msg.substring(LOG_LENGTH));
		}
	}

	/**
	 * 打印流程,默认i levle
	 * 
	 * @param msg 可以为null
	 */
	public static void printProcess(String msg) {
		if (!IS_DEBUG)
			return;

		String info = Thread.currentThread().getStackTrace()[3].getClassName() + //
				"::" + Thread.currentThread().getStackTrace()[3].getMethodName();
		if (msg != null)
			info += "  " + msg;

		i(info);
	}
	
	/**
	 * 在方法内部调用，打印调用信息
	 */
	public static void whoInvokeMe() {
		if (!IS_DEBUG)
			return;

		String callerInfo = Thread.currentThread().getStackTrace()[3].getMethodName() + //
				" called by " + Thread.currentThread().getStackTrace()[4].getClassName() + //
				"::" + Thread.currentThread().getStackTrace()[4].getMethodName();

		i(callerInfo);
	}

	private static String getTag(String tag) {
		return !TextUtils.isEmpty(tag) ? (TAG + ": " + tag + " ") : TAG;
	}
	
	private static final String TAG = "SIS_CLIENT";



	private static final int LOG_LENGTH = 3000; // android对多余某个长度的string,logcat会截断
}
