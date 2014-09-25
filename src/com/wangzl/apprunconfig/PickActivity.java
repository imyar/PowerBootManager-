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
		private ArrayList<HashMap<String, Object>> allowList; // 允许自启动应用信息保存
		private ArrayList<HashMap<String, Object>> forbidList; // 禁止自启动应用信息保存
		private List<ResolveInfo> allowInfoList; // 获取自启动receiver的信息
		private List<ResolveInfo> forbidInfoList; // 获取包含被禁止自启动receiver的信息//禁止自启动应用信息保存
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
																		.toString(adapter.changeposition),// 如果是
																											// 能够自启动的程序，则为后台，并且设置开机0小时后自动启动
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
									Toast.makeText(context, "不能选择重复的项",
											Toast.LENGTH_SHORT).show();
								}
							}
						} else {
							Toast.makeText(context, "不能选择重复的项",
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
			 * 这里的Integer参数对应AsyncTask中的第一个参数 这里的String返回值对应AsyncTask的第三个参数
			 * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
			 * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
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
			 * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
			 * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
			 */
			@Override
			protected void onPostExecute(String result) {
				appAdapter = new AllApplicationAdapter(context, getRecordPage(
						page).getResult());
				listView.setAdapter(appAdapter);

				dialog.setContentView(layout, new LinearLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

			}

			// 该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
			@Override
			protected void onPreExecute() {
				dialog.setContentView(R.layout.dialog_layout);
				Window window = dialog.getWindow();
				WindowManager.LayoutParams lp = window.getAttributes();
				int width = tools.getScreenWidth(context);
				lp.width = (int) (0.6 * width);

				TextView titleTxtv = (TextView) dialog
						.findViewById(R.id.tvLoad);

				titleTxtv.setText("查找应用中...");

			}

			/**
			 * 这里的Intege参数对应AsyncTask中的第二个参数 在doInBackground方法当中，，
			 * 每次调用publishProgress方法都会触发onProgressUpdate执行
			 * onProgressUpdate是在UI线程中执行，所有可以对UI空间进行操作
			 */
			@Override
			protected void onProgressUpdate(Integer... values) {
				// int vlaue = values[0];
				// progressBar.setProgress(vlaue);
			}
		}

		/**
		 * 获得手机上安装的非系统软件
		 * 
		 * @return ArrayList<AppInfo>
		 */
		public ArrayList<AppInfo> getAllAppinfo() {
			ArrayList<AppInfo> appList = new ArrayList<AppInfo>(); // 用来存储获取的应用信息数据
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
						appList.add(tmpInfo);// 如果非系统应用，则添加至appList
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
			ArrayList<AppInfo> appList = new ArrayList<AppInfo>(); // 用来存储获取的应用信息数据
			ArrayList<AppInfo> packages = appInfos;
			int all_packages = appInfos.size();
			if (pageSize > all_packages) {
				for (int i = 0; i < all_packages; i++) {
					AppInfo tmpInfo = packages.get(i);
					appList.add(tmpInfo);// 如果非系统应用，则添加至appList
				}
			} else {
				for (int i = pageNo * pageSize; i < pageNo * pageSize// 每页显示7项，用于分页
						+ pageSize; i++) {
					if (i < pageCount) {
						AppInfo tmpInfo = packages.get(i);
						appList.add(tmpInfo);// 如果非系统应用，则添加至appList
					}
				}

			}
			page.setResult(appList);
			return page;
		}

		// allowList的更新操作
		public void updateAllowList() {
			// 获取自启动receiver的信息
			allowInfoList = mPackageManager.queryBroadcastReceivers(intent,
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
			String appPackageName = null;
			Object icon = null;
			// 获取allowInfoList中第一个receiver对应的应用的名称
			if (allowInfoList.size() > 0) {
				appName = mPackageManager.getApplicationLabel(
						allowInfoList.get(0).activityInfo.applicationInfo)
						.toString();
				// 获取allowInfoList中第一个receiver对应的应用的包名和receiver名称，格式为"package/receiver"
				packageReceiver = allowInfoList.get(0).activityInfo.packageName
						+ "/" + allowInfoList.get(0).activityInfo.name;
				appPackageName = allowInfoList.get(0).activityInfo.packageName;
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
				// 将allowInfoList中的最后一个应用信息保存到allowList中
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

		// forbidList的更新操作
		public void updateForbidList() {
			// 获取包含被禁止自启动receiver的信息
			forbidInfoList = mPackageManager.queryBroadcastReceivers(intent,
					PackageManager.GET_DISABLED_COMPONENTS);
			int k = 0;
			// 去除系统应用receiver以及允许自启动的receiver
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
				// 获取forbidInfoList中第一个receiver对应的应用的包名和receiver名称，格式为"package/receiver"
				packageReceiver = forbidInfoList.get(0).activityInfo.packageName
						+ "/" + forbidInfoList.get(0).activityInfo.name;
				appPackageName = forbidInfoList.get(0).activityInfo.packageName;
				// 获取forbidInfoList中第一个receiver对应的应用的图标信息
				icon = mPackageManager
						.getApplicationIcon(forbidInfoList.get(0).activityInfo.applicationInfo);
				for (int i = 1; i < forbidInfoList.size(); i++) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					// 由于一个应用可能包含多个receiver，需要将这些receiver和对应的应用名称放入同一个map中，对于这些不同的receiver用";"隔开，以便之后用split方法取出
					if (appName.equals(mPackageManager.getApplicationLabel(
							forbidInfoList.get(i).activityInfo.applicationInfo)
							.toString())) {
						packageReceiver = packageReceiver
								+ ";"
								+ forbidInfoList.get(i).activityInfo.packageName
								+ "/" + forbidInfoList.get(i).activityInfo.name;

						// 如果当前的receiver和之前的receiver对应的是不同的应用，那么将之前的应用信息保存到map中，然后存储到forbidList中。
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
				// 将forbidInfoList中的最后一个应用信息保存到forbidList中,position+1与forbidInfoList大小相等时，表示
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