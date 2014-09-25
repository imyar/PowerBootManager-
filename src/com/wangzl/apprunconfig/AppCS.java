package com.wangzl.apprunconfig;

import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Service,���ڹ����Ժ������ĳ��� �������20������ʾע��
 */
public class AppCS extends Service {
	public static final String ACTION = "com.wangzl.service";
	SharedPreferences sPreferences = null;

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		sPreferences = this
				.getSharedPreferences("runApp", Context.MODE_PRIVATE);

	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

	}

	private void task1(String packagename) {
		Intent intent1 = this.getPackageManager().getLaunchIntentForPackage(
				packagename);
		this.startActivity(intent1);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		SharedPreferences sPreferences = getSharedPreferences("runApp",
				MODE_PRIVATE);
		for (int i = 0; i < 5; i++) {
			final String appName = sPreferences.getString(
					"app" + Integer.toString(i), "");

			boolean flag = sPreferences.getBoolean(
					"appisFront" + Integer.toString(i), true);
			if (flag) {
				if (!appName.equals("")) {

					int delaytime = sPreferences.getInt(
							"appTime" + Integer.toString(i), 25) * 1000;// תΪ����
					final boolean isFront = sPreferences.getBoolean(
							"appisFront" + Integer.toString(i), false);
					new Handler().postDelayed(new Runnable() {
						public void run() {
							openApp(appName, isFront);
						}
					}, delaytime);
				}
			} else {
				// ����Ǻ�̨���������κδ�����Ϊ�Ѿ��� pm�������Ϊ�Զ�������
			}

		}
		return super.onStartCommand(intent, flags, startId);
	}

	private void openApp(String packageName, boolean isFront) {

		// isFront�ж�ǰ̨���л��Ǻ�̨����

		PackageManager pm = getPackageManager();
		Intent resloveIntent = new Intent(Intent.ACTION_MAIN);
		resloveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resloveIntent.setPackage(packageName);
		ResolveInfo homeInfo = pm.resolveActivity(resloveIntent, 0);

		// String packageName = ri.activityInfo.packageName;
		ActivityInfo ai = homeInfo.activityInfo;

		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		ComponentName cn = new ComponentName(ai.packageName, ai.name);
		intent.setComponent(cn);
		startActivitySafely(intent);

	}

	void startActivitySafely(Intent intent) {
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, "δ���ָ�activity", Toast.LENGTH_SHORT).show();
		} catch (SecurityException e) {
			Toast.makeText(this, "SecurityException", Toast.LENGTH_SHORT)
					.show();
		}
	}

}