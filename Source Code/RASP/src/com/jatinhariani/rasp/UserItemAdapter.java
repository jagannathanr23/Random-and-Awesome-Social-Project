package com.jatinhariani.rasp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class UserItemAdapter extends ArrayAdapter<User>{

	User[] users;
	Context mContext;
	public UserItemAdapter(Context context,	User[] objects) {
		
		super(context, R.layout.user_item, objects);
		this.users=objects;
		this.mContext=context;
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) mContext
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view= inflater.inflate(R.layout.user_item, parent, false);
		TextView uitvName=(TextView) view.findViewById(R.id.uitvName);
		uitvName.setText(users[position].name);
		TextView uitvBio=(TextView) view.findViewById(R.id.uitvBio);
		uitvBio.setText(users[position].bio);
		return view;
	}
	
}
