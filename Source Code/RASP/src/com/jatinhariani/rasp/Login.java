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

import com.jatinhariani.rasp.utils.HTTPStuff;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AccountAuthenticatorActivity implements OnClickListener {

	JSONObject jObj;
	Context mContext;
	EditText letEmail;
	EditText letPwd;
	Button lbLogin, lbRegister;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		//delete old account
		AccountManager accountManager = AccountManager.get(this);
		Account[] accounts = accountManager.getAccountsByType("com.jatinhariani.rasp.account");
		for (Account account : accounts) {
        	accountManager.removeAccount(account, null, null);
        }
		initialize();
	}

	public void initialize() {
		mContext = this;
		letEmail = (EditText) findViewById(R.id.letEmail);
		letPwd = (EditText) findViewById(R.id.letPwd);
		lbLogin = (Button) findViewById(R.id.lbLogin);
		lbRegister = (Button) findViewById(R.id.lbRegister);
		lbLogin.setOnClickListener(this);
		lbRegister.setOnClickListener(this);
	}

	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.lbLogin:
			new LoginTask().execute("");
			break;
		case R.id.lbRegister:
			Intent i = new Intent(this, Register.class);
			startActivity(i);
			break;
		}
	}

	class LoginTask extends AsyncTask<String, Integer, String> {
		
		boolean success=false;
		ProgressDialog pdia;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			pdia = new ProgressDialog(mContext);
	        pdia.setMessage("Logging you in...");
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
			}else
			Toast.makeText(mContext, result, Toast.LENGTH_SHORT)
				.show();
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
				nameValuePairs.add(new BasicNameValuePair("email", letEmail
						.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("pwd", letPwd
						.getText().toString()));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request
				HttpResponse response = HTTPStuff.httpClient.execute(httppost);
				op = EntityUtils.toString(response.getEntity());
				jObj=new JSONObject(op);
				success=jObj.getBoolean("success");
				if(success){
					Bundle result = null;
					Account account = new Account(letEmail.getText().toString(), "com.jatinhariani.rasp.account");
					AccountManager am = AccountManager.get(mContext);
					if (am.addAccountExplicitly(account, letPwd.getText().toString(), null)) {
						result = new Bundle();
						result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
						result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
						setAccountAuthenticatorResult(result);
						return "success";
					} else {
						return "account not created!";
					}
				}else {
					JSONObject jObj2=jObj.getJSONObject("errors");
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
