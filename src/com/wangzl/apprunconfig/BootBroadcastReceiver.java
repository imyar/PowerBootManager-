package com.wangzl.apprunconfig;

import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.EditText;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class BootBroadcastReceiver extends BroadcastReceiver {
	// 重写onReceive方法
	String string = "";

	@SuppressWarnings("static-access")
	@Override
	public void onReceive(final Context context, Intent intent) {
		SharedPreferences sPreferences = context.getSharedPreferences("runApp",
				Context.MODE_PRIVATE);
		final Editor editor = sPreferences.edit();
		context.startService(new Intent(context, AppCS.class));
		// KeyguardManager keyguardManager = (KeyguardManager) context
		// .getSystemService(context.KEYGUARD_SERVICE);
		//
		// @SuppressWarnings({ "deprecation", "static-access" })
		// KeyguardLock keyguardLock = keyguardManager
		// .newKeyguardLock(context.KEYGUARD_SERVICE);
		//
		// keyguardLock.disableKeyguard();

//		int run_times = sPreferences.getInt("run_times", 0);
//		boolean isActivityed = sPreferences.getBoolean("isActivityed", false);
//		if (isActivityed) {
//			context.startService(new Intent(context, AppCS.class));
//		} else {
//			if (run_times > 30) {
//				final EditText inputServer = new EditText(context);
//				new AlertDialog.Builder(context)
//						.setTitle(R.string.about_title)
//						.setMessage("测试次数用完，请购买")
//						.setView(inputServer)
//						.setNeutralButton(android.R.string.ok,
//								new DialogInterface.OnClickListener() {
//									@Override
//									public void onClick(DialogInterface dialog,
//											int which) {
//										String chkString = inputServer
//												.getText().toString();
//										if (chkString
//												.equals("wkkj-0100-093x-tiuj")) {
//											Toast.makeText(context,
//													"恭喜，程序已经激活",
//													Toast.LENGTH_LONG).show();
//											editor.putBoolean("isActivityed",
//													true);
//											editor.commit();
//											dialog.dismiss();
//											context.startService(new Intent(
//													context, AppCS.class));
//
//										} else {
//											Toast.makeText(context, "请购买正版程序",
//													Toast.LENGTH_LONG).show();
//											dialog.dismiss();
//										}
//									}
//								}).show();
//			} else {
//				run_times += 1;
//				editor.putInt("run_times", run_times);
//				editor.commit();
//				context.startService(new Intent(context, AppCS.class));
//			}
//		}
	}
}
