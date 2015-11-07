package com.jatinhariani.rasp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.jatinhariani.rasp.utils.HTTPStuff;

public class CreateContent extends SherlockActivity implements OnClickListener {

	JSONObject jObj, jObj2;
	Context mContext;
	String circle_ids[];
	String circle_names[];
	LinearLayout ccllCircleCheck;
	EditText ccetTitle, ccetContent;
	Button ccbPublish;
	int type = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_content);
		initialize();
		// new GetUsersCircles().execute("");
	}

	public void initialize() {
		mContext = this;
		ccllCircleCheck = (LinearLayout) findViewById(R.id.ccllCircleCheck);
		ccetTitle = (EditText) findViewById(R.id.ccetTitle);
		ccetContent = (EditText) findViewById(R.id.ccetContent);
		ccbPublish = (Button) findViewById(R.id.ccbPublish);
		ccbPublish.setOnClickListener(this);
	}

	class GetUsersCircles extends AsyncTask<String, Integer, String> {

		boolean success = false;
		ProgressDialog pdia;
		LinearLayout view;
		CheckBox cbox;

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
			LayoutParams lparams = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			view = new LinearLayout(mContext);
			view.setLayoutParams(lparams);
			view.setOrientation(LinearLayout.VERTICAL);
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
						cbox = new CheckBox(mContext);
						cbox.setText(circle_names[i]);
						cbox.setId(Integer.parseInt(circle_ids[i]));
						view.addView(cbox);
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
				ccllCircleCheck.addView(view);
			} else {
				Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}

	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ccbPublish:
			new PostContent().execute("");
			break;
		}

	}

	class PostContent extends AsyncTask<String, Integer, String> {

		boolean success = false;
		ProgressDialog pdia;
		LinearLayout view;
		CheckBox cbox;

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
			LayoutParams lparams = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			view = new LinearLayout(mContext);
			view.setLayoutParams(lparams);
			view.setOrientation(LinearLayout.VERTICAL);
			HttpPost httppost = new HttpPost(
					"http://rasp.jatinhariani.com/final/create_content.php");
			String op = "";
			try {

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("content_text",
						ccetContent.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("special", ccetTitle
						.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("type", "" + type));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

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
				Toast.makeText(mContext, "Published", Toast.LENGTH_SHORT)
						.show();
				finish();
			} else {
				Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}

	}

}
