package test.lygzb.com.pressure.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import lygzb.zsmarthome.device.Device;
import lygzb.zsmarthome.device.EDeviceModel;
import test.lygzb.com.pressure.R;

public class SearchDeviceAdapter extends BaseAdapter {

	private Context context;
	private List<Device> listDevice;

	public SearchDeviceAdapter(Context context, List<Device> listDevice){
		this.context = context;
		this.listDevice = listDevice;
	}
	public int getCount() {
		return listDevice.size();
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

			convertView = vi.inflate(R.layout.search_device_list, parent, false);
			mViewHolder.textName  = (TextView) convertView.findViewById(R.id.changer);
			mViewHolder.textCoding  = (TextView) convertView.findViewById(R.id.text_coding);
			mViewHolder.redGreen  = (ImageView) convertView.findViewById(R.id.red_green);
			mViewHolder.textCtrlModel  = (TextView) convertView.findViewById(R.id.txtCtrlModel);
			convertView.setTag(mViewHolder);
		}else{
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		
		Device device = listDevice.get(position);
		mViewHolder.textName.setText(device.getName());
		mViewHolder.textCoding.setText(device.getCoding());
		if(device.getDeviceModel() == EDeviceModel.LOCAL){
			mViewHolder.textCtrlModel.setText("本地");
		}else{
			mViewHolder.textCtrlModel.setText("远程");
		}
		if(device.isNormal())
			mViewHolder.redGreen.setImageResource(R.mipmap.normal_green);
		else
			mViewHolder.redGreen.setImageResource(R.mipmap.abnormal_red);
		return convertView;
	}

	static class ViewHolder {
		private TextView textName;
		private TextView textCoding;
		private ImageView redGreen;
		private TextView textCtrlModel;
	}

}
