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

	protected static final int START = 0; // ��ʾprogressdiaglog
	protected static final int STOP = 1; // �ر�progressdiaglog
	protected static final int PROCESS = 2; // progressdiaglog
	private ProgressDialog progressDialog;

	public MorePopWindow(Activity context) {
		this.context = context;

		progressDialog = new ProgressDialog(context);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // ����progressdiaglog���ΪԲ�ν�����
		progressDialog.setTitle("��ʾ"); // ����progressdiaglog����
		progressDialog.setMessage("�Ż���...");
		progressDialog.setCancelable(false);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		conentView = inflater.inflate(R.layout.more_popup_dialog, null);
		int h = context.getWindowManager().getDefaultDisplay().getHeight();
		int w = context.getWindowManager().getDefaultDisplay().getWidth();
		// ����SelectPicPopupWindow��View
		this.setContentView(conentView);
		// ����SelectPicPopupWindow��������Ŀ�
		this.setWidth(w / 2 + 50);
		// ����SelectPicPopupWindow��������ĸ�
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// ����SelectPicPopupWindow��������ɵ��
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		// ˢ��״̬
		this.update();
		// ʵ����һ��ColorDrawable��ɫΪ��͸��
		ColorDrawable dw = new ColorDrawable(0000000000);
		// ��back���������ط�ʹ����ʧ,������������ܴ���OnDismisslistener �����������ؼ��仯�Ȳ���
		this.setBackgroundDrawable(dw);
		// mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
		// ����SelectPicPopupWindow�������嶯��Ч��
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
				process1 = Runtime.getRuntime().exec("su"); // �л���root�ʺ�
				ArrayList<HashMap<String, Object>> atuolist = updateAllowList();
				if (atuolist.size() > 0) {
					remove_all(atuolist);
				} else {
					Toast.makeText(context, "���ֻ�û�����������������Ż�",
							Toast.LENGTH_SHORT).show();
				}

			} catch (Exception e) {
				Toast.makeText(context, "û��rootȨ��!�޷�ִ�в���", Toast.LENGTH_SHORT)
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
				// ��mHandler����msg����ʾ��ʼ��ʾ������
				mHandler.sendMessage(msg);
				String cmd;
				for (int i = 0; i < allowList.size(); i++) {

					// ��ȡ��Ӧ�ð�����packagereceiver����ʽΪ��package/receiver��
					String packageReceiverList[] = allowList.get(i)
							.get("packageReceiver").toString().split(";");
					// ���͵�ǰ�Ż���Ӧ������
					Message msg1 = new Message();
					msg1.obj = allowList.get(i).get("appName");
					msg1.what = PROCESS;
					mHandler.sendMessage(msg1);
					// disable��Щreceiver
					for (int j = 0; j < packageReceiverList.length; j++) {
						cmd = "pm disable " + packageReceiverList[j];
						// ����receiver����$���ţ���Ҫ����һ��������"$"�滻��$
						cmd = cmd.replace("$", "\"" + "$" + "\"");
						// ִ������
						execCmd(cmd);

					}
				}
				Message msg2 = new Message();
				msg2.what = STOP;
				// ��mHandler����msg����ʾ�رս�����
				mHandler.sendMessage(msg2);
			}
		}.start();

	}

	// allowList�ĸ��²���
	public ArrayList<HashMap<String, Object>> updateAllowList() {
		ArrayList<HashMap<String, Object>> allowList = new ArrayList<HashMap<String, Object>>();
		PackageManager mPackageManager = context.getPackageManager();
		// ��ȡ������receiver����Ϣ
		List<ResolveInfo> allowInfoList = mPackageManager
				.queryBroadcastReceivers(new Intent(
						Intent.ACTION_BOOT_COMPLETED),
						PackageManager.GET_RECEIVERS);
		int k = 0;
		// ȥ��ϵͳӦ��receiver
		while (k < allowInfoList.size()) {
			if ((allowInfoList.get(k).activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1
					|| (allowInfoList.get(k).activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1) {
				allowInfoList.remove(k);
			} else
				k++;
		}
		// ���allowList
		allowList.clear();
		String appName = null;
		String packageReceiver = null;
		Object icon = null;
		// ��ȡallowInfoList�е�һ��receiver��Ӧ��Ӧ�õ�����
		if (allowInfoList.size() > 0) {
			appName = mPackageManager.getApplicationLabel(
					allowInfoList.get(0).activityInfo.applicationInfo)
					.toString();
			// ��ȡallowInfoList�е�һ��receiver��Ӧ��Ӧ�õİ�����receiver���ƣ���ʽΪ"package/receiver"
			packageReceiver = allowInfoList.get(0).activityInfo.packageName
					+ "/" + allowInfoList.get(0).activityInfo.name;
			// ��ȡallowInfoList�е�һ��receiver��Ӧ��Ӧ�õ�ͼ����Ϣ
			icon = mPackageManager
					.getApplicationIcon(allowInfoList.get(0).activityInfo.applicationInfo);
			for (int i = 1; i < allowInfoList.size(); i++) {
				// ����Ӧ����Ϣ
				HashMap<String, Object> map = new HashMap<String, Object>();
				// ����һ��Ӧ�ÿ��ܰ������receiver����Ҫ����Щreceiver�Ͷ�Ӧ��Ӧ�����Ʒ���ͬһ��map�У�������Щ��ͬ��receiver��";"�������Ա�֮����split����ȡ��
				if (appName.equals(mPackageManager.getApplicationLabel(
						allowInfoList.get(i).activityInfo.applicationInfo)
						.toString())) {
					packageReceiver = packageReceiver + ";"
							+ allowInfoList.get(i).activityInfo.packageName
							+ "/" + allowInfoList.get(i).activityInfo.name;
					// �����ǰ��receiver��֮ǰ��receiver��Ӧ���ǲ�ͬ��Ӧ�ã���ô��֮ǰ��Ӧ����Ϣ���浽map�У�Ȼ��洢��allowList�С�
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
			// ��allowInfoList�е����һ��Ӧ����Ϣ���浽allowList��
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

	// ��rootȨ��ִ���ⲿ����"pm disable"��"pm enable"
	public static boolean execCmd(String cmd) {
		Process process = null;
		DataOutputStream os = null;
		try {
			process = Runtime.getRuntime().exec("su"); // �л���root�ʺ�
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

	// �������thread��UI���£�������ʾ���������رս�������ˢ��listview��ʾ
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
				progressDialog.setMessage(msg.obj.toString() + "�Ż���...");
				break;

			}
		}
	};

}
