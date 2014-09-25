package com.wangzl.apprunconfig;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PickActivity extends Dialog {

	public PickActivity(Context context, int theme) {
		super(context, R.style.Dialog_Fullscreen);
	}

	/**
	 * Helper class for creating a custom dialog
	 */
	public static class Builder {
		private Context context;
		// private String title;
		private String forwardButtonText;
		private String nextButtonText;
		private ArrayList<HashMap<String, Object>> allowList; // ����������Ӧ����Ϣ����
		private ArrayList<HashMap<String, Object>> forbidList; // ��ֹ������Ӧ����Ϣ����
		private List<ResolveInfo> allowInfoList; // ��ȡ������receiver����Ϣ
		private List<ResolveInfo> forbidInfoList; // ��ȡ��������ֹ������receiver����Ϣ//��ֹ������Ӧ����Ϣ����
		private PackageManager mPackageManager;
		private Intent intent;
		private int method;
		ListView listView;
		PickActivity dialog;
		View layout;
		ArrayList<AppInfo> appInfos;

		// private View contentView;

		public Page<AppInfo> getPage() {
			return page;
		}

		public void setPage(Page<AppInfo> page) {
			this.page = page;
		}

		private Page<AppInfo> page;
		public AllApplicationAdapter appAdapter;

		private DialogInterface.OnClickListener forwardButtonClickListener,
				nextButtonClickListener;

		public Builder(Context context, int method) {
			this.context = context;
			this.method = method;
		}

		/**
		 * Set a custom content view for the Dialog. If a message is set, the
		 * contentView is not added to the Dialog...
		 * 
		 * @param v
		 * @return
		 */
		// public Builder setContentView(View v) {
		// this.contentView = v;
		// return this;
		// }

		public Builder setForwardButton(int forwardButtonText,
				DialogInterface.OnClickListener listener) {
			this.forwardButtonText = (String) context
					.getText(forwardButtonText);
			this.forwardButtonClickListener = listener;
			return this;
		}

		public Builder setNextButton(String nextButtonText,
				DialogInterface.OnClickListener listener) {
			this.nextButtonText = nextButtonText;
			this.nextButtonClickListener = listener;
			return this;
		}

		public Builder setNextButton(int nextButtonText,
				DialogInterface.OnClickListener listener) {
			this.nextButtonText = (String) context.getText(nextButtonText);
			this.nextButtonClickListener = listener;
			return this;
		}

		public Builder setForwardButton(String forwardButtonText,
				DialogInterface.OnClickListener listener) {
			this.forwardButtonText = forwardButtonText;
			this.forwardButtonClickListener = listener;
			return this;
		}

		/**
		 * Create the custom dialog
		 */
		public PickActivity create() {

			allowList = new ArrayList<HashMap<String, Object>>();
			forbidList = new ArrayList<HashMap<String, Object>>();
			intent = new Intent(Intent.ACTION_BOOT_COMPLETED);
			mPackageManager = context.getPackageManager();
			page = new Page<AppInfo>();
			page.setPageSize(7);
			page.setAutoCount(false);

			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			dialog = new PickActivity(context, R.style.Dialog);
			layout = inflater.inflate(R.layout.listviews, null);

			listView = (ListView) layout.findViewById(R.id.pa_listview);

			new ProgressBarAsyncTask().execute(1);

			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					dialog.dismiss();

					// ArrayList<AppInfo> apps = new ArrayList<AppInfo>();
					AppInfo appInfo1 = appAdapter.mlistAppInfo.get(arg2);
					// apps.add(appInfo1);

					String cmd = "pm enable " + appInfo1.getPackageReceiver();
					cmd = cmd.replace("$", "\"" + "$" + "\"");
					execCmd(cmd);

					ApplicationAdapter adapter = MainActivity.appAdapter;
					int equal_index = 0;
					String lableString1 = appInfo1.getAppLabel();
					for (int i = 0; i < 5; i++) {
						String lableString = adapter.mlistAppInfo.get(i)
								.getAppLabel();

						if (!lableString.equals(lableString1)) {
							equal_index += 1;
							if (equal_index == 4) {
								if (!lableString.equals(lableString1)) {
									AppInfo appInfo = adapter.mlistAppInfo
											.get(adapter.changeposition);
									appInfo.setAppLabel(appInfo1.getAppLabel());
									appInfo.setAppIcon(appInfo1.getAppIcon());

									boolean flag = true;
									appInfo.setPkgName(appInfo1.getPkgName());

									// appInfo.setAfter_time(25);

									adapter.getEditor()
											.putBoolean(
													"appisFront"
															+ Integer
																	.toString(adapter.changeposition),
													true);
									if (method == 1) {
										adapter.getEditor()
												.putBoolean(
														"appisFront"
																+ Integer
																		.toString(adapter.changeposition),// �����
																											// �ܹ��������ĳ�����Ϊ��̨���������ÿ���0Сʱ���Զ�����
														false);
										flag = false;
										adapter.getEditor()
												.putInt("appTime"
														+ Integer
																.toString(adapter.changeposition),
														0);
									}

									adapter.getEditor()
											.putString(
													"app"
															+ Integer
																	.toString(adapter.changeposition),
													appInfo.getPkgName());

									appInfo.setFront(flag);
									adapter.notifyDataSetChanged();
									adapter.getEditor().commit();
								} else {
									Toast.makeText(context, "����ѡ���ظ�����",
											Toast.LENGTH_SHORT).show();
								}
							}
						} else {
							Toast.makeText(context, "����ѡ���ظ�����",
									Toast.LENGTH_SHORT).show();
							i = 5;
						}
					}

				}
			});

			// set the dialog title
			// ((TextView) layout.findViewById(R.id.title)).setText(title);
			// set the confirm button
			if (forwardButtonText != null) {
				((Button) layout.findViewById(R.id.btnForward))
						.setText(forwardButtonText);
				if (forwardButtonClickListener != null) {
					((Button) layout.findViewById(R.id.btnForward))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									forwardButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_POSITIVE);
									ArrayList<AppInfo> mylist = null;
									if (page.isHasPre()) {
										int index = page.getPrePage();
										page.setPageNo(index);
										mylist = getRecordPage(page)
												.getResult();

										appAdapter.mlistAppInfo = mylist;
										appAdapter.notifyDataSetChanged();
										layout.findViewById(R.id.btnForward)
												.setBackgroundResource(
														R.drawable.lastup);
										layout.findViewById(R.id.btnNext)
												.setBackgroundResource(
														R.drawable.downup);
									} else {
										layout.findViewById(R.id.btnForward)
												.setBackgroundResource(
														R.drawable.lastdown);
										layout.findViewById(R.id.btnNext)
												.setBackgroundResource(
														R.drawable.downup);
									}
								}
							});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.btnForward).setVisibility(View.GONE);
			}
			// set the cancel button
			if (nextButtonText != null) {
				((Button) layout.findViewById(R.id.btnForward))
						.setText(nextButtonText);
				if (nextButtonClickListener != null) {
					((Button) layout.findViewById(R.id.btnNext))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									nextButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_NEGATIVE);

									if (!page.isHasNext()) {
										layout.findViewById(R.id.btnNext)
												.setBackgroundResource(
														R.drawable.nestdown);
										layout.findViewById(R.id.btnForward)
												.setBackgroundResource(
														R.drawable.lastup);
									} else {
										layout.findViewById(R.id.btnNext)
												.setBackgroundResource(
														R.drawable.downup);
										layout.findViewById(R.id.btnForward)
												.setBackgroundResource(
														R.drawable.lastup);
										int index = page.getNextPage();
										page.setPageNo(index);
										ArrayList<AppInfo> mylist = null;

										mylist = getRecordPage(page)
												.getResult();

										appAdapter.mlistAppInfo = mylist;
										appAdapter.notifyDataSetChanged();
									}
								}
							});
				}
			} else {
				// if no confirm button just set the visibility to GONE
				layout.findViewById(R.id.btnNext).setVisibility(View.GONE);
			}

			return dialog;
		}

		class ProgressBarAsyncTask extends AsyncTask<Integer, Integer, String> {

			/**
			 * �����Integer������ӦAsyncTask�еĵ�һ������ �����String����ֵ��ӦAsyncTask�ĵ���������
			 * �÷�������������UI�̵߳��У���Ҫ�����첽�����������ڸ÷����в��ܶ�UI���еĿռ�������ú��޸�
			 * ���ǿ��Ե���publishProgress��������onProgressUpdate��UI���в���
			 */
			@Override
			protected String doInBackground(Integer... params) {

				switch (method) {
				case 0:
					appInfos = getAllAppinfo();
					break;
				case 1:
					updateAllowList();
					updateForbidList();
					appInfos = new ArrayList<AppInfo>();

					for (int i = 0; i < allowList.size(); i++) {
						AppInfo appInfo = new AppInfo();

						appInfo.setAppIcon((Drawable) allowList.get(i).get(
								"icon"));
						appInfo.setAppLabel(allowList.get(i).get("appName")
								.toString());
						appInfo.setPackageReceiver(allowList.get(i)
								.get("packageReceiver").toString());
						// appInfo.setPkgName(allowList.get(i).get("appPackageName")
						// .toString());
						appInfos.add(appInfo);
					}
					for (int i = 0; i < forbidList.size(); i++) {
						AppInfo appInfo = new AppInfo();

						appInfo.setAppIcon((Drawable) forbidList.get(i).get(
								"icon"));
						appInfo.setAppLabel(forbidList.get(i).get("appName")
								.toString());
						appInfo.setPackageReceiver(forbidList.get(i)
								.get("packageReceiver").toString());
						// appInfo.setPkgName(forbidList.get(i).get("appPackageName")
						// .toString());
						appInfos.add(appInfo);
					}
					break;
				default:
					break;
				}

				return "ss";
			}

			/**
			 * �����String������ӦAsyncTask�еĵ�����������Ҳ���ǽ���doInBackground�ķ���ֵ��
			 * ��doInBackground����ִ�н���֮�������У�����������UI�̵߳��� ���Զ�UI�ռ��������
			 */
			@Override
			protected void onPostExecute(String result) {
				appAdapter = new AllApplicationAdapter(context, getRecordPage(
						page).getResult());
				listView.setAdapter(appAdapter);

				dialog.setContentView(layout, new LinearLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

			}

			// �÷���������UI�̵߳���,����������UI�̵߳��� ���Զ�UI�ռ��������
			@Override
			protected void onPreExecute() {
				dialog.setContentView(R.layout.dialog_layout);
				Window window = dialog.getWindow();
				WindowManager.LayoutParams lp = window.getAttributes();
				int width = tools.getScreenWidth(context);
				lp.width = (int) (0.6 * width);

				TextView titleTxtv = (TextView) dialog
						.findViewById(R.id.tvLoad);

				titleTxtv.setText("����Ӧ����...");

			}

			/**
			 * �����Intege������ӦAsyncTask�еĵڶ������� ��doInBackground�������У���
			 * ÿ�ε���publishProgress�������ᴥ��onProgressUpdateִ��
			 * onProgressUpdate����UI�߳���ִ�У����п��Զ�UI�ռ���в���
			 */
			@Override
			protected void onProgressUpdate(Integer... values) {
				// int vlaue = values[0];
				// progressBar.setProgress(vlaue);
			}
		}

		/**
		 * ����ֻ��ϰ�װ�ķ�ϵͳ���
		 * 
		 * @return ArrayList<AppInfo>
		 */
		public ArrayList<AppInfo> getAllAppinfo() {
			ArrayList<AppInfo> appList = new ArrayList<AppInfo>(); // �����洢��ȡ��Ӧ����Ϣ����
			List<ApplicationInfo> packages = context.getPackageManager()
					.getInstalledApplications(
							PackageManager.GET_UNINSTALLED_PACKAGES);

			for (int i = 0; i < packages.size(); i++) {
				ApplicationInfo packageInfo = packages.get(i);
				AppInfo tmpInfo = new AppInfo();
				tmpInfo.setAppLabel(packageInfo.loadLabel(
						context.getPackageManager()).toString());
				tmpInfo.setPkgName(packageInfo.packageName);

				tmpInfo.setAppIcon(packageInfo.loadIcon(context
						.getPackageManager()));
				// Only display the non-system app info
				if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
					if (!packageInfo.packageName
							.equals("com.wangzl.apprunconfig")) {
						appList.add(tmpInfo);// �����ϵͳӦ�ã��������appList
					}
				}
			}
			return appList;
		}

		public Page<AppInfo> getRecordPage(Page<AppInfo> page) {
			int pageSize = page.getPageSize();
			int pageNo = page.getPageNo();

			page.setTotalCount(appInfos.size());
			long pageCount = page.getTotalCount();
			ArrayList<AppInfo> appList = new ArrayList<AppInfo>(); // �����洢��ȡ��Ӧ����Ϣ����
			ArrayList<AppInfo> packages = appInfos;
			int all_packages = appInfos.size();
			if (pageSize > all_packages) {
				for (int i = 0; i < all_packages; i++) {
					AppInfo tmpInfo = packages.get(i);
					appList.add(tmpInfo);// �����ϵͳӦ�ã��������appList
				}
			} else {
				for (int i = pageNo * pageSize; i < pageNo * pageSize// ÿҳ��ʾ7����ڷ�ҳ
						+ pageSize; i++) {
					if (i < pageCount) {
						AppInfo tmpInfo = packages.get(i);
						appList.add(tmpInfo);// �����ϵͳӦ�ã��������appList
					}
				}

			}
			page.setResult(appList);
			return page;
		}

		// allowList�ĸ��²���
		public void updateAllowList() {
			// ��ȡ������receiver����Ϣ
			allowInfoList = mPackageManager.queryBroadcastReceivers(intent,
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
			String appPackageName = null;
			Object icon = null;
			// ��ȡallowInfoList�е�һ��receiver��Ӧ��Ӧ�õ�����
			if (allowInfoList.size() > 0) {
				appName = mPackageManager.getApplicationLabel(
						allowInfoList.get(0).activityInfo.applicationInfo)
						.toString();
				// ��ȡallowInfoList�е�һ��receiver��Ӧ��Ӧ�õİ�����receiver���ƣ���ʽΪ"package/receiver"
				packageReceiver = allowInfoList.get(0).activityInfo.packageName
						+ "/" + allowInfoList.get(0).activityInfo.name;
				appPackageName = allowInfoList.get(0).activityInfo.packageName;
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
						map.put("appPackageName", appPackageName);
						map.put("packageReceiver", packageReceiver);
						if (!packageReceiver
								.equals("com.wangzl.apprunconfig/com.wangzl.apprunconfig.BootBroadcastReceiver")) {
							allowList.add(map);
						}
						packageReceiver = allowInfoList.get(i).activityInfo.packageName
								+ "/" + allowInfoList.get(i).activityInfo.name;
						appName = mPackageManager
								.getApplicationLabel(
										allowInfoList.get(i).activityInfo.applicationInfo)
								.toString();
						appPackageName = allowInfoList.get(i).activityInfo.packageName;
						icon = mPackageManager.getApplicationIcon(allowInfoList
								.get(i).activityInfo.applicationInfo);
					}
				}
				// ��allowInfoList�е����һ��Ӧ����Ϣ���浽allowList��
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("icon", icon);
				map.put("appName", appName);
				map.put("appPackageName", appPackageName);
				map.put("packageReceiver", packageReceiver);
				if (!packageReceiver
						.equals("com.wangzl.apprunconfig/com.wangzl.apprunconfig.BootBroadcastReceiver")) {
					allowList.add(map);
				}

			}
		}

		// forbidList�ĸ��²���
		public void updateForbidList() {
			// ��ȡ��������ֹ������receiver����Ϣ
			forbidInfoList = mPackageManager.queryBroadcastReceivers(intent,
					PackageManager.GET_DISABLED_COMPONENTS);
			int k = 0;
			// ȥ��ϵͳӦ��receiver�Լ�������������receiver
			while (k < forbidInfoList.size()) {
				if ((forbidInfoList.get(k).activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1
						|| (forbidInfoList.get(k).activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1) {
					forbidInfoList.remove(k);
				} else
					k++;
			}
			k = 0;
			while (k < forbidInfoList.size()) {
				ComponentName mComponentName = new ComponentName(
						forbidInfoList.get(k).activityInfo.packageName,
						forbidInfoList.get(k).activityInfo.name);
				if (mPackageManager.getComponentEnabledSetting(mComponentName) != 2)
					forbidInfoList.remove(k);
				else
					k++;
			}
			forbidList.clear();
			String appName = null;
			String appPackageName = null;
			String packageReceiver = null;

			Object icon = null;
			if (forbidInfoList.size() > 0) {
				appName = mPackageManager.getApplicationLabel(
						forbidInfoList.get(0).activityInfo.applicationInfo)
						.toString();
				// ��ȡforbidInfoList�е�һ��receiver��Ӧ��Ӧ�õİ�����receiver���ƣ���ʽΪ"package/receiver"
				packageReceiver = forbidInfoList.get(0).activityInfo.packageName
						+ "/" + forbidInfoList.get(0).activityInfo.name;
				appPackageName = forbidInfoList.get(0).activityInfo.packageName;
				// ��ȡforbidInfoList�е�һ��receiver��Ӧ��Ӧ�õ�ͼ����Ϣ
				icon = mPackageManager
						.getApplicationIcon(forbidInfoList.get(0).activityInfo.applicationInfo);
				for (int i = 1; i < forbidInfoList.size(); i++) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					// ����һ��Ӧ�ÿ��ܰ������receiver����Ҫ����Щreceiver�Ͷ�Ӧ��Ӧ�����Ʒ���ͬһ��map�У�������Щ��ͬ��receiver��";"�������Ա�֮����split����ȡ��
					if (appName.equals(mPackageManager.getApplicationLabel(
							forbidInfoList.get(i).activityInfo.applicationInfo)
							.toString())) {
						packageReceiver = packageReceiver
								+ ";"
								+ forbidInfoList.get(i).activityInfo.packageName
								+ "/" + forbidInfoList.get(i).activityInfo.name;

						// �����ǰ��receiver��֮ǰ��receiver��Ӧ���ǲ�ͬ��Ӧ�ã���ô��֮ǰ��Ӧ����Ϣ���浽map�У�Ȼ��洢��forbidList�С�
					} else {
						map.put("icon", icon);
						map.put("appName", appName);
						map.put("packageReceiver", packageReceiver);
						map.put("appPackageName", appPackageName);
						if (!packageReceiver
								.equals("com.wangzl.apprunconfig/com.wangzl.apprunconfig.BootBroadcastReceiver")) {
							forbidList.add(map);
						}

						packageReceiver = forbidInfoList.get(i).activityInfo.packageName
								+ "/" + forbidInfoList.get(i).activityInfo.name;
						appName = mPackageManager
								.getApplicationLabel(
										forbidInfoList.get(i).activityInfo.applicationInfo)
								.toString();
						appPackageName = forbidInfoList.get(i).activityInfo.packageName;
						icon = mPackageManager
								.getApplicationIcon(forbidInfoList.get(i).activityInfo.applicationInfo);
					}
				}
				// ��forbidInfoList�е����һ��Ӧ����Ϣ���浽forbidList��,position+1��forbidInfoList��С���ʱ����ʾ
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("icon", icon);
				map.put("appName", appName);
				map.put("appPackageName", appPackageName);
				map.put("packageReceiver", packageReceiver);
				if (!packageReceiver
						.equals("com.wangzl.apprunconfig/com.wangzl.apprunconfig.BootBroadcastReceiver")) {
					forbidList.add(map);
				}
			}
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
				process.waitFor();
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
}