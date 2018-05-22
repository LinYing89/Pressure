package test.lygzb.com.pressure.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import test.lygzb.com.pressure.R;

/**
 * Created by Administrator on 2016/4/10.
 */
public class BlueListAdapter extends BaseAdapter {

	private Context context;
	private List<String> nameList;
	int color = -1;

	public BlueListAdapter(Context context, List<String> nameList){
		this.context = context;
		this.nameList = nameList;
	}

	public BlueListAdapter(Context context, List<String> nameList, int color){
		this.context = context;
		this.nameList = nameList;
		this.color = color;
	}

	public int getCount() {
		return nameList.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = ((Activity) context).getLayoutInflater().inflate(R.layout.list_text, null);
		TextView text_name = (TextView)convertView.findViewById(R.id.item_text);
		text_name.setText(nameList.get(position));
		if(color == 1)
			text_name.setTextColor(Color.BLACK);
		return convertView;
	}

}
