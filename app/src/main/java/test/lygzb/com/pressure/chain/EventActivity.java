package test.lygzb.com.pressure.chain;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import lygzb.zsmarthome.device.Device;
import lygzb.zsmarthome.device.collector.climatecollecter.ClimateDevice;
import lygzb.zsmarthome.device.electrical.Electrical;
import test.lygzb.com.pressure.R;
import test.lygzb.com.pressure.adapter.BlueListAdapter;
import test.lygzb.com.pressure.application.BackListener;
import test.lygzb.com.pressure.loop.Event;
import test.lygzb.com.pressure.loop.EventStyle;
import test.lygzb.com.pressure.loop.EventSymbol;
import test.lygzb.com.pressure.main.UserHelper;

public class EventActivity extends AppCompatActivity {

	public static Event event;
	public static Handler handler;

	private Spinner spinnerStyle;
	private Spinner spinnerDevice;
	private Spinner spinnerSymbol;
	private Spinner spinnerValue;
	private EditText editValue;
	private Button btnSave;
	private Button btnCancel;

	private List<Device> listDevice;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		toolbar.setNavigationOnClickListener(new BackListener(this));
		findViews();
		setListener();

		if(null == event){
			event = new Event();
		}

		listDevice = new ArrayList<>();
		listDevice.addAll(UserHelper.getHomeMaster().getHouseKeeper().getListCtrlableDevice());
		listDevice.addAll(UserHelper.getHomeMaster().getClimateButler().getListDevice());
		List<String> listDeviceName = new ArrayList<>();
		for(Device device : listDevice){
			listDeviceName.add(device.getName());
		}
		BlueListAdapter adapter = new BlueListAdapter(this, listDeviceName);
		spinnerDevice.setAdapter(adapter);

		if(event.getEventStyle() == EventStyle.ADD){
			spinnerStyle.setSelection(0);
		}else{
			spinnerStyle.setSelection(1);
		}

		int iDevice = listDevice.indexOf(event.getDevice());
		spinnerDevice.setSelection(iDevice);

		if(event.getDevice() instanceof Electrical){
			showElectricalStyle();
		}else{
			showClimateStyle();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		event = null;
		handler = null;
	}

	private void findViews(){
		spinnerStyle = (Spinner)findViewById(R.id.spinner_style);
		spinnerDevice = (Spinner)findViewById(R.id.spinner_device);
		spinnerSymbol = (Spinner)findViewById(R.id.spinner_symbol);
		spinnerValue = (Spinner)findViewById(R.id.spinner_value);
		editValue = (EditText)findViewById(R.id.edit_value);
		btnSave = (Button)findViewById(R.id.btn_save);
		btnCancel = (Button)findViewById(R.id.btn_cancel);
	}

	private void setListener(){
		spinnerStyle.setOnItemSelectedListener(styleOnItemSelectedListener);
		spinnerDevice.setOnItemSelectedListener(deviceOnItemSelectedListener);
		spinnerSymbol.setOnItemSelectedListener(symbolOnItemSelectedListener);
		spinnerValue.setOnItemSelectedListener(valueOnItemSelectedListener);
		btnSave.setOnClickListener(onClickListener);
		btnCancel.setOnClickListener(onClickListener);
	}

	private void showElectricalStyle(){
		spinnerValue.setVisibility(View.VISIBLE);
		editValue.setVisibility(View.GONE);
		spinnerSymbol.setSelection(1);
		spinnerSymbol.setEnabled(false);
		event.setEventSymbol(EventSymbol.EQUAL);
		event.setTriggerValue(0);
	}

	private void showClimateStyle(){
		spinnerValue.setVisibility(View.GONE);
		editValue.setVisibility(View.VISIBLE);
		setSpinnerSymbol();
		spinnerSymbol.setEnabled(true);
		editValue.setText(String.valueOf(event.getTriggerValue()));
	}

	private void setSpinnerSymbol(){
		if(event.getEventSymbol() == EventSymbol.GREATER){
			spinnerSymbol.setSelection(0);
		}else if(event.getEventSymbol() == EventSymbol.EQUAL){
			spinnerSymbol.setSelection(1);
		}else {
			spinnerSymbol.setSelection(2);
		}
	}
	/**
	 * 方式选择事件，ADD/OR
	 */
	private AdapterView.OnItemSelectedListener styleOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			if(null == event){
				return;
			}
			if(position == 0){
				event.setEventStyle(EventStyle.ADD);
			}else{
				event.setEventStyle(EventStyle.OR);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	};

	/**
	 * 设备选择事件，ADD/OR
	 */
	private AdapterView.OnItemSelectedListener deviceOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			if(null == event){
				return;
			}
			Device device = listDevice.get(position);
			event.setDevice(device);
			if(device instanceof Electrical){
				showElectricalStyle();
			}else{
				showClimateStyle();
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	};

	/**
	 * 比较符号选择事件，ADD/OR
	 */
	private AdapterView.OnItemSelectedListener symbolOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			if(null == event || event.getDevice() == null){
				return;
			}
			if(event.getDevice() instanceof Electrical){
				spinnerSymbol.setSelection(1);
				event.setEventSymbol(EventSymbol.EQUAL);
			}else{
				if(position == 0){
					event.setEventSymbol(EventSymbol.GREATER);
				}else if(position == 1){
					event.setEventSymbol(EventSymbol.EQUAL);
				}else {
					event.setEventSymbol(EventSymbol.LESS);
				}
			}

		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	};

	/**
	 * 值选择事件，ADD/OR
	 */
	private AdapterView.OnItemSelectedListener valueOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			if(null == event || event.getDevice() == null){
				return;
			}
			event.setTriggerValue(position);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	};

	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()){
				case R.id.btn_save:
					if(event.getDevice() != null){
						try {
							if(event.getDevice() instanceof ClimateDevice) {
								event.setTriggerValue(Integer.parseInt(editValue.getText().toString()));
							}
							if(null != handler){
								Message message = Message.obtain();
								message.arg1 = ChildChainActivity.REFRESH_EVENT_HANDLER_LIST;
								message.obj = event;
								handler.sendMessage(message);
							}
						}catch (Exception e){
							e.printStackTrace();
						}

					}
					finish();
					break;
				case R.id.btn_cancel:
					finish();
					break;
			}
		}
	};

}
