package com.jatinhariani.rasp;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jatinhariani.rasp.utils.HTTPStuff;
import com.viewpagerindicator.TitlePageIndicator;

public class Feed extends SherlockActivity{

	ListView lvFeed;
	JSONObject jObj, jObj2;
	Context mContext;
	Activity mActivity;
	String content_ids[];
	FeedContent[] feedContent;
	String circle_ids[];
	String circle_names[];
	String circle_urls[];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feed_pager);
		getSupportActionBar().setTitle("Feed");
		mContext=this;
		mActivity=this;
		new GetCircles().execute("");
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		new GetCircles().execute("");
		super.onResume();
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getSupportMenuInflater().inflate(R.menu.menu_feed, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		Intent i;
		switch (item.getItemId()) {
		case R.id.menu_add_feed:
			i = new Intent(this, CreateContent.class);
			startActivity(i);
			break;
//		case R.id.menu_dummy:
//			i = new Intent(this, Dummy.class);
//			startActivity(i);
//			break;
		case R.id.menu_circles:
			i = new Intent(this, UserCircles.class);
			startActivity(i);
			break;
		case R.id.menu_users:
			i = new Intent(this, UserList.class);
			i.putExtra("url", "http://rasp.jatinhariani.com/final/random_users.php");
			startActivity(i);
			break;
		case R.id.menu_logout:
			i = new Intent(this, Login.class);
			startActivity(i);
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	class GetFeedTask extends AsyncTask<String, Integer, String> {

		boolean success = false;
		ProgressDialog pdia;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			pdia = new ProgressDialog(mContext);
			pdia.setMessage("Loading...");
			pdia.show();
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			pdia.dismiss();
			if (result.equals("success")) {
				//lvFeed = (ListView) findViewById(R.id.lvFeed);
				//lvFeed.setAdapter(new FeedAdapter(mContext, mActivity, feedContent));
			} else
				Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			HttpPost httppost = new HttpPost(params[0]);
			String op = "";
			try {
				// Add your data

				// Execute HTTP Post Request
				HttpResponse response = HTTPStuff.httpClient.execute(httppost);
				op = EntityUtils.toString(response.getEntity());
				jObj = new JSONObject(op);
				success = jObj.getBoolean("success");
				if (success) {
					JSONArray jArr = jObj.getJSONArray("data");
					Log.d("jatin", "" + jArr.length());
					content_ids = new String[jArr.length()];
					feedContent = new FeedContent[jArr.length()];
					 for (int i = 0; i < jArr.length(); i++) {
						 jObj2 = jArr.getJSONObject(i);
						 content_ids[i] = jObj2.getString("content_id");
						 feedContent[i]=new FeedContent();
						 feedContent[i].content_id=Integer.parseInt(jObj2.getString("content_id"));
						 feedContent[i].user_id=Integer.parseInt(jObj2.getString("user_id"));
						 feedContent[i].content_text=jObj2.getString("content_text");
						 feedContent[i].name=jObj2.getString("name");
						 feedContent[i].img=jObj2.getString("img");
						 SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						 try {
							feedContent[i].time=df.parse(jObj2.getString("time"));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
						 feedContent[i].content_text=jObj2.getString("content_text");
						 
					 }
					return "success";
				} else {
					JSONObject jObj2 = jObj.getJSONObject("errors");
					return jObj2.getString("0");
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
			} catch (IOException e) {
				// TODO Auto-generated catch block
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "Unknown Error!";
		}

	}

	class GetCircles extends AsyncTask<String, Integer, String> {

		boolean success = false;
		ProgressDialog pdia;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			pdia = new ProgressDialog(mContext);
			pdia.setMessage("Loading Circles...");
			pdia.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			HttpPost httppost = new HttpPost(
					"http://rasp.jatinhariani.com/final/get_user_circles.php");
			String op = "";
			try {
				HttpResponse response = HTTPStuff.httpClient.execute(httppost);
				op = EntityUtils.toString(response.getEntity());
				jObj = new JSONObject(op);
				success = jObj.getBoolean("success");
				if (success) {
					JSONArray jArr = jObj.getJSONArray("data");
					circle_ids = new String[jArr.length()+2];
					circle_names = new String[jArr.length()+2];
					circle_urls = new String[jArr.length()+2];
					circle_ids[0]="-1";
					circle_ids[1]="0";
					circle_names[0]="My Content";
					circle_names[1]="My Feed";
					circle_urls[0]="http://rasp.jatinhariani.com/final/get_content.php?self=1";
					circle_urls[1]="http://rasp.jatinhariani.com/final/get_content.php";
					for (int i = 2; i < jArr.length()+2; i++) {
						jObj2 = jArr.getJSONObject(i-2);
						circle_ids[i] = jObj2.getString("circle_id");
						circle_names[i] = jObj2.getString("circle_name");
						circle_urls[i]="http://rasp.jatinhariani.com/final/get_content.php?circle_id="+jObj2.getString("circle_id");
					}
					return "success";
				} else {
					jObj2 = jObj.getJSONObject("errors");
					return jObj2.getString("0");
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
			} catch (IOException e) {
				// TODO Auto-generated catch block
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "Unknown Error!";
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			pdia.dismiss();
			if (result.equals("success")) {
				ViewPagerAdapter adapter = new ViewPagerAdapter(mContext, mActivity, circle_names, circle_urls );
			    ViewPager pager = 
			        (ViewPager)findViewById( R.id.viewpager );
			    TitlePageIndicator indicator = 
			        (TitlePageIndicator)findViewById( R.id.titles );
			    pager.setAdapter( adapter );
			    pager.setCurrentItem(1);
			    indicator.setViewPager( pager );
			} else {
				Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}

	}
}
