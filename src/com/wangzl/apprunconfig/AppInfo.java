package com.wangzl.apprunconfig;

import android.graphics.drawable.Drawable;

public class AppInfo {

	private String appLabel;
	private Drawable appIcon;
	private String pkgName;
	private boolean isFront;
	private String packageReceiver;

	public String getPackageReceiver() {
		return packageReceiver;
	}

	public void setPackageReceiver(String packageReceiver) {
		this.packageReceiver = packageReceiver;
	}

	public boolean isFront() {
		return isFront;
	}

	public void setFront(boolean isFront) {
		this.isFront = isFront;
	}

	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	private int after_time;

	public int getAfter_time() {
		return after_time;
	}

	public void setAfter_time(int after_time) {
		this.after_time = after_time;
	}

	private int range;

	public AppInfo() {
	}

	public String getAppLabel() {
		return appLabel;
	}

	public void setAppLabel(String appName) {
		this.appLabel = appName;
	}

	public Drawable getAppIcon() {
		return appIcon;
	}

	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}

	public String getPkgName() {
		return pkgName;
	}

	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}
}