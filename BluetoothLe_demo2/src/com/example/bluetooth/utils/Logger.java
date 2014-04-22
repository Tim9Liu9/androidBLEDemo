package com.example.bluetooth.utils;


import android.util.Log;


public class Logger {
	/**
	 * whether print log info
	 */
	public static boolean weatherPrint = true;

	public static void d(String tag, String msg) {
		if (weatherPrint) {
			if (Logger.isLoggable(tag, Log.DEBUG)) {
				Log.d(tag, msg);
			}
		}
	}
	
	public static void v(String tag, String msg) {
		if (weatherPrint) {
			if (Logger.isLoggable(tag, Log.VERBOSE)) {
				Log.v(tag, msg);
			}
		}
	}

	public static void i(String tag, String msg) {
		if (weatherPrint) {
			if (Logger.isLoggable(tag, Log.INFO)) {
				Log.i(tag, msg);
			}
		}
	}

	public static void w(String tag, String msg) {
		if (weatherPrint) {
			if (Logger.isLoggable(tag, Log.WARN)) {
				Log.w(tag, msg);
			}
		}
	}

	public static void e(String tag, String msg) {
		if (weatherPrint) {
			if (Logger.isLoggable(tag, Log.ERROR)) {
				Log.e(tag, msg);
			}
		}
	}

	public static boolean isLoggable(String tag, int level) {
		//return Log.isLoggable(tag.substring(0, Math.min(23, tag.length())), level);
		return true;
	}
}

