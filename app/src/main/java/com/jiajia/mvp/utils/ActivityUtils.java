package com.jiajia.mvp.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;


public class ActivityUtils {

	private static ActivityUtils activityTransitionUtils;
	public List<Activity> activityList = new ArrayList<Activity>();

	public static ActivityUtils getInstance() {
		if (activityTransitionUtils == null) {
			activityTransitionUtils = new ActivityUtils();
		}
		return activityTransitionUtils;
	}

	public void pushActivity(Activity activity) {
		activityList.add(activity);
	}
	
	public void finishAll() {
		// TODO Auto-generated method stub
		for (Activity activity:activityList) {
			activity.finish();
		}
		activityList.clear();
	}
}
