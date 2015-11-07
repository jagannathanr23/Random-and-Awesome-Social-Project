package com.jatinhariani.rasp;

import android.app.Activity;
import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ListView;

public class ViewPagerAdapter extends PagerAdapter{
	private static String[] titles = new String[] { "Page 1", "Page 2",
			"Page 3" };
	private final Context context;
	public final Activity activity;
	String[] circle_titles;
	String[] circle_urls;

	public ViewPagerAdapter(Context context, Activity activity, String[] circle_titles, String[] circle_urls) {
		this.context = context;
		this.activity=activity;
		this.circle_titles=circle_titles;
		this.circle_urls=circle_urls;
	}

	@Override
	public int getCount() {
		return circle_titles.length;
	}

	@Override
	public Object instantiateItem(View pager, int position) {
		ListView lv = new ListView(context);
		new GetFeedTask(context, activity, lv)
				.execute(circle_urls[position]);
		((ViewPager) pager).addView(lv, 0);
		return lv;
	}

	@Override
	public void destroyItem(View pager, int position, Object view) {
		((ViewPager) pager).removeView((View) view);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}

	@Override
	public void finishUpdate(View view) {
	}

	@Override
	public void restoreState(Parcelable p, ClassLoader c) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View view) {
	}

	public String getPageTitle(int position) {
		return circle_titles[position];
	}
	
}