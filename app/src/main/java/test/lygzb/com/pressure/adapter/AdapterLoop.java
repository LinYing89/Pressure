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
import test.lygzb.com.pressure.loop.Loop;

/**
 * Created by Administrator on 2016/5/31.
 */
public class AdapterLoop extends BaseAdapter {

	private Context context;
	private List<Loop> listLoop;

	public AdapterLoop(Context context, List<Loop> listLoop){
		this.context = context;
		this.listLoop = listLoop;
	}

	public int getCount() {
		return listLoop.size();
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
		final Loop loop = listLoop.get(position);
		textName.setText(loop.getName());
		switchEnable.setChecked(loop.isEnable());
		switchEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				synchronized (DeviceChainHelper.getIns()) {
					loop.setEnable(isChecked);
					if (!isChecked) {
						for (Device device : loop.getTrigger().getListDevice()) {
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
