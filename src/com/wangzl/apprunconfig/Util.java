package com.wangzl.apprunconfig;

import android.content.Context;
import android.widget.Toast;

public class Util {

	// public static void showTips(Context context) {
	//
	// CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
	// customBuilder.setTitle("退出？").setMessage("您真的决定退出程序")
	// .setNegativeButton("否", new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int which) {
	// dialog.dismiss();
	// }
	// })
	// .setPositiveButton("是的", new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int which) {
	//
	// System.exit(0);
	// }
	// });
	// customBuilder.create().show();
	//
	// }

	public static void showToast(Context context, String message, int timelen) {
		int len = 0;
		switch (timelen) {
		case 0:
			len = Toast.LENGTH_SHORT;
			break;
		case 1:
			len = Toast.LENGTH_LONG;
			break;
		default:
			break;
		}
		Toast.makeText(context, message, len).show();
	}
}
