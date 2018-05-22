package test.lygzb.com.pressure.guaguamouth;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.List;

import lygzb.zsmarthome.device.Device;
import lygzb.zsmarthome.device.collector.climatecollecter.ClimateDevice;
import lygzb.zsmarthome.device.electrical.Electrical;
import test.lygzb.com.pressure.R;
import test.lygzb.com.pressure.electrical.SceneActHelper;
import test.lygzb.com.pressure.event.AbstractEvent;
import test.lygzb.com.pressure.event.EventDevice;
import test.lygzb.com.pressure.loop.Event;
import test.lygzb.com.pressure.loop.EventStyle;
import test.lygzb.com.pressure.loop.EventSymbol;
import test.lygzb.com.pressure.main.UserHelper;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link AddGuaguaEventDeviceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddGuaguaEventDeviceFragment extends Fragment {
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";

	private String mParam1;

	private EventDevice event;

	private RadioGroup rgStyle;
	private Spinner spinnerDevice;
	private RadioGroup rgSymbol;
	private RadioButton rbGreat;
	private RadioButton rbLess;
	private EditText etxtEventValue;
	private RadioGroup rgEventEleState;
	private Button btnOk;
	private Button btnCancel;

	private SceneActHelper sceneActHelper;
	private List<Device> listOtherDevice;

	public AddGuaguaEventDeviceFragment() {
		sceneActHelper = new SceneActHelper();
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param param1 Parameter 1.
	 * @return A new instance of fragment AddGuaguaEventDeviceFragment.
	 */
	public static AddGuaguaEventDeviceFragment newInstance(int param1) {
		AddGuaguaEventDeviceFragment fragment = new AddGuaguaEventDeviceFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PARAM1, param1);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mParam1 = String.valueOf(getArguments().getInt(ARG_PARAM1));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_add_guagua_event_device, container, false);
		findViews(view);
		setListener();

		listOtherDevice = new ArrayList<>();
		listOtherDevice.addAll(UserHelper.getHomeMaster().getHouseKeeper().getListCtrlableDevice());
		listOtherDevice.addAll(UserHelper.getHomeMaster().getClimateButler().getListDevice());
		if(listOtherDevice.isEmpty()){
			btnOk.setEnabled(false);
		}
		if(AddGuaguaEventActivity.ADD){
			event = new EventDevice();
			String[] names = getDeviceNames();
			spinnerDevice.setAdapter(new ArrayAdapter(this.getContext(),android.R.layout.simple_list_item_1,android.R.id.text1,names));
			if(!listOtherDevice.isEmpty()) {
				spinnerDevice.setSelection(0);
			}
			rgStyle.check(R.id.rbOr);
			rgSymbol.check(R.id.rbEqual);
			rgEventEleState.check(R.id.rbOff);
		}else{
			event = (EventDevice)GuaguaHandler.getIns().getSelectedGuagua().getSelectedEvent();
			listOtherDevice.add(event.getDevice());
			String[] names = getDeviceNames();
			spinnerDevice.setAdapter(new ArrayAdapter(this.getContext(),android.R.layout.simple_list_item_1,android.R.id.text1,names));
			if(!listOtherDevice.isEmpty()) {
				spinnerDevice.setSelection(listOtherDevice.size() - 1);
			}

			switch (event.getEventStyle()){
				case OR:
					rgStyle.check(R.id.rbOr);
					break;
				case ADD:
					rgStyle.check(R.id.rbAdd);
					break;
			}

			switch (event.getEventSymbol()){
				case GREATER:
					rgSymbol.check(R.id.rbGreat);
					break;
				case EQUAL:
					rgSymbol.check(R.id.rbEqual);
					break;
				case LESS:
					rgSymbol.check(R.id.rbLess);
					break;
			}

			if(event.getDevice() instanceof ClimateDevice){
				etxtEventValue.setVisibility(View.VISIBLE);
				rgEventEleState.setVisibility(View.GONE);
				etxtEventValue.setText(String.valueOf(event.getTriggerValue()));
			}else{
				etxtEventValue.setVisibility(View.GONE);
				rgEventEleState.setVisibility(View.VISIBLE);
				if(event.getTriggerValue() == 0){
					rgEventEleState.check(R.id.rbOff);
				}else{
					rgEventEleState.check(R.id.rbOn);
				}
			}
		}
		return view;
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	private void findViews(View view){
		rgStyle = (RadioGroup)view.findViewById(R.id.rgStyle);
		spinnerDevice = (Spinner)view.findViewById(R.id.spinnerDevice);
		rgSymbol = (RadioGroup)view.findViewById(R.id.rgSymbol);
		rbGreat = (RadioButton)view.findViewById(R.id.rbGreat);
		rbLess = (RadioButton)view.findViewById(R.id.rbLess);
		etxtEventValue = (EditText)view.findViewById(R.id.etxtEventValue);
		rgEventEleState = (RadioGroup)view.findViewById(R.id.rgEventEleState);
		btnOk = (Button)view.findViewById(R.id.btnOk);
		btnCancel = (Button)view.findViewById(R.id.btnCancel);
	}

	private void setListener(){
		rgStyle.setOnCheckedChangeListener(onCheckedChangeListener);
		spinnerDevice.setOnItemSelectedListener(onItemSelectedListener);
		rgSymbol.setOnCheckedChangeListener(onCheckedChangeListener);
		rgEventEleState.setOnCheckedChangeListener(onCheckedChangeListener);
		btnOk.setOnClickListener(onClickListener);
		btnCancel.setOnClickListener(onClickListener);
	}

	private void setSpinnerDeviceData(){
		listOtherDevice = sceneActHelper.getListOtherEle(GuaguaHandler.getIns().getSelectedGuagua().getTrigger());
		String[] names = getDeviceNames();
		spinnerDevice.setAdapter(new ArrayAdapter(this.getContext(),android.R.layout.simple_list_item_1,android.R.id.text1,names));
	}

	private String[] getDeviceNames(){
		String[] names = new String[listOtherDevice.size()];
		for(int i=0; i< listOtherDevice.size(); i++){
			Device device = listOtherDevice.get(i);
			names[i] = device.getName();
		}
		return names;
	}

	private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
			switch (group.getCheckedRadioButtonId()){
				case R.id.rbOr:
					break;
				case R.id.rbAdd:
					break;
				case R.id.rbGreat:
					break;
				case R.id.rbEqual:
					break;
				case R.id.rbLess:
					break;
				case R.id.rbOn:
					break;
				case R.id.rbOff:
					break;
			}
		}
	};

	private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			Device dev = listOtherDevice.get(position);
			if(dev instanceof ClimateDevice){
				rbGreat.setEnabled(true);
				rbLess.setEnabled(true);
				etxtEventValue.setVisibility(View.VISIBLE);
				rgEventEleState.setVisibility(View.GONE);
			}else if(dev instanceof Electrical){
				rbGreat.setEnabled(false);
				rbLess.setEnabled(false);
				etxtEventValue.setVisibility(View.GONE);
				rgEventEleState.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	};

	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()){
				case R.id.btnOk:
					if(AddGuaguaEventActivity.ADD){
						event = new EventDevice();
					}
					switch (rgStyle.getCheckedRadioButtonId()){
						case R.id.rbOr:
							event.setEventStyle(EventStyle.OR);
							break;
						case R.id.rbAdd:
							event.setEventStyle(EventStyle.ADD);
							break;
					}
					Device device = listOtherDevice.get(spinnerDevice.getSelectedItemPosition());
					event.setDevice(device);
					switch (rgSymbol.getCheckedRadioButtonId()){
						case R.id.rbGreat:
							event.setEventSymbol(EventSymbol.GREATER);
							break;
						case R.id.rbEqual:
							event.setEventSymbol(EventSymbol.EQUAL);
							break;
						case R.id.rbLess:
							event.setEventSymbol(EventSymbol.LESS);
							break;
					}
					if(device instanceof ClimateDevice){
						event.setTriggerValue(Double.parseDouble(etxtEventValue.getText().toString()));
					}else if(device instanceof Electrical){
						switch (rgEventEleState.getCheckedRadioButtonId()){
							case R.id.rbOn:
								event.setTriggerValue(1);
								break;
							case R.id.rbOff:
								event.setTriggerValue(0);
								break;
						}
					}
					if(AddGuaguaEventActivity.ADD) {
						GuaguaHandler.getIns().getSelectedGuagua().addEvent(event);
					}
					if(ChildGuaguaActivity.handler != null){
						Message message = Message.obtain();
						message.arg1 = ChildGuaguaActivity.REFRESH_EVENT_HANDLER_LIST;
						ChildGuaguaActivity.handler.sendMessage(message);
					}
					AddGuaguaEventDeviceFragment.this.getActivity().finish();
					break;
				case R.id.btnCancel:
					AddGuaguaEventDeviceFragment.this.getActivity().finish();
					break;
			}
		}
	};

}
