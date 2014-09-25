package com.wangzl.apprunconfig;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class tools {

	// ASCII�ַ���ת��Ϊʮ�������ֽ�����
	public static byte[] convert2HexArray(String apdu) {
		int len = apdu.length() / 2;
		char[] chars = apdu.toCharArray();
		String[] hexes = new String[len];
		byte[] bytes = new byte[len];
		for (int i = 0, j = 0; j < len; i = i + 2, j++) {
			hexes[j] = "" + chars[i] + chars[i + 1];
			bytes[j] = (byte) Integer.parseInt(hexes[j], 16);
		}
		return bytes;
	}

	// ʮ�������ֽ�����ת��ΪASCII�ַ�����ָ��Ԫ�ظ���
	public static String bytes2HexString(byte[] b, int count) {
		String ret = "";
		for (int i = 0; i < count; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex.toUpperCase();
		}
		return ret;
	}
	
	public static String getTime() {
		SimpleDateFormat dateformat1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss E");//2006-12-21 14:40:59 ������
		String a1=dateformat1.format(new Date());
		return a1;
	}
	
	public static String getTimeSpan(){
		Format f = new SimpleDateFormat("yyyy-MM-dd");
		 
        Calendar c = Calendar.getInstance();
        System.out.println("������:" + f.format(c.getTime()));
 
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
 
        int lastSunday = 7 - dayOfWeek + 1;
 
        c.set(Calendar.DAY_OF_MONTH, -lastSunday);
        System.out.println("��������:" + f.format(c.getTime()));
        return "";
	}
	
	public static int getScreenWidth(Context context) {
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getWidth();
	}

	public static int getScreenHeight(Context context) {
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getHeight();
	}

	public static float getScreenDensity(Context context) {
		try {
			DisplayMetrics dm = new DisplayMetrics();
			WindowManager manager = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			manager.getDefaultDisplay().getMetrics(dm);
			return dm.density;
		} catch (Exception ex) {

		}
		return 1.0f;
	}
	
}
