package com.zdnuist.apkmanager.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.zdnuist.apkmanager.entity.ApkInfo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;

public class AppUtil {

	/**
	 * 描述：卸载程序.
	 * 
	 * @param context
	 *            the context
	 * @param packageName
	 *            包名
	 */
	public static void uninstallApk(Context context, String packageName) {
		Intent intent = new Intent(Intent.ACTION_DELETE);
		Uri packageURI = Uri.parse("package:" + packageName);
		intent.setData(packageURI);
		context.startActivity(intent);
	}

	/**
	 * 获取包信息.
	 * 
	 * @param context
	 *            the context
	 */
	public static PackageInfo getPackageInfo(Context context) {
		PackageInfo info = null;
		try {
			String packageName = context.getPackageName();
			info = context.getPackageManager().getPackageInfo(packageName,
					PackageManager.GET_ACTIVITIES);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}

	public static String getPackage(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			return packageInfo.packageName;
		} catch (Exception e) {
		}
		return "";
	}

	public static List<ApkInfo> apkInfoList = new ArrayList<ApkInfo>();
	public static List<ApkInfo> queryAppInfo(Context context) {
		PackageManager pm = context.getPackageManager();
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		// 通过查询，获得所有ResolveInfo对象.
		List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent,
				PackageManager.MATCH_DEFAULT_ONLY);
		// 调用系统排序 ， 根据name排序
		// 该排序很重要，否则只能显示系统应用，而不能列出第三方应用程序
		Collections.sort(resolveInfos,
				new ResolveInfo.DisplayNameComparator(pm));
		if (apkInfoList != null) {
			apkInfoList.clear();
			for (ResolveInfo reInfo : resolveInfos) {
				String activityName = reInfo.activityInfo.name; // 获得该应用程序的启动Activity的name
				String pkgName = reInfo.activityInfo.packageName; // 获得应用程序的包名
				String appLabel = (String) reInfo.loadLabel(pm); // 获得应用程序的Label
				Drawable icon = reInfo.loadIcon(pm); // 获得应用程序图标
				// 为应用程序的启动Activity 准备Intent
				Intent launchIntent = new Intent();
				launchIntent.setComponent(new ComponentName(pkgName,
						activityName));
				// 创建一个ApkInfo对象，并赋值
				ApkInfo apkInfo = new ApkInfo();
				apkInfo.apkName = appLabel;
				apkInfo.packageName = pkgName;
				apkInfo.icon = icon;
				apkInfo.intent = launchIntent;
				apkInfoList.add(apkInfo); // 添加至列表中
				System.out.println(appLabel + " activityName---" + activityName
						+ " pkgName---" + pkgName);
			}
		}
		return apkInfoList;
	}
	
	public static List<ApkInfo> queryAllAppInfo(Context context,int filter){
		PackageManager pm = context.getPackageManager();
		List<ApplicationInfo> listAppcations = pm
				.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
		Collections.sort(listAppcations,
				new ApplicationInfo.DisplayNameComparator(pm));// 排序
		// 根据条件来过滤
		switch (filter) {
		case FILTER_ALL_APP: // 所有应用程序
			apkInfoList.clear();
			for (ApplicationInfo app : listAppcations) {
				apkInfoList.add(getAppInfo(app,pm));
			}
			return apkInfoList;
		case FILTER_SYSTEM_APP: // 系统程序
			apkInfoList.clear();
			for (ApplicationInfo app : listAppcations) {
				if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
					apkInfoList.add(getAppInfo(app,pm));
				}
			}
			return apkInfoList;
		case FILTER_THIRD_APP: // 第三方应用程序
			apkInfoList.clear();
			for (ApplicationInfo app : listAppcations) {
				//非系统程序
				if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
					apkInfoList.add(getAppInfo(app,pm));
				} 
				//本来是系统程序，被用户手动更新后，该系统程序也成为第三方应用程序了
				else if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0){
					apkInfoList.add(getAppInfo(app,pm));
				}
			}
			break;
		case FILTER_SDCARD_APP: // 安装在SDCard的应用程序
			apkInfoList.clear();
			for (ApplicationInfo app : listAppcations) {
				if ((app.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
					apkInfoList.add(getAppInfo(app,pm));
				}
			}
			return apkInfoList;
		default:
			return null;
		}
		return apkInfoList;
	}
	
	// 构造一个AppInfo对象 ，并赋值
		public static  ApkInfo getAppInfo(ApplicationInfo app,PackageManager pm) {
			ApkInfo appInfo = new ApkInfo();
			appInfo.apkName = (String) app.loadLabel(pm);
			appInfo.icon = app.loadIcon(pm);
			appInfo.packageName = app.packageName;
			appInfo.intent = pm.getLaunchIntentForPackage(app.packageName);
			return appInfo;
		}

	
	public static final int FILTER_ALL_APP = 0; // 所有应用程序
	public static final int FILTER_SYSTEM_APP = 1; // 系统程序
	public static final int FILTER_THIRD_APP = 2; // 第三方应用程序
	public static final int FILTER_SDCARD_APP = 3; // 安装在SDCard的应用程序
}
