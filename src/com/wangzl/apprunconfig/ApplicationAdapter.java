package com.wangzl.apprunconfig;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ApplicationAdapter extends BaseAdapter {

	public List<AppInfo> mlistAppInfo = null;

	public SharedPreferences getSp() {
		return sp;
	}

	public void setSp(SharedPreferences sp) {
		this.sp = sp;
	}

	private SharedPreferences sp;

	public Editor getEditor() {
		return editor;
	}

	public void setEditor(Editor editor) {
		this.editor = editor;
	}

	private Editor editor;
	ViewHolder holder = null;
	LayoutInflater infater = null;
	public int changeposition;
	Context context;
	ArrayList<AppInfo> list = null;
	Handler handler;
	Page page;

	public ApplicationAdapter(Context context, List<AppInfo> apps) {
		infater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mlistAppInfo = apps;
		this.context = context;
		sp = context.getSharedPreferences("runApp", Context.MODE_PRIVATE);
		editor = sp.edit();
	}

	@Override
	public int getCount() {
		return mlistAppInfo.size();
	}

	@Override
	public Object getItem(int position) {
		return mlistAppInfo.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public void deleteItem(final int position) {

		CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);

		customBuilder.setMessage("你确定要取消运行当前程序吗？")
				.setNegativeButton("", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).setPositiveButton("", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						delete(position);
						dialog.dismiss();
					}
				});
		Dialog dialog = customBuilder.create();
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER);
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();
		float density = dm.density; // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
		// int densityDPI = dm.densityDpi; // 屏幕密度（每寸像素：120/160/240/320）

		int screenWidth = dm.widthPixels; // 得到实际尺寸
		int screenHeight = dm.heightPixels; // 屏幕高（像素，如：800px）

		lp.height = screenHeight / 2; // 高度
		lp.width = screenWidth / 2;
		dialogWindow.setAttributes(lp);
		dialog.show();
	}

	private void find(final int position, final int method) {
		changeposition = position;
		final PickActivity.Builder customBuilder = new PickActivity.Builder(
				context, method);

		customBuilder.setForwardButton("",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

					}
				}).setNextButton("", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				// customBuilder.appAdapter.mlistAppInfo = list;
				// customBuilder.appAdapter.notifyDataSetChanged();
			}
		});

		Dialog dialog = customBuilder.create();
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER);
		DisplayMetrics dm = new DisplayMetrics();
		dm = context.getResources().getDisplayMetrics();
		float density = dm.density; // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
		// int densityDPI = dm.densityDpi; // 屏幕密度（每寸像素：120/160/240/320）

		float screenWidth = dm.widthPixels;// 得到实际尺寸
		float screenHeight = dm.heightPixels; // 屏幕高（像素，如：800px）

		lp.height = (int) screenHeight; // 高度
		lp.width = (int) screenWidth;
		dialogWindow.setAttributes(lp);
		dialog.show();

		// switch (method) {
		// case 0:
		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// list = customBuilder.getRecordPage(page).getResult();
		// handler.sendEmptyMessage(0);
		// }
		// }).start();
		//
		//
		// break;
		// case 1:
		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// list = customBuilder.getRecordPage1(page).getResult();
		// handler.sendEmptyMessage(0);
		// }
		// }).start();
		// break;
		// default:
		// break;
		// }
	}

	private void delete(int position) {
		// mlistAppInfo.remove(position);
		// this.notifyDataSetChanged();
		AppInfo appInfo = mlistAppInfo.get(position);
		appInfo.setAppIcon(null);
		appInfo.setAppLabel("");

		editor.putString("app" + Integer.toString(position), "");
		// editor.putInt("appTime" + Integer.toString(position), 25);
		editor.commit();

		this.notifyDataSetChanged();
	}

	/*
	 * +
	 */
	public void addItemValue(int position) {
		int t_time = mlistAppInfo.get(position).getAfter_time();
		t_time += 1;
		mlistAppInfo.get(position).setAfter_time(t_time);
		this.notifyDataSetChanged();
		editor.putInt("appTime" + Integer.toString(position), t_time);
		editor.commit();
	}

	/*
	 * -
	 */
	public void removeItemValue(int position) {
		int t_time = mlistAppInfo.get(position).getAfter_time();
		if (t_time > 0) {
			t_time -= 1;
			mlistAppInfo.get(position).setAfter_time(t_time);
			this.notifyDataSetChanged();
			editor.putInt("appTime" + Integer.toString(position), t_time);
			editor.commit();
		}
	}

	private void switchF(int position) {

		Process process = null;
		try {
			process = Runtime.getRuntime().exec("su"); // 切换到root帐号
			Toast.makeText(context, "后台程序只支持能够自启动的程序，请重新选择程序",
					Toast.LENGTH_SHORT).show();
			if (mlistAppInfo.get(position).isFront()) {
				mlistAppInfo.get(position).setFront(false);
				editor.putBoolean("appisFront" + Integer.toString(position),
						false);
			} else {
				mlistAppInfo.get(position).setFront(true);
				editor.putBoolean("appisFront" + Integer.toString(position),
						true);
			}
			editor.commit();
			AppInfo appInfo = mlistAppInfo.get(position);
			appInfo.setAfter_time(5);
			appInfo.setAppIcon(null);
			appInfo.setAppLabel("");
			appInfo.setFront(false);
			appInfo.setPkgName("");
			this.notifyDataSetChanged();
			find(position, 1);
		} catch (Exception e) {
			Toast.makeText(context, "没有root权限，无法设置为后台启动", Toast.LENGTH_SHORT)
					.show();
		}

	}

	@Override
	public View getView(int position, View convertview, ViewGroup arg2) {
		View view = null;

		if (convertview == null || convertview.getTag() == null) {
			view = infater.inflate(R.layout.list_layout, null);
			holder = new ViewHolder(view);
			view.setTag(holder);
		} else {
			view = convertview;
			holder = (ViewHolder) convertview.getTag();
		}

		final AppInfo appInfo = (AppInfo) getItem(position);
		holder.appIcon.setImageDrawable(appInfo.getAppIcon());
		holder.tvAppLabel.setText(appInfo.getAppLabel());
		holder.tvNum.setText(Integer.toString(appInfo.getRange()));
		holder.tvTime.setText("  " + Integer.toString(appInfo.getAfter_time())
				+ " 秒" + " ");
		if (appInfo.isFront()) {// qiantai
			holder.btnSwitch.setBackgroundResource(R.drawable.frontbackup01);
		} else {// 后台
			holder.btnSwitch.setBackgroundResource(R.drawable.frontbackup03);
		}

		holder.btnSwitch.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

		holder.btnAdd.setOnClickListener(new lvButtonListener(position));
		holder.btnRemove.setOnClickListener(new lvButtonListener(position));
		holder.btnDelete.setOnClickListener(new lvButtonListener(position));
		holder.btnFindapp.setOnClickListener(new lvButtonListener(position));
		holder.tvNum.setOnClickListener(new lvButtonListener(position));
		holder.btnSwitch.setOnClickListener(new lvButtonListener(position));
		return view;
	}

	class ViewHolder {
		TextView tvNum;

		Button btnAdd;
		TextView tvTime;
		Button btnRemove;

		Button btnSwitch;
		Button btnDelete;
		Button btnFindapp;

		ImageView appIcon;
		TextView tvAppLabel;

		public ViewHolder(View view) {
			this.appIcon = (ImageView) view.findViewById(R.id.imgApp);
			this.tvAppLabel = (TextView) view.findViewById(R.id.tvAppLabel);

			this.tvNum = (TextView) view.findViewById(R.id.tvNum);

			this.btnAdd = (Button) view.findViewById(R.id.btn_add);
			this.tvTime = (TextView) view.findViewById(R.id.txt_time);
			this.btnRemove = (Button) view.findViewById(R.id.btn_remove);

			this.btnSwitch = (Button) view.findViewById(R.id.btn_switch);
			this.btnDelete = (Button) view.findViewById(R.id.btn_delete);
			this.btnFindapp = (Button) view.findViewById(R.id.btn_findapp);
		}
	}

	class lvButtonListener implements OnClickListener {
		private int position;

		lvButtonListener(int pos) {
			position = pos;
		}

		@Override
		public void onClick(View v) {
			int vid = v.getId();
			if (vid == holder.btnDelete.getId()) {
				deleteItem(position);
			} else if (vid == holder.btnAdd.getId()) {
				addItemValue(position);
			} else if (vid == holder.btnRemove.getId()) {
				removeItemValue(position);
			} else if (vid == holder.btnFindapp.getId()) {
				find(position, 0);
			} else if (vid == holder.btnSwitch.getId()) {
				switchF(position);
			}
			// else if (vid == holder.tvNum.getId()) {
			// if (position == mlistAppInfo.size() - 1) {
			//
			// }
			// }

		}
	}

	//
	// /**
	// * 获得手机上安装的非系统软件
	// *
	// * @return ArrayList<AppInfo>
	// */
	// public ArrayList<AppInfo> getAllAppinfo() {
	// ArrayList<AppInfo> appList = new ArrayList<AppInfo>(); // 用来存储获取的应用信息数据
	// List<ApplicationInfo> packages = context.getPackageManager()
	// .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
	//
	//
	// for (int i = 0; i < packages.size(); i++) {
	// ApplicationInfo packageInfo = packages.get(i);
	// AppInfo tmpInfo = new AppInfo();
	// tmpInfo.setAppLabel(packageInfo.loadLabel(
	// context.getPackageManager()).toString());
	// tmpInfo.setPkgName(packageInfo.packageName);
	//
	// tmpInfo.setAppIcon(packageInfo
	// .loadIcon(context.getPackageManager()));
	// // Only display the non-system app info
	// if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
	// appList.add(tmpInfo);// 如果非系统应用，则添加至appList
	// }
	// }
	// return appList;
	// }

	// private void initListView(ArrayList<AppInfo> appInfos) {
	// loadMoreView = getLayoutInflater().inflate(R.layout.load_more, null);
	// loadHeaderView = getLayoutInflater()
	// .inflate(R.layout.load_header, null);
	// listView = (ListView) findViewById(R.id.listView1); // 获取id是list的ListView
	// loadForwardButton = (Button) loadMoreView.findViewById(R.id.btnForward);
	// loadNexButton = (Button) loadMoreView.findViewById(R.id.btnNext);
	//
	// // listView.addFooterView(loadMoreView); // 设置列表底部视图
	// // listView.addHeaderView(loadHeaderView); // 设置列表底部视图
	// //
	//
	// appAdapter = new ApplicationAdapter(this, appInfos);
	//
	// listView.setAdapter(appAdapter);
	// listView.setOnItemLongClickListener(new OnItemLongClickListener() {
	//
	// @Override
	// public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
	// final int position, long arg3) {
	// // layout.setVisibility(View.VISIBLE);
	//
	// ScaleAnimation scaleAnimation = new ScaleAnimation(0.1f, 1.0f,
	// 0.1f, 1.0f);
	// // 设置动画时间
	// scaleAnimation.setDuration(500);
	// // layout.startAnimation(scaleAnimation);
	//
	// return true;
	// }
	// });

}