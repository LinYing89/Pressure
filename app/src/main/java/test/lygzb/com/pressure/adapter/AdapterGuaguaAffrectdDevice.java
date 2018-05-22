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
import lygzb.zsmarthome.device.LinkedGuaguaMouth;
import test.lygzb.com.pressure.R;

/**
 * Created by Administrator on 2017/4/20.
 */

public class AdapterGuaguaAffrectdDevice extends BaseAdapter {

	private Context context;
	private List<LinkedDevice> listLinkedDevice;

	public AdapterGuaguaAffrectdDevice(Context context, List<LinkedDevice> listLinkedDevice){
		this.context = context;
		this.listLinkedDevice = listLinkedDevice;
	}

	@Override
	public int getCount() {
		return listLinkedDevice.size();
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
		AdapterGuaguaAffrectdDevice.ViewHolder mViewHolder = null;
		if(convertView == null){
			mViewHolder = new AdapterGuaguaAffrectdDevice.ViewHolder();

			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			convertView = vi.inflate(R.layout.adapter_guagua_affrectd_list, parent, false);
			mViewHolder.txtTitle  = (TextView) convertView.findViewById(R.id.txtTitle);
			mViewHolder.txtSubTitle  = (TextView) convertView.findViewById(R.id.txtSubTitle);
			mViewHolder.txtSpeakCount  = (TextView) convertView.findViewById(R.id.txtSpeakCount);
			convertView.setTag(mViewHolder);
		}else{
			mViewHolder = (AdapterGuaguaAffrectdDevice.ViewHolder) convertView.getTag();
		}
		final LinkedDevice linkedDevice = listLinkedDevice.get(position);
		mViewHolder.txtTitle.setText(linkedDevice.getDevice().getName());
		mViewHolder.txtSubTitle.setText(linkedDevice.getAction());
		if(linkedDevice instanceof LinkedGuaguaMouth) {
			mViewHolder.txtSpeakCount.setText(String.valueOf(((LinkedGuaguaMouth)linkedDevice).getSpeakCount()));
		}
		return convertView;
	}

	static class ViewHolder {
		private TextView txtTitle;
		private TextView txtSubTitle;
		private TextView txtSpeakCount;
	}
}
