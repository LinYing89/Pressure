package test.lygzb.com.pressure.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import test.lygzb.com.pressure.R;
import test.lygzb.com.pressure.timing.Timer;

/**
 * Created by Administrator on 2016/6/14.
 */
public class AdapterTimer extends BaseAdapter {

	private Context context;
	private List<Timer> listTimer;

	public AdapterTimer(Context context, List<Timer> listTimer){
		this.context = context;
		this.listTimer = listTimer;
	}

	public int getCount() {
		return listTimer.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		convertView = vi.inflate(R.layout.adapter_list_timer, parent, false);
		TextView textOnTime  = (TextView) convertView.findViewById(R.id.text_on_time);
		TextView textOffTime  = (TextView) convertView.findViewById(R.id.text_off_time);
		TextView textWeek  = (TextView) convertView.findViewById(R.id.text_week);
		Switch switchEnable  = (Switch) convertView.findViewById(R.id.switch_enable);

		final Timer timer = listTimer.get(position);
		textOnTime.setText(timer.getOnTime().toString());
		textOffTime.setText(timer.getOffTime().toString());
		textWeek.setText(timer.getWeekHelper().getWeeksName());
		switchEnable.setChecked(timer.isEnable());
		switchEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				timer.setEnable(isChecked);
			}
		});
		return convertView;
	}
}
