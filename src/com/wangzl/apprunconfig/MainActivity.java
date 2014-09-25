package com.wangzl.apprunconfig;

import java.io.DataOutputStream;
import java.util.ArrayList;

import com.wangzl.apprunconfig.R.string;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.waps.AppConnect;

public class MainActivity extends BaseActivity implements OnClickListener {

	private ListView listView;
	private Context context;

	public static ApplicationAdapter appAdapter;
    private static String RECEIVER="com.wangzl.apprunconfig/com.wangzl.apprunconfig.BootBroadcastReceiver";
	private Button setButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = MainActivity.this;

		AppConnect.getInstance("706e67d8c8946244ce83fd99e3368f58", "appChina",
				this);

     	String	cmd = "pm enable " + RECEIVER;
		// 部分receiver包含$符号，需要做进一步处理，用"$"替换掉$
		cmd = cmd.replace("$", "\"" + "$" + "\"");
		// 执行命令
		execCmd(cmd);

		// requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
		// getWindow().setFlags(WindowManager.LayoutParams.FILL_PARENT,
		// WindowManager.LayoutParams.FILL_PARENT); //设置全屏

		setButton = (Button) findViewById(R.id.btnSet);
		setButton.setOnClickListener(this);
		// SharedPreferences sPreferences =
		// context.getSharedPreferences("runApp",
		// Context.MODE_PRIVATE);
		// final Editor editor = sPreferences.edit();
		// int run_times = sPreferences.getInt("run_times", 0);
		// boolean isActivityed = sPreferences.getBoolean("isActivityed",
		// false);
		// if (!isActivityed) {
		// if (run_times > 30) {
		// final EditText inputServer = new EditText(context);
		// new AlertDialog.Builder(context)
		// .setTitle(R.string.about_title)
		// .setMessage("测试次数用完，请购买")
		// .setView(inputServer)
		// .setNeutralButton(android.R.string.ok,
		// new DialogInterface.OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog,
		// int which) {
		// String chkString = inputServer
		// .getText().toString();
		// if (chkString
		// .equals("wkkj-0100-093x-tiuj")) {
		// Toast.makeText(context,
		// "恭喜，程序已经激活",
		// Toast.LENGTH_LONG).show();
		// editor.putBoolean("isActivityed",
		// true);
		// editor.commit();
		// dialog.dismiss();
		// } else {
		// Toast.makeText(context, "请购买正版程序",
		// Toast.LENGTH_LONG).show();
		// MainActivity.this.finish();
		// }
		// }
		// }).show();
		// } else {
		// run_times += 1;
		// editor.putInt("run_times", run_times);
		// editor.commit();
		// }
		// }

		// if (savedInstanceState == null) {
		// getSupportFragmentManager().beginTransaction()
		// .add(R.id.container, new PlaceholderFragment()).commit();
		// }

		initListView(getAllAppinfo());

