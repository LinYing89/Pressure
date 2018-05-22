package test.lygzb.com.pressure.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

import lygzb.zsmarthome.device.Device;
import lygzb.zsmarthome.device.EDeviceModel;
import lygzb.zsmarthome.device.collector.climatecollecter.ClimateDevice;
import lygzb.zsmarthome.device.collector.climatecollecter.Pressure;
import test.lygzb.com.pressure.R;
import test.lygzb.com.pressure.adapter.AdapterClimate;
import test.lygzb.com.pressure.adapter.AdapterElectrical;
import test.lygzb.com.pressure.application.Constant;
import test.lygzb.com.pressure.application.SendMsgHelper;

public class ClimateFragment extends Fragment {

	private static final String ARG_PARAM1 = "param1";

	public static final int REFRESH_VALUE = 1;
	public static final int REFRESH_DEVICE = 2;
	public static final int REFRESH_SORT= 3;

	public static MyHandler handler;

	//液位计最大高度,就是图片的高度
	private int maxHeight = 0;
	//比例，接收到的值与要显示的值之间的比例，
	private double proportion;
	private ListView listViewPressure;
	private AdapterClimate adapterClimate;

	public ClimateFragment() {
		// Required empty public constructor
	}

	public static ClimateFragment newInstance(int param1) {
		ClimateFragment fragment = new ClimateFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PARAM1, param1);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_climate, container, false);
		handler = new MyHandler(this);
		listViewPressure = (ListView)view.findViewById(R.id.list_pressure);
		setListener();
		setPressueList();
		return view;
	}

	private void setListener(){
		listViewPressure.setOnItemClickListener(onPressureItemClickListener);
		listViewPressure.setOnItemLongClickListener(onPressureLongClick);
	}

	private void setPressueList(){
		List<ClimateDevice> listDev = UserHelper.getHomeMaster().getClimateButler().getListChildDevice();
		Collections.sort(listDev);
		adapterClimate = new AdapterClimate(this.getContext(), listDev);
		listViewPressure.setAdapter(adapterClimate);
		for(ClimateDevice cd : listDev){
			cd.addStateChangedListener(onStateChangedListener);
			cd.addOnValueChangedListener(onValueChangedListener);
		}
	}

	private Device.OnStateChangedListener onStateChangedListener = new Device.OnStateChangedListener() {
		@Override
		public void onStateChanged(Device device, String s) {
			if(s.equals("0") || s.equals("1")){
				refreshAdapter(device, AdapterClimate.STATE);
			}
		}

		@Override
		public void onGateChanged(Device device, int i) {

		}

		@Override
		public void onCtrlModelChanged(Device device, EDeviceModel eDeviceModel) {

		}
	};

	private ClimateDevice.OnValueChangedListener onValueChangedListener = new ClimateDevice.OnValueChangedListener() {
		@Override
		public void onValueChanged(ClimateDevice climateDevice, double v) {
			refreshAdapter(climateDevice, AdapterClimate.VALUE);
		}
	};

	private void refreshAdapter(Device device, int which){
		if(null != AdapterClimate.handler) {
			Message msg = Message.obtain();
			msg.obj = device;
			msg.arg1 = which;
			AdapterClimate.handler.sendMessage(msg);
		}
	}

	private AdapterView.OnItemClickListener onPressureItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//			UserHelper.getHomeMaster().getClimateButler().setSelectedDevice(position);
//			UserHelper.getHomeMaster().getClimateButler().setSelectedClimateDevice((ClimateDevice) UserHelper.getHomeMaster().getClimateButler().getSelectedDevice());
//
//			adapterClimate.notifyDataSetChanged();
//			Message msg = Message.obtain();
//			msg.arg1 = 1;
//			handler.sendMessage(msg);
		}
	};

	private AdapterView.OnItemLongClickListener onPressureLongClick = new AdapterView.OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			UserHelper.getHomeMaster().getClimateButler().setSelectedDevice(position);
			UserHelper.getHomeMaster().getClimateButler().setSelectedClimateDevice((ClimateDevice) UserHelper.getHomeMaster().getClimateButler().getSelectedDevice());
			showElectricalPopUp(view);
			return true;
		}
	};

	public void showElectricalPopUp(View v) {
		LayoutInflater vi = (LayoutInflater) ClimateFragment.this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = vi
				.inflate(R.layout.pop_climate_long_click, null);
		Button btnMaxMin = (Button) layout
				.findViewById(R.id.btn_max_min);
		btnMaxMin.setVisibility(View.GONE);
		Button btnSetMax = (Button) layout
				.findViewById(R.id.btn_set_max);
		final PopupWindow popupWindow = new PopupWindow(layout, Constant.displayWidth,
				Constant.getEleHeight());

		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());

		int[] location = new int[2];
		v.getLocationOnScreen(location);

		popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, 0, Constant.displayHeight - Constant.getEleHeight());
		btnMaxMin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
				ClimateDevice climateDevice = UserHelper.getHomeMaster().getClimateButler().getSelectedClimateDevice();
				if(climateDevice instanceof Pressure){
					Pressure pressure = (Pressure)climateDevice;
					//同步液位计，防止查询液位的线程更改命令
					synchronized (UserHelper.getHomeMaster().getClimateButler().getSelectedClimateDevice()) {
						pressure.turnMaxMinValueOrder();
						SendMsgHelper.sendMessage(climateDevice.createFinalOrder(climateDevice.getQueryOrder()));
					}
				}
			}
		});
		btnSetMax.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
				showMaxValueDialog();
			}
		});
	}

	private void showMaxValueDialog() {
		ClimateDevice climateDevice = UserHelper.getHomeMaster().getClimateButler().getSelectedClimateDevice();
		if(!(climateDevice instanceof Pressure)){
			return;
		}
		final Pressure pressure = (Pressure)climateDevice;
		final EditText edit_newName = new EditText(ClimateFragment.this.getContext());
		edit_newName.setInputType(InputType.TYPE_CLASS_NUMBER);
		edit_newName.setText(String.valueOf(pressure.getiMaxValueSet()));
		new AlertDialog.Builder(ClimateFragment.this.getContext())
				.setTitle("输入最大值，单位mm")
				.setView(edit_newName)
				.setPositiveButton(Main3Activity.strEnsure,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
												int which) {
								String value = edit_newName.getText()
										.toString();
								try{
									int iValue = Integer.parseInt(value);
									pressure.setiMaxValueSet(iValue);
								}catch (Exception e){
									e.printStackTrace();
								}
							}
						}).setNegativeButton(Main3Activity.strCancel, null).create().show();
	}

	public static class MyHandler extends Handler {
		WeakReference<ClimateFragment> mActivity;

		MyHandler(ClimateFragment activity) {
			mActivity = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO handler
			final ClimateFragment theActivity = mActivity.get();
			switch (msg.arg1) {
				case REFRESH_VALUE:
					//theActivity.adapterClimate.notifyDataSetChanged();
					break;
				case REFRESH_DEVICE :
					theActivity.setPressueList();
					break;
				case REFRESH_SORT :
					Collections.sort(UserHelper.getHomeMaster().getClimateButler().getListChildDevice());
					theActivity.adapterClimate.notifyDataSetChanged();
					break;
			}

		}
	};
}
