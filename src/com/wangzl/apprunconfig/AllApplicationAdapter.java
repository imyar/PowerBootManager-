package com.wangzl.apprunconfig;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AllApplicationAdapter extends BaseAdapter {

	public ArrayList<AppInfo> mlistAppInfo = null;
	
	ViewHolder holder = null;
	LayoutInflater infater = null;
	int i_time;
	Context context;

	public AllApplicationAdapter(Context context, ArrayList<AppInfo> list) {
		infater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mlistAppInfo = list;
		this.context = context;
		// for (int i = 0; i < apps.size(); i++) {
		// mChecked.add(sp.getBoolean("ck" + Integer.toString(i), false));
		// B1.add(sp.getBoolean("b1" + Integer.toString(i), false));
		// B2.add(sp.getBoolean("b2" + Integer.toString(i), false));
		// String[] timeS = sp.getString(apps.get(i).getPkgName(), "").split(
		// ",");
		// if (sp.getBoolean("ck" + Integer.toString(i), false)) {
		// set.add(apps.get(i).getPkgName());
		// editor.putString(apps.get(i).getPkgName(), timeS[0] + ","
		// + timeS[1]);
		// editor.putString("after" + Integer.toString(i), "程序将于 "
		// + timeS[0] + "小时，" + timeS[1] + "分钟之后执行");
		// list_after.add(sp.getString("after" + Integer.toString(i), ""));
		// } else {
		// editor.putString(apps.get(i).getPkgName(), apps.get(i)
		// .getAfter_hour() + "," + apps.get(i).getAfter_minute());
		// editor.putString("after" + Integer.toString(i), "");
		// list_after.add("");
		// }
		// }
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

	@Override
	public View getView(int position, View convertview, ViewGroup arg2) {
		View view = null;

		if (convertview == null || convertview.getTag() == null) {
			view = infater.inflate(R.layout.all_list_layout, null);
			holder = new ViewHolder(view);
			view.setTag(holder);
		} else {
			view = convertview;
			holder = (ViewHolder) convertview.getTag();
		}

		final AppInfo appInfo = (AppInfo) getItem(position);
		holder.appIcon.setImageDrawable(appInfo.getAppIcon());
		holder.tvAppLabel.setText(appInfo.getAppLabel());

//		holder.btnAdd.setOnClickListener(new lvButtonListener(position));
//		holder.btnRemove.setOnClickListener(new lvButtonListener(position));
//		holder.btnDelete.setOnClickListener(new lvButtonListener(position));
//		holder.btnFindapp.setOnClickListener(new lvButtonListener(position));

		return view;
	}

	class ViewHolder {
		ImageView appIcon;
		TextView tvAppLabel;

		public ViewHolder(View view) {
			this.appIcon = (ImageView) view.findViewById(R.id.a_imgApp);
			this.tvAppLabel = (TextView) view.findViewById(R.id.a_tvAppLabel);
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
//			if (vid == holder.btnDelete.getId()) {
//				deleteItem(position);
//			} else if (vid == holder.btnAdd.getId()) {
//				addItem(position);
//			} else if (vid == holder.btnRemove.getId()) {
//				removeItem(position);
//			} else if (vid == holder.btnFindapp.getId()) {
//				find(position);
//			} else if (vid == holder.btnSwitch.getId()) {
//
//			}

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