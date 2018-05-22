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
import test.lygzb.com.pressure.loop.EventHandler;

/**
 * Created by Administrator on 2016/6/1.
 */
public class AdapterEventHandler extends BaseAdapter {
	private Context context;
	private List<EventHandler> listEventHandler;

	public AdapterEventHandler(Context context, List<EventHandler> listLoop){
		this.context = context;
		this.listEventHandler = listLoop;
	}

	public int getCount() {
		return listEventHandler.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		convertView = vi.inflate(R.layout.adapter_list_loop, parent, false);
		TextView textName  = (TextView) convertView.findViewById(R.id.text_loop_name);
		Switch switchEnable  = (Switch) convertView.findViewById(R.id.switch_enable);

		final EventHandler eventHandler = listEventHandler.get(position);
		textName.setText(eventHandler.getName());
		switchEnable.setChecked(eventHandler.isEnable());
		switchEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				eventHandler.setEnable(isChecked);
			}
		});
		return convertView;
	}
}
