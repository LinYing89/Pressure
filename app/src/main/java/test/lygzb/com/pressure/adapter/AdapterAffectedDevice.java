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

import lygzb.zsmarthome.device.DevState;
import lygzb.zsmarthome.device.LinkedDevice;
import test.lygzb.com.pressure.R;

/**
 * Created by Administrator on 2016/6/4.
 */
public class AdapterAffectedDevice extends BaseAdapter {

	private Context context;
	private List<LinkedDevice> listAffectedDevice;
	private boolean show;

	public AdapterAffectedDevice(Context context, List<LinkedDevice> listAffectedDevice, boolean show){
		this.context = context;
		this.listAffectedDevice = listAffectedDevice;
		this.show = show;
	}

	public int getCount() {
		return listAffectedDevice.size();
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

			convertView = vi.inflate(R.layout.adapter_list_loop, parent, false);
			mViewHolder.textName  = (TextView) convertView.findViewById(R.id.text_loop_name);
			mViewHolder.switchEnable  = (Switch) convertView.findViewById(R.id.switch_enable);
			convertView.setTag(mViewHolder);
		}else{
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		final LinkedDevice linkedDevice = listAffectedDevice.get(position);
		mViewHolder.textName.setText(linkedDevice.getDevice().getName());
		if(show) {
			mViewHolder.switchEnable.setVisibility(View.VISIBLE);
			if (linkedDevice.getAction().equals(DevState.ON)) {
				mViewHolder.switchEnable.setChecked(true);
			} else {
				mViewHolder.switchEnable.setChecked(false);
			}
			mViewHolder.switchEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						linkedDevice.setAction(DevState.ON);
					} else {
						linkedDevice.setAction(DevState.OFF);
					}
				}
			});
		}else{
			mViewHolder.switchEnable.setVisibility(View.GONE);
		}
		return convertView;
	}

	static class ViewHolder {
		private TextView textName;
		private Switch switchEnable;
	}
}
