package com.jatinhariani.rasp;

import java.io.IOException;
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

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.jatinhariani.rasp.AddToCircles.AddToCirclesListener;
import com.jatinhariani.rasp.utils.HTTPStuff;

public class DisplayUser extends SherlockFragmentActivity implements OnClickListener, AddToCirclesListener {

	JSONObject jObj, jObj2;
	Context mContext;
	Button dubAdd;
	int user_id;
	TextView dutvName, dutvBio;
	String[] circle_ids, circle_names;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Bundle b = getIntent().getExtras();
		user_id=Integer.parseInt(b.getString("user_id"));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_user);
		mContext = this;
		initialize();
		new GetUserTask()
				.execute("http://rasp.jatinhariani.com/final/get_user.php?user_id="
						+ b.getString("user_id"));
		new GetCircles().execute("");
	}

	public void initialize() {
		
		dutvName = (TextView) findViewById(R.id.dutvName);
		dutvBio = (TextView) findViewById(R.id.dutvBio);
		dubAdd = (Button) findViewById(R.id.dubAdd);
		dubAdd.setOnClickListener(this);
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.dubAdd:
			FragmentManager fm = getSupportFragmentManager();
			AddToCircles cnd=new AddToCircles();
			Bundle b= new Bundle();
			b.putStringArray("circle_ids", circle_ids);
			b.putStringArray("circle_names", circle_names);
			cnd.setArguments(b);
			cnd.show(fm, "cnd");
			break;
		}

	}

	class GetUserTask extends AsyncTask<String, Integer, String> {

		boolean success = false;
		ProgressDialog pdia;
		String name="", bio="";

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
				//setContentView(mView);
				dutvName.setText(name);
				dutvBio.setText(bio);
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
					for (int i = 0; i < jArr.length(); i++) {
						jObj2 = jArr.getJSONObject(i);
						name=jObj2.getString("name");
						bio=jObj2.getString("bio");
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
			pdia.setMessage("Loading...");
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
				Log.d("jatin", op);
				jObj = new JSONObject(op);
				success = jObj.getBoolean("success");
				if (success) {
					JSONArray jArr = jObj.getJSONArray("data");
					Log.d("jatin", "" + jArr.length());
					circle_ids = new String[jArr.length()];
					circle_names = new String[jArr.length()];
					for (int i = 0; i < jArr.length(); i++) {
						jObj2 = jArr.getJSONObject(i);
						circle_ids[i] = jObj2.getString("circle_id");
						circle_names[i] = jObj2.getString("circle_name");
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
				
			} else {
				Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}

	}

	class AddToCircleTask extends AsyncTask<String[], Integer, String>{
		boolean success = false;
		ProgressDialog pdia;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			pdia = new ProgressDialog(mContext);
			pdia.setMessage("Adding To Cirlces...");
			pdia.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String[]... params) {
			// TODO Auto-generated method stub
			String surl="http://rasp.jatinhariani.com/final/update_membership.php?user_id="+user_id;
			String[] str=params[0];
			for(String s : str){
				surl+="&circle_ids[]="+s;
			}
			HttpPost httppost = new HttpPost(surl);
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
				Toast.makeText(mContext, "Added", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}
	}
	
	public void onAddToCirlces(String[] s) {
		// TODO Auto-generated method stub
		new AddToCircleTask().execute(s);
	}
}
