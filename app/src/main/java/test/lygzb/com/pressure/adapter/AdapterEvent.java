package test.lygzb.com.pressure.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import lygzb.zsmarthome.device.electrical.Electrical;
import test.lygzb.com.pressure.R;
import test.lygzb.com.pressure.loop.Event;
import test.lygzb.com.pressure.loop.EventSymbol;

/**
 * Created by Administrator on 2016/6/1.
 */
public class AdapterEvent extends BaseAdapter {

	private Context context;
	private List<Event> listEvent;

	public AdapterEvent(Context context, List<Event> listEvent){
		this.context = context;
		this.listEvent = listEvent;
	}

	public int getCount() {
		return listEvent.size();
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

			convertView = vi.inflate(R.layout.adapter_list_event, parent, false);
			mViewHolder.textStyle  = (TextView) convertView.findViewById(R.id.text_style);
			mViewHolder.textDevice  = (TextView) convertView.findViewById(R.id.text_device);
			mViewHolder.textSymbol  = (TextView) convertView.findViewById(R.id.text_symbol);
			mViewHolder.textValue  = (TextView) convertView.findViewById(R.id.text_value);
			convertView.setTag(mViewHolder);
		}else{
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		Event event = listEvent.get(position);
		mViewHolder.textStyle.setText(String.valueOf(event.getEventStyle()));
		mViewHolder.textDevice.setText(event.getDevice().getName());
		String symbol = "";
		if(event.getEventSymbol() == EventSymbol.GREATER){
			symbol = ">";
		}else if(event.getEventSymbol() == EventSymbol.EQUAL){
			symbol = "=";
		}else {
			symbol = "<";
		}
		mViewHolder.textSymbol.setText(symbol);
		String value = "";
		if(event.getDevice() instanceof Electrical){
			if(event.getTriggerValue() == 0){
				value = "OFF";
			}else {
				value = "ON";
			}
		}else{
			value = String.valueOf(event.getTriggerValue() + "%");
		}
		mViewHolder.textValue.setText(value);
		return convertView;
	}

	static class ViewHolder {
		private TextView textStyle;
		private TextView textDevice;
		private TextView textSymbol;
		private TextView textValue;
	}
}
