package com.jatinhariani.rasp;

import java.io.IOException;
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
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jatinhariani.rasp.utils.HTTPStuff;

class GetFeedTask extends AsyncTask<String, Integer, String> implements OnItemClickListener{

	boolean success = false;
	ProgressDialog pdia;
	Context mContext;
	JSONObject jObj, jObj2;
	FeedContent[] feedContent;
	String content_ids[], content_names[];
	ListView lvFeed;
	Activity a;

	public GetFeedTask(Context context, Activity a, ListView lv){
		mContext=context;
		this.lvFeed=lv;
		this.a=a;
	}
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		pdia = new ProgressDialog(mContext);
		pdia.setMessage("Loading...");
		//pdia.show();
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		//pdia.dismiss();
		if (result.equals("success")) {
			lvFeed.setAdapter(new FeedAdapter(mContext, a, feedContent));
			lvFeed.setOnItemClickListener(this);
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
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse response = HTTPStuff.httpClient.execute(httppost);
			op = EntityUtils.toString(response.getEntity());
			jObj = new JSONObject(op);
			success = jObj.getBoolean("success");
			if (success) {
				JSONArray jArr = jObj.getJSONArray("data");
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
					 feedContent[i].img=jObj2.getString("img_path");
					 feedContent[i].has_liked=jObj2.getString("has_liked").equals("0")?false:true;
					 feedContent[i].like_count=Integer.parseInt(jObj2.getString("like_count"));
					 feedContent[i].comment_count=Integer.parseInt(jObj2.getString("comment_count"));
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
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Intent i=new Intent(mContext, DisplayContent.class);
		i.putExtra("content_id", feedContent[arg2].content_id);
		a.startActivity(i);
	}

	
}
