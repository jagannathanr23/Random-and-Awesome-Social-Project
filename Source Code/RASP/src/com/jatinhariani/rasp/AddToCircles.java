package com.jatinhariani.rasp;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class AddToCircles extends SherlockDialogFragment implements
		OnClickListener {
	
	CheckBox[] cboxes;
	String[] circle_ids, circle_names;
	
	AddToCircles(){
	}
	
	public interface AddToCirclesListener {
		public void onAddToCirlces(String[] selectedIds);
	}
	
	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Bundle b=getArguments();
		this.circle_ids=b.getStringArray("circle_ids");
		this.circle_names=b.getStringArray("circle_names");
		LayoutInflater factory = LayoutInflater.from(getActivity());
	    View textEntryView = factory.inflate(R.layout.fragment_add_to_circle, null, false);
	    LinearLayout ll= (LinearLayout) textEntryView.findViewById(R.id.atcCBs);
	    cboxes=new CheckBox[circle_ids.length];
	    for(int i=0; i<circle_ids.length; i++){
		    cboxes[i]=new CheckBox(getActivity());
		    cboxes[i].setId(i);
		    cboxes[i].setText(circle_names[i]);
		    ll.addView(cboxes[i]);
	    }
	    return new AlertDialog.Builder(getActivity())                
	            .setTitle("Add To Circles")
	            .setView(textEntryView)
	            .setPositiveButton("Add",
	                new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int whichButton) {          
	                    	
	                    	AddToCirclesListener activity = (AddToCirclesListener) getActivity();
	                    	List<String> sel = new ArrayList<String>();
	                    	for(int i=0; i<cboxes.length; i++){
	                    		if(cboxes[i].isChecked()){
	                    			sel.add(circle_ids[i]);
	                    		}
	                    	}
	            			activity.onAddToCirlces(sel.toArray(new String[0]));
	                    }
	                }
	            )
	            .setNegativeButton("Cancel",
	                new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int whichButton) {

	                    }
	                }
	            )
	            .create();
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}
