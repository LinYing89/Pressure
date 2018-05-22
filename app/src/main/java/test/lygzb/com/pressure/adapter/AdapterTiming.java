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

import lygzb.zsmarthome.device.Device;
import test.lygzb.com.pressure.R;
import test.lygzb.com.pressure.chain.DeviceChain;
import test.lygzb.com.pressure.chain.DeviceChainHelper;
import test.lygzb.com.pressure.timing.MyTiming;

/**
 * Created by Administrator on 2016/6/14.
 */
public class AdapterTiming extends BaseAdapter {

	private Context context;
	private List<MyTiming> listTiming;

	public AdapterTiming(Context context, List<MyTiming> listTiming){
		this.context = context;
		this.listTiming = listTiming;
	}

	public int getCount() {
		return listTiming.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = vi.inflate(R.layout.adapter_list_loop, null);
		TextView textName  = (TextView) convertView.findViewById(R.id.text_loop_name);
		Switch switchEnable  = (Switch) convertView.findViewById(R.id.switch_enable);
		final MyTiming timing = listTiming.get(position);
		textName.setText(timing.getName());
		switchEnable.setChecked(timing.isEnable());
		switchEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				synchronized (DeviceChainHelper.getIns()) {
					timing.setEnable(isChecked);
					if (!isChecked) {
						for (Device device : timing.getTrigger().getListDevice()) {
							DeviceChain dc = DeviceChainHelper.getIns().getEqDeviceChain(device);
							if(null != dc){
								dc.setTiming(-1);
								dc.setiTimingTem(-1);
							}
						}
					}
				}
			}
		});
		return convertView;
	}
}
