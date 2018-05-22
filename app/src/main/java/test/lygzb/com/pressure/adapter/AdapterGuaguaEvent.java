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
import test.lygzb.com.pressure.event.AbstractEvent;
import test.lygzb.com.pressure.event.EventDevice;
import test.lygzb.com.pressure.event.EventTime;
import test.lygzb.com.pressure.loop.Event;
import test.lygzb.com.pressure.loop.EventSymbol;

/**
 * Created by Administrator on 2017/4/20.
 */

public class AdapterGuaguaEvent extends BaseAdapter {

	private Context context;
	private List<AbstractEvent> listEvent;

	public AdapterGuaguaEvent(Context context, List<AbstractEvent> listEvent){
		this.context = context;
		this.listEvent = listEvent;
	}

	@Override
	public int getCount() {
		return listEvent.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AdapterGuaguaEvent.ViewHolder mViewHolder = null;
		if(convertView == null){
			mViewHolder = new AdapterGuaguaEvent.ViewHolder();

			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			convertView = vi.inflate(R.layout.adapter_list_event, parent, false);
			mViewHolder.textStyle  = (TextView) convertView.findViewById(R.id.text_style);
			mViewHolder.textDevice  = (TextView) convertView.findViewById(R.id.text_device);
			mViewHolder.textSymbol  = (TextView) convertView.findViewById(R.id.text_symbol);
			mViewHolder.textValue  = (TextView) convertView.findViewById(R.id.text_value);
			convertView.setTag(mViewHolder);
		}else{
			mViewHolder = (AdapterGuaguaEvent.ViewHolder) convertView.getTag();
		}
		AbstractEvent event = listEvent.get(position);
		mViewHolder.textStyle.setText(String.valueOf(event.getEventStyle()));
		if(event instanceof EventDevice) {
			mViewHolder.textDevice.setText(((EventDevice)event).getDevice().getName());
		}else if(event instanceof EventTime){
			mViewHolder.textDevice.setText("定时");
		}
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
		if(event instanceof EventDevice) {
			EventDevice eventDevice= (EventDevice)event;
			if (eventDevice.getDevice() instanceof Electrical) {
				if (eventDevice.getTriggerValue() == 0) {
					value = "OFF";
				} else {
					value = "ON";
				}
			} else {
				value = String.valueOf(eventDevice.getTriggerValue() + "%");
			}
			mViewHolder.textValue.setText(value);
		}else if(event instanceof EventTime){
			EventTime eventTime = (EventTime)event;
			mViewHolder.textValue.setText(eventTime.getTimerAlarm().getDurationTime().toString() + "[" + eventTime.getTimerAlarm().getWeekHelper().getWeeksName() + "]");
		}
		return convertView;
	}

	static class ViewHolder {
		private TextView textStyle;
		private TextView textDevice;
		private TextView textSymbol;
		private TextView textValue;
	}
}
