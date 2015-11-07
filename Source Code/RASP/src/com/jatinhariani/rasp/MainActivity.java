package com.jatinhariani.rasp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.SherlockActivity;
import com.jatinhariani.rasp.utils.HTTPStuff;

public class MainActivity extends SherlockActivity{

	String email, pwd;
	Context mContext;
	JSONObject jObj;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		HTTPStuff.httpClient=new DefaultHttpClient();
		initialize();
		
		AccountManager accountManager = AccountManager.get(this);
		Account[] accounts = accountManager.getAccountsByType("com.jatinhariani.rasp.account");
		//Account myAccount = null;
		email="";
		pwd="";
        for (Account account : accounts) {
        	email=account.name;
        	pwd=accountManager.getPassword(account);
            //myAccount = account;
            break;
        }
        if(email.equals("")){
        	Intent i=new Intent(this, Login.class);
    		startActivity(i);
     		finish();
        }else{
        	new LoginTask().execute("");
        }
	}
	
	
	public void initialize(){
		mContext = this;
	}
	
	class LoginTask extends AsyncTask<String, Integer, String> {
		
		boolean success=false;
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
			if(result.equals("success")){
				Intent i=new Intent(mContext, Feed.class);
				startActivity(i);
				finish();
			}else{
				Intent i=new Intent(mContext, Login.class);
				startActivity(i);
				finish();
			}
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
					"http://rasp.jatinhariani.com/final/login.php");
			String op = "";
			try {
				// Add your data
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("email", 
						email));
				nameValuePairs.add(new BasicNameValuePair("pwd", pwd));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request
				HttpResponse response = HTTPStuff.httpClient.execute(httppost);
				op = EntityUtils.toString(response.getEntity());
				Log.d("jatin", op);
				jObj=new JSONObject(op);
				success=jObj.getBoolean("success");
				if(success){
					return "success";
				}else{
					return "nopes!";
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
			} catch (IOException e) {
				// TODO Auto-generated catch block
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "Unknown error!";
		}

	}

}
