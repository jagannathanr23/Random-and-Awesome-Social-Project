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
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.jatinhariani.rasp.utils.HTTPStuff;

public class UserList extends SherlockActivity implements OnItemClickListener,
		OnQueryTextListener {

	JSONObject jObj, jObj2;
	ListView ullvUserList;
	Context mContext;
	String[] user_ids;
	User[] users;
	SearchView mSearchView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_list);
		Bundle b = getIntent().getExtras();
		String url="http://rasp.jatinhariani.com/final/random_users.php";
		if(b.containsKey("url"))
			url=b.getString("url");
		ullvUserList = (ListView) findViewById(R.id.ullvUserList);
		ullvUserList.setOnItemClickListener(this);
		mContext = this;
		new GetUsersTask()
				.execute(url);
		getSupportActionBar().setTitle("People");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu_user_list, menu);
		return true;
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Intent i = new Intent(this, DisplayUser.class);
		i.putExtra("user_id", user_ids[arg2]);
		startActivity(i);
	}

	public boolean onQueryTextSubmit(String query) {
		// TODO Auto-generated method stub
		Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
		return false;
	}

	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub

		return false;
	}

	class GetUsersTask extends AsyncTask<String, Integer, String> {

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
				ullvUserList.setAdapter(new UserItemAdapter(mContext, users));
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
					Log.d("jatin", "" + jArr.length());
					user_ids = new String[jArr.length()];
					users = new User[jArr.length()];
					for (int i = 0; i < jArr.length(); i++) {
						jObj2 = jArr.getJSONObject(i);
						user_ids[i] = jObj2.getString("user_id");
						users[i] = new User();
						users[i].user_id = Integer.parseInt(jObj2
								.getString("user_id"));
						users[i].bio = jObj2.getString("bio");
						users[i].email = jObj2.getString("email");
						users[i].name = jObj2.getString("name");
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

}
