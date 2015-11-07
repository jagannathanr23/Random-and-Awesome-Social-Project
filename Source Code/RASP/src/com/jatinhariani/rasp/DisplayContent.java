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
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.jatinhariani.rasp.utils.HTTPStuff;
import com.jatinhariani.rasp.utils.ImageLoader;
import com.jatinhariani.rasp.utils.TimeAgo;

public class DisplayContent extends SherlockActivity implements OnClickListener {

	Context mContext;
	public int content_id;
	FeedContent feedContent;
	JSONObject jObj, jObj2;
	Activity mActivity;
	ImageLoader imageLoader;
	Button dcbCommentButton;
	EditText dcetCommentText;
	Resources res;
	
	ImageView dcivAuthorImage, dcivLikeIcon, dcivCommentIcon, dcivShareIcon;

	TextView dctvAuthor, dctvTimestamp, dctvContent, dctvLikeCount,
			dctvCommentCount, dctvShareCount;
	
	RelativeLayout dcFavButton, dcCommentButton, dcShareButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		Bundle b = getIntent().getExtras();
		content_id = b.getInt("content_id");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_content);
		mContext = this;
		mActivity = this;
		res=mContext.getResources();
		imageLoader = new ImageLoader(getApplicationContext());
		initialize();
		
		new GetContentTask()
				.execute("http://rasp.jatinhariani.com/final/get_content.php?content_id="
						+ content_id);
	}

	public void initialize() {
		dctvAuthor = (TextView) findViewById(R.id.dctvAuthor);
		dctvTimestamp = (TextView) findViewById(R.id.dctvTimestamp);
		dctvContent = (TextView) findViewById(R.id.dctvContent);
		dctvCommentCount = (TextView) findViewById(R.id.tvCommentCount);
		dctvShareCount = (TextView) findViewById(R.id.tvShareCount);
		dctvLikeCount = (TextView) findViewById(R.id.tvLikeCount);
		dcbCommentButton = (Button) findViewById(R.id.dcbCommentButton);
		dcetCommentText= (EditText) findViewById(R.id.dcetCommentText);

		dcivAuthorImage = (ImageView) findViewById(R.id.dcivAuthorImage);
		dcivLikeIcon=(ImageView) findViewById(R.id.ivLikeIcon);
		dcivCommentIcon=(ImageView) findViewById(R.id.ivCommentIcon);
		dcivShareIcon=(ImageView) findViewById(R.id.ivShareIcon);
		
		dcFavButton= (RelativeLayout) findViewById(R.id.FavButton);
		dcCommentButton= (RelativeLayout) findViewById(R.id.CommentButton);
		dcShareButton= (RelativeLayout) findViewById(R.id.ShareButton);

		dcbCommentButton.setOnClickListener(this);
		
		
	}

	public void setStuff() {
		dctvAuthor.setText(feedContent.name);
		dctvContent.setText(feedContent.content_text);
		dctvLikeCount.setText("" + feedContent.like_count);
		dctvCommentCount.setText("" + feedContent.comment_count);
		dctvTimestamp.setText(TimeAgo.getTimeAgo(feedContent.time));
		if (feedContent.img != null && !feedContent.img.equals("blaah")) {
			imageLoader.DisplayImage(feedContent.img, dcivAuthorImage);
		}
		if(feedContent.has_liked){
			dcivLikeIcon.setImageDrawable(res.getDrawable(R.drawable.fav2));
		}
		dcFavButton.setTag(feedContent);
		
		dcFavButton.setOnClickListener( new View.OnClickListener() {
					
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new ToggleLikeTask(mContext, (RelativeLayout) v).execute("");
			}
		});
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		new CreateCommentTask().execute("");
	}

	class GetContentTask extends AsyncTask<String, Integer, String> {

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
				((DisplayContent) mActivity).setStuff();
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
				// Execute HTTP Post Request
				HttpResponse response = HTTPStuff.httpClient.execute(httppost);
				op = EntityUtils.toString(response.getEntity());
				Log.d("jatin", "a" + op);
				jObj = new JSONObject(op);
				success = jObj.getBoolean("success");
				if (success) {
					JSONArray jArr = jObj.getJSONArray("data");
					feedContent = new FeedContent();
					for (int i = 0; i < jArr.length(); i++) {
						jObj2 = jArr.getJSONObject(i);
						 feedContent=new FeedContent();
						 feedContent.content_id=Integer.parseInt(jObj2.getString("content_id"));
						 feedContent.user_id=Integer.parseInt(jObj2.getString("user_id"));
						 feedContent.content_text=jObj2.getString("content_text");
						 feedContent.name=jObj2.getString("name");
						 feedContent.img=jObj2.getString("img_path");
						 feedContent.has_liked=jObj2.getString("has_liked").equals("0")?false:true;
						 feedContent.like_count=Integer.parseInt(jObj2.getString("like_count"));
						 Log.d("count", ""+feedContent.like_count);
						 feedContent.comment_count=Integer.parseInt(jObj2.getString("comment_count"));
						 SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						 try {
							feedContent.time=df.parse(jObj2.getString("time"));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
						feedContent.content_text = jObj2
								.getString("content_text");
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
	
	class CreateCommentTask extends AsyncTask<String, Integer, String> {

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
				Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
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
			HttpPost httppost = new HttpPost("http://rasp.jatinhariani.com/final/create_comment.php");
			String op = "";
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("content_id", ""+content_id));
				nameValuePairs.add(new BasicNameValuePair("text", dcetCommentText.getText().toString()));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				
				// Execute HTTP Post Request
				HttpResponse response = HTTPStuff.httpClient.execute(httppost);
				op = EntityUtils.toString(response.getEntity());
				Log.d("jatin", "a" + op);
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

}
