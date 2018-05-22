package test.lygzb.com.pressure.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import lygzb.zsmarthome.device.Device;
import lygzb.zsmarthome.device.collector.climatecollecter.ClimateDevice;
import lygzb.zsmarthome.device.collector.climatecollecter.Pressure;
import test.lygzb.com.pressure.R;

/**
 * Created by Administrator on 2016/4/10.
 */
public class AdapterClimate extends BaseAdapter {

	public static final int STATE = 0;
	public static final int VALUE = 1;

	public static MyHandler handler;

	private LayoutInflater vi;
	private Context context;
	private List<ClimateDevice> nameList;
	private List<ViewHolder> listViewHolder;

	public AdapterClimate(Context context, List<ClimateDevice> nameList){
		this.context = context;
		this.nameList = nameList;
		vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		listViewHolder = new ArrayList<>();
		handler = new MyHandler(this);
	}

	public int getCount() {
		return nameList.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mViewHolder = null;
		ClimateDevice device = nameList.get(position);

		if(convertView == null){
			mViewHolder = new ViewHolder();
			listViewHolder.add(mViewHolder);
			convertView = vi.inflate(R.layout.climate_list, parent, false);
			mViewHolder.textName  = (TextView) convertView.findViewById(R.id.text_name);
			mViewHolder.textAlias  = (TextView) convertView.findViewById(R.id.text_alias);
			mViewHolder.textState  = (TextView) convertView.findViewById(R.id.text_value);
			mViewHolder.textPer  = (TextView) convertView.findViewById(R.id.text_per);
			mViewHolder.progressValue  = (ProgressBar) convertView.findViewById(R.id.progress_value);
			mViewHolder.device = nameList.get(position);
			convertView.setTag(mViewHolder);
			mViewHolder.rootView = convertView;
		}else{
			mViewHolder = (ViewHolder) convertView.getTag();
		}

		mViewHolder.device = device;

		mViewHolder.init();
		mViewHolder.refreshValue();
		mViewHolder.refreshState();
		return convertView;
	}

	static class ViewHolder {
		private ClimateDevice device;
		private View rootView;
		private TextView textAlias;
		private TextView textName;
		private TextView textState;
		private TextView textPer;
		private ProgressBar progressValue;

		private void init() {
			textName.setText(device.getName());
			textAlias.setText(device.getAlias());
		}

		private void refreshValue(){
			textState.setText(device.getValueString());
			if(device instanceof Pressure) {
				Pressure pressure = (Pressure)device;
				textPer.setText(pressure.getScaleValueString());
				if(null != pressure.getValue()) {
					progressValue.setProgress(pressure.getPerValue().intValue());
				}else{
					progressValue.setProgress(0);
				}
			}else{
				progressValue.setVisibility(View.GONE);
			}
		}

		private void refreshState(){
			if(!device.isNormal()){
				rootView.setBackgroundColor(Color.parseColor("#E9967A"));
			}else{
				rootView.setBackgroundColor(Color.TRANSPARENT);
			}
		}
	}

	public static class MyHandler extends Handler {
		WeakReference<AdapterClimate> mActivity;

		MyHandler(AdapterClimate activity) {
			mActivity = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			AdapterClimate theActivity = mActivity.get();
			Device dev = (Device)msg.obj;
			for(AdapterClimate.ViewHolder vh : theActivity.listViewHolder){
				if(vh.device == dev){
					switch (msg.arg1) {
						case STATE:
							vh.refreshState();
							break;
						case VALUE :
							vh.refreshValue();
							break;
					}
					break;
				}
			}

		}
	}
}
