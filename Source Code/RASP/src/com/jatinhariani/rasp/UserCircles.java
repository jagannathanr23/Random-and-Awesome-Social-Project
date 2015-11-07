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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.internal.widget.IcsAdapterView.AdapterContextMenuInfo;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jatinhariani.rasp.utils.CreateCircleFragment;
import com.jatinhariani.rasp.utils.CreateCircleFragment.CreateCircleDialogListener;
import com.jatinhariani.rasp.utils.HTTPStuff;

public class UserCircles extends SherlockFragmentActivity implements
		CreateCircleDialogListener, OnItemClickListener {

	Context mContext;
	JSONObject jObj, jObj2, jObj3;
	ListView uclvCircles;
	String[] circle_ids;
	String[] circle_names;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_circles);
		initialize();
		new GetCircles().execute("");
	    getSupportActionBar().setTitle("Circles");
	}

	public void initialize() {
		mContext = this;
		uclvCircles = (ListView) findViewById(R.id.uclvCircles);
		uclvCircles.setOnItemClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getSupportMenuInflater().inflate(R.menu.menu_user_circles, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.menu_add_circle:
			showEditDialog();
			break;
		}
		return super.onOptionsItemSelected(item);
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
				uclvCircles.setAdapter(new ArrayAdapter<String>(mContext,
						android.R.layout.simple_expandable_list_item_1,
						circle_names));
				registerForContextMenu(uclvCircles);
			} else {
				Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}

	}

	// creating circles
	private void showEditDialog() {
		FragmentManager fm = getSupportFragmentManager();
		CreateCircleFragment createCirlceFragment = new CreateCircleFragment("");
		createCirlceFragment.show(fm, "fragment_create_circle");
	}

	public void onFinishEditDialog(String circle_name) {
		Log.d("jatin", "here");
		new CreateCircleQuery().execute(circle_name);
	}

	class CreateCircleQuery extends AsyncTask<String, Integer, String> {

		boolean success = false;
		ProgressDialog pdia;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			pdia = new ProgressDialog(mContext);
			pdia.setMessage("Creating Circle...");
			pdia.show();
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			pdia.dismiss();
			if (result.equals("success")) {
				new GetCircles().execute("");
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
			HttpPost httppost = new HttpPost(
					"http://rasp.jatinhariani.com/final/create_circle.php");
			String op = "";
			try {
				// Add your data
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("circle_name",
						params[0]));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request
				HttpResponse response = HTTPStuff.httpClient.execute(httppost);
				op = EntityUtils.toString(response.getEntity());
				jObj = new JSONObject(op);
				success = jObj.getBoolean("success");
				if (success) {
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

	// deleting circles
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		android.view.MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu_circles, menu);
	}

	
	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		// TODO Auto-generated method stub
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		int index = info.position;
		switch (item.getItemId()) {
		case R.id.menu_delete_circle:
			new DeleteCircleQuery().execute("" + circle_ids[index]);
		}
		// Toast.makeText(mContext, ""+circle_ids[index],
		// Toast.LENGTH_SHORT).show();
		return super.onContextItemSelected(item);
	}

	class DeleteCircleQuery extends AsyncTask<String, Integer, String> {

		boolean success = false;
		ProgressDialog pdia;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			pdia = new ProgressDialog(mContext);
			pdia.setMessage("Creating Circle...");
			pdia.show();
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			pdia.dismiss();
			if (result.equals("success")) {
				new GetCircles().execute("");
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
			HttpPost httppost = new HttpPost(
					"http://rasp.jatinhariani.com/final/delete_circle.php");
			String op = "";
			try {
				// Add your data
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("circle_id",
						params[0]));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request
				HttpResponse response = HTTPStuff.httpClient.execute(httppost);
				op = EntityUtils.toString(response.getEntity());
				jObj = new JSONObject(op);
				success = jObj.getBoolean("success");
				if (success) {
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

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Intent i = new Intent(this, UserList.class);
		i.putExtra("url", "http://rasp.jatinhariani.com/final/get_users_in_circle.php?circle_id="+circle_ids[arg2]);
		startActivity(i);
	}

}
