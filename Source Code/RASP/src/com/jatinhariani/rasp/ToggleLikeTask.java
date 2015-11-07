package com.jatinhariani.rasp;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jatinhariani.rasp.utils.HTTPStuff;

class ToggleLikeTask extends AsyncTask<String, Integer, String>{
	
	
	Context mContext;
	RelativeLayout likeButton;
	boolean success = false;
	ProgressDialog pdia;
	JSONObject jObj, jObj2;
	FeedContent fc;
	int likeCount;
	TextView tv;
	ImageView iv;
	Resources res;

	ToggleLikeTask(Context c, RelativeLayout lb){
		mContext=c;
		res=c.getResources();
		likeButton=lb;
		fc=(FeedContent) lb.getTag();
		likeCount=fc.like_count;
		tv= (TextView) lb.findViewById(R.id.tvLikeCount);
		iv=(ImageView) lb.findViewById(R.id.ivLikeIcon);
	}
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		pdia = new ProgressDialog(mContext);
		pdia.setMessage("Updating...");
		pdia.show();
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		HttpPost httppost = new HttpPost("http://rasp.jatinhariani.com/final/toggle_like.php?content_id="+fc.content_id);
		String op = "";
		try {
			HttpResponse response = HTTPStuff.httpClient.execute(httppost);
			op = EntityUtils.toString(response.getEntity());
			Log.d("jatin", op);
			jObj = new JSONObject(op);
			success = jObj.getBoolean("success");
			if (success) {
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
			if(!fc.has_liked){
				fc.has_liked=true;
				likeCount++;
				tv.setText(""+likeCount);
				fc.like_count=likeCount;
				Toast.makeText(mContext, "Liked!", Toast.LENGTH_SHORT).show();
				iv.setImageDrawable(res.getDrawable(R.drawable.fav2));
				
			}
			else{
				fc.has_liked=false;
				likeCount--;
				tv.setText(""+likeCount);
				fc.like_count=likeCount;
				Toast.makeText(mContext, "Unliked!", Toast.LENGTH_SHORT).show();
				iv.setImageDrawable(res.getDrawable(R.drawable.fav));
			}
		} else {
			Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
		}
		super.onPostExecute(result);
	}
}
