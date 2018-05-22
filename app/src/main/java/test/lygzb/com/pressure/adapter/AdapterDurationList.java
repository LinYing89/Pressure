package test.lygzb.com.pressure.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import test.lygzb.com.pressure.R;
import test.lygzb.com.pressure.loop.Duration;

/**
 * Created by Administrator on 2016/6/15.
 */
public class AdapterDurationList extends BaseAdapter {
	private Context context;
	private List<Duration> listDuration;

	public AdapterDurationList(Context context, List<Duration> listDuration){
		this.context = context;
		this.listDuration = listDuration;
	}

	public int getCount() {
		return listDuration.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mViewHolder = null;
		if(convertView == null){
			mViewHolder = new ViewHolder();

			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			convertView = vi.inflate(R.layout.adapter_list_duration, parent, false);
			mViewHolder.textOnTime  = (TextView) convertView.findViewById(R.id.text_on_time);
			mViewHolder.textOffTime  = (TextView) convertView.findViewById(R.id.text_off_time);
			convertView.setTag(mViewHolder);
		}else{
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		final Duration duration = listDuration.get(position);
		mViewHolder.textOnTime.setText(duration.getOnTime().toString());
		mViewHolder.textOffTime.setText(duration.getOffTime().toString());
		return convertView;
	}

	static class ViewHolder {
		private TextView textOnTime;
		private TextView textOffTime;
	}
}
