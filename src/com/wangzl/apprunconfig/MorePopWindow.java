package com.wangzl.apprunconfig;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

public class MorePopWindow extends PopupWindow implements OnClickListener {
	private View conentView;
	private Activity context;

	protected static final int START = 0; // 显示progressdiaglog
	protected static final int STOP = 1; // 关闭progressdiaglog
	protected static final int PROCESS = 2; // progressdiaglog
	private ProgressDialog progressDialog;

	public MorePopWindow(Activity context) {
		this.context = context;

		progressDialog = new ProgressDialog(context);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // 设置progressdiaglog风格为圆形进度条
		progressDialog.setTitle("提示"); // 设置progressdiaglog标题
		progressDialog.setMessage("优化中...");
		progressDialog.setCancelable(false);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		conentView = inflater.inflate(R.layout.more_popup_dialog, null);
		int h = context.getWindowManager().getDefaultDisplay().getHeight();
		int w = context.getWindowManager().getDefaultDisplay().getWidth();
		// 设置SelectPicPopupWindow的View
		this.setContentView(conentView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(w / 2 + 50);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		// 刷新状态
		this.update();
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0000000000);
		// 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
		this.setBackgroundDrawable(dw);
		// mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.AnimationPreview);

		LinearLayout layout_about = (LinearLayout) conentView
				.findViewById(R.id.layout_about);
		LinearLayout layout_removeall = (LinearLayout) conentView
				.findViewById(R.id.layout_removeall);
		LinearLayout layout_exit = (LinearLayout) conentView
				.findViewById(R.id.layout_exit);

		layout_about.setOnClickListener(this);
		layout_exit.setOnClickListener(this);
		layout_removeall.setOnClickListener(this);

	}

	public void showPopupWindow(View parent) {
		if (!this.isShowing()) {
			this.showAsDropDown(parent, parent.getLayoutParams().width / 2, 18);
		} else {
			this.dismiss();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_removeall:
			Process process1 = null;
			try {
				process1 = Runtime.getRuntime().exec("su"); // 切换到root帐号
				ArrayList<HashMap<String, Object>> atuolist = updateAllowList();
				if (atuolist.size() > 0) {
					remove_all(atuolist);
				} else {
					Toast.makeText(context, "您手机没有自启动程序，无需优化",
							Toast.LENGTH_SHORT).show();
				}

			} catch (Exception e) {
				Toast.makeText(context, "没有root权限!无法执行操作", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		case R.id.layout_about:
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
			break;
		case R.id.layout_exit:
			UIHelper.Exit(context);
			break;
		default:
			break;
		}

	}

	private void remove_all(final ArrayList<HashMap<String, Object>> allowList) {
		new Thread() {
			public void run() {
				Message msg = new Message();
				msg.what = START;
				// 对mHandler发送msg，提示开始显示进度条
				mHandler.sendMessage(msg);
				String cmd;
				for (int i = 0; i < allowList.size(); i++) {

					// 获取该应用包含的packagereceiver，格式为”package/receiver“
					String packageReceiverList[] = allowList.get(i)
							.get("packageReceiver").toString().split(";");
					// 发送当前优化的应用名称
					Message msg1 = new Message();
					msg1.obj = allowList.get(i).get("appName");
					msg1.what = PROCESS;
					mHandler.sendMessage(msg1);
					// disable这些receiver
					for (int j = 0; j < packageReceiverList.length; j++) {
						cmd = "pm disable " + packageReceiverList[j];
						// 部分receiver包含$符号，需要做进一步处理，用"$"替换掉$
						cmd = cmd.replace("$", "\"" + "$" + "\"");
						// 执行命令
						execCmd(cmd);

					}
				}
				Message msg2 = new Message();
				msg2.what = STOP;
				// 对mHandler发送msg，提示关闭进度条
				mHandler.sendMessage(msg2);
			}
		}.start();

	}

	// allowList的更新操作
	public ArrayList<HashMap<String, Object>> updateAllowList() {
		ArrayList<HashMap<String, Object>> allowList = new ArrayList<HashMap<String, Object>>();
		PackageManager mPackageManager = context.getPackageManager();
		// 获取自启动receiver的信息
		List<ResolveInfo> allowInfoList = mPackageManager
				.queryBroadcastReceivers(new Intent(
						Intent.ACTION_BOOT_COMPLETED),
						PackageManager.GET_RECEIVERS);
		int k = 0;
		// 去除系统应用receiver
		while (k < allowInfoList.size()) {
			if ((allowInfoList.get(k).activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1
					|| (allowInfoList.get(k).activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1) {
				allowInfoList.remove(k);
			} else
				k++;
		}
		// 清空allowList
		allowList.clear();
		String appName = null;
		String packageReceiver = null;
		Object icon = null;
		// 获取allowInfoList中第一个receiver对应的应用的名称
		if (allowInfoList.size() > 0) {
			appName = mPackageManager.getApplicationLabel(
					allowInfoList.get(0).activityInfo.applicationInfo)
					.toString();
			// 获取allowInfoList中第一个receiver对应的应用的包名和receiver名称，格式为"package/receiver"
			packageReceiver = allowInfoList.get(0).activityInfo.packageName
					+ "/" + allowInfoList.get(0).activityInfo.name;
			// 获取allowInfoList中第一个receiver对应的应用的图标信息
			icon = mPackageManager
					.getApplicationIcon(allowInfoList.get(0).activityInfo.applicationInfo);
			for (int i = 1; i < allowInfoList.size(); i++) {
				// 保存应用信息
				HashMap<String, Object> map = new HashMap<String, Object>();
				// 由于一个应用可能包含多个receiver，需要将这些receiver和对应的应用名称放入同一个map中，对于这些不同的receiver用";"隔开，以便之后用split方法取出
				if (appName.equals(mPackageManager.getApplicationLabel(
						allowInfoList.get(i).activityInfo.applicationInfo)
						.toString())) {
					packageReceiver = packageReceiver + ";"
							+ allowInfoList.get(i).activityInfo.packageName
							+ "/" + allowInfoList.get(i).activityInfo.name;
					// 如果当前的receiver和之前的receiver对应的是不同的应用，那么将之前的应用信息保存到map中，然后存储到allowList中。
				} else {
					map.put("icon", icon);
					map.put("appName", appName);
					map.put("packageReceiver", packageReceiver);
					allowList.add(map);
					packageReceiver = allowInfoList.get(i).activityInfo.packageName
							+ "/" + allowInfoList.get(i).activityInfo.name;
					appName = mPackageManager.getApplicationLabel(
							allowInfoList.get(i).activityInfo.applicationInfo)
							.toString();
					icon = mPackageManager.getApplicationIcon(allowInfoList
							.get(i).activityInfo.applicationInfo);
				}
			}
			// 将allowInfoList中的最后一个应用信息保存到allowList中
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("icon", icon);
			map.put("appName", appName);
			map.put("packageReceiver", packageReceiver);
			if (!packageReceiver.equals("com.wangzl.apprunconfig/com.wangzl.apprunconfig.BootBroadcastReceiver")) {
				allowList.add(map);
			}
		}
		return allowList;
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

	// 用于配合thread的UI更新，包括显示进度条，关闭进度条，刷新listview显示
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case START:
				progressDialog.show();
				break;
			case STOP:
				progressDialog.cancel();
				// if(flag == 0)
				// +.refresh(allowList);
				// else
				// mAdapter.refresh(forbidList);

				break;
			case PROCESS:
				progressDialog.setMessage(msg.obj.toString() + "优化中...");
				break;

			}
		}
	};

}