		// AppConnect.getInstance(this).initPopAd(this);
		// AppConnect.getInstance(this).showPopAd(this);
		// 加入广告
		LinearLayout adlayout = (LinearLayout) findViewById(R.id.AdLinearLayout);
		AppConnect.getInstance(this).showBannerAd(this, adlayout);

	}

	void startActivitySafely(Intent intent) {
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, "未发现该activity", Toast.LENGTH_SHORT).show();
		} catch (SecurityException e) {
			Toast.makeText(this, "SecurityException", Toast.LENGTH_SHORT)
					.show();
		}
	}

	private AppInfo findAppinfo(String packageName) {
		AppInfo appInfo = new AppInfo();
		PackageInfo pi = null;
		try {
			pi = getPackageManager().getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		if (pi != null) {
			// String packageName = ri.activityInfo.packageName;
			appInfo.setAppLabel((pi.applicationInfo
					.loadLabel(getPackageManager()).toString()));
			appInfo.setAppIcon(pi.applicationInfo.loadIcon(getPackageManager()));
		}
		return appInfo;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_about) {
			new AlertDialog.Builder(context)
					.setTitle(R.string.about_title)
					.setMessage(R.string.about_text)
					.setNeutralButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							}).show();
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 初始化5个项
	 * 
	 * @return ArrayList<AppInfo>
	 */
	public ArrayList<AppInfo> getAllAppinfo() {
		ArrayList<AppInfo> appList = new ArrayList<AppInfo>();
		SharedPreferences sPreferences = getSharedPreferences("runApp",
				MODE_PRIVATE);
		for (int i = 0; i < 5; i++) {
			String packageName = sPreferences.getString(
					"app" + Integer.toString(i), "");
			if (!packageName.equals("")) {
				AppInfo tmpInfo = new AppInfo();
				tmpInfo.setRange(i + 1);
				tmpInfo.setAfter_time(sPreferences.getInt(
						"appTime" + Integer.toString(i), 5));
				tmpInfo.setPkgName(packageName);
				tmpInfo.setAppLabel(findAppinfo(packageName).getAppLabel());
				tmpInfo.setFront(sPreferences.getBoolean(
						"appisFront" + Integer.toString(i), true));// 默认为前台程序
				tmpInfo.setAppIcon(findAppinfo(packageName).getAppIcon());
				appList.add(tmpInfo);
			} else {
				AppInfo tmpInfo = new AppInfo();
				tmpInfo.setRange(i + 1);
				tmpInfo.setAfter_time(5);
				tmpInfo.setPkgName("");
				tmpInfo.setAppLabel("");
				tmpInfo.setFront(true);
				tmpInfo.setAppIcon(null);
				appList.add(tmpInfo);
			}
		}
		return appList;
	}

	private void initListView(ArrayList<AppInfo> appInfos) {

		// listView.addFooterView(loadMoreView); // 设置列表底部视图
		// listView.addHeaderView(loadHeaderView); // 设置列表底部视图
		//
		listView = (ListView) findViewById(R.id.listView1);

		appAdapter = new ApplicationAdapter(this, appInfos);

		listView.setAdapter(appAdapter);

		// listView.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
		// long arg3) {
		//
		// // appAdapter.holder.btnAdd.setOnClickListener(new
		// // OnClickListener() {
		// //
		// // @Override
		// // public void onClick(View arg0) {
		// //
		// //
		// // }
		// // });
		// AppInfo appInfo = appAdapter.mlistAppInfo.get(arg2);
		// String lable1 = appInfo.getAppLabel().toString();
		// appInfo.setB2(true);
		// appAdapter.mlistAppInfo.set(arg2, appInfo);
		// for (int i = 0; i < appAdapter.mlistAppInfo.size(); i++) {
		// AppInfo appInfo1 = appAdapter.mlistAppInfo.get(i);
		//
		// String lable2 = appInfo1.getAppLabel().toString();
		// boolean b2 = appInfo1.isB2();
		// if (!lable1.equals(lable2) && b2) {
		// appInfo1.setB2(false);
		// appAdapter.mlistAppInfo.set(i, appInfo1);
		// }
		// }
		// appAdapter.notifyDataSetChanged();
		// Toast.makeText(context, Integer.toString(arg2),
		// Toast.LENGTH_SHORT).show();
		// }
		// });

	}

	@Override
	public void onClick(View arg0) {
		MorePopWindow morePopWindow = new MorePopWindow(MainActivity.this);
		morePopWindow.showPopupWindow(setButton);
	}
	
	// 用root权限执行外部命令"pm disable"和"pm enable"
	public static boolean execCmd(String cmd) {
		Process process = null;
		DataOutputStream os = null;
		try {
			process = Runtime.getRuntime().exec("su"); // 切换到root帐号
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(cmd + "\n");
			os.writeBytes("exit\n");
			os.flush();
			int m = process.waitFor();
			System.out.println(m);
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				process.destroy();
			} catch (Exception e) {
			}
		}
		return true;
	}
}
