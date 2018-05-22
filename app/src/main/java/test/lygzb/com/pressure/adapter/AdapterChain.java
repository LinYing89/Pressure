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
import test.lygzb.com.pressure.chain.Chain;
import test.lygzb.com.pressure.chain.DeviceChain;
import test.lygzb.com.pressure.chain.DeviceChainHelper;

/**
 * Created by Administrator on 2016/6/4.
 */
public class AdapterChain extends BaseAdapter {
	private Context context;
	private List<Chain> listChain;

	public AdapterChain(Context context, List<Chain> listChain){
		this.context = context;
		this.listChain = listChain;
	}

	public int getCount() {
		return listChain.size();
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
		final Chain chain = listChain.get(position);
		textName.setText(chain.getName());
		switchEnable.setChecked(chain.isEnable());
		switchEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				synchronized (DeviceChainHelper.getIns()) {
					chain.setEnable(isChecked);
					if (!isChecked) {
						for (Device device : chain.getTrigger().getListDevice()) {
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

//	public View getView(int position, View convertView, ViewGroup parent) {
//
//		ViewHolder mViewHolder = null;
//		if(convertView == null){
//			mViewHolder = new ViewHolder();
//
//			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//			convertView = vi.inflate(R.layout.adapter_list_loop, null);
//			mViewHolder.textName  = (TextView) convertView.findViewById(R.id.text_loop_name);
//			mViewHolder.switchEnable  = (Switch) convertView.findViewById(R.id.switch_enable);
//			//mViewHolder.chain = listChain.get(position);
//			convertView.setTag(mViewHolder);
//		}else{
//			mViewHolder = (ViewHolder) convertView.getTag();
//		}
//		final Chain chain = listChain.get(position);;
//		mViewHolder.textName.setText(chain.getName());
//		mViewHolder.switchEnable.setChecked(chain.isEnable());
//		mViewHolder.switchEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				chain.setEnable(isChecked);
//			}
//		});
//		return convertView;
//	}

	static class ViewHolder {
		//private Chain chain;
		private TextView textName;
		private Switch switchEnable;
	}
}
