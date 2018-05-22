package test.lygzb.com.pressure.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import lygzb.zsmarthome.device.DevState;
import lygzb.zsmarthome.device.Device;
import lygzb.zsmarthome.device.EDeviceModel;
import lygzb.zsmarthome.device.electrical.Electrical;
import test.lygzb.com.pressure.R;
import test.lygzb.com.pressure.application.SendMsgHelper;
import test.lygzb.com.pressure.network.WebClient;

/**
 *
 * Created by Administrator on 2016/4/11.
 */
public class AdapterElectrical extends BaseAdapter {

	public static final int AUTO = 0;
	public static final int CTRL_MODEL = 1;
	public static final int STATE = 2;

	public static MyHandler handler;

	private LayoutInflater mInflater;
	private Context context;
	private List<Device> listDevice;
	private List<ViewHolder> listViewHolder;

	public AdapterElectrical(Context context, List<Device> listDevice){
		this.context = context;
		this.listDevice = listDevice;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		listViewHolder = new ArrayList<>();
		handler = new MyHandler(this);
	}
	@Override
	public int getCount() {
		return listDevice.size();
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
		final ViewHolder myViews;
		final Device device = listDevice.get(position);
		if (convertView == null){
			myViews = new ViewHolder();
			listViewHolder.add(myViews);
			convertView = mInflater.inflate(R.layout.adapter_electrical, null);
			myViews.textGate = (TextView) convertView.findViewById(R.id.text_gate);
			myViews.textName = (TextView) convertView.findViewById(R.id.text_name);
			myViews.textCtrlModel = (TextView) convertView.findViewById(R.id.text_ctrl_model);
			myViews.btnOn = (Button) convertView.findViewById(R.id.btn_on);
			myViews.btnAuto = (Button) convertView.findViewById(R.id.btn_auto);
			myViews.btnOff = (Button) convertView.findViewById(R.id.btn_off);
			convertView.setTag(myViews);
			myViews.rootView = convertView;
		}
		else {
			myViews = (ViewHolder ) convertView.getTag();
		}

		myViews.device = device;

		myViews.init();
		myViews.refreshState();
		myViews.refreshDeviceModel();
		myViews.refreshAutoState();

		return convertView;
	}

	static class ViewHolder {
		private View rootView;
		private Device device;
		private TextView textGate;
		private TextView textName;
		private TextView textCtrlModel;
		private Button btnOn;
		private Button btnAuto;
		private Button btnOff;

		private void init() {
			textName.setText(device.getName());
			textGate.setText(device.getAlias());

			btnOn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					device.handleLinked(DevState.ON);
					device.setGear(Integer.parseInt(DevState.ON));
					refreshAutoState();
					sendAutoState(device);
					SendMsgHelper.sendMessage(device);
					//Log.e("btnOn  ", device.getOrder());
				}
			});
			btnAuto.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					device.setGear(0);
					refreshAutoState();
					sendAutoState(device);
					//((Button)v).setTextColor(Color.BLUE);
					//Log.e("btnAuto", device.getOrder());
				}
			});
			btnOff.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					device.handleLinked(DevState.OFF);
					device.setGear(Integer.parseInt(DevState.OFF));
					refreshAutoState();
					sendAutoState(device);
					SendMsgHelper.sendMessage(device);
					//Log.e("btnOff ", device.getOrder());
				}
			});

			refreshAutoState();
			refreshState();
			refreshDeviceModel();
		}

		private void sendAutoState(Device device){
			if(device instanceof Electrical){
				Electrical ele = (Electrical)device;
				String state = "$G" + ele.getController().getCoding() + ":" + ele.getGear() + ele.getNum() + "#00";
				WebClient.getInstance().sendMsg(state);
			}
		}

		private void refreshAutoState(){
			switch (device.getGear()){
				case 3:
					btnOn.setTextColor(Color.BLUE);
					btnAuto.setTextColor(Color.BLACK);
					btnOff.setTextColor(Color.BLACK);
					break;
				case 4:
					btnOn.setTextColor(Color.BLACK);
					btnAuto.setTextColor(Color.BLACK);
					btnOff.setTextColor(Color.BLUE);
					break;
				case 0:
					btnOn.setTextColor(Color.BLACK);
					btnAuto.setTextColor(Color.BLUE);
					btnOff.setTextColor(Color.BLACK);
					break;
			}
		}

		private void refreshState(){
			if (!device.isNormal()) {
				rootView.setBackgroundColor(Color.parseColor("#E9967A"));
			} else {
				if (device.isWorking()) {
					rootView.setBackgroundColor(Color.parseColor("#B4EEB4"));
				} else {
					rootView.setBackgroundColor(Color.TRANSPARENT);
				}
			}
		}

		private void refreshDeviceModel(){
			if (device.getDeviceModel() == EDeviceModel.REMOTE) {
				if (!textCtrlModel.getText().equals("远程")) {
					textCtrlModel.setText("远程");
				}
			} else if (!textCtrlModel.getText().equals("本地")) {
				textCtrlModel.setText("本地");
			}
		}
	}

	public static class MyHandler extends Handler {
		WeakReference<AdapterElectrical> mActivity;

		MyHandler(AdapterElectrical activity) {
			mActivity = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			AdapterElectrical theActivity = mActivity.get();
			Device dev = (Device)msg.obj;
			for(ViewHolder vh : theActivity.listViewHolder){
				if(vh.device == dev){
					switch (msg.arg1) {
						case AUTO:
							vh.refreshAutoState();
							break;
						case CTRL_MODEL :
							vh.refreshDeviceModel();
							break;
						case STATE:
							vh.refreshState();
							break;
					}
					break;
				}
			}

		}
	}
}
