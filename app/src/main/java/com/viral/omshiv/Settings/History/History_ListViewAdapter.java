package com.viral.omshiv.Settings.History;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.viral.omshiv.daytrackviralp.R;
import java.util.ArrayList;
import java.util.List;



public class History_ListViewAdapter extends BaseAdapter {


	// Declare Variables
	Context mContext;
	LayoutInflater inflater;
  
	private List<History_support> User_Activity_History = null;

	private ArrayList<History_support> arraylist;

	public History_ListViewAdapter(Context context,
                                   List<History_support> User_Activity_History) {
		mContext = context;
		this.User_Activity_History = User_Activity_History;
		inflater = LayoutInflater.from(mContext);
		this.arraylist = new ArrayList<History_support>();
		this.arraylist.addAll(User_Activity_History);
	}

	public class ViewHolder {
		TextView start_time;
		TextView finish_time;
		TextView calories_burn;
        TextView distance_covered;
        TextView steps_taken;
        TextView elapsed_time;


    }

	@Override
	public int getCount() {
		return User_Activity_History.size();
	}

	@Override
	public History_support getItem(int position) {
		return User_Activity_History.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup parent) {
		final ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.history_listview, null);
			// Locate the TextViews in listview_item.xml
			holder.start_time = (TextView) view.findViewById(R.id.stattime);
			holder.finish_time = (TextView) view.findViewById(R.id.finsihtime);
			holder.calories_burn = (TextView) view.findViewById(R.id.caloriesburn);
            holder.distance_covered = (TextView) view.findViewById(R.id.distancecovered);
            holder.steps_taken = (TextView) view.findViewById(R.id.stepstaken);
            holder.elapsed_time = (TextView)view.findViewById(R.id.elapsedTime);
			view.setTag(holder);

		} else {
			holder = (ViewHolder) view.getTag();
		}
		// Set the results into TextViews
	    holder.start_time.setText(User_Activity_History.get(position).getStart_time());
		holder.finish_time.setText(User_Activity_History.get(position).getFinish_Time());
		holder.calories_burn.setText(User_Activity_History.get(position).getCaloriesBurn());
        holder.distance_covered.setText((User_Activity_History.get(position).getDistanceCovered()));
        holder.steps_taken.setText((User_Activity_History.get(position).getStepTaken()));
        holder.elapsed_time.setText((User_Activity_History.get(position).getElapsedTime()));

        return view;


	}



}