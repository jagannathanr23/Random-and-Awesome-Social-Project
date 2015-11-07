package com.jatinhariani.rasp;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jatinhariani.rasp.utils.HTTPStuff;
import com.jatinhariani.rasp.utils.ImageLoader;
import com.jatinhariani.rasp.utils.TimeAgo;

public class FeedAdapter extends ArrayAdapter<FeedContent>{

	private final Context context;
	private final Activity activity;
	private final FeedContent[] values;
	ImageLoader imageLoader;
	RelativeLayout rlFavButton;
	int contentId=0;
	JSONObject jObj, jObj2;
	Resources res;
	
	public FeedAdapter(Context context, Activity activity, FeedContent[] values) {
		super(context, R.layout.feed_item, values);
		this.context=context;
		res=this.context.getResources();
		this.values=values;
		this.activity=activity;
		imageLoader=new ImageLoader(activity.getApplicationContext());
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		View v=convertView;
		
		contentId=values[position].content_id;
		
		LayoutInflater inflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView;
		rowView = inflater.inflate(R.layout.feed_item, parent, false);
		TextView fitvAuthor=(TextView) rowView.findViewById(R.id.fitvAuthor);
		fitvAuthor.setText(values[position].name);
		TextView fitvContent=(TextView) rowView.findViewById(R.id.fitvContent);
		fitvContent.setText(values[position].content_text);
		TextView fitvTimestamp=(TextView) rowView.findViewById(R.id.fitvTimestamp);
		fitvTimestamp.setText(TimeAgo.getTimeAgo(values[position].time));
		TextView fitvLikeCount=(TextView) rowView.findViewById(R.id.tvLikeCount);
		fitvLikeCount.setText(""+values[position].like_count);
		TextView fitvCommentCount=(TextView) rowView.findViewById(R.id.tvCommentCount);
		fitvCommentCount.setText(""+values[position].comment_count);
		ImageView fiivLikeIcon=(ImageView) rowView.findViewById(R.id.ivLikeIcon);
		if(values[position].has_liked){
			fiivLikeIcon.setImageDrawable(res.getDrawable(R.drawable.fav2));
		}
		
		
		
		rlFavButton = (RelativeLayout) rowView.findViewById(R.id.FavButton);
		rlFavButton.setTag(values[position]);
		
		rlFavButton.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new ToggleLikeTask(context, (RelativeLayout) v).execute("");
			}
		});

		ImageView iv=(ImageView) rowView.findViewById(R.id.fiivAuthorImage);
		 if(values[position].img!=null && !values[position].img.equals("blaah")){
			 imageLoader.DisplayImage(values[position].img, iv);
		 }
		 
		return rowView;
	}

	
	
}

