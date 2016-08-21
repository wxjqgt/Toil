package com.weibo.toil.utils;

import android.util.Log;

import com.weibo.toil.BuildConfig;

/**
 * 包装后的Log输出,可控制显示哪些级别的LOG
 */
public class DLog {
	/** 所在的类名 */
	private static String className;
	/** 所在的方法名 */
	private static String methodName;
	/** 所在行号 */
	private static int lineNumber;

	/** 显示Verbose及以上的Log */
	private static final int VERBOSE = 1;
	/** 显示Debug及以上的Log */
	private static final int DEBUG = 2;
	/** 显示Info及以上的Log */
	private static final int INFO = 3;
	/** 显示Warn及以上的Log */
	private static final int WARN = 4;
	/** 显示Error及以上的Log */
	private static final int ERROR = 5;
	/** 全部不显示 */
	private static final int NOTHING = 6;

	/** 控制显示的级别，建议上线打包改成 NOTHING，即使不改上线打包 BuildConfig.DEBUG 也会是 false，双保险 */
	private static final int LEVEL = VERBOSE;

	/** 指定默认的 TAG */
	private static final String TAG = "sout";

	private DLog() {}

	/** 是否处于 debug 模式 */
	public static boolean isDebuggable() {
		return BuildConfig.DEBUG;
	}

//======================== 使用类名作为 TAG，打印信息包含类名、方法名、行号 开始  ========================//
	/** 生成类名相关字符的 TAG */
	private static String createLog(String log) {
		StringBuilder buffer = new StringBuilder();
		buffer.append("[");
		buffer.append(methodName);
		buffer.append(":");
		buffer.append(lineNumber);
		buffer.append("]");
		buffer.append(log);
		return buffer.toString();
	}

	/** 获取类名、方法名、行号 */
	private static void getCMNNames(StackTraceElement[] sElements) {
		className = sElements[1].getFileName();
		methodName = sElements[1].getMethodName();
		lineNumber = sElements[1].getLineNumber();
	}

	public static void v(String message) {
		if (isDebuggable())
			if (LEVEL <= VERBOSE) {
				getCMNNames(new Throwable().getStackTrace());
				Log.v(className, createLog(message));
			}
	}

	public static void d(String message) {
		if (isDebuggable())
			if (LEVEL <= DEBUG) {
				getCMNNames(new Throwable().getStackTrace());
				Log.d(className, createLog(message));
			}
	}

	public static void i(String message) {
		if (isDebuggable())
			if (LEVEL <= INFO) {
				getCMNNames(new Throwable().getStackTrace());
				Log.i(className, createLog(message));
			}
	}

	public static void w(String message) {
		if (isDebuggable())
			if (LEVEL <= WARN) {
				getCMNNames(new Throwable().getStackTrace());
				Log.w(className, createLog(message));
			}
	}

	public static void e(String message) {
		if (isDebuggable())
			if (LEVEL <= ERROR) {
				getCMNNames(new Throwable().getStackTrace());
				Log.e(className, createLog(message));
			}
	}
//======================== 使用类名作为，打印信息包含类名、方法名、行号 TAG 结束  ========================//

//======================== 指定使用 TAG 开始  ========================//
	public static void e(String tag, String msg) {
		if (isDebuggable())
			if (LEVEL <= ERROR)
				Log.e(tag, msg);
	}

	public static void e(String tag, String msg, Throwable tr) {
		if (isDebuggable())
			if (LEVEL <= ERROR)
				Log.e(tag, msg, tr);
	}

	public static void d(String tag, String msg) {
		if (isDebuggable())
			if (LEVEL <= DEBUG)
				Log.d(tag, msg);
	}

	public static void d(String tag, String msg, Throwable tr) {
		if (isDebuggable())
			if (LEVEL <= DEBUG)
				Log.d(tag, msg, tr);
	}

	public static void i(String tag, String msg) {
		if (isDebuggable())
			if (LEVEL <= INFO)
				Log.i(tag, msg);
	}

	public static void i(String tag, String msg, Throwable tr) {
		if (isDebuggable())
			if (LEVEL <= INFO)
				Log.i(tag, msg, tr);
	}

	public static void w(String tag, String msg) {
		if (isDebuggable())
			if (LEVEL <= WARN)
				Log.w(tag, msg);
	}

	public static void w(String tag, String msg, Throwable tr) {
		if (isDebuggable())
			if (LEVEL <= WARN)
				Log.w(tag, msg, tr);
	}

	public static void v(String tag, String msg) {
		if (isDebuggable())
			if (LEVEL <= VERBOSE)
				Log.v(tag, msg);
	}

	public static void v(String tag, String msg, Throwable tr) {
		if (isDebuggable())
			if (LEVEL <= VERBOSE)
				Log.v(tag, msg, tr);
	}
	
//======================== 指定使用默认 TAG 开始  ========================//
	public static void error(String msg) {
			e(TAG, msg);
	}
	public static void debug(Object msg){
		d(TAG,msg.toString());
	}
	public static void info(Object msg) {
			i(TAG, msg.toString());
	}
//======================== 指定使用默认 TAG 结束  ========================//
}
