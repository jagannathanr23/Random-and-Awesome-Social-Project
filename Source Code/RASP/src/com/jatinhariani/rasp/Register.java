package com.jatinhariani.rasp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;

import com.actionbarsherlock.app.SherlockActivity;
import com.jatinhariani.rasp.utils.HTTPStuff;

public class Register extends SherlockActivity implements OnClickListener {

	private static int SELECT_IMAGE = 1;
	JSONObject jObj;
	EditText retEmail, retPwd, retConfirmPwd, retDOB, retName;
	Button rbRegister;
	Context mContext;
	static Pattern emailPattern;
	String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	String PWD_PATTERN = "^[a-zA-Z0-9]*$";
	ImageButton ribProfile;
	Bitmap bMap;
	String imagePath="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		initialize();
		getSupportActionBar().setTitle("Register");
	}

	public void initialize() {
		mContext = this;
		retEmail = (EditText) findViewById(R.id.retEmail);
		retPwd = (EditText) findViewById(R.id.retPwd);
		retConfirmPwd = (EditText) findViewById(R.id.retConfirmPwd);
		retDOB = (EditText) findViewById(R.id.retDOB);
		retName = (EditText) findViewById(R.id.retName);
		rbRegister = (Button) findViewById(R.id.rbRegister);
		ribProfile = (ImageButton) findViewById(R.id.ribProfile);
		Resources res = getResources();
		bMap = BitmapFactory.decodeResource(res, R.drawable.profile);

		rbRegister.setOnClickListener(this);
		ribProfile.setOnClickListener(this);
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rbRegister:
			if (!checkEmail()) {
				showDialogBox("Invalid Email!",
						"Please enter a valid email and try again.");
				break;
			}
			if (!pwdLength()) {
				showDialogBox("Error!",
						"Password should be at least 6 characters.");
				break;
			}
			if (!pwdChars()) {
				showDialogBox("Error!",
						"Password can contain only alplanumeric characters.");
				break;
			}
			if (!checkPasswords()) {
				showDialogBox("Error!", "Passwords don't match.");
				break;
			}
			if (!checkDOB()) {
				showDialogBox("Error!", "Please enter a valid Date of Birth.");
				break;
			}
			if (!checkName()) {
				showDialogBox("Error!", "Please enter a Name.");
				break;
			}
			if(!checkImg()){
				showDialogBox("Error!","Please add an image!");
				break;
			}
			new RegisterTask().execute("");
			break;
		case R.id.ribProfile:
			startActivityForResult(
					new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
					SELECT_IMAGE);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SELECT_IMAGE)
			if (resultCode == Activity.RESULT_OK && resultCode == RESULT_OK
					&& null != data) {
				Uri selectedImage = data.getData();
				
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				
				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();
				
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();
				imagePath=picturePath;

				Matrix matrix = new Matrix();
				Bitmap b = BitmapFactory.decodeFile(picturePath);
				
				float y = b.getHeight();
				float x = b.getWidth();
				int size;
				if (y > x) {
					matrix.postScale(1f / (x / 300f), 1f / (x / 300f));
					size = (int) x;
				} else {
					matrix.postScale(1f / (y / 300f), 1f / (y / 300f));
					size = (int) y;
				}
				int x1 = (int) (x / 2 - 150);
				int y1 = (int) (y / 2 - 150);

				Log.d("jatin",  picturePath.substring(picturePath.lastIndexOf("/")));
				Bitmap croppedBitmap = Bitmap.createBitmap(b, 0, 0, size, size,
						matrix, true);
				ribProfile.setImageBitmap(croppedBitmap);
				ribProfile.setScaleType(ScaleType.CENTER_CROP);
				b.recycle();

				File f = new File(mContext.getCacheDir().getAbsolutePath(), picturePath.substring(picturePath.lastIndexOf("/")+1));
				
				Bitmap bitmap = croppedBitmap;
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				bitmap.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
				byte[] bitmapdata = bos.toByteArray();
				
				try {
					f.createNewFile();
					FileOutputStream fos = new FileOutputStream(f);
					fos.write(bitmapdata);
					imagePath=f.getAbsolutePath();
					Log.d("jatin", imagePath);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
	}

	// checks
	public boolean checkName() {
		if (retName.getText().toString().length() > 1) {
			return true;
		}
		return false;
	}

	public boolean pwdLength() {
		if (retPwd.getText().toString().length() >= 6) {
			return true;
		}
		return false;
	}

	public boolean pwdChars() {
		emailPattern = Pattern.compile(PWD_PATTERN);
		Matcher m = emailPattern.matcher(retPwd.getText().toString());
		return m.matches();
	}

	public boolean checkPasswords() {
		return retPwd.getText().toString()
				.equals(retConfirmPwd.getText().toString());
	}

	public boolean checkEmail() {
		emailPattern = Pattern.compile(EMAIL_PATTERN);
		Matcher m = emailPattern.matcher(retEmail.getText().toString());
		return m.matches();
	}

	public boolean checkDOB() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		sdf.setLenient(false);

		try {
			// if not valid, it will throw ParseException
			Date date = sdf.parse(retDOB.getText().toString());

		} catch (ParseException e) {

			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean checkImg(){
		if(imagePath.equals("")){
			return false;
		}else{
			return true;
		}
	}
	public void showDialogBox(String title, String message) {
		new AlertDialog.Builder(mContext).setTitle(title).setMessage(message)
				.setNeutralButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// continue with delete
					}
				}).show();
	}

	public class RegisterTask extends AsyncTask<String, Integer, String> {

		boolean success = false;
		ProgressDialog pdia;

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			pdia.dismiss();
			if (result.equals("success")) {
				new AlertDialog.Builder(mContext)
						.setTitle("Success!")
						.setMessage(
								"You have successfully registered. Please check your inbox for an activation code. You may login once you have activated your account.")
						.setNeutralButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										finish();
									}
								}).show();
			} else {
				showDialogBox("Error!", result);
			}
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			pdia = new ProgressDialog(mContext);
			pdia.setMessage("Registering...");
			pdia.show();
			super.onPreExecute();
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
					"http://rasp.jatinhariani.com/final/create_user.php");
			String op = "";
			try {
				// Add your data

				MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
				FileBody fb=new FileBody(new File(imagePath), "image/*");
				Log.d("d", fb.toString());
				reqEntity.addPart("Photo", fb);
				reqEntity.addPart("name", new StringBody(retName.getText()
						.toString()));
				reqEntity.addPart("email", new StringBody(retEmail.getText()
						.toString()));
				reqEntity.addPart("pwd", new StringBody(retPwd.getText()
						.toString()));
				reqEntity.addPart("dob", new StringBody(retDOB.getText()
						.toString()));
				reqEntity.addPart("bio", new StringBody(""));
				reqEntity.addPart("interests", new StringBody(""));

				httppost.setEntity(reqEntity);

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
			return op;
		}

	}

}
