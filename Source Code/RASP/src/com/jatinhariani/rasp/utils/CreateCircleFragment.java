package com.jatinhariani.rasp.utils;

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

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.jatinhariani.rasp.R;

public class CreateCircleFragment extends SherlockDialogFragment implements
		OnEditorActionListener {

	JSONObject jObj;
	Context mContext;
	String s;
	
	public interface CreateCircleDialogListener {
		void onFinishEditDialog(String inputText);
	}

	private EditText fccetCircleName;

	public CreateCircleFragment(String s) {
		this.s=s;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater
				.inflate(R.layout.fragment_create_circle, container);
		fccetCircleName = (EditText) view.findViewById(R.id.fccetCircleName);
		getDialog().setTitle("Create Circle");
		fccetCircleName.setText(s);

		// Show soft keyboard automatically
		fccetCircleName.requestFocus();
		getDialog().getWindow().setSoftInputMode(
				LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		fccetCircleName.setOnEditorActionListener(this);
		return view;
	}

	public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
		// TODO Auto-generated method stub
		if (EditorInfo.IME_ACTION_DONE == arg1) {
			// Return input text to activity
			CreateCircleDialogListener activity =
			(CreateCircleDialogListener) getActivity();
			activity.onFinishEditDialog(fccetCircleName.getText().toString());
			this.dismiss();
			return true;
		}
		return false;
	}

}
